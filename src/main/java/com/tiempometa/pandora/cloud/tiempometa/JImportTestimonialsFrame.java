/*
 * Created by JFormDesigner on Sun Dec 06 05:22:10 CST 2020
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
public class JImportTestimonialsFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7264147361475383568L;
	BackupImporter importer;

	public JImportTestimonialsFrame(BackupImporter importer) throws HeadlessException {
		super();
		this.importer = importer;
		initComponents();
		importPanel.setImporter(importer);
	}

//	public JImportTestimonialsFrame() {
//		initComponents();
//	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.tagreader.tagreader");
		importPanel = new JImportTestimonialsPanel();

		// ======== this ========
		setTitle(bundle.getString("JImportTestimonialsFrame.this.title"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(importPanel, BorderLayout.CENTER);
		setSize(560, 300);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JImportTestimonialsPanel importPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
