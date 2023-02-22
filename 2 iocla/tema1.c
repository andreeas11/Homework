#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_INPUT_LINE_SIZE 300

struct Dir;
struct File;

typedef struct Dir{
	char *name;
	struct Dir* parent;
	struct File* head_children_files;
	struct Dir* head_children_dirs;
	struct Dir* next;
} Dir;

typedef struct File {
	char *name;
	struct Dir* parent;
	struct File* next;
} File;

File* FAlloc(char* name) {
    File *file = (File*)malloc(sizeof(File));
    file->name = malloc(strlen(name) + 1);
    strcpy(file->name, name);
    return file;
}

void FFree(File* file) {
    free(file->name);
    free(file);
}

Dir* DAlloc(char* name) {
    Dir *dir = (Dir*)malloc(sizeof(Dir));
    dir->name = malloc(strlen(name) + 1);
    strcpy(dir->name, name);
    return dir;
}

void DFree(Dir* dir) {
    free(dir->name);
    free(dir);
}

void touch (Dir* parent, char* name) {
    File** p = &parent->head_children_files;
    File* f = *p;
    for(; f != NULL; p = &f->next, f = *p) {
        if (!strcmp(f->name, name)) {
            printf("File already exists\n");
            return;
        }
    }
    File *file = FAlloc(name);
    file->parent = parent;
    File **curr = &parent->head_children_files;
    while ((*curr) != NULL) curr = &(*curr)->next;
    *curr = file;
 }


void mkdir (Dir* parent, char* name) {
    Dir** p = &parent->head_children_dirs;
    Dir* d = *p;
    for(; d != NULL; p = &d->next, d = *p) {
        if (!strcmp(d->name, name)) {
            printf("Directory already exists\n");
            return;
        }
    }
    Dir *dir = DAlloc(name);
    dir->parent = parent;
    Dir **curr = &parent->head_children_dirs;
    while ((*curr) != NULL) curr = &(*curr)->next;
    *curr = dir;
}


void ls (Dir* parent) {
    for(Dir* d = parent->head_children_dirs; d != NULL; d = d->next) {
        printf("%s\n", d->name);
    }
    for(File* f = parent->head_children_files; f != NULL; f = f->next) {
        printf("%s\n", f->name);
    }
}


void rm (Dir* parent, char* name) {
    File** p = &parent->head_children_files;
    File* f = *p;
    for(; f != NULL; p = &f->next, f = *p) {
        if(!strcmp(f->name, name)) {
            File* next = f->next;
            FFree(f);
            *p = next;
            return;
        }
    }
    printf("Could not find the file\n");
}

void freeDir(Dir* dir) {
    for (Dir* d = dir->head_children_dirs; d != NULL; d = d->next) {
        freeDir(d);
    }
    for (File* f = dir->head_children_files; f != NULL; f = f->next) {
        FFree(f);
    }
    DFree(dir);
}

void rmdir(Dir* parent, char* name) {
    Dir** p = &parent->head_children_dirs;
    Dir* d = *p;
    for(; d != NULL; p = &d->next, d = *p) {
        if(!strcmp(d->name, name)) {
            Dir* next = d->next;
            freeDir(d);
            *p = next;
            return;
        }
    }
    printf("Could not find the dir\n");
}

void cd(Dir** target, char *name) {
    if (!strcmp(name, "..")) {
        if((*target)->parent != NULL) *target = (*target)->parent;
        return;
    } else {
        Dir *d = (*target)->head_children_dirs;
        for (; d != NULL; d = d->next) {
            if (!strcmp(d->name, name)) {
                *target = d;
                return;
            } else if (d->next == NULL && strcmp(d->name, name) != 0) {
                printf("No directories found!\n");
            }
        }
    }
}

char* getPath(Dir* target, char* buf) {
    if (target == NULL) return buf;
    buf = getPath(target->parent, buf);
    buf += sprintf(buf, "/%s", target->name);
    return buf;
}

