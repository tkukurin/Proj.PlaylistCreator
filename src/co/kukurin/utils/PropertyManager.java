package co.kukurin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class PropertyManager {

	private PropertyManager() {}
	
	private static Properties properties;
	
	static {
		try { properties = load(); }
		catch (Exception ignore) {}
	}
	
	public static void create() {
		properties = new Properties();
	}
	
	public static Properties load() throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(Constants.PROPERTY_LOCATION)));
		return properties;
	}
	
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
	
	public static void store(JFrame caller) {
		try {
			if(properties != null)
				properties.store(new FileOutputStream(new File(Constants.PROPERTY_LOCATION)), "");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(caller, "Error writing to property file!");
		}
	}
	
	public static boolean hasValidProperties() {
		return properties != null;
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
	
}
