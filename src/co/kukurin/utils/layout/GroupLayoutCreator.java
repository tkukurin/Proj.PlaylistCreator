package co.kukurin.utils.layout;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;

/**
 * Helper class designed to make dealing with {@link GroupLayout}s easier.
 * <p>
 * The class offers methods for linking sizes of various components and auto-adding
 * components aligned horizontally.
 * <p>
 * Implementation note: this class is not to be used as a class member variable since it would
 * put unnecessary strain on the machine's memory; where necessary, instantiate a
 * {@link GroupLayoutCreator} instance within a method and resolve screen layout
 * within the method.
 * 
 * @author Toni Kukurin
 *
 */
public class GroupLayoutCreator {
	
	/**
	 * Host for the layout
	 */
	private Container host;
	
	/**
	 * Layout being used.
	 */
	private GroupLayout gl;
	
	/**
	 * Parallel group of items - {@link SequentialGroup}s will be put in this instance
	 * using the {@link SequentialGroup#addGroup(javax.swing.GroupLayout.Group)} method.
	 */
	private ParallelGroup horizontal;
	
	/**
	 * Sequential group of items - {@link ParallelGroup}s should be put in this instance
	 * using the {@link SequentialGroup#addGroup(javax.swing.GroupLayout.Group)} method.
	 */
	private SequentialGroup vertical;
	
	/**
	 * Map containing a list of components which are to be linked.
	 */
	Map<Integer, List<Component>> linkedComponents;
	
	/**
	 * Same as calling {@link #GroupLayoutCreator(Container, false)}
	 * 
	 * @param host Host which will be used for layout. Must be non-null.
	 */
	public GroupLayoutCreator(Container host) {
		this(host, false);
	}
	
	/**
	 * Default constructor; creates and assigns a {@link GroupLayout} to
	 * this host.
	 * 
	 * @param host Host which the layout will be assigned to. Must be non-null.
	 * @param autoGaps Value for the {@link GroupLayout#getAutoCreateGaps()} method.
	 */
	public GroupLayoutCreator(Container host, boolean autoGaps) {
		Objects.requireNonNull(host, "Host should be non-null!");
		
		this.gl = new GroupLayout(host);
		this.horizontal = gl.createParallelGroup();
		this.vertical = gl.createSequentialGroup();
		this.linkedComponents = new HashMap<>();
		this.host = host;
		
		// TODO
		gl.setAutoCreateGaps(autoGaps);
		gl.setAutoCreateContainerGaps(true);
		
		host.setLayout(gl);
	}
	
	/**
	 * Adds given components to the layout horizontally, as given by item order.
	 * <p>
	 * As an example, calling the method with {@code c1, c2, c3} will create a layout
	 * where {@code c1} is the leftmost component, {@code c2} middle and {@code c3} rightmost component.
	 * <p>
	 * Also note that, if {@link #setLinkColumn(int...)} must be called prior to using this method in
	 * case you would like component widths to be linked together.
	 * 
	 * @param components List of components to be added.
	 */
	public void addHorizontally(JComponent ... components) {
		Objects.requireNonNull(components, "Please provide at least one component");
		
		SequentialGroup seq = gl.createSequentialGroup();
		ParallelGroup par = gl.createParallelGroup(GroupLayout.Alignment.CENTER);
		
		for(int i = 0; i < components.length; i++) {
			JComponent c = components[i];
			seq.addComponent(c);
			par.addComponent(c);
			host.add(c);
			
			if(linkedComponents.get(i) != null)
				linkedComponents.get(i).add(c);
		}
		
		horizontal.addGroup(seq);
		vertical.addGroup(par);
	}
	
	/**
	 * Provide a 0-based list of indices here in order to link widths for given columns.
	 * <p>
	 * For instance, giving 0 as the argument will make sure any subsequent items
	 * which are added as the first component in {@link #addHorizontally(JComponent...)}
	 * have the same width.
	 * 
	 * @param indices Indices which are to be linked
	 * @see GroupLayout#linkSize(Component...)
	 */
	public void setLinkColumn(int ... indices) {
		Objects.requireNonNull(indices, "Please provide at least one index!");
		
		for(int i : indices) {
			linkedComponents.put(i, new ArrayList<>());
		}
	}
	
	/**
	 * Sets up the layout using items provided thus far. Must be called once all items are added
	 * using {@link #addHorizontally(JComponent...)}.
	 * <p>
	 * In case {@link #setLinkColumn(int...)} has been used, this method will automaticall link
	 * all requested items before displaying them onscreen.
	 */
	public void doLayout() {
		gl.setHorizontalGroup(horizontal);
		gl.setVerticalGroup(vertical);
		
		linkedComponents.forEach((k,v) ->
			gl.linkSize(v.toArray(new Component[v.size()]))
		);
	}
}
