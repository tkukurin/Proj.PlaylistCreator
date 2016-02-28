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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import co.kukurin.utils.layout.SwingUtils;
import co.kukurin.xml.XMLPlaylistUtils;
import co.kukurin.xml.items.Playlist;
import co.kukurin.xml.items.Track;

public class JPlaylistComponent extends JList<Track> {
	
	private class PlaylistModel extends AbstractListModel<Track> {

		private Map<Path, List<Track>> loadedAlbums;
		private List<Track> loadedTracks;
		
		public PlaylistModel() {
			this.loadedAlbums = new LinkedHashMap<>();
			this.loadedTracks = new ArrayList<>();
		}
		
		@Override
		public int getSize() {
			return loadedTracks.size();
		}

		@Override
		public Track getElementAt(int index) {
			return loadedTracks.get(index);
		}
		
		public void put(Path p, List<Track> tracks) {
			int currsiz = loadedTracks.size();
			int added = tracks.size();
			
			loadedAlbums.put(p, tracks);
			loadedTracks.addAll(tracks);
			
			fireIntervalAdded(this, currsiz, currsiz + added - 1);
		}

		public void remove(Path p) {
			List<Track> toRemove = loadedAlbums.get(p);
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
		
	}
	
	private PlaylistModel loadedAlbums;
	
	public JPlaylistComponent() {
		loadedAlbums = new PlaylistModel();
		setModel(loadedAlbums);
	}

	public void addAlbums(List<Path> paths) throws IOException {
		for(Path p : paths) {
			List<Track> tracklistToAdd = new ArrayList<>();
			
			// TODO new thread?
			Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					tracklistToAdd.add(new Track(file.toFile()));
					return FileVisitResult.CONTINUE;
				}
			});
			
			loadedAlbums.put(p, tracklistToAdd);
		}
	}
	
	public void addAlbum(Path path) throws IOException {
		List<Path> al = new ArrayList<>();
		al.add(path);
		addAlbums(al);
	}
	
	public void removeAlbum(Path p) {
		SwingUtils.startupThread(() -> loadedAlbums.remove(p));
	}
	
	public void removeCurrentlySelectedTrack() {
		int idx = this.getSelectedIndex();
		
		if(idx > 0)
			loadedAlbums.remove(idx);
	}
	
	public void storePlaylist(String path) throws Exception {
		storePlaylist(Paths.get(path));
	}

	public void storePlaylist(Path path) throws Exception {
		Path parent = path.getParent();
		if(parent != null && !Files.exists(parent))
			throw new IOException("Invalid path given!");
		
		String filename = path.getFileName().toString();
		Playlist toCreate = new Playlist(filename, loadedAlbums.loadedTracks);
		XMLPlaylistUtils.createPlaylist(toCreate, path.toFile());
	}

	public void loadPlaylist(File file) throws Exception {
		Playlist p = XMLPlaylistUtils.loadPlaylist(file);
		
		loadedAlbums = new PlaylistModel();
		setModel(loadedAlbums);
		
		loadedAlbums.loadedTracks = p.getTracklist();
		determineAlbums();
	}

	private void determineAlbums() {
		for(Track t : loadedAlbums.loadedTracks) {
			File track = t.getFile();
			Path parent = track.toPath().getParent();
			
			loadedAlbums.loadedAlbums.compute(parent, (k, v) -> {
				if(v == null)
					v = new ArrayList<>();
				v.add(t);
				return v;
			});
		}
	}

}
