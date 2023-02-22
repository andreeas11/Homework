/* Stefan Andreea-Bianca - 314CB */

#include "list.h"

#ifndef _SERIES_
#define _SERIES_

#include <stdint.h>

#include "queue.h"

typedef intptr_t Episode;

typedef struct {
    char* name;
    float rating;
    int categoryId;
    Queue seasons;
    int TotalTime;
} Series;

Series* SeAlloc(char* name, float rating, int category);
void SFree(Series* series);

#endif