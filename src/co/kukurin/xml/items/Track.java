package co.kukurin.xml.items;

import java.beans.ConstructorProperties;
import java.io.File;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

import co.kukurin.utils.Constants;

@Root
public class Track {
	
	private File file;

	@Element
	private String location;
	
	@Element
	private String title;
	
	/**
	 * Necessary for serialization, otherwise should not be used.
	 */
	public Track() {}
	
	/**
	 * Serialization commit, occurs after all XML data has been parsed.
	 */
	@Commit public void commit() {
		final String checkStart = Constants.VLC_FILE_PREFIX;
		int checkStartLen = checkStart.length();
		
		if(this.location.startsWith(checkStart))
			this.file = new File(this.location.substring(checkStartLen));
	}
	
	public Track(File f) {
		this(f.getAbsolutePath());
	}
	
	public Track(String location) {
		location = location.replaceAll("\\\\", "/");
		
		this.location = Constants.VLC_FILE_PREFIX + location;
		this.file = new File(location);
		
		getFileMetadata();
	}
	
	@ConstructorProperties({"location", "title"})
	public Track(String location, String title) {
		this.location = location;
		this.title = title;
	}

	private void getFileMetadata() {
		this.title = file.getName();
		
		int extensionIndex = title.lastIndexOf('.');
		if(extensionIndex > 0)
			this.title = this.title.substring(0, extensionIndex);
	}
	
	public File getFile() {
		return file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Track other = (Track) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return title;
	}
	
}
