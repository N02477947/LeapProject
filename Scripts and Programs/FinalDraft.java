import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.lang.Math;

import javax.swing.JOptionPane;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;

class GlobalVars{							//This class sets up various global variables that are available to the whole program.
	public static int LeftSwipes = 0;		//LeftSwipes keeps track of the amount of left swipe gestures.
	public static int ClockGest = 0;		//ClockGest keeps track of the amount of clockwise gestures.
	public static int RightSwipes = 0;		//RightSwipes keeps track of the amount of right swipe gestures.
	public static int CounterGest = 0;		//CounterGest keeps track of the amount of counterclockwise gestures.
	public static String command = "0";		//Command keeps track of the command sent to the Raspberry Pi through SSH.
	public static String PreviousCommand = "";	//PreviousCommand is used to keep track of the gesture that immediately preceded the current gesture being sent to the Pi.
	public static int PreviousPalmPosition = 0;	//PreviousPalmPosition is used to keep track of the immediately preceding height of the hand above the Leap. Used for brightness control.
	public static String color = "";			//keeps track of the color of the LED. Used for switching colors using swipes.
}

class SampleListener extends Listener {

	public void onDim(int DimValue)			//This method sends a value corresponding to the height of the user's hand over the leap motion.
	{										//It accepts an integer value and converts it to a string to be sent to the RPi through the SSH protocol.
		String dimAmount = String.valueOf(DimValue);
		GlobalVars.command = dimAmount;
	}


	public void onGest(String GestType, int Gest)	//This method is the heart and soul of the program.  It sets up the command to be sent to the RPi that will control the RGB 
	{												//LED and change it's color.  This method accepts two values, a string representing the type of gesture currently recognized
													//and an integer value corresponding to said gesture. 
		if (GlobalVars.ClockGest == 1 || GlobalVars.CounterGest == 1
				|| GlobalVars.LeftSwipes == 1 || GlobalVars.RightSwipes == 1)	//This 'if' statement ensures that only one gesture will be recognized.  The Leap sends out data on every
		{																		//frame which can often be hundreds of lines long. This 'if' statement ensures that only one line of 
																				//pertinent data can affect the command sent to the Pi. 
			System.out.println(GestType);//prints out the current gesture.
			if(Gest == 0)					//if 'gest' equals 0, a clockwise circle has been recognized. This will turn the LED on, and the color will be red, as corresponds to the
											//program running on the Pi.
			{
				GlobalVars.command = "1";
				GlobalVars.color = "red";
			}
			else if(Gest == 1)				//if 'gest' equals 1, a counterclockwise circle has been recognized. This will turn the LED off, as corresponds to the 
			{								//program running on the Pi.
				GlobalVars.command = "0";
				GlobalVars.color = "off";
			}
			//The following two 'if' statements cover the left-swipe and right-swipe gestures.  There are many nested 'if' statements that control the flow of the program running on the Pi. 
			//Each 'if' statement checks the current value of the global variable 'color' and acts accordingly.  The left-swipe gesture causes a 'backward color shift' and a right-swipe gesture
			//causes a 'forward color shift'.  For example, if the color variable is equal to "purple" and a left-swipe gesture is recognized, the program should send a command to the Pi directing
			//it to shift the color to blue. For the purposes of this project, a left-swipe is given the 'Gest' value 2, a right-swipe is given the 'Gest' value 1.
			else if(Gest == 2)				
			{
				if(GlobalVars.color.equals("red"))
				{
					GlobalVars.command = "7";
					GlobalVars.color = "white";
				}
				else if(GlobalVars.color.equals("white"))
				{
					GlobalVars.command = "6";
					GlobalVars.color = "purple";
				}
				else if(GlobalVars.color.equals("purple"))
				{
					GlobalVars.command = "5";
					GlobalVars.color = "blue";
				}
				else if(GlobalVars.color.equals("blue"))
				{
					GlobalVars.command = "4";
					GlobalVars.color = "babyblue";
				}
				else if(GlobalVars.color.equals("babyblue"))
				{
					GlobalVars.command = "3";
					GlobalVars.color = "green";
				}
				else if(GlobalVars.color.equals("green"))
				{
					GlobalVars.command = "2";
					GlobalVars.color = "yellow";
				}
				else if(GlobalVars.color.equals("yellow"))
				{
					GlobalVars.command = "1";
					GlobalVars.color = "red";
				}
			}
			else if(Gest == 3)
			{
				if(GlobalVars.color.equals("red"))
				{
					GlobalVars.command = "2";
					GlobalVars.color = "yellow";
				}
				else if(GlobalVars.color.equals("yellow"))
				{
					GlobalVars.command = "3";
					GlobalVars.color = "green";
				}
				else if(GlobalVars.color.equals("green"))
				{
					GlobalVars.command = "4";
					GlobalVars.color = "babyblue";
				}
				else if(GlobalVars.color.equals("babyblue"))
				{
					GlobalVars.command = "5";
					GlobalVars.color = "blue";
				}
				else if(GlobalVars.color.equals("blue"))
				{
					GlobalVars.command = "6";
					GlobalVars.color = "purple";
				}
				else if(GlobalVars.color.equals("purple"))
				{
					GlobalVars.command = "7";
					GlobalVars.color = "white";
				}
				else if(GlobalVars.color.equals("white"))
				{
					GlobalVars.command = "1";
					GlobalVars.color = "red";
				}
			}
			try {
				Thread.sleep(500);						//creates a short delay, which helps with overlapping commands
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			GlobalVars.ClockGest = 0;					//resets the global variables corresponding to each gesture back
			GlobalVars.CounterGest = 0;					//to zero, allowing for more gestures to be entered.
			GlobalVars.LeftSwipes = 0; 
			GlobalVars.RightSwipes =0;
		}
	}

	public void onConnect(Controller controller) {		//This method is executed when the program recognizes that the Leap Motion has been connected to the PC.
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);	//It also enables the two main gestures involved in the program, swipe and circle.
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
	}

