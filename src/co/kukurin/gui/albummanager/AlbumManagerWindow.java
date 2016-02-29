package co.kukurin.gui.albummanager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
 * Manager window for the currently opened album set.
 * <p>
 * The manager receives all its data from static classes and the main window provided
 * via constructor; main content is being represented via {@link JSearchableAlbumList} instances.
 * 
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public class AlbumManagerWindow extends JFrame {
	
	private static final String WINDOW_TITLE = "Playlist album manager";
	
	private final MainWindow caller;
	
	private JSearchableAlbumList left;
	private JSearchableAlbumList right;
	
	private JButton saveBtn;
	
	/**
	 * Close operation; returns focus to and enables the caller.
	 */
	private WindowAdapter closeOp = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			caller.setEnabled(true);
			caller.requestFocus();
			
			AlbumManagerWindow.this.dispose();
		}
	};
	
	public AlbumManagerWindow(final MainWindow caller) throws IOException {
		this.caller = caller;

		initGui();
		SwingUtils.instanceDefaults(this, 640, 480);
		setLocationRelativeTo(caller);
		addWindowListener(closeOp);
		
		if(!PropertyManager.hasValidProperties())
			initWarningPanel();
		
		pack();
		setTitle(WINDOW_TITLE);
	}

	private void initGui() throws IOException {
		setLayout(new BorderLayout());
		
		initMainPanel();
		initBottomPanel();
	}

	private void initWarningPanel() {
		JPanel warning = SwingUtils.constructWarningPanel("NOTE: Properties have not been set, and the "
				+ "program may be looking for your music collection in the wrong folder.");
		add(warning, BorderLayout.NORTH);
	}

	private void initMainPanel() throws IOException {
		left = new JSearchableAlbumList("Loaded from disk", new File(PropertyManager.get(Constants.PROPERTY_MUSIC_LOCATION)));
		right = new JSearchableAlbumList("Currently in playlist", caller.getAlbumPaths());
		
		addLeftAndRightListeners();
		
		JPanel listContainer = new JPanel(new GridLayout(1, 2, 10, 0));
		listContainer.setBorder(SwingUtils.defaultMargin(SwingUtilities.LEFT,
				SwingUtilities.RIGHT, SwingUtilities.BOTTOM));
		
		listContainer.add(left, BorderLayout.WEST);
		listContainer.add(right, BorderLayout.EAST);
		add(listContainer, BorderLayout.CENTER);
	}

	private void addLeftAndRightListeners() {
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() != 2)
					return;
				
				copySelectedLeftToRight();
			}

		});
		
		left.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					copySelectedLeftToRight();
				}
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
		
		right.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DELETE) {
					right.remove(right.getSelectedItem());
				}
			}
		});
	}
	
	private void copySelectedLeftToRight() {
		File sel = left.getSelectedItem();
		
		if(sel != null)
			right.add(sel);
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
	
}
