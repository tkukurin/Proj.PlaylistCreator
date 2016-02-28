package co.kukurin.utils.layout;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Static access class containing some general Swing utility methods.
 * 
 * @author Toni Kukurin
 *
 */
public class SwingUtils {
	
	/**
	 * Default program-wide margin.
	 */
	private static final int DEFAULT_MARGIN = 8;

	/**
	 * Ensures static access.
	 */
	private SwingUtils() {}

	/**
	 * Set up default Windows look and feel if available;
	 * otherwise, ignore and do nothing.
	 */
	public static void setWindowsLookAndFeel() {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception ignore) {}
	}
	
	/**
	 * Frame startup.
	 * 
	 * @param c Class being instantiated
	 */
	public static void doStartup(Class<? extends JFrame> c) {
		SwingUtilities.invokeLater(() -> {
			try {
				c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Instances frame basics
	 * @param mainView Frame which has been created.
	 */
	public static void instanceDefaults(JFrame mainView) {
		mainView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainView.pack();
		mainView.setVisible(true);
	}
	
	/**
	 * @param mainView
	 * @param preferredWidth Frame preferred width.
	 * @param preferredHeight Frame preferred height.
	 */
	public static void instanceDefaults(JFrame mainView, int preferredWidth, int preferredHeight) {
		mainView.getContentPane().setPreferredSize(new Dimension(preferredWidth, preferredHeight));
		instanceDefaults(mainView);
	}
	
	/**
	 * Initializes a default margin in provided directions.
	 * <p>
	 * Directions provided can be one of the TOP, LEFT, DOWN and BOTTOM
	 * constants from the {@link SwingConstants} interface.
	 * 
	 * @param directions Array of directions where the border is to be created.
	 * @return Empty border to be used as margin; border width defined as
	 * {@link #DEFAULT_MARGIN}.
	 */
	public static Border defaultMargin(int ... directions) {
		int[] borders = new int[4];
		if(directions == null) return BorderFactory.createEmptyBorder();
		
		for(int i = 0; i < directions.length; i++) {
			if(directions[i] == SwingConstants.TOP) borders[0] = DEFAULT_MARGIN;
			if(directions[i] == SwingConstants.LEFT) borders[1] = DEFAULT_MARGIN;
			if(directions[i] == SwingConstants.BOTTOM) borders[2] = DEFAULT_MARGIN;
			if(directions[i] == SwingConstants.RIGHT) borders[3] = DEFAULT_MARGIN;
		}
		
		return BorderFactory.createEmptyBorder(borders[0], borders[1], borders[2], borders[3]);
	}
	
	/**
	 * Default textfield design.
	 * 
	 * @param requireMinWidth Whether the textfield should have a default min. width set.
	 * @param contents Initial contents.
	 * @return
	 */
	public static JTextField defaultTextField(boolean requireMinWidth, String contents) {
		JTextField jtf = new JTextField(contents);
		
		if(requireMinWidth)
			jtf.setPreferredSize(new Dimension(400, jtf.getPreferredSize().height));
		
		jtf.setBorder(BorderFactory.createCompoundBorder(defaultLineBorder(),
				BorderFactory.createEmptyBorder(0, 5, 0, 0)));
		return jtf;
	}
	
	/**
	 * @return Default colored line border
	 */
	public static Border defaultLineBorder() {
		return BorderFactory.createLineBorder(Color.GRAY);
	}
	
	/**
	 * Creates and runs a new thread with given runnable.
	 * @param r
	 */
	public static void startupThread(Runnable r) {
		new Thread(r).start();
	}

}
