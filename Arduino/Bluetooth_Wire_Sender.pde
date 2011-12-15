// Wire Slave Sender
// by Nicholas Zambetti <http://www.zambetti.com>

// Demonstrates use of the Wire library
// Sends data as an I2C/TWI slave device
// Refer to the "Wire Master Reader" example for use with this

// Created 29 March 2006

// This example code is in the public domain.


#include <Wire.h>
#include <MeetAndroid.h>

MeetAndroid meetAndroid;

int MARQUEE_MODE = 0;
int MODE_NORMAL = 0;
int MODE_PARTY = 1;
int MODE_STROBE = 2;
int MODE_SCREAM = 3;
int MODE_BOO = 4;
int MODE_RIGGED = 5;

void setup()
{
  Wire.begin(2);                // join i2c bus with address #2
  Wire.onRequest(requestEvent); // register event
  Serial.begin(57600);
  meetAndroid.registerFunction(testEvent, 'A');
}

void loop()
{
  //delay(100);
  meetAndroid.receive();
}

void testEvent(byte flag, byte numOfValues)
{
  MARQUEE_MODE = meetAndroid.getInt();
}

// function that executes whenever data is requested by master
// this function is registered as an event, see setup()
void requestEvent()
{
  if( MARQUEE_MODE == MODE_NORMAL )
  {
    Wire.send("+@");
  }else if( MARQUEE_MODE == MODE_PARTY )
  {
    Wire.send("+#");
  }else if( MARQUEE_MODE == MODE_STROBE )
  {
    Wire.send("+$");
  }else if( MARQUEE_MODE == MODE_SCREAM )
  {
    Wire.send("+%");
  }else if( MARQUEE_MODE == MODE_BOO )
  {
    Wire.send("+^");
  }else if( MARQUEE_MODE == MODE_RIGGED )
  {
    Wire.send("+&");
  }
}
