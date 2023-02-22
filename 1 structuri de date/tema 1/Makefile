CC = gcc
CFLAGS=-Wall -Wextra -g

build: clean tema1

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

tema1.exe: tema1.o Hash.o List.o
	$(CC) $(CFLAGS) $^ -o $@

tema1: tema1.o Hash.o List.o
	$(CC) $(CFLAGS) $^ -o $@

.PHONY: clean checkMem

clean:
	rm -f *.o tema1

checkMem:
	valgrind --leak-check=yes --track-origins=yes ./tema1