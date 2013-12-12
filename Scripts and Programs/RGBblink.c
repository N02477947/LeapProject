//RGBBlink.c
//This program flashes the three main colors of an RGB LED twice.
//It basically acts as a notification system that will run when
//the Pi is turned on and ready to accept command-line arguments.
#include <stdio.h>
#include <wiringPi.h>

#define RED		0	//The following lines define which pins on the wiringPi
#define GREEN	1	//GPIO pinout correspond to the RGB LEDs color pins leads
#define BLUE	2	//Pin 0 is connected to the "red" lead, pin 1 to the "green" lead,
					//Pin 2 is connected to the "blue" lead.

int main (void)
{
  int i = 0;
  wiringPiSetup () ;		//Sets up the wiringPi module
  pinMode (RED, OUTPUT) ;	//Sets the pin corresponding to "RED" as a GPIO out
  digitalWrite(RED, 0);		//Writes a zero to "RED" which means that portion of the LED is off
  pinMode (GREEN, OUTPUT);	//Sets the pin corresponding to "GREEN" as a GPIO out
  digitalWrite(GREEN, 0);	//Writes a zero to "GREEN" which means that portion of the LED is off
  pinMode (BLUE, OUTPUT);	//Sets the pin corresponding to "BLUE" as a GPIO out
  digitalWrite(BLUE, 0);	//Writes a zero to "BLUE" which means that portion of the LED is off

  for (i = 0; i < 2; i++)	//Loops twice
  {
    digitalWrite (RED,1) ;	// RED OFF
    digitalWrite (GREEN, 0) ;//GREEN OFF
    digitalWrite (BLUE, 0) ;  //BLUE ON
    delay (500) ;
    digitalWrite (RED, 0) ;  // RED OFF
    digitalWrite (GREEN, 1);  //GREEN ON
    digitalWrite (BLUE, 0);  //BLUE OFF
    delay (500) ;
    digitalWrite (RED, 0) ;  // RED OFF
    digitalWrite (GREEN, 0) ;//GREEN OFF
    digitalWrite (BLUE, 1) ;  //BLUE ON
    delay (500) ;
  }
  digitalWrite (RED, 0);	//Turns the "RED" portion of the LED off
  digitalWrite (GREEN, 0);	//Turns the "GREEN" portion of the LED off
  digitalWrite (BLUE, 0);	//Turns the "BLUE" portion of the LED off

  return 0 ;
}
