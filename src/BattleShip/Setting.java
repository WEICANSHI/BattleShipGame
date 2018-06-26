package BattleShip;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Setting panel, for set grid size and timer
 *
 */
@SuppressWarnings("serial")
public class Setting extends JPanel{
	// start button
	protected JButton ready = new JButton("Ready");
	protected JComboBox<Integer> box; // box for grid size
	protected JLabel label = new JLabel("CHOOSE THE GRID SIZE        ");
	protected JLabel info;
	// box for timer can be selected
	protected JLabel choose_time = new JLabel("CHOOSE THE TIMER        ");
	protected JComboBox<Integer> times;
	protected JLabel unit = new JLabel("Second");
	
	/**
	 * Constructor: only the choice can be select
	 */
	public Setting() {
		Integer[] choice = {10,11,12,13,14,15,16,17,18,19,20};
		Integer[] choiceT = {10,20,30,40,50,60};
		box = new JComboBox<Integer>(choice);
		times = new JComboBox<Integer>(choiceT);
		// set the layout
		this.setLayout(new GridLayout(4,1));
		// set the combine Jpanel
		JPanel combine = new JPanel();
		combine.add(label);
		combine.add(box);
		// get another panel
		JPanel combine2 = new JPanel();
		combine2.add(choose_time);
		combine2.add(times);
		combine2.add(unit);
		// future information or setting can be added
		info = new JLabel("Future extension here----Future extension here-----Future extension here");
		this.add(combine);
		this.add(combine2);
		this.add(ready);
		this.add(info);
	}
	
	// Unit Testing
//	public static void main(String args[]) {
//		JFrame frame = new JFrame();
//		Setting setting = new Setting();
//		frame.add(setting);
//		frame.setVisible(true);
//		frame.setSize(1024, 768);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	}
}
