package co.kukurin.gui.albummanager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import co.kukurin.gui.main.MainWindow;
import co.kukurin.utils.Constants;
import co.kukurin.utils.PropertyManager;
import co.kukurin.utils.layout.SwingUtils;

/**
 * Manager for the current album set.
 * 
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public class AlbumManagerWindow extends JFrame {
	
	private MainWindow caller;
	
	private JSearchableAlbumList left;
	private JSearchableAlbumList right;
	
	private JButton saveBtn;
	
	private WindowAdapter closeOp = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			caller.setEnabled(true);
			caller.requestFocus();
			AlbumManagerWindow.this.dispose();
		}
	};
	
	public AlbumManagerWindow(MainWindow caller) throws IOException {
		this.caller = caller;

		initGui();
		SwingUtils.instanceDefaults(this);
		setLocationRelativeTo(caller);
		addWindowListener(closeOp);
		
		pack();
	}

	private void initGui() throws IOException {
		setLayout(new BorderLayout());
		
		initMidPanel();
		initBottomPanel();
	}

	private void initBottomPanel() {
		saveBtn = new JButton("Save and close");
		
		JPanel btnContainer = new JPanel();
		btnContainer.setBorder(SwingUtils.defaultMargin(SwingUtilities.LEFT,
				SwingUtilities.RIGHT, SwingUtilities.BOTTOM));
		
		saveBtn.addActionListener(evt -> {
			caller.updateAlbumList(right.getAlbums());
			closeOp.windowClosing(null);
		});
		
		btnContainer.add(saveBtn);
		add(btnContainer, BorderLayout.SOUTH);
	}

	private void initMidPanel() throws IOException {
		left = new JSearchableAlbumList(new File(PropertyManager.get(Constants.PROPERTY_OPEN_LOCATION)));
		right = new JSearchableAlbumList(caller.getAlbumPaths());
		
		addListMouseListeners();
		
		JPanel listContainer = new JPanel(new GridLayout(1, 2, 10, 0));
		listContainer.setBorder(SwingUtils.defaultMargin(SwingUtilities.LEFT,
				SwingUtilities.RIGHT, SwingUtilities.BOTTOM));
		
		listContainer.add(left, BorderLayout.WEST);
		listContainer.add(right, BorderLayout.EAST);
		add(listContainer, BorderLayout.CENTER);
	}

	private void addListMouseListeners() {
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() != 2)
					return;
				
				right.add(left.getSelectedItem());
			}
		});
		
		right.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() != 2)
					return;
				
				right.remove(right.getSelectedItem());
			}
		});
	}
	
}
