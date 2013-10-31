/

#include <stdio.h>
	// Standard include
#include <wiringPi.h>	

// LED Pin - wiringPi pin 0 is BCM_GPIO 17.


#define LED 0	//

Sets up a local parameter to define the GPIO pin connected to LED, in this case, it's pin zero 
		//wiringPi uses it's own pinout which is included in the wiringPi section of the repo.
int main (void)
{
  
int i = 0;
  		//sets up a local int for looping purposes
wiringPiSetup () ;
  	//This calls the wiringPiSetup function included in the wiringPi module. This is required for the program to function.
pinMode (LED, OUTPUT) ;

  //This line sets up the pin mode.  GPIO pins can be input or output, here we are setting up pin 0 to be an output.
for (i = 0; i <= 3; i++)
  	//Sets up a for-loop that will cycle 4 times, blinking the LED 4 times.
	{
    
	digitalWrite (LED, HIGH) ;	// Turns on the LED connected to pin 0
    
	delay (500) ;		// calls the delay function included in the wiringPi module.  This will cause a 500ms delay.
	digitalWrite (LED, LOW) ;	// Turns the LED connected to pin 0 off.
	delay (500) ;
  		//Another 500ms delay.
	}
  
	return 0 ;
			//Nothing is returned
}
