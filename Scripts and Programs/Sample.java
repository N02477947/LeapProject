
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Math;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;


class GlobalVars{
	public static int LeftSwipes = 0;
	public static int ClockGest = 0;
	public static int RightSwipes = 0;
	public static int CounterGest = 0;

}

class SampleListener extends Listener {
	public void onInit(Controller controller) {
		System.out.println("Initialized");
	}

	public void onGest()
	{
		//Open the command line
		try {
			System.out.println("Opening Commandline");
			Runtime runTime = Runtime.getRuntime();
			Process process = runTime.exec(new String[]{"cmd", "/k", "start", "cmd"});
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Create robot and try typing
			try{
				Robot robot = new Robot();
				robot.delay(5000);
				robot.keyPress(KeyEvent.VK_H);
				robot.keyPress(KeyEvent.VK_E);
				robot.keyPress(KeyEvent.VK_L);
				
				robot.keyPress(KeyEvent.VK_L);
				robot.keyPress(KeyEvent.VK_O);
			} catch(AWTException e){
				e.printStackTrace();
			}
			//Close Commandline
			System.out.println("Closing Commandline");
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onConnect(Controller controller) {
		System.out.println("Connected");
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
	}

	public void onDisconnect(Controller controller) {
		//Note: not dispatched when running in a debugger.
		System.out.println("Disconnected");
	}

	public void onExit(Controller controller) {
		System.out.println("Exited");
	}


	public void onFrame(Controller controller) {
		// Get the most recent frame and report some basic information
		String GestType = "";

		Frame frame = controller.frame();

		if (!frame.hands().isEmpty()) {
			// Get the first hand
			Hand hand = frame.hands().get(0);

			// Check if the hand has any fingers
			FingerList fingers = hand.fingers();
			if (!fingers.isEmpty()) {
				// Calculate the hand's average finger tip position
				Vector avgPos = Vector.zero();
				for (Finger finger : fingers) {
					avgPos = avgPos.plus(finger.tipPosition());
				}
				avgPos = avgPos.divide(fingers.count());
			}

			// Get the hand's normal vector and direction
			Vector normal = hand.palmNormal();
			Vector direction = hand.direction();
		}

		GestureList gestures = frame.gestures();

		for (int i = 0; i < gestures.count(); i++) {
			Gesture gesture = gestures.get(i);

			switch (gesture.type()) {
			case TYPE_CIRCLE:
				CircleGesture circle = new CircleGesture(gesture);

				// Calculate clock direction using the angle between circle normal and pointable
				//				String clockwiseness;
				if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {
					// Clockwise if angle is less than 90 degrees
					GestType = "clockwise circle";
					GlobalVars.ClockGest++;
				} else {
					GestType = "counterclockwise circle";
					GlobalVars.CounterGest++;
				}

				// Calculate angle swept since last frame
				double sweptAngle = 0;
				if (circle.state() != State.STATE_START) {
					CircleGesture previousUpdate = new CircleGesture(controller.frame(1).gesture(circle.id()));
					sweptAngle = (circle.progress() - previousUpdate.progress()) * 2 * Math.PI;
				}

				break;
			case TYPE_SWIPE:

				SwipeGesture swipe = new SwipeGesture(gesture);
				if( swipe.direction().getX() < 0)
				{                	        
					GestType = "left swipe";
					GlobalVars.LeftSwipes++;
				}

				if(swipe.direction().getX() > 0)
				{
					GestType = "right swipe";
					GlobalVars.RightSwipes++;
				}
				break;
			default:
				System.out.println("Unknown gesture type.");
				break;
			}
			if (GlobalVars.ClockGest == 1 || GlobalVars.CounterGest == 1
					|| GlobalVars.LeftSwipes == 1 || GlobalVars.RightSwipes == 1) {
				//System.out.println(GlobalVars.counter);
				System.out.println(GestType);
				onGest();

			}	
		}
	}
}



class Sample {
	public static void main(String[] args) {



		// Create a sample listener and controller
		SampleListener listener = new SampleListener();
		Controller controller = new Controller();

		// Have the sample listener receive events from the controller
		controller.addListener(listener);

		// Keep this process running until Enter is pressed
		System.out.println("Press Enter to quit...");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Remove the sample listener when done
		controller.removeListener(listener);
	}
}
