/* Stefan Andreea-Bianca - 314CB*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "series.h"
#include "queue.h"
#include "stack.h"

typedef struct {
    List categories[4];
    List* top10;
    Queue later;
    Stack current;
    Stack history;
} Flix;

Flix* FAlloc() {
    Flix* flix = (Flix*)balloc(sizeof(Flix));

    flix->top10 = &flix->categories[3];

    return flix;
}

void FFree(Flix* flix) {
    int i = 0;
    for(; i < 4; i++) {
        LFreeExt(flix->categories[i], (void (*)(void *)) SFree);
    }
    LFreeExt(flix->later, (void (*)(void *)) SFree);
    LFreeExt(flix->current, (void (*)(void *)) SFree);
    LFreeExt(flix->history, (void (*)(void *)) SFree);

    bfree(flix);
}

int pozitie(Flix* flix, Series *series, int categoryId)
{
    int poz = 0;
    Cell * p = flix->categories[categoryId];
    for(; p != NULL; p = p->next) {
        poz++;
        if(series == p->elem)
            return poz;
    }
    return poz;
}

int pozitieTop(Flix* flix, Series *series)
{
    int poz = 0;
    Cell *p = *flix->top10;
    for(; p != NULL; p = p->next) {
        poz++;
        if(series == p->elem)
            return poz;
    }
    return poz;
}

int pozitieLater(Flix* flix, Series *series)
{
    int poz = 0;
    Cell *p = flix->later;
    for(; p != NULL; p = p->next) {
        poz++;
        if(series == p->elem)
            return poz;
    }
    return poz;
}

int pozitieWatching(Flix* flix, Series *series)
{
    int poz = 0;
    Cell *p = flix->current;
    for(; p != NULL; p = p->next) {
        poz++;
        if(series == p->elem)
            return poz;
    }
    return poz;
}

int findHistory(Flix* flix, char* name)
{
    Cell* p = flix->history;
    for (; p != NULL; p = p->next) {
        Series* series = (Series*)p->elem;
        if (!strcmp(series->name, name)) return 1;
    }
    return 0;
}

int findWatching(Flix* flix, Series* series)
{
    Cell* p = flix->current;
    for (; p != NULL; p = p->next) {
        Series* serie = (Series*)p->elem;
        if (series == serie) return 1;
    }
    return 0;
}


Series* findSeries(Flix* flix, char* name)
{
    Cell* p = flix->later;
    for (; p != NULL; p = p->next) {
        Series* series = (Series*)p->elem;
        if (!strcmp(series->name, name)) return series;
    }
    Cell* q = flix->current;
    for (; q != NULL; q = q->next) {
        Series* series = (Series*)q->elem;
        if (!strcmp(series->name, name)) return series;
    }
    Cell* r = *flix->top10;
    for (; r != NULL; r = r->next) {
        Series* series = (Series*)r->elem;
        if (!strcmp(series->name, name)) return series;
    }
    int i;
    for(i = 0; i < 4; i++) {
        Cell* s = flix->categories[i];
        for (; s != NULL; s = s->next) {
            Series* series = (Series*)s->elem;
            if (!strcmp(series->name, name)) return series;
        }
    }
    return NULL;
}

int Compare(Series *series, Series *serie)
{
    if(series->rating < serie->rating || (series->rating == serie->rating && strcmp(series->name, serie->name) > 0)) return 1;
    if(series->rating > serie->rating  || (series->rating == serie->rating && strcmp(series->name, serie->name) < 0)) return -1;
    return 0;
}

void printSerial(Series *series, int index, FILE *outFile)
{
    if(index == 0) fprintf(outFile, "(%s, %0.1f)", series->name, series->rating);
    else fprintf(outFile, ", (%s, %0.1f)", series->name, series->rating);
}

void addSeason(Series* series, Queue* args) {
    int episodeCount = (int) atol(QPop(args));
    Queue *season = (Queue*)&QPush(&series->seasons, NULL)->elem;
    int j = 0;
    for (; j < episodeCount; j++) {
        Episode episodeTime = (Episode) atol(QPop(args));
        QPush(season, (void*)episodeTime);
        series->TotalTime += episodeTime;
    }
}

Series* addSeries(int categoryIndx, Queue* args) {
    char *name = QPop(args);
    float rating = atof(QPop(args));
    Series *series = SeAlloc(name, rating, categoryIndx);
    int seasonCount = (int) atol(QPop(args));
    int i = 0;
    for (; i < seasonCount; i++) {
        addSeason(series, args);
    }
    return series;
}

void add(Flix* flix, Queue* args, FILE *outFile) {
    int categoryIndx = (int) atol(QPop(args)) - 1;
    Series* series = addSeries(categoryIndx, args);
    LAddSorted(&flix->categories[categoryIndx], series, (int (*)(void *, void *)) Compare);
    fprintf(outFile, "Serialul %s a fost adaugat la pozitia %d.\n", series->name, pozitie(flix, series, categoryIndx));
}

void add_sez(Flix* flix, Queue* args, FILE *outFile)
{
    char* name = QPop(args);
    Series *series = findSeries(flix, name);

    addSeason(series, args);

    fprintf(outFile, "Serialul %s are un sezon nou.\n", name);
}

void add_top(Flix* flix, Queue* args, FILE *outFile)
{
    int poz = (int)atol(QPop(args));
    Series *series = addSeries(4, args);

    LAddPoz(flix->top10, series, poz);
    int i = 0;
    List p = *flix->top10;
    for (; p->next != NULL && i < 9; p = p->next, i++);
    if(p->next != NULL) {
        LFreeExt(p->next, (void (*)(void *)) SFree);
        p->next = NULL;
    }

    fprintf(outFile, "Categoria top10: ");
    LPrint(*flix->top10, (void (*)(void *, int,  FILE *))printSerial, outFile);
    fprintf(outFile, ".\n");

}

void later(Flix* flix, List args, FILE *outFile)
{
    char* name = QPop(&args);
    Series *series = findSeries(flix, name);

    if(series->categoryId == 0 || series->categoryId == 1 || series->categoryId == 2) {
        QPush(&flix->later, LRemovePoz(&flix->categories[series->categoryId], pozitie(flix, series, series->categoryId)));
        series->categoryId = -1;
    }
    else {
        QPush(&flix->later, LRemovePoz(flix->top10, pozitieTop(flix, series)));
        series->categoryId = -1;
    }
    fprintf(outFile, "Serialul %s se afla in coada de asteptare pe pozitia %d.\n", name, pozitieLater(flix, series));
}

void watch(Flix* flix, List args, FILE *outFile)
{
    char* name = QPop(&args);
    int watchTime = (int)atol(QPop(&args));
    Series *series = findSeries(flix, name);

    if(findHistory(flix, name)) return;
    if(!findWatching(flix, series)) {
        if(series->categoryId == 0 || series->categoryId == 1 || series->categoryId == 2)
            LRemovePoz(&flix->categories[series->categoryId], pozitie(flix, series, series->categoryId));
        else if(series->categoryId == -1) LRemovePoz(&flix->later, pozitieLater(flix, series));
        else LRemovePoz(flix->top10, pozitieTop(flix, series));
        SPush(&flix->current, series);
        series->categoryId = -2;
    } else {
        void *aux = NULL;
        Stack Saux = NULL;
        Cell* p = flix->current;
        for(;(Series *)p->elem != series; p = flix->current){
            SPush(&Saux, SPop(&flix->current));
        }
        aux = SPop(&flix->current);
        while(Saux != NULL) {
            SPush(&flix->current, SPop(&Saux));
        }
        SPush(&flix->current,aux);
    }

    series->TotalTime -= watchTime;

    if (series->TotalTime <=0) {

        void *aux = NULL;
        Stack Saux = NULL;
        Cell* p = flix->current;
        for(;(Series *)p->elem != series; p = flix->current){
            SPush(&Saux, SPop(&flix->current));
        }
        aux = SPop(&flix->current);
        while(Saux != NULL) {
            SPush(&flix->current, SPop(&Saux));
        }
        SPush(&flix->history, aux);

        fprintf(outFile, "Serialul %s a fost vizionat integral.\n", name);
    }

}

void show(Flix* flix, Queue args, FILE *outFile)
{

    char *category = QPop(&args);
    if(!strcmp(category, "1") || !strcmp(category, "2") || !strcmp(category, "3")) {
        int categoryIndx = (int)atol(category);
        fprintf(outFile, "Categoria %d: ", categoryIndx);
        LPrint(flix->categories[categoryIndx - 1], (void (*)(void *, int, FILE *))printSerial, outFile);
        fprintf(outFile, ".\n");
    } else if(!strcmp(category, "later")) {
        fprintf(outFile, "Categoria %s: ", category);
        LPrint(flix->later, (void (*)(void *, int,  FILE *))printSerial, outFile);
        fprintf(outFile, ".\n");
    } else if(!strcmp(category, "top10")) {
        fprintf(outFile, "Categoria %s: ", category);
        LPrint(*flix->top10, (void (*)(void *, int,  FILE *))printSerial, outFile);
        fprintf(outFile, ".\n");
    } else if(!strcmp(category, "watching")) {
        fprintf(outFile, "Categoria %s: ", category);
        LPrint(flix->current, (void (*)(void *, int,  FILE *))printSerial, outFile);
        fprintf(outFile, ".\n");
    } else if(!strcmp(category, "history")) {
        fprintf(outFile, "Categoria %s: ", category);
        LPrint(flix->history, (void (*)(void *, int,  FILE *))printSerial, outFile);
        fprintf(outFile, ".\n");
    } else {
        fprintf(stderr, "Categorie necunoscuta: '%s'", category);
    }
}

Queue splitLine(char *line) {
    Queue result = NULL;

    char *token;
    token = strtok(line, " ");
    while (token != NULL) {
        QPush(&result, (void*)token);
        token = strtok(NULL, " ");
    }
    return result;
}

void executeCommandLine(Flix* flix, Queue *commandLine, FILE *outFile) {
    char* command = (char*)QPop(commandLine);
    if(!strcmp(command, "add")) add(flix, commandLine, outFile);
    else if(!strcmp(command, "add_sez")) add_sez(flix, commandLine, outFile);
    else if(!strcmp(command, "add_top")) add_top(flix, commandLine, outFile);
    else if(!strcmp(command, "later")) later(flix, *commandLine, outFile);
    else if(!strcmp(command, "watch")) watch(flix, *commandLine, outFile);
    else if(!strcmp(command, "show")) show(flix, *commandLine, outFile);
    else printf("???");
}

void test(Flix* flix, char *line, FILE *outFile) {
   List commandLine = splitLine(line);
   executeCommandLine(flix, &commandLine, outFile);
}

void Lines(char *filename, Flix* flix, FILE *outFile)
{

    FILE *f = fopen(filename, "rt");
    if (f == NULL)
        return;

    int index = 1;
    size_t len = 500;
    char* line = balloc(500);
    while(1) {
        if (getline(&line, &len, f) == -1)
            break;
        if(line[strlen(line)-1] == '\n') line[strlen(line)-1] = '\0';
        if(line[strlen(line)-1] == '\r') line[strlen(line)-1] = '\0';
        test(flix, line, outFile);
        index ++;
    }
    bfree(line);
    fclose(f);
}

int main(int argc, char *argv[]) {
    if(argc !=3) {
        printf("there should be 3 arguments");
        return 0;
    }

    char *input_filename = argv[1];
    char *output_filename = argv[2];

    Flix *flix = FAlloc();

    FILE *out = fopen(output_filename, "wt");

    Lines(input_filename, flix, out);

    fclose(out);

    FFree(flix);

    return 0;
}