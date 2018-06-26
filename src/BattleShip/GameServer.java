package BattleShip;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Server side of the game,
 * Multithreading, run with the panels at the same time
 */
public class GameServer extends EndSys implements Runnable{
	protected ServerSocket server = null; // server socket
	protected Socket incoming = null; // incoming socket
	protected InputStream inStream = null; //instream
	protected OutputStream outStream = null;// outstream
	protected PrintWriter out = null; // protected writed
	protected Scanner in = null; 
	
	protected String shootTarget = null; // determine the row and col of the clicking (encoded)
	protected boolean deployReady = false; // check if other end is ready
	
	protected MainFrame frame; // the frame of reference
	protected Grid grid; // the grid reference
	
	protected String start; // determine client is ready to start
	protected boolean startSend = false;  // determine if the information send
	protected boolean clientStart = false;// determine if server start or not
	
	protected boolean gameOver = false; // determine the state of the game
	protected boolean win = false; // determine wheter win or loss
	protected int destroyShip = 0; // record the number of destroy ship
	protected int timer;// record the timer set by server
	
	@Override
	/**
	 * Run the client end
	 */
	public void run() {
		try {
			// create socket and accept connection
			server = new ServerSocket(9999);
			incoming = server.accept(); // incoming socket accet
			
			// input stream, output stream define
			inStream = incoming.getInputStream();
			outStream = incoming.getOutputStream();
			// init scanner, out put printer
			in = new Scanner(inStream, "UTF-8");
			out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);
			
			// waiting connection
			while(in.hasNextLine()) {
				// connect with the client
				if(in.nextLine().equals("Connect Successfully")) {
					frame.startDeploy(); // start another stage
					// send the size and timer information to the client
					out.println(DataWrapper.sendBase(frame.GridSize, frame.Timer));
					this.timer = frame.Timer; // renew the timer
					break; // go to next stage
				}
			}
			
			// check if player is ready
			while(start == null) {
				try {
					Thread.sleep(500);
				}catch(Exception e) {
					System.out.println("Sleep interrupt");					
				}
			}
			// let the end know the player is ready
			out.println(start);
			
			// check if another end is ready for battle
			while(in.hasNextLine()) {
				if(in.nextLine().equals("Ready for battle")) {
					grid = frame.deploy.getGrid(); // get the deploied grid
					frame.startBattle(); // start the battle
					break; // go to next stage
				}
			}
			
			// renew the scanner
			in = new Scanner(inStream, "UTF-8");
			// server, wating for clients shot
			while(!gameOver) {
				// waiting for clients shot
				while(in.hasNextLine()) {
					String opDecision = in.nextLine();
					// if the command is surrender, opponent run out of time, he loss
					if(opDecision.equals("Surrender")) {
						gameOver = true;
						win = true;
						break;
					}
					// decode the feedback
					String shotFeedback = DataParser.recieveShot(opDecision, grid);
					frame.ground.repaint();
					// not a valid request
					if(shotFeedback.equals("Invalid")) {
						continue;
					}
					// else, return the string and give chance to server
					if(shotFeedback.equals("Target") || shotFeedback.equals("Miss")) {
						out.println(shotFeedback);
						System.out.println("Client " + shotFeedback);
						break;
					}
					// opponent destroy the ship, let he know
					if(shotFeedback.substring(0, 7).equals("Destroy")) {
						out.println(shotFeedback);
						destroyShip ++; // increase the number of destroy ship
						break;
					}
				}
				
				if(gameOver) break; // if gameover, not continue
				
				// check if server has remaining ship
				if(destroyShip == grid.num_ship) {
					gameOver = true;
					win = false;
					break;
				}
				
				// It's the turn for Server shot
				// unlock the panel
				frame.ground.unlock();
				
				// count the time of move
				int counter = 0;
				int remainT = timer; // init the remaing time
				frame.ground.timer = remainT;
				while(shootTarget == null) {
					try {
						Thread.sleep(100); // sleep and count
						counter++; // add the counter
					}catch(Exception e) {
						System.out.println("Sleep interrupt");					
					}
					// if counter reach 1s, renew the remaining time
					if(counter >= 10) {
						counter = 0;
						remainT --;
						frame.ground.timer = remainT;
						frame.ground.repaint();
					}
					// if run out of time, loss the game
					if(remainT <= 0) {
						out.println("Surrender");
						gameOver = true;
						win = false;
						break;
					}
				}
				
				if(gameOver) break; // don't continue
				
				// server decide to shoot, shootTarget not null at this point
				// send the decision to client
				out.println(shootTarget);
				// clean the decision
				shootTarget = null;
				
				// wait the shoot feedback
				while(in.hasNextLine()) {
					String feedback = in.nextLine();
					// target the ship, write the buffer
					if(feedback.equals("Target")) {
						frame.ground.writeBuffer(2);
					}
					// if miss, write the buffer
					else if(feedback.equals("Miss")) {
						frame.ground.writeBuffer(1);
					}
					// destroy the ship, write the buffer
					else if(feedback.substring(0, 7).equals("Destroy")) {
						frame.ground.writeBuffer(2);
						frame.ground.addOpponentShip(feedback);
						if(frame.ground.opShips.size() == grid.num_ship) {
							gameOver = true;
							win = true;
						}
					}
					break;
				}
				
			}
			
			// give win or loss panel
			frame.endInfo(win);
			// close the resources
			out.close();
			server.close();
			inStream.close();
			outStream.close();
			incoming.close();
			out.close();
			
			
		}catch(Exception e) {
			System.err.println("error");
		}
		
	}
	
	public GameServer(MainFrame frame) {
		this.frame = frame;
	}
}
