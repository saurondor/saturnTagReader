/*
 * Created by JFormDesigner on Sat Feb 22 15:10:41 CST 2020
 */

package com.tiempometa.pandora.tagreader;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.timing.model.RawChipRead;

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
	}

	public void add(RawChipRead macshaTagRead) {
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
			"7dlu, 159dlu, 7dlu",
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
