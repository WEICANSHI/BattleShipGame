package BattleShip;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Login in page, allow user to create room or add a room of specific dns
 *
 */
@SuppressWarnings("serial")
public class Login extends JPanel{
	private BufferedImage img = null;	// imagebuffer
	private Image image = null; // image for login page
	private ImageObserver imageObserver = null;//observer
	
	protected JButton create = new JButton("Create New Room");
	protected JTextField dns = new JTextField(10);
	protected JButton connect = new JButton("Connect");
	
	/**
	 * Contructor: the create the login in page
	 */
	public Login() {
		// load the image
		try {
			img = ImageIO.read(new File("Image/page.png"));
		} catch (IOException e) {
			System.out.println("Image Load Fail");
		}
		//get the imageIcon
		ImageIcon icon = new ImageIcon(img);
		image = icon.getImage();
		imageObserver = icon.getImageObserver();
		
		
	}
	
	/**
	 * paint the image and the path
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), imageObserver);
		// draw the image
		this.setLayout(null);
		// set the location of each button
		create.setBounds(this.getWidth()/3, this.getHeight() - this.getHeight()/3, this.getWidth()/3, this.getHeight()/10);
		dns.setBounds(this.getWidth()/4, this.getHeight() - this.getHeight()/5, this.getWidth()/3, this.getHeight()/10);
		connect.setBounds(this.getWidth()/4 + this.getWidth()/3 + this.getWidth()/40, 
				this.getHeight() - this.getHeight()/5, this.getWidth()/6, this.getHeight()/10);
		
		// set the size of word
		Font f = new Font(Font.SERIF,Font.BOLD + Font.ITALIC, 30);
		create.setFont(f);
		dns.setFont(f);
		connect.setFont(f);
		// add the button to the panel
		this.add(create);
		this.add(dns);
		this.add(connect);
	}
}
