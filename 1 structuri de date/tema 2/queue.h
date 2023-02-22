/* Stefan Andreea-Bianca - 314CB*/

#include "list.h"

#ifndef _QUEUE_
#define _QUEUE_

typedef List Queue;

Queue QAlloc(void *elem);
Cell* QPush(Queue* queue, void* elem);
void* QPop(Queue* queue);

#endif