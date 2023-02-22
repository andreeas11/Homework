/* Stefan Andreea-Bianca 314CBa*/

#include "List.h"

void LForEach(List list, void (*func)(void *elem)) {
    Cell *cell = list;
    if (cell) do {
            func(cell->elem);
            cell = cell->next;
        } while (cell != list);
}

void LForEachExt(List list, void (*func)(void *elem, void *data), void *data) {
    Cell *cell = list;
    if (cell) do {
        func(cell->elem, data);
        cell = cell->next;
    } while (cell != list);
}

void LFree(List list) {
    LForEach(list, free);
    free(list);
}

void *LFind(List list, int (*elPick)(void *, void *), void *data) {
    Cell *cell = list;
    if (cell) do {
        if (elPick(cell->elem, data)) {
            return cell->elem;
        }
        cell = cell->next;
    } while (cell != list);
    return NULL;
}

int LInsert(List *list, void *elem, int (*elCompare)(void *, void *))
{
    Cell *first = *list;
    if (first == NULL) {
        Cell *cell = (Cell*)malloc(sizeof(Cell));
        cell->elem = elem;
        cell->prev = cell;
        cell->next = cell;

        *list = cell;
    } else {
        Cell *current = first;
        int compare;
        do {
            compare = elCompare(current->elem, elem);
            if (compare == 0) return 0;
            if (compare > 0) break;
            current = current->next;
        } while (current != first);

        Cell *cell = (Cell*)malloc(sizeof(Cell));
        cell->elem = elem;
        cell->next = current;
        cell->prev = current->prev;

        current->prev->next = cell;
        current->prev = cell;

        if (current == first && compare > 0) *list = cell;
    }

    return 1;
}

int LRemove(List *list, void *elem)
{
    Cell *cell = *list;
    if (cell != NULL) {
        do {
            if (elem == cell->elem) {
                if (cell->next == cell) {
                    *list = NULL;
                } else {
                    cell->prev->next = cell->next;
                    cell->next->prev = cell->prev;
                    if (cell == *list) *list = cell->next;
                }
                free(cell);
                return 1;
            }
            cell = cell->next;
        } while (cell != *list);
    }
    return 0;
}