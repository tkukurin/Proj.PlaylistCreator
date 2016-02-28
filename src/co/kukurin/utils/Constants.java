package co.kukurin.utils;

import java.awt.Font;

/**
 * Static class used for general-purpose constants
 * 
 * @author Toni Kukurin
 *
 */
public class Constants {
	
	/**
	 * Non-instantiable
	 */
	private Constants() {}
	
	// XML constants
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	// VLC constants
	public static final String VLC_PLAYLIST_EXTENSION = ".xspf";
	public static final String VLC_FILE_PREFIX = "file:///";
	
	// App constants
	public static final String PROPERTY_LOCATION = "./properties/defaults.properties";
	public static final String PROGRAM_TITLE = "VLC Playlist creator";
	public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);

	// Properties
	public static final String PROPERTY_BASEDIR = "music.location";

}
