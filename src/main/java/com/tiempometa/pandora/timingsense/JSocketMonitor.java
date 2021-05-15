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
public class JSocketMonitor extends JFrame implements SocketMonitor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7273455332917507342L;

	public JSocketMonitor() {
		initComponents();
	}

	private void clearLogButtonActionPerformed(ActionEvent e) {
		int response = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas borrar la bitácora de datos?",
				"Borrar bitácora del monitor", JOptionPane.YES_NO_OPTION);
		if (response == JOptionPane.YES_OPTION) {
			textPane1.setText("");
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.timingsense.tscollector");
		scrollPane1 = new JScrollPane();
		textPane1 = new JTextPane();
		clearLogButton = new JButton();

		// ======== this ========
		setTitle(bundle.getString("JSocketMonitor.this.title"));
		setIconImage(new ImageIcon(
				getClass().getResource("/com/tiempometa/pandora/tagreader/tiempometa_icon_large_alpha.png"))
						.getImage());
		Container contentPane = getContentPane();
		contentPane.setLayout(
				new FormLayout("default, $lcgap, 586dlu", "default, $lgap, 22dlu, $lgap, 120dlu, $lgap, default"));

		// ======== scrollPane1 ========
		{

			// ---- textPane1 ----
			textPane1.setBackground(Color.black);
			textPane1.setForeground(Color.white);
			textPane1.setText(bundle.getString("JSocketMonitor.textPane1.text"));
			scrollPane1.setViewportView(textPane1);
		}
		contentPane.add(scrollPane1, CC.xywh(3, 3, 1, 3));

		// ---- clearLogButton ----
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
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JScrollPane scrollPane1;
	private JTextPane textPane1;
	private JButton clearLogButton;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	@Override
	public void appendText(String text) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				textPane1.setText(textPane1.getText() + text);

			}
		});

	}

	@Override
	public void setText(String text) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				textPane1.setText(text);

			}
		});
	}
}
