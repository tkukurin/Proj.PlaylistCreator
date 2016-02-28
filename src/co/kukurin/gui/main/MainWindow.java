package co.kukurin.gui.main;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import co.kukurin.file.finder.AlbumFileVisitor;
import co.kukurin.gui.FromStringWindow;
import co.kukurin.utils.Constants;
import co.kukurin.utils.PropertyManager;
import co.kukurin.utils.layout.GroupLayoutCreator;
import co.kukurin.utils.layout.SwingUtils;

/**
 * Main program window.
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	
	private JPlaylistComponent playlist;
	
	
	private JTextField baseLocation;
	private JTextField albumLocations;
	private JTextField playlistName;
	
	public MainWindow() {
		SwingUtils.setWindowsLookAndFeel();
		
		setJMenuBar(MainMenu.init(this));
		setTitle(Constants.PROGRAM_TITLE);
		//setResizable(false);
		
		initGui();
		SwingUtils.instanceDefaults(this);
		setLocationRelativeTo(null);
	}
	
	private void initGui() {
		JLabel playlistLabel = new JLabel("Playlist items:");
		playlistLabel.setLabelFor(playlist);
		add(playlistLabel, BorderLayout.NORTH);
		
		playlist = new JPlaylistComponent();
		add(new JScrollPane(playlist), BorderLayout.CENTER);
		
//		JPanel itemContainer = new JPanel();
//
//		GroupLayoutCreator glc = new GroupLayoutCreator(itemContainer, true);
//		glc.setLinkColumn(0, 2);
//
//		initBaseLocationInput(glc);
//		initAlbumsInput(glc);
//		initPlaylistNameInput(glc);
//
//		glc.doLayout();
//		add(itemContainer, BorderLayout.NORTH);
	}
	
	/**
	 * Initializes the base location (first row in the GUI).
	 * @param glc
	 */
	private void initBaseLocationInput(GroupLayoutCreator glc) {
		baseLocation = setupTextarea(PropertyManager.get(Constants.PROPERTY_BASEDIR));
		JButton musicLocationBtn = initBaseLocationBtn();
		
		JPanel mlMidPanel = new JPanel(new BorderLayout());
		mlMidPanel.add(baseLocation, BorderLayout.CENTER);
		mlMidPanel.add(musicLocationBtn, BorderLayout.EAST);
		
		JButton storePropertiesBtn = new JButton("Save base location");
		storePropertiesBtn.addActionListener(evt -> PropertyManager.store(this));
		
		glc.addHorizontally(new JLabel("Base Location"), mlMidPanel, storePropertiesBtn);
	}
	
	private JButton initBaseLocationBtn() {
		JButton openBtn = new JButton("Open...");
		
		openBtn.addActionListener(evt -> {
			showDialogAndUpdateTextField(baseLocation);
			PropertyManager.put(Constants.PROPERTY_BASEDIR, baseLocation.getText());
		});
		
		return openBtn;
	}
	
	/**
	 * Initializes the album input (second row in the GUI).
	 * @param glc
	 */
	private void initAlbumsInput(GroupLayoutCreator glc) {
		albumLocations = setupTextarea("");
		JButton albumLocationsBtn = initAlbumLocationsBtn();
		
		JPanel midPanel = new JPanel(new BorderLayout());
		midPanel.add(albumLocations, BorderLayout.CENTER);
		midPanel.add(albumLocationsBtn, BorderLayout.EAST);
		JButton fromStringBtn = initFromStringBtn();
		
		glc.addHorizontally(new JLabel("Albums to add"), midPanel, fromStringBtn);
	}
	
	private JButton initAlbumLocationsBtn() {
		JButton albumLocationsBtn = new JButton("Open...");
		albumLocationsBtn.addActionListener(evt -> {
			showDialogAndUpdateTextField(albumLocations);
		});
		
		return albumLocationsBtn;
	}
	
	private JButton initFromStringBtn() {
		JButton fsb = new JButton("From string");
		fsb.addActionListener(evt -> {
			this.setEnabled(false);
			
			SwingUtilities.invokeLater(() -> {
				new FromStringWindow(this);
			});
		});
		return fsb;
	}

	/**
	 * Initializes the playlist name input (third row in the GUI).
	 * @param glc
	 */
	private void initPlaylistNameInput(GroupLayoutCreator glc) {
		playlistName = setupTextarea("");
		JButton editPlaylistBtn = new JButton("Edit playlist");
		
		editPlaylistBtn.addActionListener(evt -> {
			// TODO
		});
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(playlistName, BorderLayout.CENTER);
		container.add(editPlaylistBtn, BorderLayout.EAST);
		
		JButton createPlaylistBtn = initCreatePlaylistBtn();
		glc.addHorizontally(new JLabel("Playlist name"), container, createPlaylistBtn);
	}
	
	private JButton initCreatePlaylistBtn() {
		JButton createPlaylistBtn = new JButton("Create playlist");
		createPlaylistBtn.addActionListener(evt -> {
			String baseLocationStr = baseLocation.getText();
			String playlistNameStr = playlistName.getText();
			String albumLocationsStr = albumLocations.getText();
			
			if(containsEmptyString(baseLocationStr, playlistNameStr, albumLocationsStr)) {
				displayMessage("Please first fill out all fields!");
				return;
			}
			
			AlbumFileVisitor afv = new AlbumFileVisitor(albumLocationsStr, playlistNameStr);
			SwingUtils.startupThread(() -> {
				try {
					System.out.println(Thread.currentThread());
					
					for(String token : baseLocationStr.split(File.pathSeparator)) {
						Path curr = Paths.get(token);
						Files.walkFileTree(curr, afv);
					}
				} catch (Exception e) {
					displayMessage("File walker failed!");
					return;
				}
			});
			
			try {
				// XMLPlaylistUtils.createPlaylist(afv.getCreatedPlaylist(), playlistNameStr);
			} catch (Exception e) {
				displayMessage("Playlist creation failed: " + e.getLocalizedMessage());
				return;
			}
			
			displayMessage("Playlist creation successful!");
		});
		
		return createPlaylistBtn;
	}
	
	/**
	 * Calls {@link #getMultipleDirectorySelectionDialogResult()} and retrieves its
	 * result; sets given textfield value accordingly.
	 * @param tf
	 */
	private void showDialogAndUpdateTextField(JTextField tf) {
		Optional<File[]> selectedFiles = getMultipleDirectorySelectionDialogResult();
		if(!selectedFiles.isPresent())
			return;
		
		StringBuilder sb = new StringBuilder();
		for(File f : selectedFiles.get())
			sb.append(f.getAbsolutePath()).append(File.pathSeparator);
		
		tf.setText(sb.toString());
	}
	
	private Optional<File[]> getMultipleDirectorySelectionDialogResult() {
		String firstToken = baseLocation.getText();
		int pathSeparatorIdx = firstToken.indexOf(File.pathSeparator);
		
		if(pathSeparatorIdx > 0)
			firstToken = firstToken.substring(0, pathSeparatorIdx);
		
		JFileChooser chooser = new JFileChooser(firstToken);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		
		int result = chooser.showOpenDialog(this);
		if(result != JFileChooser.APPROVE_OPTION)
			return Optional.empty();
		
		return Optional.of(chooser.getSelectedFiles());
	}
	
	/**
	 * Adds given file to the list of albums currently in the playlist.
	 * @param file
	 */
	public void addAlbum(File file) {
		try {
			playlist.addAlbum(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		String prepend = albumLocations.getText();
//
//		if(!prepend.isEmpty())
//			prepend += File.pathSeparator;
//
//		albumLocations.setText(prepend + file.toString());
	}
	
	private boolean containsEmptyString(String ... str) {
		if(str == null || str.length == 0)
			return true;
		
		for(String s : str)
			if(s == null || s.isEmpty())
				return true;
		
		return false;
	}

	public void displayFromStringWindow() {
		SwingUtilities.invokeLater(() -> new FromStringWindow(this));
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

	private JTextField setupTextarea(String text) {
		return SwingUtils.defaultTextField(true, text);
	}

}
