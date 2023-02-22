/* Stefan Andreea-Bianca 314CBa*/

#include "Hash.h"

Pair* PInit(char *key, void *value)
{
    Pair *p = (Pair*)malloc(sizeof(Pair));
    p->key = key;
    p->value = value;
    return p;
}

int PairPickByKey(Pair *pair, char *key) {
    return !strcmp(pair->key, key);
}

int PairCompare(Pair *pair1, Pair *pair2) {
    return strcmp(pair1->key, pair2->key);
}

unsigned long StrHash(char *key)
{
    unsigned long suma;
    for (suma = 0; *key != '\0'; key++)
        suma += *key;
    return suma;
}

Hashtable* HInit(long bucketCount, unsigned long (*keyHashFn)(char *))
{
    Hashtable *h = (Hashtable*)malloc(sizeof(Hashtable));
    if (!h) {
        printf("eroare alocare hash\n");
        return NULL;
    }

    h->buckets = (List*)calloc(bucketCount*sizeof(List), 1);
    if (!h->buckets) {
        printf("eroare alocare vector de pointeri TLG in hash\n");
        free(h);
        return NULL;
    }

    h->bucketCount = bucketCount;
    h->keyHashFn = keyHashFn;

    return h;
}

void HFree(Hashtable* h) {
    LForEach(*h->buckets, free);
    LFree(*h->buckets);
    free(h);
}


int HFind(Hashtable *h, char *key)
{
    unsigned long indx = h->keyHashFn(key) % h->bucketCount;

    List bucket = h->buckets[indx];
    if (LFind(bucket, (int (*)(void *, void *))PairPickByKey, key)) return 1;
    return 0;
}

int HPut(Hashtable *h, char *key, void *value)
{
    unsigned long indx = h->keyHashFn(key) % h->bucketCount;

    List *bucket = &h->buckets[indx];
    Pair* pair = (Pair*)PInit(key, value);
    int done = LInsert(bucket, pair, (int (*)(void *, void *))PairCompare);
    if (!done) free(pair);
    return done;
}

void *HGet(Hashtable *h, char *key)
{
    unsigned long indx = h->keyHashFn(key) % h->bucketCount;

    List bucket = h->buckets[indx];
    Pair* pair = (Pair*)LFind(bucket, (int (*)(void *, void *))PairPickByKey, key);
    if (pair) {
        return pair->value;
    } else return NULL;
}

void PairPrint(Pair *p, FILE *file)
{
    fprintf(file, "%s ", (char*)p->value);
}

void HPrint_Bucket(List bucket, FILE *file)
{
    if (bucket == NULL) fprintf(file, "VIDA");
    else
        LForEachExt(bucket, (void (*)(void *, void *)) PairPrint, file);
    fprintf(file, "\n");
}

void HPrint(Hashtable *h, FILE *file)
{
    long indx;
    for(indx = 0; indx < h->bucketCount; indx++) {
        List bucket = h->buckets[indx];
        if (bucket) {
            fprintf(file, "%ld: ", indx);
            HPrint_Bucket(h->buckets[indx], file);
        }
    }
}

void HRemove(Hashtable *h, char *key)
{
    unsigned long indx = h->keyHashFn(key) % h->bucketCount;

    List *bucket = &h->buckets[indx];
    Pair *pair = (Pair*)LFind(*bucket, (int (*)(void *, void *))PairPickByKey, key);
    if (pair != NULL) {
        LRemove(bucket, pair);
        free(pair);
    }
}