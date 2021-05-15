/*
 * Copyright (c) 2019 Gerardo Esteban Tasistro Giubetic
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.tiempometa.pandora.gui;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author Gerardo Esteban Tasistro Giubetic
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
