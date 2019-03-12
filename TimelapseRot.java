package tesin;
//import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
//import java.io.Writer;

import lejos.nxt.*;
import lejos.util.Delay;
import lejos.util.Stopwatch;
import lejos.util.Timer;
import lejos.util.TimerListener;
public class TimelapseRot {
	public static int timer_counter=0;
	//public static boolean stop_timer=false;
	public static Timer timer;
	
	public static void main(String args[])
	{
		Menu();
	}
	public static void mainprogramm(int[] settings) {
		System.out.println("wait is :"	+settings[1]);
		System.out.println("amount is :"+settings[2]);
		System.out.println("degree is :"+settings[3]);
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

		
		
	}
	public static void rotate(int wait, int amount,int degree)
	{
		timer_counter++;
		System.out.println("Rotation "+timer_counter);
		Motor.A.rotate(degree, true); //rotate by degree and dont wait for rotation to finish.
		System.out.println("Waiting for "+wait+" s");
		
		displaySettings(0, false);
		
		if(timer_counter>=amount)
		{
			//stop_timer=true;
			timer.stop();
		}
	}
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
		//LCD.drawString("selected: "+selected, 2, 7);
		//LCD.drawString("mode: "+mode, 2, 8);
	}
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
			//int pressed = Button.waitForAnyPress();
			int pressed=Button.readButtons();
			while(pressed==0)
			{
				pressed=Button.readButtons();
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
				//data = readConfig(); //obsolete
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
				//data = readConfig(); //obsolete
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
	public static void RunSelected(int index)
	{
		if(index==1)
		{
			//Run Programm Method
			int[] tmp = readConfig();
			LCD.clear();
			mainprogramm(tmp);
//			System.out.println("ERROR: No Programm to Load!");
			System.out.println("> Press Any Key to Continue");
			Button.waitForAnyPress();
			
			
		}
		else if(index==2)
		{
			//Run Settings
			LCD.clear();
			//System.out.println("ERROR: No Settings to Load!");
			Settings();
			LCD.clear(); 
			System.out.println("> Press Any Key to Continue");
			Button.waitForAnyPress();
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
	
	public static void drawLineX(int y)
	{
		for(int i = 0;i<=LCD.SCREEN_WIDTH;i++)
		{
			LCD.setPixel(i, y, 1);
		}
	}
}




