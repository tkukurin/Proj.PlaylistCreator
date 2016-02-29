package co.kukurin.gui.albummanager;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import co.kukurin.gui.actions.list.SelectListIndicesViaKeypress;
import co.kukurin.gui.model.concrete.FileListModel;
import co.kukurin.utils.layout.SwingUtils;

/**
 * A searchable album list, consisting of an optional title label, a search bar and a {@link JList} instance.
 * 
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public class JSearchableAlbumListComponent extends JPanel {
	
	private KeyAdapter updateActiveSetOnKeypress = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			((FileListModel)fileList.getModel()).updateActiveSet(searchField.getText());
		}
	};

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
	public JSearchableAlbumListComponent(String title) {
		if(title != null && !title.isEmpty())
			titleLabel = new JLabel(title);
		else
			titleLabel = null;
		
		fileList = new JList<>(new FileListModel());
		fileList.setFocusable(false);
		fileList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				searchField.requestFocus();
			}
		});
		
		searchField = new JTextField();
		searchLabel = new JLabel(SEARCH_IN_LIST);
		
		searchLabel.setLabelFor(searchField);
		
		initSearchField();
		initGui();
	}
	
	public JSearchableAlbumListComponent(String title, Collection<File> files) {
		this(title);
		
		Objects.requireNonNull(files);
		((FileListModel)fileList.getModel()).addAll(files);
	}
	
	public JSearchableAlbumListComponent(Collection<File> files) {
		this("", files);
	}
	
	public JSearchableAlbumListComponent(String title, File p) throws IOException {
		this(title);
	
		Objects.requireNonNull(p);
		traversePathAndAdd(p);
	}

	public JSearchableAlbumListComponent(File p) throws IOException {
		this("", p);
	}
	
	private void initSearchField() {
		searchField.setToolTipText(TOOLTIP_TEXT);
		addKeyListener(updateActiveSetOnKeypress);
		addKeyListener(new SelectListIndicesViaKeypress(fileList));
	}

	private void initGui() {
		setLayout(new BorderLayout(0, 5));
		
		JPanel upperWrap = new JPanel(new BorderLayout());
		upperWrap.setBorder(BorderFactory.createEmptyBorder());
		if(titleLabel != null) {
			titleLabel.setBorder(SwingUtils.defaultMargin(SwingUtilities.TOP, SwingUtilities.BOTTOM));
			SwingUtils.setHeader1(titleLabel);
			upperWrap.add(titleLabel, BorderLayout.NORTH);
		}
		upperWrap.add(searchLabel, BorderLayout.CENTER);
		upperWrap.add(searchField, BorderLayout.SOUTH);
		add(upperWrap, BorderLayout.NORTH);
		
		JScrollPane fileListContainer = new JScrollPane(fileList);
		add(fileListContainer, BorderLayout.CENTER);
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
	
	public JList<File> getFileList() {
		return fileList;
	}
	
	public FileListModel getFileListModel() {
		return (FileListModel) fileList.getModel();
	}
	
	// listener overrides
	
	@Override
	public synchronized void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
		fileList.addMouseListener(l);
	}
	
	@Override
	public synchronized void addKeyListener(KeyListener l) {
		super.addKeyListener(l);
		searchField.addKeyListener(l);
	}
	
}
