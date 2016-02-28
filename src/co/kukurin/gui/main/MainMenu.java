package co.kukurin.gui.main;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import co.kukurin.gui.inputscreen.InputScreenWindow;
import co.kukurin.utils.Constants;
import co.kukurin.utils.PropertyManager;

@SuppressWarnings("serial")
public class MainMenu {
	
	/**
	 * Static non-instantiable class
	 */
	private MainMenu() {}
	
	/**
	 * Helper class which automatically defines a shortcut key and name for given action object.
	 * @author Toni Kukurin
	 *
	 */
	public static abstract class DefaultMenuAction extends AbstractAction {
		public DefaultMenuAction(String name, String key) {
			Objects.requireNonNull(name, "name must be a valid string!");
			Objects.requireNonNull(key, "a key combination must be provded");
			
			putValue(NAME, name);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(key));
		}
	}
	
	/**
	 * Helper functional interface for cleaner code.
	 * @author Toni Kukurin
	 *
	 */
	@FunctionalInterface
	private static interface ActionAdapter {
		public void actionPerformed(ActionEvent e);
	}
	
	/**
	 * Helper menu for cleaner code.
	 */
	private static JMenu activeMenu;
	
	/**
	 * Main function of the initializer; sets up caller and returns
	 * the default menu.
	 * 
	 * @param caller
	 * @return
	 */
	public static JMenuBar init(MainWindow caller) {
		JMenuBar newBar = new JMenuBar();
		
		newBar.add(fileMenu(caller));
		newBar.add(playlistMenu(caller));
		newBar.add(propertiesMenu(caller));
		
		return newBar;
	}
	
	/**
	 * @param caller
	 * @return file menu
	 */
	private static JMenu fileMenu(MainWindow caller) {
		JMenu file = new JMenu("File");
		activeMenu = file;
		
		createAndAdd("New", "ctrl N", e -> {
			caller.newPlaylist();
		});
		
		createAndAdd("Open", "ctrl O", e -> {
			JFileChooser chooser = new JFileChooser(PropertyManager.get(Constants.PROPERTY_SAVE_LOCATION));
			int result = chooser.showOpenDialog(caller);
			
			if(result != JFileChooser.APPROVE_OPTION)
				return;
			
			File selected = chooser.getSelectedFile();
			caller.openPlaylist(selected);
		});
		
		createAndAdd("Save", "ctrl S", e -> {
			JFileChooser chooser = new JFileChooser(PropertyManager.get(Constants.PROPERTY_SAVE_LOCATION));
			int result = chooser.showSaveDialog(caller);
			
			if(result != JFileChooser.APPROVE_OPTION)
				return;
			
			File selected = chooser.getSelectedFile();
			caller.storeCurrentPlaylist(selected);
		});
		
		return file;
	}
	
	/**
	 * @param caller
	 * @return playlist menu.
	 */
	private static JMenu playlistMenu(MainWindow caller) {
		JMenu playlist = new JMenu("Playlist");
		activeMenu = playlist;
		
		createAndAdd("Manage albums", "ctrl M", e -> {
			caller.displayAlbumManager();
		});
		
		// TODO ?
//		createAndAdd("Group by folder", "ctrl alt G", e-> {
//
//		});
		
		return playlist;
	}
	
	/**
	 * @param caller
	 * @return properties menu
	 */
	private static JMenu propertiesMenu(MainWindow caller) {
		JMenu properties = new JMenu("Properties");
		activeMenu = properties;
		
		createAndAdd("Edit default folders", "ctrl P", e -> {
			SwingUtilities.invokeLater(() -> new InputScreenWindow().setLocationRelativeTo(caller));
		});
		
		return properties;
	}
	
	private static JMenuItem createAndAdd(String name, String key, ActionAdapter act) {
		JMenuItem menuItem = new JMenuItem(new DefaultMenuAction(name, key) {
			@Override
			public void actionPerformed(ActionEvent e) {
				act.actionPerformed(e);
			}
		});
		
		activeMenu.add(menuItem);
		return menuItem;
	}
	
}
