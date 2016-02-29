package co.kukurin.gui.albummanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import co.kukurin.gui.actions.list.DeleteListItemOnDoubleClick;
import co.kukurin.gui.actions.list.DeleteListItemsOnKeypress;
import co.kukurin.gui.main.MainWindow;
import co.kukurin.gui.model.concrete.FileListModel;
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
	
	private static final int DEFAULT_HEIGHT = 480;
	private static final int DEFAULT_WIDTH = 640;
	
	private static final int MIN_WIDTH = 380;
	private static final int MIN_HEIGHT = 380;

	private static final String WINDOW_TITLE = "Playlist album manager";
	
	private final MainWindow caller;
	
	private JSearchableAlbumListComponent left;
	private JSearchableAlbumListComponent right;
	
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
		SwingUtils.instanceDefaults(this, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setLocationRelativeTo(caller);
		
		initListeners();
		setTitle(WINDOW_TITLE);
	}

	private void initGui() throws IOException {
		setLayout(new BorderLayout());
		
		if(PropertyManager.get(Constants.PROPERTY_MUSIC_LOCATION).isEmpty())
			initWarningPanel();
		
		initMainPanel();
		initBottomPanel();
	}

	private void initWarningPanel() {
		JPanel warning = SwingUtils.constructWarningPanel("NOTE: Properties have not been set, and the "
				+ "program may be looking for your music collection in the wrong folder.");
		add(warning, BorderLayout.NORTH);
	}

	private void initMainPanel() throws IOException {
		left = new JSearchableAlbumListComponent("Loaded from disk", new File(PropertyManager.get(Constants.PROPERTY_MUSIC_LOCATION)));
		right = new JSearchableAlbumListComponent("Currently in playlist", caller.getAlbumPaths());
		
		JPanel listContainer = new JPanel(new GridLayout(1, 2, 10, 0));
		listContainer.setBorder(SwingUtils.defaultMargin(SwingUtilities.LEFT,
				SwingUtilities.RIGHT, SwingUtilities.BOTTOM));
		
		listContainer.add(left, BorderLayout.WEST);
		listContainer.add(right, BorderLayout.EAST);
		add(listContainer, BorderLayout.CENTER);
	}
	
	private void initBottomPanel() {
		saveBtn = new JButton("Save and close");
		
		JPanel btnContainer = new JPanel(new BorderLayout());
		btnContainer.setBorder(SwingUtils.defaultMargin(SwingUtilities.LEFT,
				SwingUtilities.RIGHT, SwingUtilities.BOTTOM));
		
		saveBtn.addActionListener(evt -> {
			caller.updateAlbumList(right.getFileListModel().getEntireFileset());
			closeOp.windowClosing(null);
		});
		
		btnContainer.add(saveBtn, BorderLayout.WEST);
		add(btnContainer, BorderLayout.SOUTH);
	}
	
	private void initListeners() {
		addWindowListener(closeOp);
		SwingUtils.simulateExitOnEscape(left, this);
		SwingUtils.simulateExitOnEscape(right, this);
		SwingUtils.simulateExitOnEscape(saveBtn, this);
		
		addLeftAndRightListeners();
	}

	private void addLeftAndRightListeners() {
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				right.getFileList().setSelectedIndex(-1);
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
		
		right.addMouseListener(new DeleteListItemOnDoubleClick(right.getFileList(), right.getFileListModel()));
		right.addKeyListener(new DeleteListItemsOnKeypress(right.getFileList(), right.getFileListModel()));
	}
	
	private void copySelectedLeftToRight() {
		JList<File> leftList = left.getFileList();
		FileListModel leftModel = left.getFileListModel();
		
		int [] selIdx = leftList.getSelectedIndices();
		
		for(int index : selIdx) {
			if(index > leftModel.getSize())
				continue;
			
			File curr = leftModel.getElementAt(index);
			if(curr != null)
				right.getFileListModel().add(curr);
		}
	}
}
