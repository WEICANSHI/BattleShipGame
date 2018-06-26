package BattleShip;

/**
 * Cruiser ship: where length is 4, width is 1
 * Load the picture of vertical and horizontal
 *
 */
public class Cruiser extends Ship{
	/**
	 * Constructor: create a Cruiser ship, where length is 3, width is 1
	 * and load the picture of battle ship
	 */
	public Cruiser() {
		this.scale_length = 3;
		this.scale_width = 1;
		this.verticalImage = DataParser.readImage("Image/Cruiser.png");
		this.horizonImage = DataParser.readImage("Image/Cruiserh.png");
	}
}
