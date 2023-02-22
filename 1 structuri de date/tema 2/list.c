/* Stefan Andreea-Bianca - 314CB*/

#include "list.h"

List LAlloc(void* elem)
{
    List list = (List)balloc(sizeof(Cell));
    if (list) {
        list->elem = elem;
        list->next = NULL;
    }
    return list;
}

void LPrint(List list, void (*elemPrint)(void *elem, int index, FILE *outFile), FILE *outFile)
{
    fprintf(outFile, "[");
    int index = 0;
    for (; list != NULL; list = list->next) {
        elemPrint(list->elem, index++, outFile);
    }
    fprintf(outFile, "]");
}

void LFree(List list) {
    while(list) {
        Cell* aux = list;
        list = aux->next;
        bfree(aux);
    }
}

void LFreeExt(List list, void (*elemfree)(void *elem))
{
    while(list) {
        Cell* aux = list;
        list = aux->next;
        elemfree(aux->elem);
        bfree(aux);
    }
}

Cell* LAddFirst(List* list, void* elem)
{
    Cell* aux = LAlloc(elem);
    if(aux == NULL) return NULL;
    if(*list != NULL)
        aux->next = *list;
    *list = aux;
    return aux;
}

Cell* LAddLast(List* list, void* elem)
{
    List p;
    List aux = LAlloc(elem);
    if(*list == NULL) {
        *list = aux;
        return aux;
    }
    p = *list;
    for(; p->next != NULL; p = p->next);
    p->next = aux;
    return aux;
}

Cell* LAddSorted(List* list, void* elem, int (*elemComp)(void *elem, void *ele)) {
    Cell *aux = LAlloc(elem);
    Cell *prev = NULL;
    Cell *curr = *list;
    if(*list == NULL) *list = aux;
    for(; curr != NULL; prev = curr, curr = curr->next) {
        if(elemComp(curr->elem, elem) >= 0) {
            aux->next = curr;
            if(prev == NULL)
                *list = aux;
            else prev->next = aux;
            return aux;
        }
        if(curr->next == NULL) {
            curr->next = aux;
            return aux;
        }

    }
    return aux;
}

Cell* LAddPoz(List* list, void* elem, int poz)
{
    Cell *curr = *list, *prev = NULL;
    List aux = LAlloc(elem);
    while (poz-- >= 0) {
        if (poz == 0) {
            if (curr != NULL) aux->next = curr;
            if (prev != NULL) {
                prev->next = aux;
            } else {
                *list = aux;
            }
            return aux;
        }
        prev = curr;
        curr = curr->next;
    }
    return NULL;
}

void* LRemoveFirst(List* list)
{
    List p = *list;
    *list = p->next;
    void* elem = p->elem;
    bfree(p);
    return elem;
}

void* LRemoveLast(List* list)
{
    List p = *list;
    void *elem;
    if(p->next == NULL) {
        *list = NULL;
        elem = p->elem;
        bfree(p);
    } else {
        for (; p->next->next != NULL; p = p->next);
        elem = p->next->elem;
        p->next = NULL;
        bfree(p->next);
    }
    return elem;
}

void* LRemovePoz(List* list, int index)
{
    Cell *curr = *list, *prev = NULL;
    void *elem = NULL;
    while (index-- >= 0) {
        if (index == 0) {
            if (prev != NULL) prev->next = curr->next;
            else {
                *list = curr->next;
            }
            elem = curr->elem;
            bfree(curr);
            return elem;
        }
        prev = curr;
        curr = curr->next;
    }
    return elem;
}