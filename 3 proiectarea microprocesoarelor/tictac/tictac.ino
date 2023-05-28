#include <stdio.h>
#include <stdlib.h>

#include <SPI.h>
#include <TFT.h>
#define cs 10
#define dc 9
#define rst 8

const int buttonPin = 7;
int buttonState = 0;
int VRx = A0;
int VRy = A1;
int SW = 2;

int xPosition = 0;
int yPosition = 0;
int SW_state = 0;
int mapX = 0;
int mapY = 0;

TFT screen = TFT(cs, dc, rst);

int mat[3][3]= {{-1,-1,-1},  
                {-1,-1,-1},
                {-1,-1,-1}};
int x = 0;
int y = 0;
int prevX = 0;
int prevY = 0;
int prevVal = mat[0][0];
int full = 0;
int counter = 0;
int roundCounter = 0;
int enter = 0;

//coord 

//1: 15, 8
//2: 68, 8
//3: 121, 8
//4: 15, 51
//5: 68, 51
//6: 121, 51
//7: 15, 95
//8: 68, 95
//9: 121, 95

void next()
{
    prevX = x;
    prevY = y;
    prevVal = mat[y][x];
}

bool isWin()
{
    for(int i = 0; i < 3; i ++)
    {
        if(mat[i][0] == mat[i][1] && mat[i][0] == mat[i][2] && mat[i][0] >= 0) return true; //randuri
        if(mat[0][i] == mat[1][i] && mat[0][i] == mat[2][i] && mat[0][i] >= 0) return true; //coloane
    }
    if(mat[0][0] == mat[1][1] && mat[0][0] == mat[2][2] && mat[0][0] >= 0) return true; //diag principala
    if(mat[0][2] == mat[1][1] && mat[0][2] == mat[2][0] && mat[0][2] >= 0) return true; //diag secundara
    return false;
}

void resetGame(){
  xPosition = 0;
  yPosition = 0;
  SW_state = 0;
  mapX = 0;
  mapY = 0;
  for(int i = 0; i < 3; i++){
    for(int j = 0; j < 3; j++){
       mat[i][j] = -1;
    }
  }
  x = 0;
  y = 0;
  prevX = 0;
  prevY = 0;
  prevVal = mat[0][0];
  full = 0;
  counter = 0;
  roundCounter = 1;
  enter = 0;
}

void Enter()
{
    if(roundCounter % 2 == 0) //rand x
        mat[y][x] = 1;
    else mat[y][x] = 0; //rand 0

    counter++;

    if(isWin() == true) {//gata joc + display castigator
      displayWinner();
      resetGame();
    }
    else if(counter >= 9) {
      displayDraw();
      resetGame();
    }
}

void Round(){
    next();
    mat[y][x] = 2;
    drawAll();
    while(enter == 0)
    {
      xPosition = analogRead(VRx);
      yPosition = analogRead(VRy);
      SW_state = digitalRead(SW);
      mapX = map(xPosition, 0, 1023, -512, 512);
      mapY = map(yPosition, 0, 1023, -512, 512);
      buttonState = digitalRead(buttonPin);
      
      if(mapX > 400) { //move left
        if(y != 0) y--;
        mat[prevY][prevX] = prevVal;
        next();
        mat[y][x] = 2;
        drawAll();
      } else if(mapX < -400) { //move right
        if(y < 2) y++;
        mat[prevY][prevX] = prevVal;
        next();
        mat[y][x] = 2;
        drawAll();
      } else if(mapY > 400) { //move up
        if(x != 0) x--;
        mat[prevY][prevX] = prevVal;
        next();
        mat[y][x] = 2;
        drawAll();
      }else if(mapY < -400) { //move down
        if(x < 2) x++;
        mat[prevY][prevX] = prevVal;
        next();
        mat[y][x] = 2;
        drawAll();
      } else if(buttonState == 1 && prevVal == -1){
        Enter();
        enter = 1;
        drawAll();
        roundCounter++;
      }
      delay(300);
    }
}

void drawX(int x, int y) {
  screen.setTextSize(4);
  screen.text("X",x,y);
}

void drawO(int x, int y) {
  screen.setTextSize(4);
  screen.text("O",x,y);
}

void drawtemp(int x, int y) {
  screen.setTextSize(4);
  screen.text("?",x,y);
}

void draw(int c, int i, int j) {
  int a, b;
  if(i == 0) a = 15;
  if(i == 1) a = 68;
  if(i == 2) a = 121;
  if(j == 0) b = 8;
  if(j == 1) b = 51;
  if(j == 2) b = 95;
  
  if(c == 1) drawX(a,b);
  else if(c == 0) drawO(a,b);
  else if(c == 2) drawtemp(a,b);
}

void drawAll(){
  screen.fillScreen(0);
  screen.stroke(0,255,0);
  screen.line(0,43,160,43);
  screen.line(0,86,160,86);
  screen.line(53,0,53,128);
  screen.line(106,0,106,128);
  
  for(int i = 0; i < 3; i++){
    for(int j = 0; j < 3; j++){
       if(mat[i][j] != -1) draw(mat[i][j], i, j);
    }
  }
}

void displayWinner() {
  screen.fillScreen(0);
  screen.stroke(0,255,0);
  screen.setTextSize(2);
  if(roundCounter % 2 == 0) screen.text("X Wins",0,20);
  else screen.text("0 Wins",0,20);
  delay(5000);
}

void displayDraw() {
  screen.fillScreen(0);
  screen.stroke(0,255,0);
  screen.setTextSize(2);
  screen.text("Draw",0,20);
  delay(5000);
}

void setup() {
  screen.begin();
  //screen.background(0,0,0); 
  screen.fillScreen(0);
  pinMode(VRx, INPUT);
  pinMode(VRy, INPUT);
  pinMode(SW, INPUT_PULLUP);
  pinMode(buttonPin, INPUT);
}

void loop() {
  while(true){
    enter = 0;
    Round();
  }
}
