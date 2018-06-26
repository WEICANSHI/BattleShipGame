package BattleShip;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Deployment stage: Enable deploy the ship to the grid
 *
 */
@SuppressWarnings("serial")
public class Deployment extends JPanel implements MouseListener, MouseMotionListener, KeyListener{
	protected boolean done = false; // indicate the deployment finished or not
	protected static JButton start = new JButton("Start ! ! ! "); // the start button
	
	private Image window = null; // window for ship pop out
	private Grid grid; // which use to assign the grid and deploy ship
	private int size; // the size/number of square of the grid
	private int scale; // the size of each square
	private int start_point; // the start x and y axis
	// store the current ships deploy on the grid
	private ArrayList<Ship> ships = new ArrayList<Ship>();
	//use to record the current mouse position
	private int mouseX;
	private int mouseY;
	private Ship currentShip; // the ship in the window, has not been deploy yet
	private Ship chosedShip; //deployed ship, click to move the position
	private int current_index;
	private boolean focus = false; // clicked on a ship
	
	private JButton vertical = new JButton("VERTICAL");
	private JButton horizontal = new JButton("HORIZONTAL");
	
	/**
	 * Constructor: Construct the grid and ships
	 * @param size: the size of the grid
	 * @param list: the list of ships, the carrier, battleship, cruiser, submarine, destroyer
	 * with correspoding index
	 */
	public Deployment(int size, int[] list) {
		// load the window Image
		window =  DataParser.readImage("Image/window.png");
		this.size = size;	// according to the size, generate grids
		grid = new Grid(size); // init the grid
		grid.deployShip(list); // deploy the ship
		currentShip = grid.getShips(current_index); // current ship init to the first one
		
		// add button for rotating the ship
		this.add(vertical);
		this.add(horizontal);
		// configuration of button
		vertical.setVisible(false);
		horizontal.setVisible(false);
		
		// add listener
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		vertical.addMouseListener(this);
		horizontal.addMouseListener(this);
		this.setFocusable(true); // the focus for key listener
	}
	
	/**
	 * Paint the components: ships, grid, window
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		computeScale(); // compute the scale and starting point
		// if deployment is done, show the start button
		if(done) {
			// set the location of button
			start.setBounds(this.getWidth()*2/3 + scale, start_point, 
							this.getWidth()/5, this.getHeight()/10);
			Font f = new Font(Font.SERIF,Font.BOLD + Font.ITALIC, 30);
			start.setFont(f);
			this.add(start);
		}
		// paint the back ground
		g.drawImage(MainFrame.background, 0, 0, this.getWidth(), this.getHeight(), null);
		
		
		Graphics2D g2 = (Graphics2D) g;
		drawGrid(g2);	// draw the grid
		
		// draw all the ship deployed except the one focus one
		for(int i = 0; i < ships.size(); i++) {
			Ship thisShip = ships.get(i);
			// don't draw the chosed ship
			if(chosedShip != thisShip) {
				drawDeploedShip(g2, thisShip);
			}
		}
		
		// if the chosed one is from the window, don't draw the window
		if(!done && chosedShip != currentShip) {
			drawWindow(g2);
		}else {
			// don't show the button
			vertical.setVisible(false);
			horizontal.setVisible(false);
		}
		// draw out the chosed ship if is clicked on it 
		if(focus) {
			// adjust the ship to the center of mouse
			int adjustedX = adjustX();
			int adjustedY = adjustY(chosedShip);
			drawShip(g2, chosedShip, adjustedX, adjustedY, scale);
		}
	}
	
	/**
	 * Compute the scale of the grid
	 */
	private void computeScale() {
		// get the smaller length
		int min = this.getHeight() < this.getWidth() * 2 / 3 ? this.getHeight() : this.getWidth() * 2 / 3;
		// compute the scale and starting point
		scale = min/(size + 1);
		start_point = scale/2;
	}
	
	/**
	 * Draw the grids
	 * @param g
	 */
	private void drawGrid(Graphics2D g) {
		// set the grid color and stroke
		g.setColor(Color.ORANGE);
		g.setStroke(new BasicStroke(3.0f));
		// draw vertical lines
		for(int i = start_point; i <= start_point + scale * size; i += scale) {
			// y is the start point and end point
			g.drawLine(i, start_point, i, start_point + scale * size);
		}
		// draw horizontal lines
		for(int i = start_point; i <= start_point + scale * size; i += scale) {
			// x is the start point and the end point
			g.drawLine(start_point, i, start_point + scale * size, i);
		}
	}
	
