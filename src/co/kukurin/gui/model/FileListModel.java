package co.kukurin.gui.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Basic {@link ListModel} containing a list of file items and offering basic add/remove options.
 * The model additionally keeps track of an "active set" of items which at any given moment
 * stores a subset of the total list of items. See {@link #updateActiveSet(String)} for more info.
 * 
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public class FileListModel extends UpdateableListModel<File> {
	
	private List<File> activeFiles;
	private String lastFilter;
	
	public FileListModel() {
		this.activeFiles = this.items;
		lastFilter = null;
	}

	@Override
	public int getSize() {
		return activeFiles.size();
	}

	@Override
	public File getElementAt(int index) {
		return activeFiles.get(index);
	}
	
	public List<File> getActiveFileset() {
		return activeFiles;
	}
	
	public Collection<File> getEntireFileset() {
		return items;
	}
	
	@Override
	protected void addCallback() {
		updateActiveSet(lastFilter);
	}
	
	@Override
	protected void removeCallback() {
		updateActiveSet(lastFilter);
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
			this.activeFiles = items;
		} else {
			s = s.toLowerCase();
			final String[] tokens = s.split("\\s+");
			
			this.activeFiles = new ArrayList<>();
			items.forEach(file -> {
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
		
		lastFilter = s;
		fireContentsChanged(this, 0, activeFiles.size() - 1);
	}

}