	public void onDisconnect(Controller controller) {	//This method is executed should the Leap Motion become disconnected while the program is running.
		//Note: not dispatched when running in a debugger.
		System.out.println("Disconnected");
	}


	public void onFrame(Controller controller) {	//This method is called every time a frame is read in from the Leap Motion. 
													//A frame is like a still picture that the leap has captured. This occurs around
													//300 times per second on USB 3.0. Each frame is then analyzed by the Leap and 
													//pertinent data(like hand position or gestures) are used to control the program
													
		String GestType = "";						//sets up the GestType variable that will store a string corresponding to the type of gesture recognized
		int Gest = 0;								//Gives an integer value to the type of gesture recognized
		Frame frame = controller.frame();			//Creates a new frame object

		if (!frame.hands().isEmpty()) {				//if the frame is not empty
			// Get the first hand
			Hand hand = frame.hands().get(0);		//get pertinent data on the hand in the frame

			// Check if the hand has any fingers
			FingerList fingers = hand.fingers();	//get how many fingers are present on the hand
			if (!fingers.isEmpty()) {				//do if there are fingers present
				
				Vector avgPos = Vector.zero();		// Calculate the hand's average finger tip position
				for (Finger finger : fingers) {
					avgPos = avgPos.plus(finger.tipPosition());
				}
				avgPos = avgPos.divide(fingers.count());
			}

			int palm = (int)hand.palmPosition().getY()-25;	//sets up a variable to keep track of the hands height above the Leap, used in brightness control.
			
			if(palm >= 8 && palm <= 100 && (palm != GlobalVars.PreviousPalmPosition )) //If the value of palm is greater than 8 or less than 100 AND the value for palm has changed
			{																			//call the onDim method to control the brightness of the LED.
				GlobalVars.PreviousPalmPosition = palm;
				onDim(palm);
			}
		}


		GestureList gestures = frame.gestures();	//Creates new GestureList object

		for (int i = 0; i < gestures.count(); i++) {
			Gesture gesture = gestures.get(i);		//Gets the types of gestures to recognize as set up by the OnConnect method.
				
			//What follows is a case-switch statement that determines whether or not a gesture has been recognized in a fram. 
			//The two types of gestures are circle and swipe.
			switch (gesture.type()) {
			case TYPE_CIRCLE:
				CircleGesture circle = new CircleGesture(gesture);	//Creates a new circle object
				if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {// Clockwise if angle is less than 90 degrees
					
					GestType = "clockwise circle";		
					GlobalVars.ClockGest++;		//increment the ClockGest variable
					Gest = 0;					//For this project, a clockwise circle is given 'Gest' equal to 0.
					onGest(GestType, Gest);		//Calls the onGest method to update the command to be sent to the Pi.
				} else {
					GestType = "counterclockwise circle";
					GlobalVars.CounterGest++;	//increment the CounterGest variable.
					Gest = 1;					//For this project, a counterclockwise circle is given 'Gest' equal to 1.
					onGest(GestType, Gest);		//Calls the onGest method to update the command to be sent to the Pi.
				}
				break;

			case TYPE_SWIPE:

				SwipeGesture swipe = new SwipeGesture(gesture);
				if( swipe.direction().getX() < 0)	//Gets the horizontal value of the swipe direction. if the value is less than 1, a left swipe is recognized.
				{                	        
					GestType = "left";				
					GlobalVars.LeftSwipes++;		//increment LeftSwipes
					Gest = 2;						//For this project, a left-swipe is given 'Gest' equal to 2.
					onGest(GestType, Gest);			//Calls the onGest method to update the command to be sent to the Pi.
				}

				if(swipe.direction().getX() > 0)	//If the value of getX is greater than 1, a right-swipe is recognized.
				{
					GestType = "right";
					GlobalVars.RightSwipes++;		//increment RightSwipes
					Gest = 3;						//For this project, a right-swipe is given 'Gest' equal to 3.
					onGest(GestType, Gest);			//Calls the onGest method to update the command to be sent to the Pi.
				}
				break;
			default:							
				System.out.println("Unknown gesture type.");
				break;
			}
		}	
	}
}


