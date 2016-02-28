package co.kukurin.xml.items;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Version;

@Root
@NamespaceList({
	@Namespace(reference="http://xspf.org/ns/0/"),
	@Namespace(reference="http://www.videolan.org/vlc/playlist/ns/0/", prefix="vlc")
})
@Version(revision=1.0)
public class Playlist {
	
	@Element
	private String title;
	
	@ElementList(name="trackList")
	private List<Track> tracklist;
	
	public Playlist(String title) {
		this.title = title;
		this.tracklist = new LinkedList<>();
	}
	
	public Playlist(String title, List<Track> tracklist) {
		this.title = title;
		this.tracklist = tracklist;
	}
	
	public boolean add(Track track) {
		return tracklist.add(track);
	}
	
	public boolean add(String filepath) {
		return add(new Track(filepath));
	}
	
	public boolean remove(Track track) {
		return tracklist.remove(track);
	}
	
	public boolean remove(String filepath) {
		return remove(new Track(filepath));
	}
}
