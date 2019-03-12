package tesin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lejos.nxt.*;
import lejos.util.Delay;
import lejos.util.Stopwatch;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class TimelapseRot {
	public static int timer_counter=0; //amount of times the Motor already turned by x degree 
	public static boolean stop_timer=false; 
	public static Timer timer;
	
	public static void main(String args[])
	{
		Button.setKeyClickVolume(0); //DISABLE SOUNDS
		Menu(); //run main screen
	}
	
	/**
	 * This Method gets called when pressing on Start in the main Menu
	 * @param settings needs an int[] with settings. readConfig() returns the needed Array. 
	 */
	public static void mainprogramm(int[] settings) {
		stop_timer=false; //reset, if programm gets started again
		timer_counter=0; //reset, if programm gets started again
		boolean abort = false;
//		System.out.println("wait is :"	+settings[1]); //debug
//		System.out.println("amount is :"+settings[2]); //debug
//		System.out.println("degree is :"+settings[3]); //debug
		System.out.println("Starting Task..");
		System.out.println("Press abort for 1 Second to stop!");
		Delay.msDelay(1500);
		LCD.clear();
		 
		class tim_listener implements TimerListener
		{
			public void timedOut() {
				rotate(settings[1],settings[2],settings[3]);
			}
			
		}
		tim_listener listener = new tim_listener(); //Create Listener
		//Timer tim = new Timer(settings[1]*1000,listener); //Create Timer with listener
		timer = new Timer(settings[1]*1000,listener);
		timer.start();
		
		//System.out.println("RUN!");
		while(stop_timer==false)
		{
			//System.out.println("RUN!");
			if(Button.readButtons()==Button.ID_ESCAPE)
			{
				if(abort==false)
				{
					abort=true;
				}
				else //abort is still true
				{
					stop_timer=true;
					timer.stop();
				}
			}
			else
			{
				abort=false; //reset to false if button wasnt pressed long enough
			}
			Delay.msDelay(1000);
		}
		System.out.println("Finished Task, returning to Menu..");
		Delay.msDelay(1500);
		Motor.A.flt();
		LCD.clear();
		Menu();
		//System.out.println("RUN!");
		//Delay.msDelay(2500);
	}
	/**
	 * This Method gets called by the Timer, every x seconds
	 * @param wait seconds to wait
	 * @param amount amount of times to repeat
	 * @param degree how many degrees to turn every time
	 */
	public static void rotate(int wait, int amount,int degree)
	{
		if(stop_timer==true) //dont rotate if timer was deactivated, sometimes the timer sends a last input even tho it was deactivated.
		{
			return;
		}
		
		Motor.A.setAcceleration(500); //hardcoded atm
		
		timer_counter++;
		System.out.println("R: "+timer_counter+" - " + Motor.A.getTachoCount()%360);
		Motor.A.rotate(degree, true); //rotate by degree and dont wait for rotation to finish.
		
		//displaySettings(0, false);
		
		if(timer_counter>=amount)
		{
			//stop_timer=true;
			timer.stop();
			stop_timer=true;
			System.out.println("STOP!");
		}
	}
	/**
	 * Writes an int[] into the Config File.
	 * @param input the int[] its supposed to save. Can be gathered from readConfig()
	 */
	public static void writeConfig(int[] input)
	{
//		System.out.println("Writing Config..");
		FileOutputStream out = null; // declare outside the try block
	    File data = new File("settings.txt");

	    try { 
	      out = new FileOutputStream(data);
	    } catch(IOException e) {
	    	System.err.println("Failed to create output stream");
	    	//Button.waitForAnyPress();
	    	System.exit(1);
	    }

	    DataOutputStream dataOut = new DataOutputStream(out);

	    try { // write
	    for(int i=0;i<input.length;i++)
	    {
	    	dataOut.writeInt(input[i]);
	    }
	      out.close(); // flush the buffer and write the file
	    } catch (IOException e) {
	      System.err.println("Failed to write to output stream");
	    }
	}
	/**
	 * Reads the Config file. 
	 * @return The Config Array
	 */
	public static int[] readConfig()
	{
		 File data = new File("settings.txt");
		 int[] output = new int[5]; //FIX LENGTH SUCKS XD

		    try {
		      InputStream is = new FileInputStream(data);
		      DataInputStream din = new DataInputStream(is);
		      for(int i=0;i<5;i++)
		      {
		    	  int x = din.readInt();
		    	  output[i]=x;
		      }
		      din.close();
		    } catch (IOException ioe) {
		      System.err.println("Read Exception");
		    }
		return output;
	}
	/**
	 * Method that displays the Main Menu
	 * @param selected which option is selected atm
	 */
	public static void displayMenu(int selected)
	{
		String Header = "Menu";
		String startButton = "Start";
		String settingsButton = "Settings";
		String exitButton = "Return";
		int leftSpace = 2;
		int lineheight = 1;
		int[] heights = {0,2,3,4};
		
		LCD.clear();
		LCD.drawString(Header, leftSpace, heights[0]);
		drawLineX((lineheight*8)+4);
		LCD.drawString(startButton, 	leftSpace, 		heights[1]);
		LCD.drawString(settingsButton, 	leftSpace, 		heights[2]);
		LCD.drawString(exitButton, 		leftSpace,		heights[3]);
		LCD.drawString(">", 			leftSpace-1,	heights[selected]);
	}
	/**
	 * The Method that displays the Settings screen
	 * @param selected which option is selected atm
	 * @param mode true:edit values with left/right false: select option with left/right
	 */
	public static void displaySettings(int selected,boolean mode)
	{
		String Header = 			"Settings";
		String intervalButton = 	"wait   ";
		String repeatButton = 		"repeat ";
		String degreeButton = 		"degree ";
		String resetButton = 		"reset";
		String exitButton = 		"Exit";
		String cursor =				">";
		int leftSpace = 1;
		int lineheight = 1;
		
		if(mode==true)
		{
			leftSpace++;
		}
		int[] heights = {0,2,3,4,5,6};
		
		int[] data = readConfig();
		
		LCD.clear();
		LCD.drawString(Header, leftSpace, heights[0]);
		drawLineX((lineheight*8)+4);
		LCD.drawString(intervalButton	+" ["+data[1]+"]", 	leftSpace, 		heights[1]);
		LCD.drawString(repeatButton		+" ["+data[2]+"]", 	leftSpace, 		heights[2]);
		LCD.drawString(degreeButton		+" ["+data[3]+"]", 	leftSpace,		heights[3]);
		LCD.drawString(resetButton,							leftSpace,		heights[4]);
		LCD.drawString(exitButton, 							leftSpace,		heights[5]);
		LCD.drawString(cursor, 								leftSpace-1,	heights[selected]);
	}
	/**
	 * Runs the Settings-Menu. Dont use DisplaySettings!
	 */
	public static void Settings()
	{
		boolean inSettings = true;
		boolean changemode = false;
		int selected=1;
		int[] data = readConfig();
		
		while(inSettings)
		{
				
			displaySettings(selected,changemode);
			Stopwatch timer = new Stopwatch();
			
			int pressed=Button.readButtons();
			while(pressed==0)
			{
				pressed=Button.readButtons();
				Delay.msDelay(50);
			}
			if (timer.elapsed()>100) //button was pressed first time
			{
				Delay.msDelay(400);
			}
			timer.reset();
			if (pressed==Button.ID_LEFT) {
				
				if(changemode==false) {
				selected--;
				if(selected<1)
				selected=5; } 
				else
				{
				data = readConfig();
				data[selected]=data[selected]-1;
				writeConfig(data);
				}
				
			} else if (pressed==Button.ID_RIGHT) {
				
				if(changemode==false) {
				selected++;
				if(selected>5)
				selected=1; }
				else
				{
				//Sound.beep();
				data = readConfig();
				data[selected]=data[selected]+1;
				writeConfig(data);
				}
				
			} else if (pressed==Button.ID_ESCAPE) {
				inSettings=false;
				Menu();
				//RunSelected(3);
			} else if (pressed==Button.ID_ENTER) {
				if(selected>=1 && selected<=3)
				{
					if(changemode==false)
					{	
						changemode=true;
					}
					else
					{
						changemode=false;
					}
				}
				else
				{
					if(selected==4) //reset stats
					{
						int[] tmp = {0,5,180,1};
						writeConfig(tmp);
					}
					else if(selected==5)
					{
						inSettings=false;
						Menu();
					}
				}
			}
		}
	}
	
	/**
	 * Starts the Menu, dont use displayMenu!
	 */
	public static void Menu()
	{	
		boolean inMenu = true;
		int selected=1;
		
		while(inMenu)
		{
			displayMenu(selected);
			int pressed = Button.waitForAnyPress();
			
			if (pressed==Button.ID_LEFT) {
				
				selected--;
				
				if(selected<1)
				selected=3;
				
			} else if (pressed==Button.ID_RIGHT) {
			
				selected++;
				
				if(selected>3)
				selected=1;
				
			} else if (pressed==Button.ID_ESCAPE) {
				inMenu=false;
				//RunSelected(3);
			} else if (pressed==Button.ID_ENTER) {
				inMenu=false;
				RunSelected(selected);
			}
		}
	}
/**
 * runs the selected Option (Main Menu)
 * @param index the selected option.
 */
	public static void RunSelected(int index)
	{
		if(index==1)
		{
			//Run Programm Method
			int[] tmp = readConfig();
			LCD.clear();
			mainprogramm(tmp);
//			System.out.println("ERROR: No Programm to Load!");
			System.out.println("> Press Any Key to Continue(after main)");
			Button.waitForAnyPress();
			
			
		}
		else if(index==2)
		{
			//Run Settings
			LCD.clear();
			//System.out.println("ERROR: No Settings to Load!");
			Settings();
			//LCD.clear(); 
			//System.out.println("> Press Any Key to Continue (After Menu Run)");
			//Button.waitForAnyPress();
			//NXT.shutDown();
		}
		else if(index==3)
		{
			LCD.clear();
			System.out.println("Goodbye!...");
			//System.out.println("> Press Any Key to Continue");
			//Button.waitForAnyPress();
			//NXT.shutDown();
		}
		else
		{
			LCD.clear();
			System.out.println("ERROR: Wrong MENU Index!");
			System.out.println("Goodbye!...");
			System.out.println("> Press Any Key to Continue");
			Button.waitForAnyPress();
			//NXT.shutDown();
		}	
		
	}
	/**
	 * draws a line
	 * @param y height of the line
	 */
	public static void drawLineX(int y)
	{
		for(int i = 0;i<=LCD.SCREEN_WIDTH;i++)
		{
			LCD.setPixel(i, y, 1);
		}
	}
}




