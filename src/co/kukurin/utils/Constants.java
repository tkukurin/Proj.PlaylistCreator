package co.kukurin.utils;

import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

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
	public static final FileNameExtensionFilter VLC_FILENAME_FILTER
		= new FileNameExtensionFilter("VLC Playlist files (.xspf)", "xspf");
	public static final String VLC_PLAYLIST_EXTENSION = ".xspf";
	public static final String VLC_FILE_PREFIX = "file:///";
	
	// App constants
	public static final String PROPERTY_LOCATION = "./defaults.properties";
	public static final String PROGRAM_TITLE = "VLC Playlist creator";

	// Properties
	public static final String PROPERTY_SPLIT_STRING = ".";
	public static final Character PROPERTY_SPLIT_CHAR = '.';
	public static final String PROPERTY_SAVE_LOCATION = "save.location";
	public static final String PROPERTY_OPEN_LOCATION = "open.location";
	public static final String PROPERTY_MUSIC_LOCATION = "music.location";
	
	// General
	public static final List<String> AUDIO_SUFFIXES = Arrays.asList(".mp3", ".wav", ".flac", ".avi");
}
