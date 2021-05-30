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
package com.tiempometa.pandora.tagreader;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JTagReadPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6577799327052798518L;
	TagReadTableModel tableModel = new TagReadTableModel();

	public JTagReadPanel() {
		initComponents();
		tagReadTable.setModel(tableModel);
		tagReadTable.setAutoCreateRowSorter(true);
	}

	public void add(TagReadLog macshaTagRead) {
		tableModel.getTagReads().add(macshaTagRead);
		tableModel.fireTableDataChanged();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.tagreader.tagreader");
		label1 = new JLabel();
		scrollPane1 = new JScrollPane();
		tagReadTable = new JTable();

		//======== this ========
		setFont(new Font("Tahoma", Font.BOLD, 12));
		setLayout(new FormLayout(
			"7dlu, 407dlu, 7dlu",
			"2*(default, $lgap), default"));

		//---- label1 ----
		label1.setText(bundle.getString("JTagReadPanel.label1.text"));
		label1.setFont(new Font("Tahoma", Font.BOLD, 12));
		add(label1, CC.xy(2, 1));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(tagReadTable);
		}
		add(scrollPane1, CC.xy(2, 3));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel label1;
	private JScrollPane scrollPane1;
	private JTable tagReadTable;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
