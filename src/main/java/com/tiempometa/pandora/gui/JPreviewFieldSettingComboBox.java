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
	private static final String[] MODEL = new String[] { "No mostrar", "En posición 1", "En posición 2", "En posición 3", "En posición 4", "En posición 5", "En posición 6", "En posición 7", "En posición 8", "En posición 9" };

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
