package co.kukurin.gui.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;
	
	private static final int MIN_WIDTH = 480;
	private static final int MIN_HEIGHT = 320;
	
	private JPlaylistComponent playlist;
	
	private WindowAdapter checkForSaveOnClose = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			int result = queryFileSaveIfNecessary();
			
			if(result == JOptionPane.CANCEL_OPTION)
				return;
			else if(result == JOptionPane.YES_OPTION)
				MainMenuFactory.displaySaveFileDialog(MainWindow.this);
			
			MainWindow.this.dispose();
		}
	};
	
	public MainWindow() {
		setJMenuBar(MainMenuFactory.init(this));
		
		initGui();
		initListeners();
		
		setTitle(Constants.PROGRAM_TITLE);
		SwingUtils.instanceDefaults(this, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setLocationRelativeTo(null);
	}

	private void initGui() {
		JLabel playlistLabel = new JLabel("Playlist items:");
		SwingUtils.setHeader1(playlistLabel);
		
		playlistLabel.setLabelFor(playlist);
		playlistLabel.setBorder(SwingUtils.defaultMargin(SwingUtilities.LEFT, SwingUtilities.TOP,
				SwingUtilities.RIGHT, SwingUtilities.BOTTOM));
		add(playlistLabel, BorderLayout.NORTH);
		
		playlist = new JPlaylistComponent();
		JScrollPane playlistWrap = new JScrollPane(playlist);
		playlistWrap.setBorder(BorderFactory.createCompoundBorder(
				SwingUtils.defaultMargin(SwingUtilities.LEFT, SwingUtilities.RIGHT, SwingUtilities.BOTTOM),
				SwingUtils.defaultLineBorder()));
		add(playlistWrap, BorderLayout.CENTER);
	}
	
	private void initListeners() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				playlist.requestFocus();
			}
		});
		
		addWindowListener(checkForSaveOnClose);
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

	/**
	 * Displays an {@link AlbumManagerWindow}, making this window temporarily disabled.
	 */
	public void displayAlbumManager() {
		this.setEnabled(false);
		
		SwingUtilities.invokeLater(() -> {
			try {
				new AlbumManagerWindow(this);
			} catch(IOException ex) {
				displayMessage("Error opening album manager: " + ex.getLocalizedMessage());
			}
		});
	}
	
	/**
	 * Sets up a new playlist.
	 */
	public void newPlaylist() {
		if(playlist.isHasBeenModified())
			System.out.println("Has been modified!");
		
		playlist.resetPlaylist();
	}
	
	/**
	 * Tries to open playlist from existing.
	 * @param f
	 */
	public void openPlaylist(File f) {
		try {
			playlist.loadPlaylist(f);
		} catch (Exception e) {
			displayMessage("Error opening playlist: " + e);
		}
	}

	/**
	 * Storest current playlist to given location.
	 * @param f
	 */
	public void storeCurrentPlaylist(File f) {
		try {
			playlist.storePlaylist(f.toPath());
		} catch (Exception e) {
			displayMessage("Error writing playlist: " + e);
		}
	}
	
	/**
	 * Queries the user to save file if current has been modified.
	 * 
	 * @return user result as {@link JOptionPane} integer constant.
	 */
	public int queryFileSaveIfNecessary() {
		if(playlist.isHasBeenModified()) {
			int result = JOptionPane.showConfirmDialog(this, "Save current file?", "File was modified",
					JOptionPane.YES_NO_CANCEL_OPTION);
			return result;
		}
		
		return JOptionPane.NO_OPTION;
	}
	
	/**
	 * Default message dialog.
	 * @param msg Message
	 */
	private void displayMessage(String msg) {
		final int msgLenByLine = 50;
		int msgLen = msg.length();
		
		if(msgLen > msgLenByLine) {
			String[] tokens = msg.split("\\s+");
			StringBuilder sb = new StringBuilder();
			
			int currlen = 0;
			for(String token : tokens) {
				sb.append(token + " ");
				currlen += token.length();
				
				if(currlen >= msgLenByLine) {
					currlen = 0;
					sb.append(System.lineSeparator());
				}
			}
			
			msg = sb.toString();
		}
		
		JOptionPane.showMessageDialog(this, msg);
	}

	/**
	 * @return All albums extracted from current playlist.
	 */
	public java.util.Collection<File> getAlbumPaths() {
		return playlist.getAlbums();
	}
	
	public File getCurrentOpenFileLocation() {
		return playlist.getModelLocation();
	}

}
