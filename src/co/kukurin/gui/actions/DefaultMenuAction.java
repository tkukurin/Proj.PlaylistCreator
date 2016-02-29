package co.kukurin.gui.actions;

import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * Helper class which automatically defines a shortcut key and name for given action object.
 * @author Toni Kukurin
 *
 */
@SuppressWarnings("serial")
public abstract class DefaultMenuAction extends AbstractAction {
	
	public DefaultMenuAction(String name, String key) {
		Objects.requireNonNull(name, "name must be a valid string!");
		Objects.requireNonNull(key, "a key combination must be provded");
		
		putValue(NAME, name);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(key));
	}
	
}
