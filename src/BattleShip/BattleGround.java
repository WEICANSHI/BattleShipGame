package BattleShip;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Battle Ground: Consisted of two grids, the upper coner grid is player's grid,
 * with the ship player deployed. The larger grid is opponent's, player click the
 * grid to shoot the opponent ships 
 *
 */
@SuppressWarnings("serial")
public class BattleGround extends JPanel implements MouseListener{
	
	protected MainFrame frame; // reference to MainFrame, where enable the ground call out
	// some of the functions or variable
	protected Grid mygrid; // the grid player deploed
	protected int[][] opponentGrid; // the opponent grid, paint the fire or miss
	protected int[][] myfireGrid;  // my grid, paint the fire or miss
	protected int size; // the size of grids
	protected int timer; // timer for the motion, if timer count out, the player would loss
	protected ArrayList<Ship> opShips = new ArrayList<Ship>(); // record the ships of opponent that plyer destroy
	
	private int myScale; // the scale of player grid
	private int opScale; // the scale of opponent grdi
	private int my_start_axis; // start position of player grid
	private int op_start_axis; // start position of opponent grid
	private boolean click_lock = true; // locked the clicking function
	
	private int rowBuffer = -1; // buffer for memorize the opponent shooting row
	private int colBuffer = -1; // buffer for memorize the opponent shooting row
	
	private int myScore; // record the score of opponent
	private int opScore; // record the score of opponent

	private Image targetImg; // fire Image
	private Image missImg; // miss Image
	
	/**
	 * Constructor: Initialize the basic component
	 * @param grid:  the grid the player deployed in the deployment state
	 * @param frame: reference of main frame
	 */
	public BattleGround(Grid grid, MainFrame frame) {
		mygrid = grid; // record the grid
		this.frame = frame; // record the frame as reference
		this.size = mygrid.getSize(); // get the size of the grid
		opponentGrid = new int[size][size]; // init opponent fire grid
		myfireGrid = new int[size][size]; // init my fire grid
		targetImg = DataParser.readImage("Image/target.png"); // load the fire image
		missImg = DataParser.readImage("Image/Miss.png"); // load the miss image
		// add mouse listener to this frame
		addMouseListener(this);
	}
	
	
	/**
	 * Paint Component: paint ships, grid, background
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		computeScale(); // private method, compute the scale, allow resize the window
		// paint the back ground
		g.drawImage(MainFrame.background, 0, 0, this.getWidth(), this.getHeight(), null);
		
		// draw opponent grid and player grid
		Graphics2D g2 = (Graphics2D) g;
		drawGrid(g2, myScale, my_start_axis, my_start_axis);
		drawGrid(g2, opScale, op_start_axis, my_start_axis);
		
		// draw my ships, loop through all ships deployed
		for(int i = 0; i < mygrid.num_ship; i++) {
			Ship thisShip = mygrid.getShips(i);
			drawShip(g2, thisShip, false);
		}
		
		// draw opponent ships, the destroied ships would be showed
		for(int i = 0; i < opShips.size(); i++) {
			Ship thisShip = opShips.get(i);
			drawShip(g2, thisShip, true);
		}
		
		// draw opponent fire grid
		myScore = 0; // init score
		for(int i = 0; i < opponentGrid.length; i++) {
			for(int j = 0; j < opponentGrid[i].length; j++) {
				drawTarget(g2, i, j, true);// draw fire and miss
				// count the score of player
				if(opponentGrid[i][j] == 2) myScore ++;
			}
		}
		
		// draw my fire grid
		opScore = 0; // init score
		myfireGrid = mygrid.getfireGrid();
		for(int i = 0; i < myfireGrid.length; i++) {
			for(int j = 0; j < myfireGrid[i].length; j++) {
				drawTarget(g2, i, j, false); // draw fire and miss
				// count the score of player
				if(myfireGrid[i][j] == 2) opScore ++;
			}
		}
		
		// draw the static String
		drawStatic(g2);
		
	}
	
	/**
	 * Compute the scale for grid, allowing resizing the window but not distored
	 * the drawing, and maintain the right position of ships
	 */
	private void computeScale() {
		// width start the 1/3 of grid
		int myWidth = this.getWidth()/3;
		// match the smaller length of window
		int mymin = myWidth < this.getHeight() ? myWidth : this.getHeight();
		myScale = mymin/(size + 2); // compute the scale of player grid
		my_start_axis = myScale; // start position should be one scale
		
		// compute the opponent grid
		int opWidth = this.getWidth() - 2 * my_start_axis - myScale * size;
		// match the smaller length of window
		int opmin = opWidth < this.getHeight() ? opWidth : this.getHeight();
		opScale = opmin/(size + 1); // compute the scale
		op_start_axis = 2 * my_start_axis + myScale * size; // start after players grid
		// drawn and one scale more
	}
	
	/**
	 * Draw the grid, according to scale and start position
	 * @param g: Graphics2D paint
	 * @param scale: the scale of grid, either opponent or player's
	 * @param startX: start X position
	 * @param startY: start Y position
	 */
	private void drawGrid(Graphics2D g, int scale, int startX, int startY) {
		// set the grid color and stroke
		g.setColor(Color.ORANGE);
		g.setStroke(new BasicStroke(3.0f));
		// draw vertical lines
		for(int i = startX; i <= startX + scale * size; i += scale) {
			// y is the start point and end point
			g.drawLine(i, startY, i, startY + scale * size);
		}
		// draw horizontal lines
		for(int i = startY; i <= startY + scale * size; i += scale) {
			// x is the start point and the end point
			g.drawLine(startX, i, startX + scale * size, i);
		}
	}
	
