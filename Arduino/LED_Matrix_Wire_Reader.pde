// Wire Master Reader
// by Nicholas Zambetti <http://www.zambetti.com>

// Demonstrates use of the Wire library
// Reads data from an I2C/TWI slave device
// Refer to the "Wire Slave Sender" example for use with this

// Created 29 March 2006

// This example code is in the public domain.


#include <Wire.h>
#include <RGB.h>
#include <RGBMatrix.h>

int NUM_BOARDS = 7;
int MARQUEE_MODE = 0;
char colors[6] = {GREEN, BLUE, TEAL, GREEN, BLUE, TEAL};

void setup()
{
  delay( 5000 );
  Wire.begin();
  RGBMatrix.begin(NUM_BOARDS);
} 

void loop()
{
  Wire.requestFrom(2, 50);    // request 6 bytes from slave device #2
  int i = 0;
  byte inByte = '\0';
  byte modeByte = '\0';
  while(Wire.available()){   // slave may send less than requested{
    if(inByte != '+'){
      inByte = Wire.receive();
    }else{
      if( modeByte != '@')
      {
        if(modeByte != '#'){
          if(modeByte != '$'){
            modeByte=Wire.receive();
          }
        }
      }
      
      if(modeByte=='@'){
        MARQUEE_MODE=0;
        char c=Wire.receive();
      }else if(modeByte=='#'){
        MARQUEE_MODE=1;
        char c=Wire.receive();
      }else if(modeByte=='$'){
        MARQUEE_MODE=2;
        char c=Wire.receive();
      }else if(modeByte=='%'){
        MARQUEE_MODE=3;
        char c=Wire.receive();
      }else if(modeByte=='^'){
        MARQUEE_MODE=4;
        char c=Wire.receive();
      }
    }
  }
  if(MARQUEE_MODE==0){
    showNormalMode();
  }else if(MARQUEE_MODE==1){
    showPartyMode();
  }else if(MARQUEE_MODE==2){
    showStrobeMode();
  }else if(MARQUEE_MODE==3){
    showScreamMode();
  }else if(MARQUEE_MODE==4){
    showBooMode();
  }
  i = 0;
  RGBMatrix.clear();
}

void showPartyMode(){
  
  displayPartyWords();
  displayPartyWords();
  
  for(char color=0; color<6; color++){
    //Fill the frame 1 row at a time.
    for(int row=0; row<NUM_ROWS; row++){
      //Fill the row on every board in the frame.
      for(int board=0; board<NUM_BOARDS; board++){
        RGBMatrix.fillRow(board, row, colors[color]);
      }
      //Update the display each time we add a row.
      RGBMatrix.display();
      delay(20);        
    }
    
    //After adding all of the rows to the frame, we want to remove them.
    //We can 'remove' a row by coloring the row with black.
    //Add a black row to the frame 1 row at a time
    for(int row=0; row<NUM_ROWS; row++){
      //Add the black row to the entire frame.
      for(int board=0; board<NUM_BOARDS; board++){
        RGBMatrix.fillRow(board, row, BLACK);
      }
      //Update the display with the 'removed' row.
      RGBMatrix.display();
      delay(20);        
    }
  }
  RGBMatrix.clear();
}

void displayPartyWords()
{
  RGBMatrix.clear();
  RGBMatrix.fillChar( 5, 'P', RED );
  RGBMatrix.fillChar( 4, 'A', RED );
  RGBMatrix.fillChar( 3, 'R', RED );
  RGBMatrix.fillChar( 2, 'T', RED );
  RGBMatrix.fillChar( 1, 'Y', RED );
  RGBMatrix.display();
  delay(300);
  RGBMatrix.clear();
  delay(300);
  RGBMatrix.fillChar( 5, 'M', RED );
  RGBMatrix.fillChar( 4, 'O', RED );
  RGBMatrix.fillChar( 3, 'D', RED );
  RGBMatrix.fillChar( 2, 'E', RED );
  RGBMatrix.display();
  delay(300);
  RGBMatrix.clear();
  delay(300);
}

void showStrobeMode()
{
  for(int board=0; board<NUM_BOARDS; board++){
    RGBMatrix.fillScreen(board, TEAL);
  }
  RGBMatrix.display();
  delay(700);
  RGBMatrix.clear();
  delay(700);
}

void showNormalMode()
{
  RGBMatrix.scroll("INTERACTIVE STEPHEN HAWKING", RED, 15);
  RGBMatrix.scroll("TEXT YOUR NUMBER HERE TO MAKE STEPHEN TALK", RED, 15);
  RGBMatrix.scroll("KEEP IT CLEAN I HAVE YOUR PHONE NUMBER", RED, 15);
}

void showScreamMode()
{
  RGBMatrix.clear();
  RGBMatrix.fillChar( 6, 'S', RED );
  RGBMatrix.fillChar( 5, 'C', RED );
  RGBMatrix.fillChar( 4, 'R', RED );
  RGBMatrix.fillChar( 3, 'E', RED );
  RGBMatrix.fillChar( 2, 'A', RED );
  RGBMatrix.fillChar( 1, 'M', RED );
  RGBMatrix.display();
  delay(300);
  RGBMatrix.clear();
  delay(300);
}

void showBooMode()
{
  RGBMatrix.clear();
  RGBMatrix.fillChar( 4, 'B', RED );
  RGBMatrix.fillChar( 3, 'O', RED );
  RGBMatrix.fillChar( 2, 'O', RED );
  RGBMatrix.display();
  delay(300);
  RGBMatrix.clear();
  delay(300);
}
