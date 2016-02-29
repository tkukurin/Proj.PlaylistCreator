package co.kukurin.gui.actions.list;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JList;

public class SelectListIndicesViaKeypress extends KeyAdapter {
	
	private JList<?> list;
	
	private boolean shiftPressed;
	private boolean isDownward;
	
	public SelectListIndicesViaKeypress(JList<?> list) {
		this.list = list;
		this.isDownward = false;
		this.shiftPressed = false;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SHIFT)
			shiftPressed = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftPressed = false;
			return;
		}
		
		int[] selIdx = list.getSelectedIndices();
		int listSize = list.getModel().getSize();
		
		if(selIdx == null || selIdx.length == 0) {
			selIdx = new int[] { -1 };
		}
		
		int newSelection = selIdx[0];
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			if(selIdx.length == 1)
				isDownward = true;
			
			if(selIdx[0] >= listSize - 1 && selIdx.length == 1)
				selIdx[0] = -1;
			
			selIdx[selIdx.length - 1]++;
			if(shiftPressed) {
				if(isDownward) {
					if(selIdx[selIdx.length - 1] == listSize)
						return;
					
					newSelection = selIdx[selIdx.length - 1];
					list.addSelectionInterval(newSelection, newSelection);
				} else {
					newSelection = selIdx[0];
					list.removeSelectionInterval(newSelection, newSelection);
				}
			} else {
				newSelection = selIdx[selIdx.length -1];
				list.setSelectedIndex(newSelection);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_UP) {
			if(selIdx.length == 1)
				isDownward = false;
			
			if(selIdx[0] <= 0 && selIdx.length == 1) {
				if(shiftPressed)
					return;
				
				selIdx[0] = listSize;
			}
			
			selIdx[0]--;
			if(shiftPressed) {
				if(isDownward) {
					newSelection = selIdx[selIdx.length - 1];
					list.removeSelectionInterval(newSelection, newSelection);
				} else {
					newSelection = selIdx[0];
					list.addSelectionInterval(newSelection, newSelection);
				}
			} else {
				newSelection = selIdx[0];
				list.setSelectedIndex(newSelection);
			}
		}
		
		list.ensureIndexIsVisible(newSelection);
	}
}
