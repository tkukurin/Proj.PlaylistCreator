package co.kukurin.main;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import co.kukurin.gui.inputscreen.InputScreenWindow;
import co.kukurin.gui.main.MainWindow;
import co.kukurin.utils.Constants;
import co.kukurin.utils.PropertyManager;
import co.kukurin.utils.layout.SwingUtils;

/**
 * Program entry point; simply checks for properties and calls the {@link MainWindow} class.
 * @author Toni Kukurin
 *
 */
public class Main {
	
	/**
	 * @param args Not used.
	 */
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		SwingUtils.setWindowsLookAndFeel();
		File propertyFile = new File(Constants.PROPERTY_LOCATION);
		
		if(!propertyFile.exists()) {
			PropertyManager.createDefaultProperties();
			JOptionPane.showMessageDialog(null, "No properties file found!");
			
			SwingUtilities.invokeLater(() -> new InputScreenWindow(Main.class));
			synchronized(Main.class) {
				Main.class.wait();
			}
		}
		
		SwingUtils.doStartup(MainWindow.class);
	}

}