char *pwd (Dir* target) {
    char* path = malloc(1000);
    getPath(target, path);
    printf("%s\n", path);
    return path;
}

void stop (Dir* target) {
    freeDir(target);
}

void tree (Dir* target, int level) {
    for(Dir* d = target->head_children_dirs; d != NULL; d = d->next) {
        for(int i = 0; i < level; i++) printf("    ");
        printf("%s\n", d->name);
        tree(d, level+1);
    }
    for(File* f = target->head_children_files; f != NULL; f = f->next) {
        for (int j = 0; j < level; j++) printf("    ");
        printf("%s\n", f->name);
    }
}

void mv(Dir* parent, char *oldname, char *newname) {
    File* f = parent->head_children_files;
    int existsnew = 0, okfile = 0;

    Dir* d = parent->head_children_dirs;
    Dir* ant;
    Dir* found = (Dir*)malloc(sizeof(Dir));
    int okdir = 0, k = 0;

    for(; f != NULL; f = f->next) {
        if (!strcmp(f->name, newname)) {
            existsnew = 1;
        }
        if (!strcmp(f->name, oldname)) {
            okfile = 1;
        }
    }
    for(; d != NULL; d = d->next) {
        if (!strcmp(d->name, newname)) {
            existsnew = 1;
        }
        if (!strcmp(d->name, oldname)) {
            okdir = 1;
        }
    }
    if(existsnew) {
        printf("File/Director already exists\n");
        return;
    }
    if (!okdir && !okfile) {
        printf("File/Director not found\n");
        return;
    }
    for(f = parent->head_children_files; f != NULL; f = f->next) {
        if(!strcmp(f->name, oldname)) {
            rm(parent, oldname);
            touch(parent, newname);
            return;
        }
    }
    for (ant = NULL, d = parent->head_children_dirs; d != NULL; ant = d, d = d->next) {
        if (!strcmp(d->name, oldname)) {
            found = d;
            found->name = newname;
            if (ant != NULL) ant->next = d->next;
            else k = 1;
        }
    }
    if (k) {
        parent->head_children_dirs = parent->head_children_dirs->next;
        if (parent->head_children_dirs == NULL) {
            found->next = NULL;
            parent->head_children_dirs = found;
            freeDir(found);
            return;
        }
        for (d = parent->head_children_dirs; d->next != NULL; d = d->next);
        d->next = found;
        found->next = NULL;
        freeDir(found);
        return;
    }
    for (d = parent->head_children_dirs; d->next != NULL; d = d->next);
    d->next = found;
    found->next = NULL;
}

int main () {
    Dir *home = DAlloc("home");

    char* tokens[3];
    char* command = malloc(5000);
    do {
        fgets(command, 500, stdin);

        tokens[0] = strtok(command, " \n");
        for(int i = 1; tokens[i-1] != NULL; i++)
            tokens[i] = strtok(NULL, " \n");

        if(!strcmp(tokens[0], "touch")) touch(home, tokens[1]);
        else if(!strcmp(tokens[0], "mkdir")) mkdir(home, tokens[1]);
        else if(!strcmp(tokens[0], "ls")) ls(home);
        else if(!strcmp(tokens[0], "rm")) rm(home, tokens[1]);
        else if(!strcmp(tokens[0], "rmdir")) rmdir(home, tokens[1]);
        else if(!strcmp(tokens[0], "cd")) cd(&home, tokens[1]);
        else if(!strcmp(tokens[0], "tree")) tree(home, 0);
        else if(!strcmp(tokens[0], "pwd")) { pwd(home); }
        else if(!strcmp(tokens[0], "stop")) stop(home);
        else if(!strcmp(tokens[0], "mv")) mv(home, tokens[1], tokens[2]);

    } while (strcmp(tokens[0], "stop") != 0);
    free(command);
    return 0;
}
