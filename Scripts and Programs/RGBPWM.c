//RGBPWM.c
//This is the main program used on the Raspberry Pi for this project.
//The program uses a simple number system to switch between colors 
//and brightness levels of the selected LEDs.
//This program utilizes the softPwm capabilities of the wiringPi
//module. Pulse width modulation, or PWM controls the "pulse width" of 
//a GPIO pin.  The pulse width is the amount of time a pin is set to 
//logic '1' (or 'on') in a clock cycle.  It's a relatively complex
//concept but for the purposes of this project, the larger(higher number)
//the pulse width, the brighter the LED will be and vice versa. 

#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <wiringPi.h>
#include <softPwm.h>

#define RANGE	100		//Defines the Range of the Pulse width modulation
#define RED		0		//Defines the various color leads from the RGB LED as 
#define GREEN	1		//they correspond to the GPIO pinout of the wiringPi module			
#define BLUE	2

int main ()
{
	printf("Software RGB PWM test \n");
	int LEDColor = 0;	//sets up a color variable used when dimming the LED
	int something;		//A very unhelpfully named variable that basically controls the whole program.
						//This variable determines what color to display and brightness levels.
						
	wiringPiSetup ();	//sets up the wiringPi module
	
	softPwmCreate (RED, 0, RANGE);	//The following three lines setup the pins previously defined
	softPwmCreate (GREEN, 0, RANGE);//as pulse width modulators.  The first argument is the pin to be
	softPwmCreate (BLUE, 0, RANGE); //setup as PWM, the second is the initial value to send to the pins.
									//Here they are set to zero as the LED shouldn't light up at first.
									//The third argument is the range the PWM should have, here I've 
									//set it up for the predefined value of 100, which just so happens
									//to be the max value allowed by wiringPi for a PWM.
									
for(;;)								//Infinite loop
	{
		something = 0;

		printf("Enter Something\n");

		scanf("%d", &something); //scanf accepts a value and stores it to the 'something' variable
		
			if(something == 0)		//If zero is entered, the LED should be off
			{
			softPwmWrite(RED, 0);	//softPwmWrite accepts two arguments, the pin to write to, and the value to write to it
			softPwmWrite(GREEN, 0);	//Here, all the values are 0 as the LED should be completely off.
			softPwmWrite(BLUE, 0);
			}

			if(something == 1)		//If 1 is entered, the LED should be Red, as indicated by the RED pin receiving a value
			{						//of 100.
			softPwmWrite(RED, 100);
			softPwmWrite(GREEN, 0);
			softPwmWrite(BLUE, 0);
			LEDColor = 1;			//LEDColor is set to 1, which will be used later when working with Brightness levels.
			}

			else if(something == 2)	//if 2 is entered, the LED should be Yellow-ish.
			{
			softPwmWrite(RED, 50);	//Here, RED is only given a value of 50 and green a value of 25. 
			softPwmWrite(GREEN, 25);	//These values can be modified to attempt different colors.
			softPwmWrite(BLUE, 0);		//These values were arrived at by trial-and-error and give
										//the closest semblance of the color yellow.
			LEDColor = 2;				//LEDColor is set to 2
			}

			else if(something == 3) //if 3 is entered, the LED should be Green
			{
			softPwmWrite(RED, 0);
			softPwmWrite(GREEN, 100);
			softPwmWrite(BLUE, 0);
			LEDColor = 3;			//LEDColor is set to 3
			}

			else if(something == 4) //if 4 is entered, the LED should be light blue
			{
			softPwmWrite(RED, 0);
			softPwmWrite(GREEN, 100);
			softPwmWrite(BLUE, 100);
			LEDColor = 4;			//LEDColor is set to 4
			}
			else if(something == 5)	//if 5 is entered, the LED should be a brilliant blue
			{
			softPwmWrite(RED, 0);
			softPwmWrite(GREEN, 0);
			softPwmWrite(BLUE, 100);
			LEDColor = 5;			//LEDColor is set to 5
			}
			else if(something == 6)	//if 6 is entered, the LED should be purple/pink
			{
			softPwmWrite(RED, 100);
			softPwmWrite(GREEN, 0);
			softPwmWrite(BLUE, 100);
			LEDColor = 6;			//LEDColor is set to 6
			}
			else if(something == 7)//if 7 is entered, the LED should be a white-ish color
			{
			softPwmWrite(RED, 100);
			softPwmWrite(GREEN, 100);
			softPwmWrite(BLUE, 100);
			LEDColor = 7;			//LEDColor is set to 7
			}
			else if(something > 7)	//The following controls Brightness. If a number above 7 is entered
			{						//the program fetches the previously set LEDColor and sets the pulse
									//width according to the value of 'something'. This could fall between
									//7 and 100, allowing for a nice range of brightness
									
				if(LEDColor == 1)	//if LEDColor was previously set as 1("red"), dim only the red diode
				{
				softPwmWrite(RED, something);
				softPwmWrite(GREEN, 0);
				softPwmWrite(BLUE, 0);
				}
				if(LEDColor == 2)	//if LEDColor was previously set as 2("yellow"), dim only the red and green diodes
				{
				softPwmWrite(RED, something);
				softPwmWrite(GREEN, something);
				softPwmWrite(BLUE, 0);
				}
				if(LEDColor == 3)	//if LEDColor was previously set as 3("green"), dim only the green diode
				{
				softPwmWrite(RED, 0);
				softPwmWrite(GREEN, something);
				softPwmWrite(BLUE, 0);
				}
				if(LEDColor == 4)	//if LEDColor was previously set as 4("light blue"), dim only the green and blue diodes
				{
				softPwmWrite(RED, 0);
				softPwmWrite(GREEN, something);
				softPwmWrite(BLUE, something);
				}
				if(LEDColor == 5)	//if LEDColor was previously set as 5("blue"), dim only the blue diode
				{
				softPwmWrite(RED, 0);
				softPwmWrite(GREEN, 0);
				softPwmWrite(BLUE, something);
				}
				if(LEDColor == 6)	//if LEDColor was previously set as 6("purple"), dim only the red and blue diodes
				{
				softPwmWrite(RED, something);
				softPwmWrite(GREEN, 0);
				softPwmWrite(BLUE, something);
				}
				if(LEDColor == 7)	//if LEDColor was previously set as 7("white"), dim all the diodes.
				{
				softPwmWrite(RED, something);
				softPwmWrite(GREEN, something);
				softPwmWrite(BLUE, something);
				}
			}
	}
}


