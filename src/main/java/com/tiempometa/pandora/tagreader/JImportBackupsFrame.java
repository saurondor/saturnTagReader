/*
 * Created by JFormDesigner on Sat Feb 22 15:32:31 CST 2020
 */

package com.tiempometa.pandora.tagreader;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.tiempometa.pandora.ipicoreader.*;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JImportBackupsFrame extends JFrame {

	BackupImporter importer;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4073697142284509557L;

	public JImportBackupsFrame(BackupImporter importer) {
		initComponents();
		this.importer = importer;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.tagreader.tagreader");
		importPanel = new JImportBackupPanel();

		// ======== this ========
		setTitle(bundle.getString("JImportBackupsFrame.this.title"));
		setIconImage(new ImageIcon(
				getClass().getResource("/com/tiempometa/pandora/tagreader/tiempometa_icon_large_alpha.png"))
						.getImage());
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(importPanel, BorderLayout.CENTER);
		setSize(605, 315);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JImportBackupPanel importPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
