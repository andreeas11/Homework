#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include <math.h>

typedef struct cell
{
    long long int elem;
    struct cell * next;
} Cell, *List;

List LAlloc(long long int elem) //alocare memorie celula + elem
{
    List list = (List)malloc(sizeof(Cell));
    if (list) {
        list->elem = elem;
        list->next = NULL;
    }
    return list;
}

List LAdd(List* list, long long int elem) //adaugare elementul la inceputul listei
{
   List aux = LAlloc(elem);
   if(aux == NULL)
       return NULL;
   if(*list != NULL)
       aux->next = *list;
   *list = aux;
   return aux;
}

List LSortedMerge (List l1, List l2) {
    List res = NULL;
    if(l1 == NULL) //daca l1 e gol => l2
        return l2;
    if (l2 == NULL) //daca l2 e gol => l1
        return l1;
    //compara primul elem din l1 cu primul din l2
    if(l1->elem == l2->elem) //daca sunt egale elem se va pastra doar unu
        res = LSortedMerge(l1->next, l2);
    if(l1->elem < l2->elem) { //daca cel din l1 <= cel din l2 => vom lua elem din l1 si apelam recursiv pt eleme urmator cu l1->next
        res = l1;
        res->next = LSortedMerge(l1->next, l2);
    }
    if(l1->elem > l2->elem) { //daca cel din l1 > cel din l2 => vom lua elem din l2 si apelam recursiv pt eleme urmator cu l2->next
        res = l2;
        res->next = LSortedMerge(l1, l2->next);
    }
    return res;
}

void LDivide(List src, List* firstHalf, List* secondHalf) {
    List twice = src->next; //va inainta mai rapid (cu cate 2)
    List once = src; //va inainta mai incet, una cate una

    while (twice != NULL) { //parcurge listele
        twice = twice->next;
        if (twice != NULL) {
            once = once->next;
            twice = twice->next;
        }
    }
//    imparte lista in 2
    *firstHalf = src;
    *secondHalf = once->next;
    once->next = NULL;
}

void LSort (List* head) { //mergesort
    List myHead = *head;
    List l1;
    List l2;

    if ((myHead == NULL) || (myHead->next == NULL))
        return;

    LDivide(myHead, &l1, &l2); //injumatateste lista
    LSort(&l1);
    LSort(&l2);

    *head = LSortedMerge(l1, l2); //impreuneaza listele sortate
}

List LConcatenate(List list1, List list2) //concateneaza 2 liste
{
    if(list1 == NULL)
        return list2;
    if(list2 == NULL)
        return list1;
    List p = list1;
    while (p->next != NULL) //parcurge list1
        p = p->next;
    p->next = list2; //leaga finalul list1 de inceputul list2
    list2 = NULL;
    return list1;
}

struct thread_mapper { //structura pt mapper
    int M;                            //nr de mapperi
    FILE* file;                       //fileul
    int nrOfFiles;                    //nr de fileuri care trebuie citite
    int counter;
    List readNr[5];//[3001];          //citim info din files in ele
    pthread_mutex_t lockM;            //mutexul
    pthread_barrier_t* barrierM;      //bariera
    List list[5][10];                  //lista de liste (pt fiecare thread de mapper vor fi nr de reduceri)
    long long int powMat[10][66000];  //matricea cu puteri in care verific daca e nr
};

struct thread_reducer {
    int R;
    pthread_barrier_t* barrierR;      //bariera
};

struct arguments {
    int thread_id;                  //thread id
    struct thread_mapper* mapper;
    struct thread_reducer* reducer;
};

int binarySearch(long long int v[], int st, int end, long long int val){ //cautarea binara
    int mid;
    if(end >= st){
        mid = (end + st) / 2;
        if(v[mid] == val)
            return mid + 1;
        else if(v[mid] < val)
            return binarySearch(v, mid + 1, end, val);
        else if(v[mid] > val)
            return binarySearch(v, st, mid - 1, val);
    }
    return -1;
}

int checkIfPow(long long int (*powMat)[66000], List l[5], long long int val, int rows, int columns) {
    for(int i = 2; i < rows; i++) {
        int rez = binarySearch(powMat[i], 1, columns, val); //se aplica cautarea binara pe fiecare linie
        if (rez != -1) {
            LAdd(&l[i], val); //daca gaseste putere adauga in lista
        }
    }
    return 0;
}

