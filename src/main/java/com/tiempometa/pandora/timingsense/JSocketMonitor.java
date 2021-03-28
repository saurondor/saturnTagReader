/*
 * Created by JFormDesigner on Sun Mar 28 07:14:02 CST 2021
 */

package com.tiempometa.pandora.timingsense;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JSocketMonitor extends JFrame {
	public JSocketMonitor() {
		initComponents();
	}

	private void clearLogButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.timingsense.tscollector");
		scrollPane1 = new JScrollPane();
		textPane1 = new JTextPane();
		clearLogButton = new JButton();

		//======== this ========
		setTitle(bundle.getString("JSocketMonitor.this.title"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"default, $lcgap, 215dlu",
			"default, $lgap, 22dlu, $lgap, 120dlu, $lgap, default"));

		//======== scrollPane1 ========
		{

			//---- textPane1 ----
			textPane1.setBackground(Color.black);
			textPane1.setForeground(Color.white);
			textPane1.setText(bundle.getString("JSocketMonitor.textPane1.text"));
			scrollPane1.setViewportView(textPane1);
		}
		contentPane.add(scrollPane1, CC.xywh(3, 3, 1, 3));

		//---- clearLogButton ----
		clearLogButton.setText(bundle.getString("JSocketMonitor.clearLogButton.text"));
		clearLogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearLogButtonActionPerformed(e);
			}
		});
		contentPane.add(clearLogButton, CC.xy(3, 7));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrollPane1;
	private JTextPane textPane1;
	private JButton clearLogButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
