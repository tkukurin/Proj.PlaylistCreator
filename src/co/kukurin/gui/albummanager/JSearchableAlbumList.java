package co.kukurin.gui.albummanager;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import co.kukurin.gui.model.FileListModel;
import co.kukurin.utils.layout.GroupLayoutCreator;

@SuppressWarnings("serial")
public class JSearchableAlbumList extends JPanel {

	private static final String TOOLTIP_TEXT = "Empty string will display all entries";
	private static final String SEARCH_IN_LIST = "Search:";
	
	private JLabel titleLabel;
	private JLabel searchLabel;
	private JTextField searchField;
	private JList<File> fileList;
	
	/**
	 * Creates the default searchable album list instance, along with given title.
	 * <p>
	 * In case the title is null or empty, no title will be displayed.
	 * 
	 * @param title
	 */
	public JSearchableAlbumList(String title) {
		if(title != null && !title.isEmpty())
			titleLabel = new JLabel(title);
		else
			titleLabel = null;
		
		fileList = new JList<>(new FileListModel());
		fileList.setFocusable(false);
		
		searchField = new JTextField();
		searchLabel = new JLabel(SEARCH_IN_LIST);
		
		searchLabel.setLabelFor(searchField);
		
		initSearchField();
		initGui();
	}
	
	public JSearchableAlbumList(String title, Collection<File> files) {
		this(title);
		
		Objects.requireNonNull(files);
		((FileListModel)fileList.getModel()).addAll(files);
	}
	
	public JSearchableAlbumList(Collection<File> files) {
		this("", files);
	}
	
	public JSearchableAlbumList(String title, File p) throws IOException {
		this(title);
	
		Objects.requireNonNull(p);
		traversePathAndAdd(p);
	}

	public JSearchableAlbumList(File p) throws IOException {
		this("", p);
	}
	
	private void initSearchField() {
		searchField.setToolTipText(TOOLTIP_TEXT);
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				((FileListModel)fileList.getModel()).updateActiveSet(searchField.getText());
				
				int selIdx = fileList.getSelectedIndex();
				int listSize = fileList.getModel().getSize();
				
				if(selIdx >= listSize)
					selIdx = -1;
				
				if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					if(selIdx >= listSize - 1)
						selIdx = -1;
					
					selIdx++;
					fileList.setSelectedIndex(selIdx);
				} else if(e.getKeyCode() == KeyEvent.VK_UP) {
					if(selIdx <= 0)
						selIdx = listSize;
						
					selIdx--;
					fileList.setSelectedIndex(selIdx);
				}
				
				fileList.ensureIndexIsVisible(selIdx);
			}
		});
	}

	private void initGui() {
		GroupLayoutCreator glc = new GroupLayoutCreator(this, true);
		
		if(titleLabel != null)
			glc.addHorizontally(titleLabel);
		
		glc.addHorizontally(searchLabel, searchField);
		// glc.addHorizontally(searchField);
		
		JScrollPane fileListContainer = new JScrollPane(fileList);
		fileListContainer.setPreferredSize(new Dimension(300, 500));
		glc.addHorizontally(fileListContainer);
		
		glc.doLayout();
	}
	
	private void traversePathAndAdd(File p) throws IOException {
		FileListModel model = (FileListModel) fileList.getModel();
		
		Files.walkFileTree(p.toPath(), new SimpleFileVisitor<Path>() {
			Set<Path> containsFiles = new HashSet<>();
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				containsFiles.add(file.getParent());
				return super.visitFile(file, attrs);
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if(containsFiles.contains(dir))
					model.add(dir.toFile());
				return super.postVisitDirectory(dir, exc);
			}
		});
	}
	
	public File getSelectedItem() {
		if(fileList.getSelectedIndex() >= fileList.getModel().getSize()
				|| fileList.getSelectedIndex() < 0)
			return null;
		
		return fileList.getSelectedValue();
	}

	public void add(File selectedItem) {
		((FileListModel) fileList.getModel()).add(selectedItem);
	}
	
	public void remove(File item) {
		((FileListModel) fileList.getModel()).remove(item);
	}
	
	@Override
	public synchronized void addMouseListener(MouseListener l) {
		fileList.addMouseListener(l);
	}
	
	@Override
	public synchronized void addKeyListener(KeyListener l) {
		super.addKeyListener(l);
		searchField.addKeyListener(l);
	}

	public Collection<File> getAlbums() {
		return ((FileListModel)fileList.getModel()).getEntireFileset();
	}
	
}
