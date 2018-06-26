package BattleShip;

/**
 * 
 * Carrier: where length is 5, width is 1
 * Load the picture of vertical and horizontal
 *
 */
public class Carrier extends Ship{
	/**
	 * Constructor: create a Carrier, where length is 5, width is 1
	 * and load the picture of Carrier
	 */
	public Carrier() {
		this.scale_length = 5;
		this.scale_width = 1;
		this.verticalImage = DataParser.readImage("Image/Carrier.png");
		this.horizonImage = DataParser.readImage("Image/Carrierh.png");
	}
 
}
