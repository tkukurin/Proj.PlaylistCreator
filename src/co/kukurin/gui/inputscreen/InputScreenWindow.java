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
	}

	/**
	 * Initializes the display window.
	 */
	private void createPropertyDisplay() {
		Enumeration<Object> keys = PropertyManager.getKeys();
		Iterator<Object> values = PropertyManager.getValues().iterator();
		
		GroupLayoutCreator glc = new GroupLayoutCreator(getContentPane(), true);
		glc.setLinkColumn(0);
		
		while(keys.hasMoreElements()) {
			String currKey = keys.nextElement().toString();
			JLabel currKeyLabel = new JLabel(currKey);
			this.keys.add(currKey);
			
			String currValue = values.hasNext() ? values.next().toString() : "";
			JTextField currValueField = SwingUtils.defaultTextField(true, currValue);
			inputFields.add(currValueField);
			
			glc.addHorizontally(currKeyLabel, currValueField);
		}
		
		JButton confirmBtn = new JButton(new MainMenu.DefaultMenuAction("Confirm", "") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Iterator<JTextField> iter = InputScreenWindow.this.inputFields.iterator();
				InputScreenWindow.this.keys.forEach(key -> {
					PropertyManager.put(key, iter.next().getText());
				});
				
				PropertyManager.store(InputScreenWindow.this);
				InputScreenWindow.this.dispose();
			}
		});
		
		glc.addHorizontally(new JPanel(), confirmBtn);
		glc.doLayout();
	}
}
