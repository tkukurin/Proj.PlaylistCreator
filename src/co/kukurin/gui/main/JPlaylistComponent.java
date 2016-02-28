package co.kukurin.gui.main;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import co.kukurin.utils.layout.SwingUtils;
import co.kukurin.xml.XMLPlaylistUtils;
import co.kukurin.xml.items.Playlist;
import co.kukurin.xml.items.Track;

@SuppressWarnings("serial")
public class JPlaylistComponent extends JList<Track> implements ListDataListener {
	
	private class PlaylistModel extends AbstractListModel<Track> {

		private Map<File, List<Track>> loadedAlbums;
		private List<Track> loadedTracks;
		
		public PlaylistModel() {
			this.loadedAlbums = new LinkedHashMap<>();
			this.loadedTracks = new ArrayList<>();
			
			addListDataListener(JPlaylistComponent.this);
		}
		
		@Override
		public int getSize() {
			return loadedTracks.size();
		}

		@Override
		public Track getElementAt(int index) {
			return loadedTracks.get(index);
		}
		
		public void put(File f, List<Track> tracks) {
			int currsiz = loadedTracks.size();
			int added = tracks.size();
			
			loadedAlbums.put(f, tracks);
			loadedTracks.addAll(tracks);
			
			fireIntervalAdded(this, currsiz, currsiz + added - 1);
		}

		public void remove(File f) {
			List<Track> toRemove = loadedAlbums.get(f);
			loadedTracks.removeAll(toRemove);
			
			fireContentsChanged(this, 0, loadedTracks.size());
		}
		
		/**
		 * Does not remove tracks from the map, since the map is being used only
		 * to store tracks related to specific albums.
		 * @param index
		 */
		public void remove(int index) {
			loadedTracks.remove(index);
		}

		public void reset() {
			int tracksiz = loadedTracks.size();
			this.loadedAlbums = new LinkedHashMap<>();
			this.loadedTracks = new ArrayList<>();
			
			fireIntervalRemoved(this, 0, tracksiz - 1);
		}
		
	}
	
	private PlaylistModel model;
	private File modelLocation;
	private boolean hasBeenModified;
	
	public JPlaylistComponent() {
		hasBeenModified = false;
		model = new PlaylistModel();
		setModel(model);
	}

	public void addAlbums(List<File> paths) throws IOException {
		for(File p : paths) {
			List<Track> tracklistToAdd = new ArrayList<>();
			
			// TODO new thread?
			Files.walkFileTree(p.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					tracklistToAdd.add(new Track(file.toFile()));
					return FileVisitResult.CONTINUE;
				}
			});
			
			model.put(p, tracklistToAdd);
		}
	}
	
	public void addAlbum(File path) throws IOException {
		List<File> al = new ArrayList<>();
		al.add(path);
		addAlbums(al);
	}
	
	public void removeAlbum(File p) {
		SwingUtils.startupThread(() -> model.remove(p));
	}
	
	public void updateAlbums(Collection<File> albums) throws IOException {
		for(File album : albums) {
			if(model.loadedAlbums.containsKey(album))
				model.remove(album);
			else addAlbum(album);
		}
	}
	
	public Collection<File> getAlbums() {
		return model.loadedAlbums.keySet();
	}
	
	public void removeCurrentlySelectedTrack() {
		int idx = this.getSelectedIndex();
		
		if(idx > 0)
			model.remove(idx);
	}
	
	public void storePlaylist(String path) throws Exception {
		storePlaylist(Paths.get(path));
	}

	public void storePlaylist(Path path) throws Exception {
		Path parent = path.getParent();
		if(parent != null && !Files.exists(parent))
			throw new IOException("Invalid path given!");
		
		String filename = path.getFileName().toString();
		Playlist toCreate = new Playlist(filename, model.loadedTracks);
		XMLPlaylistUtils.createPlaylist(toCreate, path.toFile());
	}

	public void loadPlaylist(File file) throws Exception {
		Playlist p = XMLPlaylistUtils.loadPlaylist(file);
		
		model = new PlaylistModel();
		setModel(model);
		
		model.loadedTracks = p.getTracklist();
		determineAlbums();
		
		hasBeenModified = false;
		modelLocation = file;
	}
	
	public void resetPlaylist() {
		model.reset();
		hasBeenModified = false;
	}

	private void determineAlbums() {
		for(Track t : model.loadedTracks) {
			File track = t.getFile();
			File parent = track.getParentFile();
			
			model.loadedAlbums.compute(parent, (k, v) -> {
				if(v == null)
					v = new ArrayList<>();
				v.add(t);
				return v;
			});
		}
	}
	
	public boolean isHasBeenModified() {
		return hasBeenModified;
	}
	
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
