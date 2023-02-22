/* Stefan Andreea-Bianca - 314CB*/

#include "queue.h"

Queue QAlloc(void *elem)
{
    return LAlloc(elem);
}

Cell* QPush(Queue *queue, void *elem)
{
    return LAddLast(queue, elem);
}

void* QPop(Queue* queue)
{
    return LRemoveFirst(queue);
}