/* Stefan Andreea-Bianca 314CBa*/

#include "Alocare.h"

void *allocated[100000];
int allocated_count = 0;

void *balloc(size_t size) {
    void *result = calloc(size, 1);
    return result;
}

void bfree(void *block) {
    if (block != NULL)
        free(block);
    return;
    if (block != NULL) {
        int i;
        int done = 0;
        for (i = 0; i < allocated_count; i++) {
            if (block == allocated[i]) {
                allocated[i] = NULL;
                free(block);
                done = 1;
                break;
            }
        }
        if (!done)
            fprintf(stderr, "Freeing unknown block: %p\n", block);
    }  else
        fprintf(stderr, "NULL block\n");
}
