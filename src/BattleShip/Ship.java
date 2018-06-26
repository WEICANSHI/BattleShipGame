package BattleShip;

import java.awt.Image;

/**
 * Represent the ship in the grid
 *
 */
public class Ship {
	public int axis_length; // actual length and width
	public int axis_width;
	public int X; // head X position of the ship
	public int Y; // head Y position of the ship
	
	public int row; // head row
	public int col; // head col
	public int scale_length; // number of scale it locate
	public int scale_width;
	
	public int damage; // the damage of the ship
	
	public int dir; // 0 represent the vertical, 1 represent the horizontal
	
	public Image verticalImage; // vertical and horizontal image
	public Image horizonImage;
	
	/**
	 * Init the dir as 1
	 */
	public Ship() {
		dir = 1;
	}
	
	/**
	 * 1 is vertical, 0 horizontal. Return corresponding image
	 * @return Image of the ship
	 */
	public Image getImage() {
		if(dir == 1) return verticalImage;
		else{
			assert(dir == 0);
			return horizonImage;
		}
	}
	
	/**
	 * Set the actual x and y position
	 * @param x: x position
	 * @param y: y position
	 */
	public void setAixsPos(int x, int y) {
		X = x;
		Y = y;
	}
	
	/**
	 * Set the actual length and width
	 * @param width
	 * @param length
	 */
	public void setAxisLen(int width, int length) {
		axis_width = width;
		axis_length = length;
	}
	
	/**
	 * Check if clicked on the ship
	 * @param x: the X position clicked by mouse
	 * @param y: the Y position clicked by mouse
	 * @return true if mouse clicked on this ship, false other wise
	 */
	public boolean clicked(int x, int y) {
		if(x > X && x < X + axis_width && y > Y && y < Y + axis_length)
			return true;
		else return false;
	}
	
	/**
	 * Set the position and edit the actual position
	 * @param row
	 * @param col
	 * @param scale
	 * @param start_point
	 */
	public void setScalePos(int row, int col, int scale, int start_point) {
		this.X = start_point + col * scale;
		this.Y = start_point + row * scale;
		this.col = col;
		this.row = row;
	}
	
	/**
	 * Rotate the ship to a certain direction
	 * @param dir: the direction of the ship to turn
	 */
	public void rotate(int dir) {
		// turn to the same direction, do nothing
		if(this.dir == dir) {
			return;
		}
		// assign the direction
		this.dir = dir;
		int tmp;
		// switch axis width and length
		tmp = axis_width;
		axis_width = axis_length;
		axis_length = axis_width;
		// switch scale length and width
		tmp = scale_length;
		scale_length = scale_width;
		scale_width	= tmp;
		// modify X and Y
		Y = Y - axis_width + axis_length;
		// modify col and length
		if(dir == 1) row = row + 1 - scale_length;
		if(dir == 0) row = row - 1 + scale_width;
	}
	
	/**
	 * shot the ship: check if shoot or not and if the ship is damage
	 * @param row the shot row
	 * @param col the shot col
	 * @return 1 if shot, 0 if miss, 2 if destroy
	 */
	public int shot(int row, int col) {
		// if shoot at the ship
		if(row >= this.row && row <= this.row + scale_length - 1 &&
				col >= this.col && col <= this.col + scale_width - 1) {
			damage++; // increase the damage
			int total_size = this.scale_length > this.scale_width ? this.scale_length : this.scale_width;
			if(damage == total_size) return 2; // if the ship is destroy
			System.out.println("I am going to return 1");
			return 1; // if miss
		}
		return 0;
	}
	
	
	//Unit testing
//	public static void main(String args[]) {
//		Ship ship = new Ship();
//		Ship tmp = ship;
//		if(ship == ship) {
//			System.out.println("yes");
//		}
//		ship.setAixsPos(100, 10);
//		if(tmp == ship) {
//			System.out.println("yes");
//		}
//	}
}
