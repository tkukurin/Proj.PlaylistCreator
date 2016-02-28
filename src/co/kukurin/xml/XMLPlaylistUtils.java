package co.kukurin.xml;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import co.kukurin.utils.Constants;
import co.kukurin.xml.items.Playlist;

/**
 * Utility methods for writing to XML files.
 * @author Toni Kukurin
 *
 */
public class XMLPlaylistUtils {
	
	/**
	 * Non-instantiable.
	 */
	private XMLPlaylistUtils() {}
	
	public static void createPlaylist(Playlist toCreate, File result) throws Exception {
		Objects.requireNonNull(toCreate);
		Objects.requireNonNull(result);
		
		Serializer s = new Persister();
		String filename = result.getName();
		
		if(!filename.endsWith(Constants.VLC_PLAYLIST_EXTENSION))
			filename += Constants.VLC_PLAYLIST_EXTENSION;
		
		File temp = new File(filename + "-tempFile.temp");
		
		temp.createNewFile();
		s.write(toCreate, temp);
		byte[] tempBytes = Files.readAllBytes(temp.toPath());
		temp.delete();
		
		// I don't know of an easier way to prepend the header currently
		result.createNewFile();
		Files.write(result.toPath(), Constants.XML_HEADER.getBytes());
		Files.write(result.toPath(), tempBytes, StandardOpenOption.APPEND);
	}
	
	public static Playlist loadPlaylist(File location) throws Exception {
		if(location == null || location.isDirectory() || !location.exists())
			throw new IllegalArgumentException("Invalid playlist location given!");
		
		Serializer s = new Persister();
		Playlist p = s.read(Playlist.class, location);
		
		return p;
	}
}