	/**
	 * Draw static: including who is the mocing, timer, and scores
	 * @param g: Graphics2D paint
	 */
	private void drawStatic(Graphics2D g) {
		// set the grid color and stroke
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(3.0f));
		g.setFont(new Font("TimesRoman", Font.PLAIN, 20)); // set the font
		
		//compute the start position
		int startX = my_start_axis;
		int startY = 2 * my_start_axis + myScale * size;
		
		// check who is moving
		// clicking is lock, is the turn of opponent
		if(click_lock)
			g.drawString("Waiting for Opponent", startX, startY);
		// other wise the turn of player
		else
			g.drawString("Remaining Time: " + timer, startX, startY);
		
		// draw the score
		g.drawString("My score: " + myScore, startX, startY + myScale);
		g.drawString("Opponent score: " + opScore, startX, startY + 2 * myScale);
		
	}
	
	
	/** 
	 * Loop through the grid, draw out each grid is target or not, where
	 * target = 2, miss = 1, nothing = 0
	 * @param g: Graphics2D paint
	 * @param row: the row of grid
	 * @param col: the col of grid
	 * @param op: determine if drawing opponent or player grid
	 */
	private void drawTarget(Graphics2D g, int row, int col, boolean op) {
		int X, Y; // draw position
		int scale; // the scale of the grid
		Image img = null; // record the image
		// draw on opponent grid
		if(op) {
			// compute the start position
			X = op_start_axis + col * opScale;
			Y = my_start_axis + row * opScale;
			scale = opScale;
			// miss drawing
			if(opponentGrid[row][col] == 1) img = missImg;
			else if(opponentGrid[row][col] == 2) img = targetImg; // target drawing
		}
		// draw on my grid
		else {
			// compute the start position
			X = my_start_axis + col * myScale;
			Y = my_start_axis + row * myScale;
			scale = myScale;
			// missing draw
			if(myfireGrid[row][col] == 1) img = missImg; // miss drawing
			else if(myfireGrid[row][col] == 2) img = targetImg; // target drawing
		}
		
		// draw by the elements computed
		g.drawImage(img, X, Y, scale, scale, null);
	}
	
	/**
	 * Draw the ships
	 * every time ship is draw, renew the parameters
	 * @param g Graphics2D
	 * @param ship: the ship would be drawn
	 * @param op: the ship is opponent(false) or player(true)
	 */
	private void drawShip(Graphics2D g, Ship ship, boolean op) {
		Image shipImage = ship.getImage(); // get the image of ship
		int shipX, shipY;
		int width, length;
		// if draw opponent's ship
		if(op) {
			// compute the start position and width and length
			shipX = op_start_axis + ship.col * opScale;
			shipY = my_start_axis + ship.row * opScale;
			width = ship.scale_width * opScale;
			length = ship.scale_length * opScale;
		}else {
			// compute the start position and width and length
			shipX = my_start_axis + ship.col * myScale;
			shipY = my_start_axis + ship.row * myScale;
			width = ship.scale_width * myScale;
			length = ship.scale_length * myScale;
		}
		// draw out the ship
		g.drawImage(shipImage, shipX, shipY, width, length, null);
	}
	
	/**
	 * Add the ship of opponent: if the ship is destroy, this method would be call
	 * to add the ship to the container
	 * @param shipInfo: the information of ship destroy
	 */
	public void addOpponentShip(String shipInfo) {
		// decode the information of the ship and get the clone of the ship
		Ship ship = DataParser.decodeShip(shipInfo);
		opShips.add(ship); // add the clone ship to the container
		repaint(); // repaint
	}
	

	

	
	
	public void unlock() {
		System.out.println("unlocked");
		click_lock = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("get click");
		// clikc lock, can't click
		if(click_lock) return;
		int X = e.getX();
		int Y = e.getY();
		// check if click in the grid
		if(X > op_start_axis && X < op_start_axis + opScale * size &&
			Y > my_start_axis && Y < my_start_axis + opScale * size) {
			// compute the row and col
			int row = (Y - my_start_axis)/opScale;
			int col = (X - op_start_axis)/opScale;
			// the grid has been clicked
			if(opponentGrid[row][col] != 0) return;
			this.rowBuffer = row;
			this.colBuffer = col;
			frame.sendShoot(row, col);
			// lock again
			click_lock = true;
			repaint();
		}
	}
	
	/**
	 * Write the buffer, where the opponent cliking
	 * @param t: target or miss. where target = 2, miss = 1, nothing = 0
	 */
	public void writeBuffer(int t) {
		assert(rowBuffer != -1); // assertion for test, init as -1
		assert(colBuffer != -1); // assertion for test, init as -1
		opponentGrid[rowBuffer][colBuffer] = t; // record the buffer to grid
		rowBuffer = -1; // clean the buffer, turn to -1
		colBuffer = -1; // clean the buffer, turn to -1
		repaint();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
// Unit testing for this panel
//	public static void main(String args[]) {
//	JFrame frame = new JFrame();
//	frame.setSize(1024, 768);
//	int ships[] = {1,1,1,1,1};
//	Deployment d = new Deployment(10, ships);
//	frame.add(d);
//	frame.setVisible(true);
//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	
//	Deployment.start.addMouseListener(new MouseListener() {
//		@Override
//		public void mouseClicked(MouseEvent e) {
//			if(e.getSource() == Deployment.start) {
//				Grid grid = d.getGrid();
//				BattleGround ground = new BattleGround(grid);
//				frame.remove(d);
//				frame.add(ground);
//				frame.setVisible(true);
//			}
//		}
//		@Override
//		public void mouseReleased(MouseEvent e) {}
//		@Override
//		public void mousePressed(MouseEvent e) {}
//		@Override
//		public void mouseExited(MouseEvent e) {}
//		@Override
//		public void mouseEntered(MouseEvent e) {}
//	});
//}
}