void* readFile(void* arg) {
    struct arguments myarguments = *(struct arguments *) arg;
    char buffer[1024];
    while (1) {
        pthread_mutex_lock(&myarguments.mapper->lockM);
        if (myarguments.mapper->counter >= myarguments.mapper->nrOfFiles) { //verificam daca counterul >= cu nr de fisiere care trebuie citite; daca da deblocam si iesim
            pthread_mutex_unlock(&myarguments.mapper->lockM);
            break;
        }
        fscanf(myarguments.mapper->file, "%s", buffer); //citim numele fisierelor

        myarguments.mapper->counter++;
        pthread_mutex_unlock(&myarguments.mapper->lockM);

        //citim din fileuri nr si le adaugam la lista numerelor citite din fisiere (fiecare nr va fi pus in lista aferenta threadului care l a citit)
        FILE* file = fopen(buffer, "r");
        int nrOfNr, nr;
        fscanf(file, "%d", &nrOfNr);
        for (int i = 0; i < nrOfNr; i++) {
            fscanf(file, "%d", &nr);
            LAdd(&myarguments.mapper->readNr[myarguments.thread_id], nr);
        }
        fclose(file);

    }

    //verificam ce nr din listele de nr citite sunt puteri perfecte si se adauga cu checkIfPow in listele aferente
    int i = myarguments.thread_id;
    for (; myarguments.mapper->readNr[i] != NULL; myarguments.mapper->readNr[i] = myarguments.mapper->readNr[i]->next) {
        checkIfPow(myarguments.mapper->powMat,                          //matricea de puteri
                   myarguments.mapper->list[i],                              //lista pt mapperul aferent
                   myarguments.mapper->readNr[i]->elem,                     //elm din lista cu nr citite din fisiere
                   myarguments.reducer->R + 2, 66000);
    }

    pthread_barrier_wait(myarguments.mapper->barrierM); //pt a ne asigura ca nu vor lucra simultan mapperii si reducerii
    pthread_exit(NULL);
}

void *reduceLists(void *arg) {
    struct arguments myarguments = *(struct arguments *) arg;
    pthread_barrier_wait(myarguments.reducer->barrierR); //pt a ne asigura ca nu vor lucra simultan mapperii si reducerii

    //concateneaza listele care au nr la aceleasi puteri => vom avea o singura lista pt fiecare putere
    int i = myarguments.thread_id + 2;
    List init = myarguments.mapper->list[0][i];
    for (int m = 1; m < myarguments.mapper->M; m++)
        LConcatenate(init, myarguments.mapper->list[m][i]);

    //sortam lista si scoatem duplicatele
    LSort(&init);

    //numaram nr de elemente din lista
    int cnt = 0;
    while (init != NULL) {
        cnt++;
        init = init->next;
    }

    //punem in fisierul aferent nr de elem
    char filename[100];
    sprintf(filename, "out%d.txt", i);

    FILE *file = fopen(filename, "w");
    fprintf(file, "%d", cnt);
    fclose(file);

    pthread_exit(NULL);
}

int main(int argc, char* argv[]) {
    if(argc < 4) {
        printf("Numar insuficient de parametri: ./tema1 M R input.txt\n");
        exit(1);
    }

    long long int MAX_LONG = 9223372036854775807;
    struct thread_mapper mapper;
    struct thread_reducer reducer;

    mapper.M = atoi(argv[1]);
    reducer.R = atoi(argv[2]);

    //matricea de puteri
    for(int i = 2; i < reducer.R + 2; i++) {
        for (int j = 1; j < 54780; j++)
            mapper.powMat[i][j] = pow(j, i);
    }

        for(int j = 6208; j < 54780; j++)
            mapper.powMat[5][j] = MAX_LONG;
//    printf("%lld\n", mapper.powMat[5][3]); //3 la 5

    pthread_t tidM[mapper.M];
    pthread_t tidR[reducer.R];

    //initializarea elem de sincronizare
    pthread_mutex_init(&(mapper.lockM), NULL);
    pthread_barrier_t barrier;
    pthread_barrier_init(&barrier, NULL, mapper.M + reducer.R);
    mapper.barrierM = &barrier;
    reducer.barrierR = &barrier;

    mapper.counter = 0;
    mapper.file = fopen(argv[3], "r");

    fscanf(mapper.file, "%d\n", &mapper.nrOfFiles);

    struct arguments argsM[mapper.M];
    struct arguments argsR[reducer.R];

    //creare mapperi+ reducers
    for(int i = 0; i < mapper.M; i++) {
        argsM[i].thread_id = i;
        argsM[i].mapper = &mapper;
        argsM[i].reducer = &reducer;
        pthread_create(&tidM[i], NULL, readFile, &argsM[i]);
    }

    for (int i = 0; i < reducer.R; i++) {
        argsR[i].thread_id = i;
        argsR[i].mapper = &mapper;
        argsR[i].reducer = &reducer;
        pthread_create(&tidR[i], NULL, reduceLists, &argsR[i]);
    }

    //join mapperi + reducers
    for (int i = 0; i < mapper.M; i++) {
        pthread_join(tidM[i], NULL);
    }

    for (int i = 0; i < reducer.R; i++) {
        pthread_join(tidR[i], NULL);
    }

    fclose(mapper.file);
    //stergere elem de sincronizare
    pthread_mutex_destroy(&(mapper.lockM));
    pthread_barrier_destroy(&barrier);

    return 0;
}