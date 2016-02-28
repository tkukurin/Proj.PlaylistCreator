package co.kukurin.demo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import co.kukurin.utils.Constants;
import co.kukurin.xml.items.Playlist;
import co.kukurin.xml.items.Track;

public class XMLDemo {
	
	public static void main(String[] args) throws Exception {
		List<Track> tracklist = Arrays.asList(new Track("E:\\Download\\House of Cards Season 3\\House of Cards Episode 01 - Chapter 27.mp4"),
				new Track("E:\\Music\\Pink Floyd\\1977 - Animals\\Pigs (Three Different Ones).mp3"));
		Playlist p = new Playlist("Test playlist", tracklist);
		
		Serializer s = new Persister();
		File result = new File("test.xspf");
		File temp = new File("testTemp.xspf");
		
		temp.createNewFile();
		s.write(p, temp);
		byte[] tempBytes = Files.readAllBytes(temp.toPath());
		temp.delete();
		
		// TODO I don't know of an easier way to prepend the header
		// currently
		result.createNewFile();
		Files.write(result.toPath(), Constants.XML_HEADER.getBytes());
		Files.write(result.toPath(), tempBytes, StandardOpenOption.APPEND);
	}
	
}
