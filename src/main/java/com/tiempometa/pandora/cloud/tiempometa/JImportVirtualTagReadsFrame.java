/*
 * Created by JFormDesigner on Sun Dec 06 05:21:21 CST 2020
 */

package com.tiempometa.pandora.cloud.tiempometa;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.tagreader.BackupImporter;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JImportVirtualTagReadsFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6950378327362052947L;
	BackupImporter importer;

	public JImportVirtualTagReadsFrame(BackupImporter importer) throws HeadlessException {
		super();
		this.importer = importer;
		initComponents();
	}

//	public JImportVirtualTagReadsFrame() {
//		initComponents();
//	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.tagreader.tagreader");
		importPanel = new JImportVirtualTagReadsPanel();

		// ======== this ========
		setTitle(bundle.getString("JImportVirtualTagReadsFrame.this.title"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(importPanel, BorderLayout.CENTER);
		setSize(565, 310);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JImportVirtualTagReadsPanel importPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
