package co.kukurin.gui.inputscreen;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import co.kukurin.gui.actions.DefaultMenuAction;
import co.kukurin.gui.main.MainWindow;
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
	
	private boolean invokeMainOnClose;
	
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
	public InputScreenWindow(boolean invokeMainOnClose) {
		this();
		
		setLocationRelativeTo(null);
		this.invokeMainOnClose = invokeMainOnClose;
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
		addObjectsFromPropertyValues(keys, values, glc, confirmBtn);

		glc.addHorizontally(new JPanel(), confirmBtn);
		glc.doLayout();
	}

	private void addObjectsFromPropertyValues(Enumeration<Object> keys, Iterator<Object> values,
			GroupLayoutCreator glc, JButton confirmBtn) {
		while(keys.hasMoreElements()) {
			String currKey = keys.nextElement().toString();
			JLabel currKeyLabel = new JLabel(PropertyManager.makePropertyHumanReadable(currKey));
			this.keys.add(currKey);
			
			String currValue = values.hasNext() ? values.next().toString() : "";
			JTextField currValueField = SwingUtils.defaultTextField(true, currValue);
			inputFields.add(currValueField);
			
			SwingUtils.simulateClickOnEnter(currValueField, confirmBtn);
			SwingUtils.simulateExitOnEscape(currValueField, this);
			
			glc.addHorizontally(currKeyLabel, currValueField);
		}
	}

	private JButton getConfirmBtn() {
		return new JButton(new DefaultMenuAction("Confirm", "") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Iterator<JTextField> iter = InputScreenWindow.this.inputFields.iterator();
				InputScreenWindow.this.keys.forEach(key -> {
					PropertyManager.put(key, iter.next().getText());
				});
				
				PropertyManager.store(InputScreenWindow.this);
				InputScreenWindow.this.dispose();
				
				if(invokeMainOnClose)
					SwingUtilities.invokeLater(() -> new MainWindow());
			}
		});
	}
	
	
}
