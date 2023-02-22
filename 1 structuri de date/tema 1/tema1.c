/* Stefan Andreea-Bianca 314CBa*/

#include "Hash.h"

void executaOperatii(char *numeFisier, long bucket_count, FILE *outFisier) {
    FILE *f;
    size_t len = 0;
    Hashtable *h = HInit(bucket_count, StrHash);


    f = fopen(numeFisier, "rt");
    if (f == NULL)
        return;

    char *lines[1000];
    int line_count = 0;
    while (1) {
        char * line = malloc(100);
        lines[line_count++] = line;
        if (getline(&line, &len, f) == -1) break;

        char *command = strtok(line, " ");
        char *key = strtok(NULL, " ");
        char *value = strtok(NULL, " ");

        if (command[strlen(command) - 1] == '\n')
            command[strlen(command) - 1] = '\0';
        else if (key[strlen(key) - 1] == '\n')
            key[strlen(key) - 1] = '\0';
        else if (value[strlen(value) - 1] == '\n')
            value[strlen(value) - 1] = '\0';


        if (!strcmp(command, "put")) {
            HPut(h, key, (void*)value);
        } else
        if (!strcmp(command, "get")) {
            void * value = HGet(h, key);
            if(value)
                fprintf(outFisier,"%s\n", (char*)value);
            else fprintf(outFisier,"NULL\n");
        } else
        if (!strcmp(command, "remove")) {
            HRemove(h, key);
        } else
        if (!strcmp(command, "find")) {
            if(HFind(h, key))
                fprintf(outFisier, "True\n");
            else fprintf(outFisier, "False\n");
        } else
        if (!strcmp(command, "print")) {
            HPrint(h, outFisier);
        } else
        if (!strcmp(command, "print_bucket")) {
            int index = strtol(key, NULL, 10);
            if (index < bucket_count)
                HPrint_Bucket(h->buckets[index], outFisier);
        }
    }
    int i;
    for (i = 0; i < line_count; i++) free(lines[i]);
    HFree(h);
    fclose(f);
}

int main(int argc, char *argv[]) {
    if(argc !=4) {
        printf("there should be 3 arguments");
        return 0;
    }

    char *M = argv[1];
    char *input_filename = argv[2];
    char *output_filename = argv[3];

    long bucket_count = strtol(M, NULL, 10);

      FILE *out = fopen(output_filename, "wt");

    executaOperatii(input_filename, bucket_count, out);

    fclose(out);
    return 0;
}