package co.kukurin.xml;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

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
	
	public static void createPlaylist(Playlist toCreate, String filename) throws Exception {
		Serializer s = new Persister();
		
		if(!filename.endsWith(Constants.VLC_PLAYLIST_EXTENSION))
			filename += Constants.VLC_PLAYLIST_EXTENSION;
		
		File result = new File(filename);
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
}
