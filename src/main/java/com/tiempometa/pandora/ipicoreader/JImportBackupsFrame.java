/*
 * Created by JFormDesigner on Sat Feb 22 15:32:31 CST 2020
 */

package com.tiempometa.pandora.ipicoreader;

import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JImportBackupsFrame extends JFrame {
	public JImportBackupsFrame() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.ipicoreader.ipicoreader");
		importPanel = new JImportBackupPanel();

		//======== this ========
		setTitle(bundle.getString("JImportBackupsFrame.this.title"));
		setIconImage(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/ipicoreader/tiempometa_icon_large_alpha.png")).getImage());
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(importPanel, BorderLayout.CENTER);
		setSize(605, 315);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JImportBackupPanel importPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
