package co.kukurin.gui;

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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import co.kukurin.gui.model.FileListModel;
import co.kukurin.utils.SwingUtils;
import co.kukurin.utils.GroupLayoutCreator;

/**
 * Window opened when the "From String" button is clicked in the main
 * program.
 * 
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public class FromStringWindow extends JFrame {
	
	private static final Dimension LIST_PANEL_SIZE = new Dimension(300, 500);

	private MainWindow caller;
	
	private JTextField searchField;
	private JButton searchBtn;
	
	private JList<File> left;
	private JList<File> right;
	
	private WindowAdapter closeOp = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			caller.setEnabled(true);
			caller.requestFocus();
			FromStringWindow.this.dispose();
		}
	};
	
	public FromStringWindow(MainWindow caller) {
		this.caller = caller;

		initGui();
		SwingUtils.instanceDefaults(this);
		setLocationRelativeTo(caller);
		addWindowListener(closeOp);
		
		pack();
	}

	private void initGui() {
		setLayout(new BorderLayout());
		
		initTopPanel();
		initMidPanel();
		initBottomPanel();
	}

	private void initBottomPanel() {
		JButton exitBtn = new JButton("Save and close");
		
		exitBtn.addActionListener(evt -> {
			((FileListModel)right.getModel()).getActiveFileset().forEach(file -> {
				caller.addAlbum(file);
			});
			
			closeOp.windowClosing(null);
		});
		
		add(exitBtn, BorderLayout.SOUTH);
	}

	private void initMidPanel() {
		left = new JList<>(new FileListModel());
		right = new JList<>(new FileListModel());
		
		populateLeftList();
		addListMouseListeners();
		
		JPanel listContainer = new JPanel(new GridLayout(1, 2, 10, 0));
		listContainer.setBorder(SwingUtils.defaultMargin(SwingUtilities.LEFT,
				SwingUtilities.RIGHT, SwingUtilities.BOTTOM));
		
		JScrollPane leftSp = new JScrollPane(left);
		leftSp.setPreferredSize(LIST_PANEL_SIZE);
		
		listContainer.add(leftSp, BorderLayout.WEST);
		listContainer.add(new JScrollPane(right), BorderLayout.EAST);
		add(listContainer, BorderLayout.CENTER);
	}

	private void addListMouseListeners() {
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() != 2)
					return;
				
				((FileListModel)right.getModel()).add(left.getSelectedValue());
			}
		});
		
		right.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() != 2)
					return;
				
				((FileListModel)right.getModel()).remove(right.getSelectedValue());
			}
		});
	}

	private void initTopPanel() {
		JPanel topPanel = new JPanel(new BorderLayout());
		GroupLayoutCreator glc = new GroupLayoutCreator(topPanel, true);
		
		JLabel albumsToFindLabel = new JLabel("Search albums by name...");
		searchField = SwingUtils.defaultTextField(true, "");
		searchBtn = new JButton("Search");
		initTopPanelListeners();
		
		glc.addHorizontally(albumsToFindLabel, searchField, searchBtn);
		glc.doLayout();
		add(topPanel, BorderLayout.NORTH);
	}

	private void initTopPanelListeners() {
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					searchBtn.doClick();
			}
		});
		
		searchBtn.addActionListener(evt -> {
			FileListModel leftModel = ((FileListModel)left.getModel());
			leftModel.updateActiveSet(searchField.getText());
		});
	}

	private void populateLeftList() {
		FileListModel leftModel = (FileListModel) left.getModel();
		String baseLocStr = caller.getBaseLocationStr();
		
		for(String token : baseLocStr.split(File.pathSeparator)) {
			try {
				File loc = new File(token);
				Files.walkFileTree(loc.toPath(), new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						leftModel.add(dir.toFile());
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				JOptionPane.showMessageDialog(caller, "Error retrieving directories from " + token);
			}
		}
	}
	
}
