/* Stefan Andreea-Bianca - 314CB */

#include <stdlib.h>

#include "series.h"

Series* SeAlloc(char* name, float rating, int category) {
    Series *series = balloc(sizeof(Series));
    series->name = balloc(100 * sizeof(char));
    series->seasons = NULL;

    strcpy(series->name, name);
    series->rating = rating;
    series->categoryId = category;
    return series;
}

void SFree(Series* series)
{
    bfree(series->name);
    LFreeExt(series->seasons, (void (*)(void *)) LFree);
    bfree(series);
}