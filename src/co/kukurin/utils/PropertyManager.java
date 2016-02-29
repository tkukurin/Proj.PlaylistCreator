package co.kukurin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Static class used to manage properties.
 * <p>
 * The properties are loaded as defined in the {@link Constants} class.
 * 
 * @author Toni Kukurin
 *
 */
public class PropertyManager {

	/**
	 * Non-instantiable
	 */
	private PropertyManager() {}
	
	/**
	 * Properties accessible via this class.
	 */
	private static Properties properties;
	
	static {
		try { properties = load(); }
		catch (Exception ignore) {}
	}
	
	/**
	 * Creates a new property list.
	 */
	public static void createDefaultProperties() {
		properties = new Properties();
		properties.put(Constants.PROPERTY_OPEN_LOCATION, "");
		properties.put(Constants.PROPERTY_SAVE_LOCATION, "");
		properties.put(Constants.PROPERTY_MUSIC_LOCATION, "");
	}
	
	/**
	 * Loads properties from the default location.
	 * 
	 * @return Loaded properties.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Properties load() throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(Constants.PROPERTY_LOCATION)));
		return properties;
	}
	
	/**
	 * Loads properties from the default location.
	 * <p>
	 * In case of error, displays it in a {@link JOptionPane} relative to caller.
	 * 
	 * @param caller
	 * @return Loaded properties.
	 */
	public static Properties load(JFrame caller) {
		try {
			Properties p = load();
			return p;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(caller, "Error loading property file!");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(caller, "Unhandled error: " + e);
		}
		
		return null;
	}
	
	/**
	 * Stores the current state of properties into the default location.
	 * <p>
	 * In case of error, displays it in a {@link JOptionPane} relative to caller.
	 * 
	 * @param caller
	 */
	public static void store(JFrame caller) {
		try {
			if(properties != null)
				properties.store(new FileOutputStream(new File(Constants.PROPERTY_LOCATION)), "");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(caller, "Error writing to property file!");
		}
	}
	
	public static boolean hasValidProperties() {
		if(properties == null)
			return false;
		
		for(Object val : getValues()) {
			if(val == null || val.toString().isEmpty())
				return false;
		}
		
		return true;
	}
	
	public static String get(String key) {
		if(properties == null)
			return null;
		
		return properties.getProperty(key);
	}
	
	public static void put(String key, String value) {
		if(properties != null)
			properties.put(key, value);
	}
	
	public static Enumeration<Object> getKeys() {
		return properties.keys();
	}
	
	public static Collection<Object> getValues() {
		return properties.values();
	}
}
