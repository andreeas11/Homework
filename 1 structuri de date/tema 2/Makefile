CC = gcc
CFLAGS=-Wall -Wextra -g

build: clean tema2

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

tema2.exe: tema2.o queue.o stack.o series.o list.o Alocare.o
	$(CC) $(CFLAGS) $^ -o $@

tema2: tema2.o queue.o stack.o series.o list.o Alocare.o
	$(CC) $(CFLAGS) $^ -o $@

.PHONY: clean checkMem

clean:
	rm -f *.o tema2

checkMem: clean tema2
	valgrind --leak-check=yes --track-origins=yes ./tema2