	/**
	 * Draw the window where new ships pop up
	 * @param g: Graphics2D paint
	 */
	private void drawWindow(Graphics2D g) {
		// compute the lefting area for drawing the window
		int LeftX = this.getWidth() - (start_point + scale * size);
		// window should fit the 3/4 of smaller length
		int windowSize = LeftX < this.getHeight() ? LeftX : this.getHeight();
		windowSize = windowSize * 3/4;
		// compute the starting position of window		
		int windowX = start_point + scale * size + LeftX/8;
		int windowY = start_point + scale * size - windowSize;
		// compute the scale in the window
		int windowScale = windowSize/5;
		// draw out the window
		g.drawImage(window, windowX, windowY, windowSize, windowSize, null);
		
		// draw when ship is vertical
		if(currentShip.dir == 1) 
			drawShip(g, currentShip, windowX + 2 * windowScale, windowY + (5 - currentShip.scale_length)/2 * windowScale, windowScale);
		// draw ship is horizontal
		else {
			drawShip(g, currentShip, windowX + (5 - currentShip.scale_width)/2 * windowScale, windowY + 2 * windowScale, windowScale);
		}
		
		// add the button to the window
		vertical.setBounds(windowX, windowY - windowScale, windowSize/2, windowScale);
		vertical.setVisible(true);
		horizontal.setBounds(windowX + windowSize/2, windowY - windowScale, windowSize/2, windowScale);
		horizontal.setVisible(true);
	}
	
	/**
	 * Draw the ship by x and y provide. every time ship is draw, renew the parameters
	 * @param g: Graphics2D paint
	 * @param ship: the ship to draw
	 * @param shipX: the X of ship
	 * @param shipY: the Y of ship
	 * @param scale: the scale
	 */
	private void drawShip(Graphics2D g, Ship ship, int shipX, int shipY, int scale) {
		Image shipImage = ship.getImage(); // get the image of ship
		int width = ship.scale_width * scale;	// get the actual width of ship
		int length = ship.scale_length * scale; // get the actual length of ship
		ship.setAxisLen(width, length); // renew the actual width and length
		ship.setAixsPos(shipX, shipY); // renew the actual head position
		g.drawImage(shipImage, shipX, shipY, width, length, null);
	}
	
	/**
	 * Draw the deployed ship, where should draw according to row and col of the ship head
	 * @param g: Graphics2D paint
	 * @param ship: the ship to draw
	 */
	private void drawDeploedShip(Graphics2D g, Ship ship) {
		int shipX = start_point + scale * ship.col;
		int shipY = start_point + scale * ship.row;
		drawShip(g, ship, shipX, shipY, scale);
	}
	
	/**
	 * The adjust X, ensure the mouse point to the bottom center of the ship
	 * @return the X ajusted
	 */
	private int adjustX() {
		return mouseX - scale/2;
	}
	
	/**
	 * The adjust Y, ensure the mouse point to the bottom center of the ship
	 * @return the Y ajusted
	 */
	private int adjustY(Ship ship) {
		return mouseY - ship.axis_length + scale/2;
	}
	
