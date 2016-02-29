package co.kukurin.gui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

/**
 * A list model which offers the default Collection add/remove methods with corresponding listener invocations.
 * 
 * @author Toni Kukurin
 * @param <E>
 */
@SuppressWarnings("serial")
public class UpdateableListModel<E> extends AbstractListModel<E> {

	protected List<E> items;
	
	public UpdateableListModel() {
		this.items = new ArrayList<>();
	}
	
	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public E getElementAt(int index) {
		return items.get(index);
	}
	
	/**
	 * Adds all elements of given collection using {@link Collection#addAll(Collection)}.
	 * 
	 * @param items Elements to be added.
	 * @return Same as {@link Collection#addAll(Collection)}
	 */
	public boolean addAll(Collection<E> items) {
		int oldsiz = this.items.size();
		boolean result = this.items.addAll(items);
		
		if(result)
			SwingUtilities.invokeLater(() -> fireIntervalAdded(this, oldsiz, this.items.size() - 1));
		
		addCallback();
		return result;
	}
	
	/**
	 * Adds given item to collection using {@link List#add(Object)}.
	 * 
	 * @param item Item to be added.
	 * @return Same as {@link List#add(Object)}.
	 */
	public boolean add(E item) {
		boolean val = items.add(item);
		
		if(val)
			SwingUtilities.invokeLater(() -> fireIntervalAdded(this, items.size() - 1, items.size() - 1));
		
		addCallback();
		return val;
	}
	
	/**
	 * Removes all elements from internal collection which are also in given collection.
	 * 
	 * @param items Items to be removed.
	 * @return Same as {@link List#removeAll(Collection)}
	 */
	public boolean removeAll(Collection<E> items) {
		boolean result = this.items.removeAll(items);
		
		if(result) {
			int endIndexModified = this.items.size() - 1;
			
			if(endIndexModified < 0)
				endIndexModified = 0;
			
			final int endIndex = endIndexModified;
			SwingUtilities.invokeLater(() -> fireContentsChanged(this, 0, endIndex));
		}
		
		removeCallback();
		return result;
	}
	
	/**
	 * Removes given item from internal collection.
	 * 
	 * @param item item to be removed
	 * @return True if item has been found and will be removed; false otherwise.
	 */
	public boolean remove(E item) {
		for(int i = 0; i < items.size(); i++) {
			if(items.get(i).equals(item)) {
				remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes item at given index.
	 * 
	 * @param i Index which is to be removed.
	 * @return Same as {@link List#remove(int)}.
	 */
	public E remove(int i) {
		E val = items.remove(i);
		SwingUtilities.invokeLater(() -> fireIntervalRemoved(this, i, i));
		removeCallback();
		
		return val;
	}
	
	/**
	 * Method called after a given remove/removeAll invocation.
	 * <p>
	 * Does nothing by default, its purpose is to be overridden if necessary to provide internal callbacks
	 * after each content change.
	 */
	protected void removeCallback() {}
	
	/**
	 * Method called after a given add/addAll invocation.
	 * <p>
	 * Does nothing by default, its purpose is to be overridden if necessary to provide internal callbacks
	 * after each content change.
	 */
	protected void addCallback() {}

}
