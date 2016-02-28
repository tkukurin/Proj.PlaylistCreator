package co.kukurin.gui.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Basic {@link ListModel} containing a list of file items and offering basic add/remove options.
 * The model additionally keeps track of an "active set" of items which at any given moment
 * stores a subset of the total list of items. See {@link #updateActiveSet(String)} for more info.
 * 
 * @author Toni Kukurin
 *
 */
public class FileListModel implements ListModel<File> {
	
	private List<ListDataListener> listeners;
	private List<File> files;
	private List<File> activeFiles;
	
	public FileListModel() {
		this.files = new ArrayList<>();
		this.activeFiles = files;
		
		this.listeners = new ArrayList<>();
	}

	@Override
	public int getSize() {
		return activeFiles.size();
	}

	@Override
	public File getElementAt(int index) {
		return activeFiles.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	
	public boolean add(File f) {
		boolean val = files.add(f);
		
		if(val) {
			listeners.forEach(l -> l.intervalAdded(
					new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, files.size() - 1, files.size() - 1)));
		}
		
		return val;
	}
	
	public List<File> getActiveFileset() {
		return activeFiles;
	}
	
	public boolean remove(File f) {
		for(int i = 0; i < files.size(); i++) {
			if(files.get(i).equals(f)) {
				files.remove(i);
				
				final int idx = i;
				listeners.forEach(l -> l.intervalRemoved(
						new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, idx, idx)));
				
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
		
		listeners.forEach(l -> l.contentsChanged(
				new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, activeFiles.size() - 1)));
	}

}
