/**
 * 
 */
package com.tiempometa.pandora.gui;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author gtasi
 * @param <E>
 *
 */
public class JPreviewFieldSettingComboBox extends JComboBox<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 281420026242170045L;
	private static final String[] MODEL = new String[] { "No mostrar", "En posici�n 1", "En posici�n 2", "En posici�n 3", "En posici�n 4", "En posici�n 5", "En posici�n 6", "En posici�n 7", "En posici�n 8", "En posici�n 9" };

	public JPreviewFieldSettingComboBox() {
		super();
		setModel(new DefaultComboBoxModel<String>(MODEL));
	}

	public JPreviewFieldSettingComboBox(ComboBoxModel<String> aModel) {
		super(aModel);
		// TODO Auto-generated constructor stub
	}

	public JPreviewFieldSettingComboBox(String[] items) {
		super(items);
		// TODO Auto-generated constructor stub
	}

	public JPreviewFieldSettingComboBox(Vector<String> items) {
		super(items);
		// TODO Auto-generated constructor stub
	}

}
