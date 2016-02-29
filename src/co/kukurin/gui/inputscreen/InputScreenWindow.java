package co.kukurin.gui.inputscreen;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import co.kukurin.gui.main.MainMenu;
import co.kukurin.utils.PropertyManager;
import co.kukurin.utils.layout.GroupLayoutCreator;
import co.kukurin.utils.layout.SwingUtils;

/**
 * Property editor window.
 * <p>
 * Retrieves its values from {@link PropertyManager} and displays their current states
 * in editable textfields.
 * 
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public class InputScreenWindow extends JFrame {
	
	private static final String WINDOW_TITLE = "Set properties";
	
	/**
	 * This object may be null; in case it isn't, the object provided here must be
	 * notified using Java's default {@link Object#notify()} method after this
	 * window is disposed.
	 */
	private Object notifyUponCompletion;
	
	/**
	 * Crude and simple way of handling intial call to the properties window.
	 * <p>
	 * In case window is closed by pressing the confirm button, this listener will be removed; otherwise,
	 * the listener will be called and will exit the program altogether.
	 */
	private WindowAdapter syncDisposeListener = new WindowAdapter() {
		@Override
		public void windowClosed(WindowEvent e) {
			// crude, but easy way out.
			System.exit(0);
		}
	};
	
	private List<String> keys;
	private List<JTextField> inputFields;
	
	/**
	 * Default constructor.
	 */
	public InputScreenWindow() {
		inputFields = new LinkedList<>();
		keys = new LinkedList<>();
		
		createPropertyDisplay();
		SwingUtils.instanceDefaults(this);
		
		setResizable(false);
		setTitle(WINDOW_TITLE);
	}

	/**
	 * Input screen invocation used when another thread needs to be notified after the
	 * property defaults have been set.
	 * 
	 * @param syncObject Object on which the synchronization takes place.
	 */
	public InputScreenWindow(Object syncObject) {
		this();
		
		this.notifyUponCompletion = syncObject;
		setLocationRelativeTo(null);
		
		addWindowListener(syncDisposeListener);
	}

	/**
	 * Initializes the display window.
	 */
	private void createPropertyDisplay() {
		Enumeration<Object> keys = PropertyManager.getKeys();
		Iterator<Object> values = PropertyManager.getValues().iterator();
		
		GroupLayoutCreator glc = new GroupLayoutCreator(getContentPane(), true);
		glc.setLinkColumn(0);
		
		JButton confirmBtn = getConfirmBtn();
		
		while(keys.hasMoreElements()) {
			String currKey = keys.nextElement().toString();
			JLabel currKeyLabel = new JLabel(currKey);
			this.keys.add(currKey);
			
			String currValue = values.hasNext() ? values.next().toString() : "";
			JTextField currValueField = SwingUtils.defaultTextField(true, currValue);
			inputFields.add(currValueField);
			
			SwingUtils.simulateClickOnEnter(currValueField, confirmBtn);
			SwingUtils.simulateExitOnEscape(currValueField, this);
			
			glc.addHorizontally(currKeyLabel, currValueField);
		}
		
		glc.addHorizontally(new JPanel(), confirmBtn);
		glc.doLayout();
	}

	private JButton getConfirmBtn() {
		return new JButton(new MainMenu.DefaultMenuAction("Confirm", "") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Iterator<JTextField> iter = InputScreenWindow.this.inputFields.iterator();
				InputScreenWindow.this.keys.forEach(key -> {
					PropertyManager.put(key, iter.next().getText());
				});
				
				PropertyManager.store(InputScreenWindow.this);
				InputScreenWindow.this.removeWindowListener(syncDisposeListener);
				InputScreenWindow.this.dispose();
				
				if(notifyUponCompletion != null) {
					synchronized(notifyUponCompletion) {
						notifyUponCompletion.notify();
					}
				}
			}
		});
	}
	
	
}
