/* Stefan Andreea-Bianca 314CBa*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdarg.h>

#ifndef TEMA_1_LIST_H
#define TEMA_1_LIST_H

#endif //TEMA_1_LIST_H


typedef struct SCell
{
    void *elem;
    struct SCell *prev, *next;
} Cell, *List;

typedef struct
{
    char *key;
    void *value;
} Pair;

void LForEach(List list, void (*func)(void *elem));
void LForEachExt(List list, void (*func)(void *elem, void *data), void *data);
void LFree(List list);
void *LFind(List list, int (*elPick)(void *elem, void *data), void *data);
int LInsert(List *list, void *elem, int (*elCompare)(void *, void *));
int LRemove(List *list, void *elem);
