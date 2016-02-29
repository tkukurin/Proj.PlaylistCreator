package co.kukurin.gui.actions.list;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

import co.kukurin.gui.model.UpdateableListModel;

public class DeleteListItemOnDoubleClick extends MouseAdapter {
	private JList<?> list;
	private UpdateableListModel<?> model;
	
	public DeleteListItemOnDoubleClick(JList<?> list, UpdateableListModel<?> model) {
		this.list = list;
		this.model = model;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() != 2)
			return;
		
		int sel = list.getSelectedIndex();
		if(sel >= 0)
			model.remove(sel);
		
		list.setSelectedIndex(-1);
	}
}
