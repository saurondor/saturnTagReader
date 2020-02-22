/*
 * Created by JFormDesigner on Sat Feb 22 14:40:34 CST 2020
 */

package com.tiempometa.pandora.ipicoreader;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JIpicoReaderPanel extends JPanel {
	public JIpicoReaderPanel() {
		initComponents();
	}

	private void checkPointComboBoxItemStateChanged(ItemEvent e) {
		// TODO add your code here
	}

	private void connectButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void applyCheckpointButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void setTimeButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void removeReaderButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.ipicoreader.ipicoreader");
		label1 = new JLabel();
		readerAddressTextField = new JTextField();
		connectButton = new JButton();
		setTimeButton = new JButton();
		removeReaderButton = new JButton();
		label4 = new JLabel();
		textField5 = new JTextField();
		label2 = new JLabel();
		comboBox1 = new JComboBox<>();
		textField1 = new JTextField();
		textField2 = new JTextField();
		checkPointComboBox1 = new JComboBox();
		applyCheckpointButton = new JButton();
		label3 = new JLabel();
		textField3 = new JTextField();
		textField4 = new JTextField();
		checkPointComboBox2 = new JComboBox();
		label5 = new JLabel();
		tagsReadLabel = new JLabel();

		// ======== this ========
		setBorder(new TitledBorder(bundle.getString("JIpicoReaderPanel.this.border")));
		setLayout(new FormLayout(
				"5dlu, $lcgap, default, $lcgap, 57dlu, 2*($lcgap, 15dlu), $lcgap, 84dlu, $lcgap, default, $lcgap, 19dlu, $lcgap, default",
				"5dlu, 4*($lgap, default)"));

		// ---- label1 ----
		label1.setText(bundle.getString("JIpicoReaderPanel.label1.text"));
		add(label1, CC.xy(3, 3));
		add(readerAddressTextField, CC.xywh(5, 3, 5, 1));

		// ---- connectButton ----
		connectButton.setText(bundle.getString("JIpicoReaderPanel.connectButton.text"));
		connectButton.setBackground(Color.red);
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectButtonActionPerformed(e);
			}
		});
		add(connectButton, CC.xy(11, 3));

		// ---- setTimeButton ----
		setTimeButton.setText(bundle.getString("JIpicoReaderPanel.setTimeButton.text"));
		setTimeButton.setEnabled(false);
		setTimeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTimeButtonActionPerformed(e);
			}
		});
		add(setTimeButton, CC.xy(13, 3));

		// ---- removeReaderButton ----
		removeReaderButton
				.setIcon(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/ipicoreader/x-remove.png")));
		removeReaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeReaderButtonActionPerformed(e);
			}
		});
		add(removeReaderButton, CC.xy(17, 3));

		// ---- label4 ----
		label4.setText(bundle.getString("JIpicoReaderPanel.label4.text"));
		add(label4, CC.xy(3, 5));
		add(textField5, CC.xywh(5, 5, 5, 1));

		// ---- label2 ----
		label2.setText(bundle.getString("JIpicoReaderPanel.label2.text"));
		add(label2, CC.xy(3, 7));

		// ---- comboBox1 ----
		comboBox1.setModel(new DefaultComboBoxModel<>(new String[] { "Todos", "RX 1 (Izq.)" }));
		add(comboBox1, CC.xy(5, 7));

		// ---- textField1 ----
		textField1.setBackground(Color.green);
		add(textField1, CC.xy(7, 7));

		// ---- textField2 ----
		textField2.setBackground(Color.red);
		add(textField2, CC.xy(9, 7));

		// ---- checkPointComboBox1 ----
		checkPointComboBox1.setBackground(Color.red);
		checkPointComboBox1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkPointComboBoxItemStateChanged(e);
			}
		});
		add(checkPointComboBox1, CC.xy(11, 7));

		// ---- applyCheckpointButton ----
		applyCheckpointButton.setText(bundle.getString("JIpicoReaderPanel.applyCheckpointButton.text"));
		applyCheckpointButton.setBackground(Color.red);
		applyCheckpointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyCheckpointButtonActionPerformed(e);
			}
		});
		add(applyCheckpointButton, CC.xy(13, 7));

		// ---- label3 ----
		label3.setText(bundle.getString("JIpicoReaderPanel.label3.text"));
		add(label3, CC.xy(5, 9));

		// ---- textField3 ----
		textField3.setBackground(Color.blue);
		add(textField3, CC.xy(7, 9));

		// ---- textField4 ----
		textField4.setBackground(Color.yellow);
		add(textField4, CC.xy(9, 9));

		// ---- checkPointComboBox2 ----
		checkPointComboBox2.setBackground(Color.red);
		checkPointComboBox2.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkPointComboBoxItemStateChanged(e);
			}
		});
		add(checkPointComboBox2, CC.xy(11, 9));

		// ---- label5 ----
		label5.setText(bundle.getString("JIpicoReaderPanel.label5.text"));
		add(label5, CC.xy(13, 9));

		// ---- tagsReadLabel ----
		tagsReadLabel.setText(bundle.getString("JIpicoReaderPanel.tagsReadLabel.text"));
		add(tagsReadLabel, CC.xywh(15, 9, 3, 1));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel label1;
	private JTextField readerAddressTextField;
	private JButton connectButton;
	private JButton setTimeButton;
	private JButton removeReaderButton;
	private JLabel label4;
	private JTextField textField5;
	private JLabel label2;
	private JComboBox<String> comboBox1;
	private JTextField textField1;
	private JTextField textField2;
	private JComboBox checkPointComboBox1;
	private JButton applyCheckpointButton;
	private JLabel label3;
	private JTextField textField3;
	private JTextField textField4;
	private JComboBox checkPointComboBox2;
	private JLabel label5;
	private JLabel tagsReadLabel;

	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setTagReadListener(TagReadListener tagReadListener) {
		// TODO Auto-generated method stub

	}
}
