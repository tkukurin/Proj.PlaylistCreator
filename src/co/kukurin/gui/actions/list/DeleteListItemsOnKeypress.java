package co.kukurin.gui.actions.list;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JList;

import co.kukurin.gui.model.UpdateableListModel;

public class DeleteListItemsOnKeypress extends KeyAdapter {
	
	private JList<?> list;
	private UpdateableListModel<?> model;
	
	public DeleteListItemsOnKeypress(JList<?> list, UpdateableListModel<?> model) {
		this.list = list;
		this.model = model;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int kc = e.getKeyCode();
		
		if(kc == KeyEvent.VK_DELETE || kc == KeyEvent.VK_ENTER) {
			int[] selected = list.getSelectedIndices();
			for(int i = selected.length - 1; i >= 0; i--)
				model.remove(selected[i]);
			
			list.setSelectedIndex(-1);
		}
	}
}
