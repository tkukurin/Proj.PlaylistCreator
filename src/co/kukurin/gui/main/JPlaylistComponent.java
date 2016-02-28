package co.kukurin.gui.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import co.kukurin.gui.model.PlaylistModel;
import co.kukurin.utils.layout.SwingUtils;
import co.kukurin.xml.XMLPlaylistUtils;
import co.kukurin.xml.items.Playlist;
import co.kukurin.xml.items.Track;

@SuppressWarnings("serial")
public class JPlaylistComponent extends JList<Track> implements ListDataListener {
	
	private PlaylistModel model;
	private File modelLocation;
	
	/**
	 * Tells us whether currently opened playlist has been modified.
	 * <p>
	 * The {@link JPlaylistComponent} class uses event listeners bounded on its model
	 * to update this value to true, and resets automatically to false when an entirely new
	 * model has been created.
	 */
	private boolean hasBeenModified;
	
	/**
	 * Default constructor.
	 */
	public JPlaylistComponent() {
		hasBeenModified = false;
		
		model = new PlaylistModel(this);
		setModel(model);
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_DELETE) {
					int[] selected = getSelectedIndices();
					for(int i = selected.length - 1; i >= 0; i--)
						model.remove(selected[i]);
					
					setSelectedIndex(-1);
				}
			}
		});
	}

	/**
	 * Adds all albums from collection.
	 * 
	 * @param paths Collection of paths which represent albums on the system.
	 * @throws Exception I/O error, masked as {@link RuntimeException} so it is possible to throw it from
	 * within a {@link Runnable} instance.
	 */
	public void addAlbums(Collection<File> paths) throws Exception {
		for(File p : paths) {
			List<Track> tracklistToAdd = new ArrayList<>();
			
			Thread t = new Thread(() -> {
				try {
					Files.walkFileTree(p.toPath(), new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							tracklistToAdd.add(new Track(file.toFile()));
							return FileVisitResult.CONTINUE;
						}
					});
				} catch (Exception e) {
					// wrapper exception
					throw new RuntimeException(e);
				}
			});
			t.start();
			
			SwingUtilities.invokeLater(() -> model.put(p, tracklistToAdd) );
		}
	}
	
	/**
	 * Adds album to model whose location corresponds to given file.
	 * 
	 * @param f
	 * @throws Exception I/O error.
	 */
	public void addAlbum(File f) throws Exception {
		List<File> al = new ArrayList<>();
		al.add(f);
		addAlbums(al);
	}
	
	/**
	 * Removes album from model whose location corresponds to given file.
	 * @param f
	 */
	public void removeAlbum(File f) {
		SwingUtils.startupThread(() -> model.remove(f));
	}
	
	
	/**
	 * Updates the albums currently in model by removing all invalid and adding
	 * valid albums.
	 * <p>
	 * Albums are considered to be valid if their {@link File} representation can be found
	 * within given collection.
	 * 
	 * @param albums Collection which contains albums which are to be kept
	 * @throws Exception I/O error
	 */
	public void updateAlbums(Collection<File> albums) throws Exception {
		Objects.requireNonNull(albums);
		Iterator<File> it = model.getLoadedAlbums().keySet().iterator();
		
		while(it.hasNext()) {
			File album = it.next();
			
			if(!albums.contains(album)) {
				model.remove(album, false);
				it.remove();
			} else {
				albums.remove(album);
			}
		}
		
		addAlbums(albums);
	}
	
	/**
	 * @return All loaded albums in list's internal model.
	 */
	public Collection<File> getAlbums() {
		return model.getLoadedAlbums().keySet();
	}
	
	/**
	 * Removes the currenlty selected track from model.
	 */
	public void removeCurrentlySelectedTrack() {
		int idx = this.getSelectedIndex();
		
		if(idx > 0)
			model.remove(idx);
	}
	
	/**
	 * Creates path from string, and then calls {@link #storePlaylist(Path)}.
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void storePlaylist(String path) throws Exception {
		storePlaylist(Paths.get(path));
	}

	/**
	 * @param path File which the file will be saved to.
	 * @throws Exception I/O error.
	 */
	public void storePlaylist(Path path) throws Exception {
		Path parent = path.getParent();
		if(parent != null && !Files.exists(parent))
			throw new IOException("Invalid path given!");
		
		String filename = path.getFileName().toString();
		Playlist toCreate = new Playlist(filename, model.getItems());
		XMLPlaylistUtils.createPlaylist(toCreate, path.toFile());
	}

	/**
	 * @param file File which the playlist will be loaded from
	 * @throws Exception I/O error
	 */
	public void loadPlaylist(File file) throws Exception {
		Playlist p = XMLPlaylistUtils.loadPlaylist(file);
		
		model = new PlaylistModel(this);
		setModel(model);
		
		model.setItems(p.getTracklist());
		determineAlbums();
		
		hasBeenModified = false;
		modelLocation = file;
	}
	
	/**
	 * Resets the playlist to its initial (empty, non-modified) state.
	 */
	public void resetPlaylist() {
		model.reset();
		hasBeenModified = false;
	}

	/**
	 * Extracts albums from individual track paths.
	 */
	private void determineAlbums() {
		for(Track t : model.getItems()) {
			File track = t.getFile();
			File parent = track.getParentFile();
			
			model.getLoadedAlbums().compute(parent, (k, v) -> {
				if(v == null)
					v = new ArrayList<>();
				v.add(t);
				return v;
			});
		}
	}
	
	/**
	 * @return Whether this playlist has been modified.
	 */
	public boolean isHasBeenModified() {
		return hasBeenModified;
	}
	
	/**
	 * @return Location for the file currently opened. Null if editing a new file.
	 */
	public File getModelLocation() {
		return modelLocation;
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		hasBeenModified = true;
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		hasBeenModified = true;
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		hasBeenModified = true;
	}

}
