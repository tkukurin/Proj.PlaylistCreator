package co.kukurin.gui.albummanager;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
	private static final String SEARCH_IN_LIST = "Type query to search:";
	
	private JLabel label;
	private JTextField searchField;
	private JList<File> fileList;
	
	public JSearchableAlbumList() {
		fileList = new JList<>(new FileListModel());
		searchField = new JTextField();
		label = new JLabel(SEARCH_IN_LIST);
		
		label.setLabelFor(searchField);
		
		initSearchField();
		initGui();
	}
	
	public JSearchableAlbumList(Collection<File> files) {
		this();
		
		Objects.requireNonNull(files);
		((FileListModel)fileList.getModel()).addAll(files);
	}

	public JSearchableAlbumList(File p) throws IOException {
		this();
		
		Objects.requireNonNull(p);
		traversePathAndAdd(p);
	}
	
	private void initSearchField() {
		searchField.setToolTipText(TOOLTIP_TEXT);
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				((FileListModel)fileList.getModel()).updateActiveSet(searchField.getText());
			}
		});
	}

	private void initGui() {
		GroupLayoutCreator glc = new GroupLayoutCreator(this, true);
		glc.addHorizontally(label);
		glc.addHorizontally(searchField);
		
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

	public Collection<File> getAlbums() {
		return ((FileListModel)fileList.getModel()).getEntireFileset();
	}
	
}
