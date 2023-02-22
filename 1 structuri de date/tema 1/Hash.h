/* Stefan Andreea-Bianca 314CBa*/

#include "List.h"

#ifndef TEMA_1_HASH_H
#define TEMA_1_HASH_H

#endif //TEMA_1_HASH_H

typedef struct
{
    unsigned long (*keyHashFn)(char *);

    long bucketCount;
    List *buckets;
} Hashtable;


Pair* PInit(char *key, void *value);
int PairPickByKey(Pair *pair, char *key);
unsigned long StrHash(char *key);
Hashtable* HInit(long bucketCount, unsigned long (*keyHashFn)(char *));
void HFree(Hashtable* h);
int HFind(Hashtable *h, char *key);
int HPut(Hashtable *h, char *key, void *value);
void *HGet(Hashtable *h, char *key);
void PairPrint(Pair *p, FILE *file);
void HPrint_Bucket(List bucket, FILE *file);
void HPrint(Hashtable *h, FILE *file);
void HRemove(Hashtable *h, char *key);
