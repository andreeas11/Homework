/* Stefan Andreea-Bianca - 314CB*/

#include "list.h"

#ifndef _STIVA_
#define _STIVA_

typedef List Stack;

Stack SAlloc(void *elem);
void* SPush(Stack* stack, void* elem);
void* SPop(Stack* stack);

#endif