package co.kukurin.xml.items;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

/**
 * (De)serializable class ready to be used as an XML object.
 * <p>
 * The class represents a default Playlist document with necessary info.
 * 
 * @author Toni Kukurin
 *
 */
@Root(strict=false)
@NamespaceList({
	@Namespace(reference="http://xspf.org/ns/0/"),
	@Namespace(reference="http://www.videolan.org/vlc/playlist/ns/0/", prefix="vlc")
})
// @Version(revision=1, required=false)
public class Playlist {
	
	@Element
	private String title;
	
	@ElementList(name="trackList", entry="track")
	private List<Track> tracklist;
	
	@Attribute
	private static int version;
	
	/**
	 * Necessary for serialization, otherwise shouldn't be used as
	 * the title is not set by using this constructor.
	 */
	public Playlist() {
		this("");
	}
	
	/**
	 * Instantiates a title and empty tracklist.
	 * @param title Playlist title
	 */
	public Playlist(String title) {
		this.title = title;
		this.tracklist = new LinkedList<>();
	}
	
	/**
	 * Instantiates a title and tracklist as given.
	 * @param title Playlist title
	 * @param tracklist Tracklist to be associated with the playlist
	 */
	public Playlist(String title, List<Track> tracklist) {
		this.title = title;
		this.tracklist = tracklist;
	}
	
	// addition
	public boolean addAll(Collection<Track> tracks) {
		return tracklist.addAll(tracks);
	}
	
	public boolean add(Track track) {
		return tracklist.add(track);
	}
	
	public boolean add(String filepath) {
		return add(new Track(filepath));
	}
	
	// removal
	public Track remove(int index) {
		return tracklist.remove(index);
	}
	
	public boolean remove(Track track) {
		return tracklist.remove(track);
	}
	
	public boolean remove(String filepath) {
		return remove(new Track(filepath));
	}

	/**
	 * @return current tracklist.
	 */
	public List<Track> getTracklist() {
		return tracklist;
	}

}
