package co.kukurin.gui.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;

/**
 * Basic {@link ListModel} containing a list of file items and offering basic add/remove options.
 * The model additionally keeps track of an "active set" of items which at any given moment
 * stores a subset of the total list of items. See {@link #updateActiveSet(String)} for more info.
 * 
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public class FileListModel extends AbstractListModel<File> {
	
	private List<File> files;
	private List<File> activeFiles;
	
	public FileListModel() {
		this.files = new ArrayList<>();
		this.activeFiles = files;
	}

	@Override
	public int getSize() {
		return activeFiles.size();
	}

	@Override
	public File getElementAt(int index) {
		return activeFiles.get(index);
	}
	
	public void addAll(Collection<File> files) {
		int oldsiz = this.files.size();
		this.files.addAll(files);
		
		fireIntervalAdded(this, oldsiz, this.files.size() - 1);
	}
	
	public boolean add(File f) {
		boolean val = files.add(f);
		if(val)
			fireIntervalAdded(this, files.size() - 1, files.size() - 1);
		
		return val;
	}
	
	public List<File> getActiveFileset() {
		return activeFiles;
	}
	
	public Collection<File> getEntireFileset() {
		return files;
	}
	
	public boolean remove(File f) {
		for(int i = 0; i < files.size(); i++) {
			if(files.get(i).equals(f)) {
				files.remove(i);
				
				final int idx = i;
				fireIntervalRemoved(this, idx, idx);
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Updates the currently active set according to given string;
	 * tokens are extracted using blank space as the default separator, and
	 * then logically AND-ed with each file in storage (i.e., the filepath
	 * must contain all of the given tokens for it to be included in the active set).
	 * 
	 * @param s String to be checked against
	 */
	public void updateActiveSet(String s) {
		if(s == null || s.isEmpty()) {
			this.activeFiles = files;
		} else {
			s = s.toLowerCase();
			final String[] tokens = s.split("\\s+");
			
			this.activeFiles = new ArrayList<>();
			files.forEach(file -> {
				boolean matching = true;
				String lowercaseFilename = file.toString().toLowerCase();
				
				for(String token : tokens) {
					if(!lowercaseFilename.contains(token)) {
						matching = false;
						break;
					}
				}
				
				if(matching)
					activeFiles.add(file);
			});
		}
		
		fireContentsChanged(this, 0, activeFiles.size() - 1);
	}

}
