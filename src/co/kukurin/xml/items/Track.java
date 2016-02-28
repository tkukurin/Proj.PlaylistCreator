package co.kukurin.xml.items;

import java.io.File;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import co.kukurin.utils.Constants;

@Root
public class Track {
	
	// TODO currently has no purpose
	private File file;

	@Element
	private String location;
	
	@Element
	private String title;
	
	public Track(File f) {
		this(f.getAbsolutePath());
	}
	
	public Track(String location) {
		location = location.replaceAll("\\\\", "/");
		
		this.location = Constants.VLC_FILE_PREFIX + location;
		this.file = new File(location);
		
		getFileMetadata();
	}

	private void getFileMetadata() {
		this.title = file.getName();
		
		int extensionIndex = title.indexOf('.');
		if(extensionIndex > 0)
			this.title = this.title.substring(0, extensionIndex);
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
	
}
