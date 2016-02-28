package co.kukurin.main;

import co.kukurin.gui.MainWindow;
import co.kukurin.utils.SwingUtils;

/**
 * Program entry point; simply calls the {@link MainWindow} class.
 * @author Toni Kukurin
 *
 */
public class Main {
	
	/**
	 * @param args Not used.
	 */
	public static void main(String[] args) {
		SwingUtils.doStartup(MainWindow.class);
	}

}
