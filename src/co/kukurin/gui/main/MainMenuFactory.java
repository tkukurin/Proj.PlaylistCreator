package co.kukurin.gui.main;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import co.kukurin.gui.actions.DefaultMenuAction;
import co.kukurin.gui.inputscreen.InputScreenWindow;
import co.kukurin.utils.Constants;
import co.kukurin.utils.PropertyManager;

@SuppressWarnings("serial")
public class MainMenuFactory {

	/**
	 * Static non-instantiable class
	 */
	private MainMenuFactory() {}
	
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
			int save = caller.queryFileSaveIfNecessary();
			
			if(save == JOptionPane.YES_OPTION)
				displaySaveFileDialog(caller);
			else if(save == JOptionPane.CANCEL_OPTION)
				return;
			
			caller.newPlaylist();
		});
		
		createAndAdd("Open", "ctrl O", e -> {
			int save = caller.queryFileSaveIfNecessary();
			
			if(save == JOptionPane.YES_OPTION)
				displaySaveFileDialog(caller);
			else if(save == JOptionPane.CANCEL_OPTION)
				return;
			
			JFileChooser chooser = new JFileChooser(PropertyManager.get(Constants.PROPERTY_OPEN_LOCATION));
			chooser.setFileFilter(Constants.VLC_FILENAME_FILTER);
			int result = chooser.showOpenDialog(caller);
			
			if(result != JFileChooser.APPROVE_OPTION)
				return;
			
			File selected = chooser.getSelectedFile();
			caller.openPlaylist(selected);
		});
		
		createAndAdd("Save", "ctrl S", e -> {
			File curr = caller.getCurrentOpenFileLocation();
			
			if(curr == null)
				displaySaveFileDialog(caller);
			else
				caller.storeCurrentPlaylist(curr);
		});
		
		createAndAdd("Save as", "ctrl alt S", e -> {
			displaySaveFileDialog(caller);
		});
		
		return file;
	}

	public static void displaySaveFileDialog(MainWindow caller) {
		String saveLoc = PropertyManager.get(Constants.PROPERTY_SAVE_LOCATION);
		File openLoc = caller.getCurrentOpenFileLocation();
		
		if(openLoc != null)
			saveLoc = openLoc.getAbsolutePath();
		
		JFileChooser chooser = new JFileChooser(saveLoc);
		chooser.setFileFilter(Constants.VLC_FILENAME_FILTER);
		int result = chooser.showSaveDialog(caller);
		
		if(result != JFileChooser.APPROVE_OPTION)
			return;
		
		File selected = chooser.getSelectedFile();
		if(selected.exists()) {
			int result2 = JOptionPane.showConfirmDialog(caller, "File already exists; overwrite?",
					"File exists", JOptionPane.YES_NO_OPTION);

			if(result2 == JOptionPane.NO_OPTION)
				displaySaveFileDialog(caller);
		}
		
		caller.storeCurrentPlaylist(selected);
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
		
		// TODO ?
//		createAndAdd("Fix invalid file locations", "", e -> {
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
