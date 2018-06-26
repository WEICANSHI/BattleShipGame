package BattleShip;

/**
 * Destroyer ship: where length is 2, width is 1
 * Load the picture of vertical and horizontal
 *
 */
public class Destroyer extends Ship{
	/**
	 * Constructor: create a Destroyer, where length is 4, width is 1
	 * and load the picture of battle ship
	 */
	public Destroyer() {
		this.scale_length = 2;
		this.scale_width = 1;
		this.verticalImage = DataParser.readImage("Image/Destroyer.png");
		this.horizonImage = DataParser.readImage("Image/Destroyerh.png");
	}
}
