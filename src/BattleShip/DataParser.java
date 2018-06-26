package BattleShip;

import java.awt.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * 
 * DataParser: read Image and decode the information send by another end
 *
 */
public class DataParser {
	/**
	 * read the basic information
	 * @param base: consist of grid size and timer, encode as String(size + timer/100)
	 * @return the double value of size + timer/100
	 */
	public static double readBase(String base) {
		double size_timer = Double.parseDouble(base);
		return size_timer;
	}
	
	/**
	 * Read the image by the file name provided
	 * @param fileName: the image file name
	 * @return the image read
	 * @throws RuntimeException if imag can't load
	 */
	public static Image readImage(String fileName) {
		BufferedImage img = null;	// image buffer
		Image image = null; // Image init as null
		try {
			img = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			throw new RuntimeException("Image Load Fail");
		}
		//get the imageIcon
		ImageIcon icon = new ImageIcon(img);
		image = icon.getImage();
		return image;
	}
	
	/**
	 * Read the image by the file provided
	 * @param file: the image file
	 * @return the image read
	 * @throws RuntimeException if imag can't load
	 */
	public static Image readImage(File file) {
		BufferedImage img = null;	// image
		Image image = null;// Image init as null
		try {
			img = ImageIO.read(file);
		} catch (IOException e) {
			throw new RuntimeException("Image Load Fail");
		}
		//get the imageIcon
		ImageIcon icon = new ImageIcon(img);
		image = icon.getImage();
		return image;
	}
	
	
	/**
	 * Recieve the shoot by opponent, encoded as String(row, col)
	 * @param shot: the string type of shot row and col, format: "row, col"
	 * @param grid: my grid, use for checking miss or target
	 * @return "Invalid" if the format or number is invalid, "Miss" if no shot, "Target" if get shot at a ship
	 */
	public static String recieveShot(String shot, Grid grid) {
		// init the value, which is invalid
		int row = -1;	int col = -1;
		try {
			// read and parse the data
			// get index of ','
			int split = shot.indexOf(",");
			if(split == -1) return "can't split";
			String str_row = shot.substring(0, split).trim();
			String str_col = shot.substring(split + 1).trim();
			// parse the integer
			row = Integer.parseInt(str_row);
			col = Integer.parseInt(str_col);
		}catch(NumberFormatException e) {
			return "not a number"; // the string indicate a invalid data
		}
		
		//check if the number is out of bound
		if(row < 0 || col < 0 || row >= grid.getSize() || col >= grid.getSize())
			return "index out of bound";
		
		// valid, call the grid to check if miss or shoot
		return grid.shot(row, col);
	}
	
	/**
	 * Decode the ship and create a clone ship by information provided
	 * encoded as "Destroy, " + type + ", " + row + ", " + col + ", " + dir;
	 * @param shipInfo: the information of the ship
	 * @return a clone ship of the opponent's
	 */
	public static Ship decodeShip(String shipInfo) {
		Ship ship = null;
		
		// header is the 'Destroy'
		int split = shipInfo.indexOf(",");
		// remove header and first ','
		shipInfo = shipInfo.substring(split + 1).trim();
		
		// get next ','
		split = shipInfo.indexOf(",");
		// get the shipSize and remove shipSize and ','
		int shipSize = Integer.parseInt(shipInfo.substring(0, split).trim());
		shipInfo = shipInfo.substring(split + 1).trim();
		
		// get next ','
		split = shipInfo.indexOf(",");
		// get the row and remove row and ','
		int row = Integer.parseInt(shipInfo.substring(0, split).trim());
		shipInfo = shipInfo.substring(split + 1).trim();
		
		// get next ','
		split = shipInfo.indexOf(","); 
		// get the col and remove col and ','
		int col = Integer.parseInt(shipInfo.substring(0, split).trim());
		shipInfo = shipInfo.substring(split + 1).trim();
		
		// the left should be direction
		int dir = Integer.parseInt(shipInfo.trim());
		
		// destroyer with size 2
		if(shipSize == 2) {
			ship = new Destroyer();
		}else if(shipSize == 3) {
			ship = new Cruiser(); // Submarine and Cruiser with size 3, but encode the Submarine
			// with size 30
		}else if(shipSize == 4) {
			ship = new BattleShip(); // BattleShip with size 4
		}else if(shipSize == 5) {
			ship = new Carrier(); // Carrier with size 3
		}else if(shipSize == 30) {
			ship = new Submarine();// Submarine with size 3 but encode as 30
			shipSize = 3; // decode the 30 to 3
		}
		
		//check if the ship is still null (invalid ship)
		if(ship != null) {
			ship.dir = dir; // get the direction of the ship
			// vertical, length > width
			if(dir == 1) {
				ship.scale_length = shipSize;
				ship.scale_width = 1;
			}
			// horizontal, length < width
			else {
				ship.scale_length = 1;
				ship.scale_width = shipSize;
			}
			ship.col = col; // record the col
			ship.row = row; // record the row 
		}
		
		return ship;
	}
	
	
	// Unit test for the parser
//	public static void main(String args[]) {
//		String test = "Destroy, " + 30 + ", " + 3 + ", " + 2 + ", " + 1;
//		Ship ship = decodeShip(test);
//		System.out.println(ship.row);
//		System.out.println(ship.col);
//		System.out.println(ship.scale_length);
//		System.out.println(ship.dir);
//		
//		String encode = DataWrapper.DeterShip(ship);
//		System.out.println(encode);
//	}
	
}
