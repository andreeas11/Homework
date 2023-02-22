/* Stefan Andreea-Bianca - 314CB */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "Alocare.h"

#ifndef _LIST_
#define _LIST_

typedef struct cell
{
    void *elem;
    struct cell * next;
} Cell, *List;

List LAlloc(void*);
int LLength(List);
void LFree(List list);
void LFreeExt(List list, void (*elemfree)(void *elem));
void LPrint(List, void (*elemPrint)(void *elem, int index, FILE *outFile), FILE *outFile);
Cell* LAddFirst(List*, void*);
Cell* LAddLast(List*, void*);
Cell* LAddSorted(List* list, void* elem, int (*elemComp)(void *elem, void* ele));
Cell* LAddPoz(List* list, void* elem, int poz);
void* LRemoveFirst(List*);
void* LRemoveLast(List*);
void* LRemovePoz(List* list, int index);

#endif