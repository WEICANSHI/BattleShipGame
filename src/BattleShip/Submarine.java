package BattleShip;

/**
 * Submarine: where length is 3, width is 1
 * Load the picture of vertical and horizontal
 *
 */
public class Submarine extends Ship{
	/**
	 * Constructor: create a Submarine, where length is 3, width is 1
	 * and load the picture of battle ship
	 */
	public Submarine() {
		this.scale_length = 3;
		this.scale_width = 1;
		this.verticalImage = DataParser.readImage("Image/Submarine.png");
		this.horizonImage = DataParser.readImage("Image/Submarineh.png");
	}
}
