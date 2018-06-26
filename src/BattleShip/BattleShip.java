package BattleShip;

/**
 * Battle ship: where length is 4, width is 1
 * Load the picture of vertical and horizontal
 *
 */
public class BattleShip extends Ship{
	/**
	 * Constructor: create a battle ship, where length is 4, width is 1
	 * and load the picture of battle ship
	 */
	public BattleShip() {
		this.scale_length = 4;
		this.scale_width = 1;
		this.verticalImage = DataParser.readImage("Image/BattleShip.png");
		this.horizonImage = DataParser.readImage("Image/BattleShiph.png");
	}
}