class Sample {
	public static void main(String[] args) {
		try{
			JSch jsch=new JSch();			//Creates new JSsch object
			String host=null;				//creates a host string
			String user="pi";				//creates a user string, modify this to reflect the username you use on your pi.
			host= "192.168.1.200";			//modify this to reflect the Static IP address of you Pi
			Session session=jsch.getSession(user, host, 22);	//Starts new SSH session.
			session.setPassword("raspberry");					//Modify this to reflect your username's password.
			session.setConfig("StrictHostKeyChecking", "no");	//Disables any warning messages upon connecting to the Pi.
			session.connect(30000);   // making a connection with timeout.
			Channel channel=session.openChannel("shell");	//Creates new channel object.

			PipedInputStream pip = new PipedInputStream(40);	//creates new pipedinputstream.  This is used to send commands to the Pi without the use of a keyboard.
			channel.setInputStream(pip);						//sets the channels inputstream to the newly created piped inputstream.

			PipedOutputStream pop = new PipedOutputStream(pip);//creates new pipedoutputstream.
			final  PrintStream print = new PrintStream(pop);	//creates new printstream.

			channel.setOutputStream(System.out);				//sets up the output from the remote Pi.
			channel.connect(3*1000);					

			// Create a sample listener and controller
			SampleListener listener = new SampleListener();		//creates a listener object. This is what controls the input from the Leap Motion
			Controller controller = new Controller();			//creates a new controller object.

			
			controller.addListener(listener);					// Have the sample listener receive events from the controller

			Thread.sleep(500);									//short delay
			print.println("sudo ./rgbpwm");						//this sends a command to the Pi to start the main program.
			Thread.sleep(500);
			
			boolean exit = true;
			while(exit == true)									//loop for a good long time.
			{
				if(!GlobalVars.PreviousCommand.equals(GlobalVars.command))	//Only sends a command to the Pi when the command has changed.
				{
					GlobalVars.PreviousCommand = GlobalVars.command;		//Updates PreviousCommand
					System.out.println(GlobalVars.color);					//Prints the color that should appear on the LED
					print.println(GlobalVars.command);						//sends the appropriate command to the Pi
					Thread.sleep(500);										//delay
				}
			}
			print.close();													//Upon exit, close the printstream
			controller.removeListener(listener);							//Upon exit, remove the listener from the controller
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}


