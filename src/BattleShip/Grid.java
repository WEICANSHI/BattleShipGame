package BattleShip;

import java.util.ArrayList;

/**
 * Grid record the ships and the fires
 */
public class Grid {
	private int size; // size of the grid
	private int[][]grid; // reocrd fire or miss
	private ArrayList<Ship> ships = new ArrayList<>(); // the ships in the grid
	public int num_ship; // the number of ships
	
	public Grid(int size) {
		this.size = size; // init the size of the grid
		grid = new int[size][size]; // init grid
	}
	
	/**
	 * Shooting to a position ,check if a ship is fired or not
	 * @param row: shoot row
	 * @param col: shoot col
	 * @return "Target" if shoot the ship, "Miss" if miss the shooting
	 */
	public String shot(int row, int col) {
		for(int i = 0; i < ships.size(); i++) {
			int ret = ships.get(i).shot(row, col); // check if the ship is shoot
			// return value 1, the ship is shoot
			if(ret == 1) {
				grid[row][col] = 2; // renew the ship grid
				return "Target";
			}else if(ret == 2) {
				grid[row][col] = 2;// renew the ship grid
				// the ship is destroy if ret = 2
				// write the destroied ship information
				String shipInfo = DataWrapper.DeterShip(ships.get(i));
				return "Destroy, " + shipInfo;
			}
		}
		// no ship is shoot, record miss
		grid[row][col] = 1;
		return "Miss";
	}
	
	/**
	 * Getter: get the fire grid
	 * @return grid
	 */
	public int[][] getfireGrid(){
		return grid;
	}
	
	/**
	 * Getter: get the size of the grid
	 * @return size
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Getter: get the ship at an index
	 * @param index: ship index
	 * @return the ship at the index
	 */
	public Ship getShips(int index){
		return ships.get(index);
	}
	
	/**
	 * requires ships.length == 5
	 * @param ships: a int list with number of ship at index
	 * when i=0 is Carrier, i=1 BattleShip, i=2 Cruiser
	 * i=3 Submarine, i=4 Destroyer
	 */
	public void deployShip(int[] ships) {
		for(int i = 0; i < ships.length; i++) {
			for(int j = 0; j < ships[i]; j++) {
				Ship ship = null;
				if(i == 0) {
					ship = new Carrier();
				}
				else if(i == 1) {
					ship = new BattleShip();
				}
				else if(i == 2) {
					ship = new Cruiser();
				}
				else if(i == 3) {
					ship = new Submarine();
				}
				else if(i == 4) {
					ship = new Destroyer();
				}
				this.ships.add(ship);
				num_ship ++; // increase the number of ship in the grid
			}
		}
	}
	
}
