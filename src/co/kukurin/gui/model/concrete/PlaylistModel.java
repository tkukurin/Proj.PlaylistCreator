package co.kukurin.gui.model.concrete;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.kukurin.gui.main.JPlaylistComponent;
import co.kukurin.gui.model.UpdateableListModel;
import co.kukurin.xml.items.Track;

/**
 * Model used within this component.
 * <p>
 * The model offers some useful functionality on top of {@link UpdateableListModel}'s default options,
 * namely it keeps track of all loaded albums thus far and provides the ability to delete all items from an
 * album in one swoop.
 * 
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public class PlaylistModel extends UpdateableListModel<Track> {
	
	/**
	 * Map of loaded albums and their locations on the system; note that not all tracks from this list will
	 * necessarily always be in the current model's active set. However, since this map is only used whilst
	 * removing items (i.e., when all songs from a certain album are to be deleted), that part is not a
	 * major concern currently.
	 */
	private Map<File, List<Track>> loadedAlbums;
	
	/**
	 * Default constructor; adds a data listener to enclosing instance of {@link JPlaylistComponent};
	 * see {@link JPlaylistComponent#hasBeenModified}.
	 */
	public PlaylistModel(JPlaylistComponent caller) {
		this.loadedAlbums = new LinkedHashMap<>();
		
		addListDataListener(caller);
	}
	
	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public Track getElementAt(int index) {
		return items.get(index);
	}
	
	public void put(File f, List<Track> tracks) {
		loadedAlbums.put(f, tracks);
		addAll(tracks);
	}

	public void remove(File f) {
		remove(f, true);
	}
	
	public void remove(File f, boolean removeFromMap) {
		List<Track> toRemove = loadedAlbums.get(f);
		
		if(removeFromMap)
			loadedAlbums.remove(f);
		
		removeAll(toRemove);
	}

	public void reset() {
		int tracksiz = items.size();
		this.loadedAlbums = new LinkedHashMap<>();
		this.items = new ArrayList<>();
		
		if(tracksiz <= 0)
			tracksiz = 1;
		
		fireIntervalRemoved(this, 0, tracksiz - 1);
	}
	
	public List<Track> getItems() {
		return this.items;
	}
	
	public Map<File, List<Track>> getLoadedAlbums() {
		return this.loadedAlbums;
	}
	
	public void setItems(List<Track> items) {
		this.items = items;
		fireContentsChanged(this, 0, items.size() - 1);
	}
	
}
