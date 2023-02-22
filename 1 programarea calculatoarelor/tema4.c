#include "utils.h"

void* (* Abilities[4])(void* x) = {RotateMatrix, DecodeString, InvertArray, KillPlayer};

char *fromEnumtoString(PlayerRoles playerRole)
{
	char *str = calloc(MAX_LEN_STR_ATR, sizeof(char));
	switch(playerRole)
	{
		case Rotator:
			str = strcpy(str, "Rotator");
			break;
		case Decoder:
			str = strcpy(str, "Decoder");
			break;
		case Invertor:
			str = strcpy(str, "Invertor");
			break;
		case Impostor:
			str = strcpy(str, "Impostor");
			break;
	}
	return str;
}

void sprint_matrix(char *str, char ***matrix, int n) {
	for(int i = 0; i < n; i++) {
		for(int j = 0; j < n; j++) {
			str += sprintf(str, "%s", matrix[i][j]);
			if (j < n - 1) str += sprintf(str, " ");
		}
		if (i < n - 1) str += sprintf(str, "\n");
	}
 	*str = 0;
}

void PrintMatrix(char ***matrix, int n) {
	char *buf = malloc(n * n * 10);
	sprint_matrix(buf, matrix, n);
	printf("%s\n", buf);
	free(buf);
}


// Task 1
void *RotateMatrix(void *input)
{
	// TODO
    int n = *(int*)input;
	char ***matrix = malloc(n * sizeof(char **));
	for(int i = 0; i < n; i++) {
		matrix[i] = malloc(n * sizeof(char *));
		for(int j = 0; j < n; j++) {
			matrix[i][j] = malloc(10);
			int count = sprintf(matrix[i][j], "%d%d", i + 1, j + 1);
			matrix[i][j][count] = 0;
		}
	}
	for(int i = 0; i < n / 2; i++) {
		for(int j = i; j < n - 1 - i; j++) {
			char *temp = matrix[i][j];
			matrix[i][j] = matrix[n - j - 1][i];
			matrix[n - j - 1][i] = matrix[n - i - 1][n - j - 1];
			matrix[n - i - 1][n - j - 1] = matrix[j][n - i - 1];
			matrix[j][n - i - 1] = temp;
		}
	}
	char *result = malloc(1000000);
	sprint_matrix(result, matrix, n);
	for (int i = 0; i < n; i++) {
	    for (int j = 0; j < n; j++) {
	        free(matrix[i][j]);
	    }
    	free(matrix[i]);
	}
  	free(matrix); 
	return result;
}


// Task 2
void *DecodeString(void *input)
{
	// TODO
	
	int sum = 0;
	char *str = (char*)input;
	char c;
	do {
		int n = 0;
		while (1) {
			c = *(str + n);
			if (c == 0 || c == '_') {
                break;
            }
			n ++;
		}
		*(str + n) = 0;
        sum += atoi(str);
		str += (n + 1);
	} while (c);

	char *sumStr = calloc(1000, 1);
	sprintf(sumStr, "%d", sum);
	return sumStr;
}

//Task 3
void *InvertArray(void *input)
{
	// TODO
    char *result, *cursor;
    int *ints = (int*)input;
    int n = *ints;
    cursor = result = malloc(10000 * sizeof(char));
    if(n % 2 == 0) {
        for(int i = 1; i <= n; i += 2) {
            cursor += sprintf(cursor, "%d ", ints[i + 1]);
            cursor += sprintf(cursor, "%d%s", ints[i], i == n ? "": " ");
        }
    } else {
        for (int i = n; i >= 1; i--) {
            cursor += sprintf(cursor, "%d%s", ints[i], i == 1 ? "" : " ");
        }
    }
    *cursor = 0;
    return result;
}

//Task 4
Player *allocPlayer()
{
	// TODO
	Player *my_Player = malloc(sizeof(Player));
	my_Player->name = malloc(MAX_LEN_STR_ATR * sizeof(char));
	my_Player->color = malloc(MAX_LEN_STR_ATR * sizeof(char));
	my_Player->hat = malloc(MAX_LEN_STR_ATR * sizeof(char));
	my_Player->alive = 1;
    return my_Player;
}


//Task 4
Game *allocGame()
{
	// TODO
	Game *my_Game = malloc(sizeof(Game));
	my_Game->name = malloc(MAX_LEN_STR_ATR * sizeof(char));
	return my_Game;
}

PlayerRoles fromStringToEnum(char *role) {
    if(strcmp(role, "Rotator") == 0) {
        return Rotator;
    }
    if(strcmp(role, "Decoder") == 0) {
        return Decoder;
    }
    if(strcmp(role, "Invertor") == 0) {
        return Invertor;
    }
    if(strcmp(role, "Impostor") == 0) {
        return Impostor;
    }
    return -1;
}

//Task 5
Player *ReadPlayer(FILE *inputFile)
{
    Player *player = allocPlayer();

    player->indexOfLocation = 0;

    fscanf(inputFile, "%s\n%s\n%s\n%d\n", player->name, player->color, player->hat, &player->numberOfLocations);
    player->locations = malloc(player->numberOfLocations * sizeof(Location));
    for(int i = 0; i < player->numberOfLocations; i++) {
        fscanf(inputFile, i == 0 ? "(%d,%d)" : " (%d,%d)", &player->locations[i].x, &player->locations[i].y);
    }
    fscanf(inputFile, "\n");
    char * role = malloc(MAX_LEN_STR_ATR * sizeof(char));

    fscanf(inputFile, "%s\n", role);

    player->playerRole = fromStringToEnum(role);
    player->ability = Abilities[player->playerRole];
    free(role);
	return player;
}



