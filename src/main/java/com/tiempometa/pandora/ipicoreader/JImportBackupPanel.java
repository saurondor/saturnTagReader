/*
 * Created by JFormDesigner on Sat Feb 22 15:31:19 CST 2020
 */

package com.tiempometa.pandora.ipicoreader;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JImportBackupPanel extends JPanel {
	public JImportBackupPanel() {
		initComponents();
	}

	private void fileOpenButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void applyCheckPointButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void importButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.ipicoreader.ipicoreader");
		label1 = new JLabel();
		fileLabel = new JLabel();
		fileOpenButton = new JButton();
		scrollPane1 = new JScrollPane();
		tagReadsTable = new JTable();
		checkPointComboBox = new JComboBox();
		applyCheckPointButton = new JButton();
		importButton = new JButton();

		//======== this ========
		setLayout(new FormLayout(
			"8dlu, $lcgap, 334dlu",
			"8dlu, $lgap, default, $lgap, 12dlu, $lgap, default, $lgap, 58dlu, $lgap, 15dlu, 2*($lgap, default)"));

		//---- label1 ----
		label1.setText(bundle.getString("JImportBackupPanel.label1.text"));
		add(label1, CC.xy(3, 3));
		add(fileLabel, CC.xy(3, 5));

		//---- fileOpenButton ----
		fileOpenButton.setText(bundle.getString("JImportBackupPanel.fileOpenButton.text"));
		fileOpenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileOpenButtonActionPerformed(e);
			}
		});
		add(fileOpenButton, CC.xy(3, 7));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(tagReadsTable);
		}
		add(scrollPane1, CC.xy(3, 9));

		//---- checkPointComboBox ----
		checkPointComboBox.setEnabled(false);
		add(checkPointComboBox, CC.xy(3, 11));

		//---- applyCheckPointButton ----
		applyCheckPointButton.setText(bundle.getString("JImportBackupPanel.applyCheckPointButton.text"));
		applyCheckPointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyCheckPointButtonActionPerformed(e);
			}
		});
		add(applyCheckPointButton, CC.xy(3, 13));

		//---- importButton ----
		importButton.setText(bundle.getString("JImportBackupPanel.importButton.text"));
		importButton.setEnabled(false);
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importButtonActionPerformed(e);
			}
		});
		add(importButton, CC.xy(3, 15));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private JLabel fileLabel;
	private JButton fileOpenButton;
	private JScrollPane scrollPane1;
	private JTable tagReadsTable;
	private JComboBox checkPointComboBox;
	private JButton applyCheckPointButton;
	private JButton importButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
