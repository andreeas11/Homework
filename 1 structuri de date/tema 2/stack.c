/* Stefan Andreea-Bianca - 314CB*/

#include "stack.h"

Stack SAlloc(void *elem)
{
    return LAlloc(elem);
}

void* SPush(Stack *stack, void *elem)
{
    return LAddFirst(stack, elem);
}

void* SPop(Stack* stack)
{
    return LRemoveFirst(stack);
}