// Task 5
Game *ReadGame(FILE *inputFile)
{
	// TODO
    Game *my_Game = allocGame();

    fscanf(inputFile, "%s\n%d\n%d\n", my_Game->name, &my_Game->killRange, &my_Game->numberOfCrewmates);
    //fprintf(stderr, "%s\n%d\n%d\n", my_Game->name, my_Game->killRange, my_Game->numberOfCrewmates);
    my_Game->crewmates = malloc(my_Game->numberOfCrewmates * sizeof(Player *));

    for(int i = 0; i < my_Game->numberOfCrewmates; i++) {
        my_Game->crewmates[i] = ReadPlayer(inputFile);
    }

    my_Game->impostor = ReadPlayer(inputFile);

    return my_Game;
}

// Task 6
void WritePlayer(Player *player, FILE *outputFile)
{
	// TODO
    fprintf(outputFile, "Player %s ", player->name);
    fprintf(outputFile, "with color %s, ", player->color);
    fprintf(outputFile, "hat %s and ", player->hat);
    PlayerRoles role = player->playerRole;
    char *str = fromEnumtoString(role);
    fprintf(outputFile, "role %s has entered the game.\n", str);
    free(str);
    fprintf(outputFile, "Player's locations: ");
    for(int i = 0; i < player->numberOfLocations; i++) {
        fprintf(outputFile, i == 0 ? "(%d,%d)" : " (%d,%d)", player->locations[i].x, player->locations[i].y);
    }
    fprintf(outputFile, "\n");
}

// Task 6
void WriteGame(Game *game, FILE *outputFile) {
    // TODO
    fprintf(outputFile, "Game %s has just started!\n", game->name);
    fprintf(outputFile, "\tGame options:\nKill Range: %d\n", game->killRange);
    fprintf(outputFile, "Number of crewmates: %d\n\n", game->numberOfCrewmates);
    fprintf(outputFile, "\tCrewmates:\n");

    for(int i = 0; i < game->numberOfCrewmates; i++) {
        WritePlayer(game->crewmates[i], outputFile);
    }
    fprintf(outputFile, "\n");

    fprintf(outputFile, "\tImpostor:\n");
    WritePlayer(game->impostor, outputFile);
}

//Task 7
void *KillPlayer(void *input)
{
	// TODO
    Game *game = input;
	Player *impostor = game->impostor;
	int killDistance = 2147483647, killIndex = -1;
    for(int i = 0; i < game->numberOfCrewmates; i++) {
        Player *player = game->crewmates[i];
        if (player->alive) {
            int distance = abs(player->locations[player->indexOfLocation].x -
                               impostor->locations[impostor->indexOfLocation].x) +
                           abs(player->locations[player->indexOfLocation].y -
                               impostor->locations[impostor->indexOfLocation].y);
            if (distance <= game->killRange && distance <= killDistance) {
                killIndex = i;
                killDistance = distance;
            }
        }
    }
    char *result = malloc(1000);
    if (killIndex >= 0) {
        game->crewmates[killIndex]->alive = 0;
        sprintf(result, "Impostor %s has just killed crewmate %s from distance %d.", impostor->name, game->crewmates[killIndex]->name, killDistance);
    } else {
        sprintf(result, "Impostor %s couldn't kill anybody.", impostor->name);
    }

    return (void*)result;
}

// Task 8
void CalcuateNextCycleOfGame(Game *game, FILE *outputFile, void **inputMatrix)
{
	// TODO
	for (int i = 0; i < game->numberOfCrewmates; i++) {
        Player *player = game->crewmates[i];
        if (player->alive == 0) {
            fprintf(outputFile, "Crewmate %s is dead.\n", player->name);
        } else {
            player->indexOfLocation ++;
            if (player->indexOfLocation >= player->numberOfLocations) player->indexOfLocation = 0;
            Location location = player->locations[player->indexOfLocation];

            fprintf(outputFile, "Crewmate %s went to location (%d,%d).\n", player->name, location.x, location.y);

            fprintf(outputFile, "Crewmate %s's output:\n", player->name);

            char *output = (char*)(player->ability)(inputMatrix[i]);
            fprintf(outputFile, "%s\n", output);
            free(output);
        }
    }
    Player *impostor = game->impostor;
    impostor->indexOfLocation ++;
    if (impostor->indexOfLocation >= impostor->numberOfLocations) impostor->indexOfLocation = 0;
    Location location = impostor->locations[impostor->indexOfLocation];

    fprintf(outputFile, "Impostor %s went to location (%d,%d).\n", impostor->name, location.x, location.y);
    fprintf(outputFile, "Impostor %s's output:\n", impostor->name);

    char* output = KillPlayer(game);
    fprintf(outputFile, "%s\n", output);
    free(output);
}

// Task 9
void FreePlayer(Player *player)
{
	// TODO
    free(player->name);
    free(player->color);
    free(player->hat);
    free(player->locations);
    free(player);
}

// Task 9
void FreeGame(Game *game)
{
	// TODO
	free(game->name);
	for (int i = 0; i < game->numberOfCrewmates; i++) FreePlayer(game->crewmates[i]);
	FreePlayer(game->impostor);
	free(game->crewmates);
	free(game);

}
