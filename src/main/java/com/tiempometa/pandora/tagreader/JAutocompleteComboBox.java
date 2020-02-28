/**
 * 
 */
package com.tiempometa.pandora.tagreader;

import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

/**
 * @author gtasi
 *
 */
public class JAutocompleteComboBox extends JComboBox<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3417787122957006433L;
	private static final Logger logger = Logger.getLogger(JAutocompleteComboBox.class);

	private String[] options = new String[0];
	private DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(options);

	public void setOptions(String[] options) {
		this.options = options;
		model = new DefaultComboBoxModel<String>(this.options);
		setModel(model);
	}

	/**
	 * 
	 */
	private void addAutocompleteListener() {
		JTextComponent editor = (JTextComponent) this.getEditor().getEditorComponent();
		editor.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				logger.debug("KeyTyped");
			}

			@Override
			public void keyReleased(KeyEvent e) {
				logger.debug("** KeyReleased " + e);
				switch (e.getKeyChar()) {
				case KeyEvent.CHAR_UNDEFINED:

					break;

				default:
					if ((e.getModifiers() & KeyEvent.CTRL_MASK) == 0) {
						switch (e.getKeyCode()) {
						case KeyEvent.VK_DOWN:
						case KeyEvent.VK_KP_DOWN:
						case KeyEvent.VK_UP:
						case KeyEvent.VK_KP_UP:
						case KeyEvent.VK_ENTER:
							String selectedItem = (String) getSelectedItem();
							String lookupItem = lookupItem(editor.getText());
							logger.debug("Selected item on enter or keydown/up is " + selectedItem
									+ " - lookup item is " + lookupItem);
							editor.setText(lookupItem);
							if (isDisplayable())
								setPopupVisible(true);
							break;

						default:
							String text = editor.getText();
							List<String> items = filterItems(text);
							model = new DefaultComboBoxModel<String>(items.toArray(new String[items.size()]));
							setModel(model);
							editor.setText(text);
							if (isDisplayable())
								setPopupVisible(true);
							break;
						}
					}

					break;
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				logger.debug("KeyPressed");

			}

		});
	}

	private List<String> filterItems(String pattern) {
		List<String> list = new ArrayList<String>();
		logger.debug("Looking up item by pattern " + pattern + " in options list with size " + options.length);
		for (int i = 0, n = options.length; i < n; i++) {
			String currentItem = options[i];
			logger.debug("Matching " + currentItem + " to pattern " + pattern);
			if ((currentItem != null) && currentItem.startsWith(pattern)) {
				logger.debug("Found item " + currentItem);
				list.add(currentItem);
			}
		}
		return list;
	}

	private String lookupItem(String pattern) {
		// iterate over all items
		logger.debug("Looking up item by pattern " + pattern);
		for (int i = 0, n = model.getSize(); i < n; i++) {
			String currentItem = model.getElementAt(i);
			// current item starts with the pattern?
			if (currentItem.toString().startsWith(pattern)) {
				logger.debug("Found item " + currentItem);
				setSelectedIndex(i);
				return currentItem;
			}
		}
		// no item starts with the pattern => return null
		return null;
	}

	public JAutocompleteComboBox() {
		super();
		setEditable(true);
		addAutocompleteListener();
	}

	public JAutocompleteComboBox(ComboBoxModel<String> aModel) {
		super(aModel);
		setEditable(true);
		addAutocompleteListener();
	}

	public JAutocompleteComboBox(String[] items) {
		super(items);
		setEditable(true);
		addAutocompleteListener();
	}

	public JAutocompleteComboBox(Vector<String> items) {
		super(items);
		setEditable(true);
		addAutocompleteListener();
	}

	@Override
	public synchronized void addFocusListener(FocusListener l) {
		// TODO Auto-generated method stub
		super.addFocusListener(l);
		JTextComponent editor = (JTextComponent) this.getEditor().getEditorComponent();
		editor.addFocusListener(l);
	}

}
