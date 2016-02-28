package co.kukurin.gui.main;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import co.kukurin.gui.albummanager.AlbumManagerWindow;
import co.kukurin.utils.Constants;
import co.kukurin.utils.layout.SwingUtils;

/**
 * Main program window.
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	
	private JPlaylistComponent playlist;
	
	public MainWindow() {
		setJMenuBar(MainMenu.init(this));
		setTitle(Constants.PROGRAM_TITLE);
		
		initGui();
		SwingUtils.instanceDefaults(this, 640, 480);
		setLocationRelativeTo(null);
	}
	
	private void initGui() {
		// TODO cleaner GUI
		
		JLabel playlistLabel = new JLabel("Playlist items:");
		playlistLabel.setLabelFor(playlist);
		playlistLabel.setBorder(SwingUtils.defaultMargin(SwingUtilities.LEFT, SwingUtilities.TOP,
				SwingUtilities.RIGHT));
		add(playlistLabel, BorderLayout.NORTH);
		
		playlist = new JPlaylistComponent();
		JScrollPane playlistWrap = new JScrollPane(playlist);
		playlistWrap.setBorder(BorderFactory.createCompoundBorder(
				SwingUtils.defaultMargin(SwingUtilities.LEFT, SwingUtilities.RIGHT, SwingUtilities.BOTTOM),
				SwingUtils.defaultLineBorder()));
		add(playlistWrap, BorderLayout.CENTER);
	}
	
	/**
	 * Adds given file to the list of albums currently in the playlist.
	 * @param file
	 */
	public void addAlbum(File file) {
		try {
			playlist.addAlbum(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes given file from the list of albums currently in the playlist.
	 * @param file
	 */
	public void removeAlbum(File file) {
		playlist.removeAlbum(file);
	}
	
	/**
	 * Adds and removes albums as necessary according to given collection.
	 * @param albums
	 */
	public void updateAlbumList(Collection<File> albums) {
		try {
			playlist.updateAlbums(albums);
		} catch (Exception e) {
			e.printStackTrace();
			displayMessage("Error updating albums: " + e.toString());
		}
	}

	public void displayAlbumManager() {
		SwingUtilities.invokeLater(() -> {
			try {
				new AlbumManagerWindow(this);
			} catch(IOException ex) {
				displayMessage("Error opening: " + ex.getLocalizedMessage());
			}
		});
	}
	
	public void newPlaylist() {
		if(playlist.isHasBeenModified())
			System.out.println("Has been modified!");
		
		playlist.resetPlaylist();
	}
	
	public void openPlaylist(File f) {
		try {
			playlist.loadPlaylist(f);
		} catch (Exception e) {
			displayMessage("Error opening playlist: " + e);
		}
	}

	public void storeCurrentPlaylist(File f) {
		try {
			playlist.storePlaylist(f.toPath());
		} catch (Exception e) {
			displayMessage("Error writing playlist: " + e);
		}
	}
	
	private void displayMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	public java.util.Collection<File> getAlbumPaths() {
		return playlist.getAlbums();
	}

}