	/**
	 * Click on the ship, then the ship would be moved
	 * click on the button, according function would make
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// click the vertical button, rotate ship to vertical
		if(e.getSource() == this.vertical) {
			currentShip.rotate(1); // call out the rotate button
			repaint(); // repaint
			return; // stop following execute
		}// click the vertical button, rotate ship to horizontal
		else if(e.getSource() == this.horizontal) {
			currentShip.rotate(0);// call out the rotate button
			repaint();// repaint
			return;// stop following execute
		}
		
		// not click on the button, check if is click on the ship
		int mouseX = e.getX();
		int mouseY = e.getY();
		
		// if a ship is already been chosed, clicked it again,
		// deployed the ship
		if(focus) {
			deploy(mouseX, mouseY);
			repaint();
		}
		
		// if no ship is focus, then focus the ship
		else {
			// check if focus ship is in window
			if(!done && currentShip.clicked(mouseX, mouseY)) {
				chosedShip = currentShip;
				focus = true;
				this.mouseX = mouseX;
				this.mouseY = mouseY;
				repaint();
			}
			// check if focus ship is deployed
			for(int i = 0; i < ships.size(); i++) {
				if(ships.get(i).clicked(mouseX, mouseY)) {
					//System.out.println("ship " + i + "choosed");
					chosedShip = ships.get(i);
					focus = true;
					this.mouseX = mouseX;
					this.mouseY = mouseY;
					repaint();
				}
			}
		}
		
	}
	
	/**
	 * Deploy the ship where link on the mouse
	 * @param mouseX: the mouse X position
	 * @param mouseY: the mouse Y position
	 */
	private void deploy(int mouseX, int mouseY) {
		// make sure the chosed ship not null
		if(chosedShip == null) return;
		int mousecol = (mouseX - start_point)/scale;
		int mouserow = (mouseY - start_point)/scale;
		// check if the ship is in the grid
		if(mousecol >= 0 && mousecol + chosedShip.scale_width - 1 < size &&
			mouserow - chosedShip.scale_length + 1 >= 0 && mouserow < size) {
			
			// upper row, lower row, left col, right col
			boolean ret = checkCollide(mouserow - chosedShip.scale_length + 1, mouserow, mousecol, 
										mousecol + chosedShip.scale_width - 1);
			if(ret) return;
			// when deploy, set the row and col of the ship
			chosedShip.setScalePos(mouserow - chosedShip.scale_length + 1, mousecol, scale, start_point);
	
			if(chosedShip == currentShip && !done) {
				ships.add(currentShip);
				current_index ++;
				if( current_index < grid.num_ship ) {
					currentShip = grid.getShips(current_index);
				}else {
					done = true;
					currentShip = null;
				}

			}
			focus = false;
			chosedShip = null;
			repaint();
		}
	}
	
	/**
	 * Check if tow ships is overlap
	 * @param urow: upper row of the ship
	 * @param lrow: lower row of the ship
	 * @param lcol: left col
	 * @param rcol: right col
	 * @return true if ship is overlap, false otherwise
	 */
	private boolean checkCollide(int urow, int lrow, int lcol, int rcol) {
		// debug assert
		assert(urow <= lrow); 
		assert(lcol <= rcol);
		// check if ship is at certain position
		for(int i = 0; i < ships.size(); i++) {
			Ship thisShip = ships.get(i);
			if(thisShip == chosedShip) continue;
			// the max row smaller than the min row, then not overlap
			int start_row = thisShip.row - 1 >= 0 ? thisShip.row - 1 : thisShip.row;
			int end_row = thisShip.row + thisShip.scale_length < size ? 
							thisShip.row + thisShip.scale_length : thisShip.row - 1 + thisShip.scale_length;
			// the max col smaller than the min col, then not overlap
			int start_col = thisShip.col - 1 >= 0 ? thisShip.col - 1 : thisShip.col;
			int end_col = thisShip.col + thisShip.scale_width < size ? 
					thisShip.col + thisShip.scale_width : thisShip.col - 1 + thisShip.scale_width;
			
			// assertion for debug
			assert(start_row <= end_row);
			assert(start_col <= end_col);
			
			// no collide in this case
			if(lrow < start_row || end_row < urow || rcol < start_col || end_col < lcol) {
				continue;
			}else {
				// left are collide
				return true;
			}
		}
		return false;
	}
	
	@Override
	/**
	 * The ship should attach to the mouse when it be clicked
	 */
	public void mouseMoved(MouseEvent e) {
		// get the position of the mouse
		int mouseX = e.getX();
		int mouseY = e.getY();
		// if clicked on the ship
		if(focus) {
			this.mouseX = mouseX;
			this.mouseY = mouseY;
			repaint();
		}
	}
	
	@Override
	/**
	 * Press of key bord, change the direction of the ship
	 * if the ship is chosed
	 */
	public void keyPressed(KeyEvent e) {
		// if a ship is selected
		if(focus) {
			// turn to vertical
			if(e.getKeyCode() == KeyEvent.VK_UP) {
				chosedShip.rotate(1);
			}
			// turn to horizontal
			else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
				chosedShip.rotate(0);
			}
			repaint();
		}
	}
	
	/**
	 * Getter: simply get the grid of player deployment
	 * @return the grid
	 */
	public Grid getGrid() {
		return grid;
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


	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	// Unit test for the deployment
//	public static void main(String args[]) {
//		JFrame frame = new JFrame();
//		frame.setSize(1024, 768);
//		int ships[] = {1,1,1,1,1};
//		Deployment d = new Deployment(10, ships);
//		frame.add(d);
//		frame.setVisible(true);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	}
	
}
