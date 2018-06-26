package BattleShip;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client side of the game, if enter DNS
 * Multithreading, run with the panels at the same time
 */
public class GameClient extends EndSys implements Runnable{
	
	protected Socket client = null; // socket
	protected Grid grid; // the grid of the client
	protected InputStream inStream = null; // define the input string
	protected OutputStream outStream = null; // define the input string
	protected PrintWriter out = null; // the wirter writ out the string
	protected Scanner in = null; // scan the input string
	protected MainFrame frame; //reference of te frame
	
	protected String dns; // the dns of the client provided
	protected String shootTarget; // the tartget shoot by client
	protected String start; // record the state of the game
	protected boolean startSend = false; // determine if the information send
	protected boolean serverStart = false; // determine if server start or not
	
	protected boolean gameOver = false;// determine the state of the game
	protected boolean win = false; // determine wheter win or loss
	
	protected int destroyShip; // record the number of destroy ship
	
	protected int timer; // record the timer set by server
	
	@Override
	/**
	 * Run the client end
	 */
	public void run() {
		try {
			// create socket and accept connection
			client = new Socket(dns, 9999);
			// input stream, output stream define
			inStream = client.getInputStream();
			outStream = client.getOutputStream();
			
			// init scanner, out put printer
			in = new Scanner(inStream, "UTF-8");
			//input = new Scanner(System.in);
			out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);
			
			out.println("Connect Successfully");
			// waiting for the grid size and timer
			while(in.hasNextLine()) {
				String string = in.nextLine(); // get the info
				Double combine = DataParser.readBase(string); // decode the info
				frame.GridSize = combine.intValue(); // init the size by encoding
				Double timer = (combine - frame.GridSize)*100; // init the timer by encoding
				frame.Timer = timer.intValue(); // record the timer to main frame
				this.timer = timer.intValue(); // recode the timer to this
				break; // go to next stage
			}
			
			// connect server succesfully, then turn to deploy stage
			frame.startDeploy();
			
			// determine the client click start or not
			while(start == null) {
				try {
					Thread.sleep(500);
				}catch(Exception e) {
					System.out.println("Sleep interrupt");					
				}
			}
			// client click the start, let the server side know
			out.println(start);
			
			//check if server is ready for battle
			while(in.hasNextLine()) {
				// if server send the message
				if(in.nextLine().equals("Ready for battle")) {
					// then turn to the battle stage
					grid = frame.deploy.getGrid();
					frame.startBattle();
					break;
				}
			}
			
			// renew the scanner
			in = new Scanner(inStream, "UTF-8");
			// battle stage
			while(!gameOver) {
				// It's the turn for Server shot, client start shoot first
				frame.ground.unlock(); // unlock the panel
				int counter = 0; // count the time
				int remainT = timer; // set the remaining time to the record
				frame.ground.timer = remainT; //set the timer for ground
				while(shootTarget == null) {
					try {
						Thread.sleep(100); // sleep for 100 millisecond
						counter++; // counter increase
					}catch(Exception e) {
						System.out.println("Sleep interrupt");					
					}
					// check if 1 second is reach
					if(counter >= 10) {
						counter = 0; // init the counter to 0 and decrease 1s of the remainint time
						remainT --;
						frame.ground.timer = remainT; // renew the timer in the ground
						frame.ground.repaint(); // repaint to renew the timer in ground
					}
					if(remainT <= 0) { // if the timer if 0, loss the game
						out.println("Surrender"); // send the loss info to server
						gameOver = true; // set game over to true
						win = false; // set the win or loss
						break;
					}
				}
				
				// if game over not continue the following steps
				if(gameOver) break;
				
				// server decide to shoot, shootTarget not null at this point
				// send the decision to server
				out.println(shootTarget);
				// clean the decision
				shootTarget = null;
				
				//wait the shoot feedback, determine the other end
				while(in.hasNextLine()) {
					String feedback = in.nextLine();
					//if target the ship, wirte the buffer as target
					if(feedback.equals("Target")) {
						frame.ground.writeBuffer(2);
					}
					// if miss, wirte the buffer as miss
					else if(feedback.equals("Miss")) {
						frame.ground.writeBuffer(1);
					}
					// if the ship is destroy, write buffer as target and 
					else if(feedback.substring(0, 7).equals("Destroy")) {
						frame.ground.writeBuffer(2);
						// add to the destroy ship
						frame.ground.addOpponentShip(feedback);
						// count if all ship are shoot down
						if(frame.ground.opShips.size() == grid.num_ship) {
							gameOver = true;
							win = true;
						}
					}
					break;
				}
				
				// if game over, not go following steps
				if(gameOver) break;
				
				// client done, waiting for server shoot
				while(in.hasNextLine()) {
					String opDecision = in.nextLine();
					// check the information
					if(opDecision.equals("Surrender")) {
						gameOver = true;
						win = true;
						break;
					}
					// decode the feedback
					String shotFeedback = DataParser.recieveShot(opDecision, grid);
					frame.ground.repaint(); // repaint according to the feedback
					// not a valid request
					if(shotFeedback.equals("Invalid")) continue;
					// else, return the string and give chance to server
					if(shotFeedback.equals("Target") || shotFeedback.equals("Miss")) {
						out.println(shotFeedback);
						break;
					}
					// if the ship is destroied, let the another side know
					if(shotFeedback.substring(0, 7).equals("Destroy")) {
						out.println(shotFeedback); // let another end know
						destroyShip ++;
						break;
					}
				}
				
				//check if server has remaining ship
				if(destroyShip == grid.num_ship) {
					gameOver = true;
					win = false;
					break;
				}
			}
			
			// close all the opening resources
			frame.endInfo(win);
			out.close();
			client.close();
			inStream.close();
			outStream.close();
			out.close();
			
		}catch(Exception e) {
			System.err.println("error");
		}
	}
	
	/**
	 * Constructor, init by given dns, and add the frame reference to the class
	 * @param frame
	 * @param dns
	 */
	public GameClient(MainFrame frame, String dns) {
		this.frame = frame;
		this.dns = dns;
	}
}
