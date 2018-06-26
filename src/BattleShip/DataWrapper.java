package BattleShip;

/**
 * DataWrapper: encode the data to a particular string or int
 * 
 */
public class DataWrapper {
	/**
	 * Send the basic information
	 * @param size: the size of grid
	 * @param timer: the timer set by server
	 * @return base: consist of grid size and timer, encode as String(size + timer/100)
	 */
	public static String sendBase(int size, int timer) {
		double Size = size;
		double Timer = timer;
		return Size + Timer/100 + "0";
	}
	
	/**
	 * Send the shooting position click by player
	 * @param row: the row of the grid
	 * @param col: the col of the grid
	 * @return string(row + ", " + col);
	 */
	public static String sendShoot(Integer row, Integer col) {
		return row + ", " + col;
	}
	
	/**
	 * Encode the ship to a string with format 
	 * Shipsize + ", " + row + ", " + col + ", " + dir;
	 * @param ship: the ship to encode
	 * @return a string for the ship
	 */
	public static String DeterShip(Ship ship) {
		// get the lenght of ship
		int shipSize = ship.scale_length > ship.scale_width ?
				ship.scale_length : ship.scale_width;
		int row = ship.row; // get the position of ship
		int col = ship.col;
		int dir = ship.dir; // get the direction of ship
		// encode the submarine to 30, which has the same size of Cursier
		if(shipSize == 3) {
			if(ship instanceof Submarine) shipSize = 30;
		}
		
		return shipSize + ", " + row + ", " + col + ", " + dir;
	}
}
