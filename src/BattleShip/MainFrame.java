package BattleShip;


import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


enum Sys{Server, Client} // determine whether is server or client

/**
 * Main Frame, add or remove component for represent stages
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements MouseListener{
	protected Login login; // login page
	protected Setting setting; // setting page
	protected Deployment deploy; // deployment page
	protected BattleGround ground; // battle page
	protected JFrame pop; // pop out infor
	protected JButton ok; // okbutton
	protected static Image background = DataParser.readImage("Image/sea.jpg"); //default background
	
	// drop down menu, enable change the background
	private JMenuBar menubar = new JMenuBar(); // menubar at the left coner
	private JMenu option = new JMenu("Setting"); // Name of the name
	private JMenuItem advance = new JMenuItem("Back Ground Theme : Read input file"); // item in the menue
	
	// file chooser
	private JFileChooser chooser = new JFileChooser(".\\Theme");
	
	// type of system
	protected Sys type;
	protected EndSys sys;
	
	protected int GridSize = 10; //default grid size
	protected int Timer = 30; // default timer
	
	public MainFrame() {
		login = new Login(); // start with login page
		this.add(login);
		// configuration
		this.setVisible(true);
		this.setSize(1024, 768);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// listener
		login.create.addMouseListener(this);
		login.connect.addMouseListener(this);

		option.add(advance);
		menubar.add(option);
		
		
		// add listener to the menue item
		advance.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// advance for read input file, lunch the file chooser
				if(e.getSource() == advance) {
					// show file chooser
					chooser.showOpenDialog(null);
					// input select file
			        File file=chooser.getSelectedFile();
			        if(file != null) {
			        	try {
			        		background = DataParser.readImage(file);
			        		if(deploy != null) {
			        			deploy.repaint();
			        		}
			        	}catch(RuntimeException e1) {
			        		background = DataParser.readImage("Image/sea.jpg");
			        	}
			        }
				}
			}
		});
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		// Setting page, set background...
		if(arg0.getSource() == login.create) {
			this.remove(login); // remove the login but show the setting page
			type = Sys.Server;
			setting = new Setting();
			setting.ready.addMouseListener(this);
			this.add(setting);
			this.setVisible(true);
			
		}
		// click connect act as server
		else if(arg0.getSource() == login.connect) {
			String dns = login.dns.getText(); // init the dns
			type = Sys.Client; // the type of system
			sys = new GameClient(this, dns);
			new Thread((GameClient) sys).start();
			
		}
		// ready for battle
		else if(setting != null && arg0.getSource() == setting.ready) {
			GridSize = (int) setting.box.getSelectedItem(); // get the suze of grid
			Timer = (int) setting.times.getSelectedItem(); // get the timer
			// start the server connection
			sys = new GameServer(this);
			new Thread((GameServer) sys).start();
		}else if(arg0.getSource() == Deployment.start) {
			// start the battle
			if(sys instanceof GameServer) {
				((GameServer) sys).start = "Ready for battle";
			}
			else if(sys instanceof GameClient) {
				((GameClient) sys).start = "Ready for battle";
			}
		}
	}
	
	/**
	 * Step to deploy stage
	 */
	public void startDeploy() {
		// remove the prrevious component
		if(this.setting != null) this.remove(this.setting);
		if(this.login != null) this.remove(this.login);
		
		// ships in the grid
		int ships[] = {1,1,1,1,1};
		// deploy the ships
		this.deploy = new Deployment(GridSize, ships);
		this.add(this.deploy);
		this.setVisible(true);
		Deployment.start.addMouseListener(this);
		this.setJMenuBar(menubar);
	}
	
	/**
	 * Step to battle ground: start the battle
	 */
	public void startBattle() {
		menubar.setVisible(false); // invisible the menu bar
		Grid grid = deploy.getGrid(); // deply battle ground
		this.remove(this.deploy); // remove previous panel
		ground = new BattleGround(grid, this);
		this.add(ground);
		this.setVisible(true);
	}
	
	/**
	 * Send the shooting to another end
	 * @param row: shoot row
	 * @param col: shoot col
	 */
	public void sendShoot(Integer row, Integer col) {
		// encode the shooting
		String shootTarget = DataWrapper.sendShoot(row, col);
		// either send to  server or client
		if(sys instanceof GameServer) {
			((GameServer) sys).shootTarget = shootTarget;
		}
		else if(sys instanceof GameClient) {
			((GameClient) sys).shootTarget = shootTarget;
		}
	}
	
	/**
	 * Reload the game, start with the login page
	 */
	public void reload() {
		this.remove(ground); // remove previous ground
		login = new Login();
		this.add(login);
		this.setVisible(true);
		// add listener
		login.create.addMouseListener(this);
		login.connect.addMouseListener(this);
	}
	
	/**
	 * Pop up window show the result, either win or loss
	 * @param win: determine win or loss
	 */
	public void endInfo(boolean win) {
		pop = new JFrame(); // init a new frame
		JLabel lb = null;
		if(win) {
			lb = new JLabel("YOU WIN");
		}else {
			lb = new JLabel("YOU LOSS");
		}
		ok = new JButton("OK");
		// add listener to the button
		ok.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getSource() == ok) {
					pop.dispose();
					reload();
					sys = null;
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
		});
		// set lay out and show the frame
		pop.setLayout(new GridLayout(2, 1));
		pop.add(ok);
		pop.add(lb);
		pop.setVisible(true);
		pop.setSize(400, 300);
		pop.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {}


	@Override
	public void mouseExited(MouseEvent arg0) {}


	@Override
	public void mousePressed(MouseEvent arg0) {}


	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	
	@SuppressWarnings("unused")
	public static void main(String args[]) {
		MainFrame page = new MainFrame();
	}
}
