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
package com.tiempometa.pandora.foxberry;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import com.tiempometa.pandora.ipicoreader.*;
import com.tiempometa.pandora.macsha.MacshaBackupImporter;
import com.tiempometa.pandora.macsha.MacshaCloudBackupImporter;
import com.tiempometa.pandora.rfidtiming.UltraBackupImporter;
import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.pandora.timingsense.TimingsenseBackupImporter;

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
		importPanel.setImporter(importer);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.tagreader.tagreader");
		importPanel = new JImportBackupPanel();

		//======== this ========
		setTitle(bundle.getString("JImportBackupsFrame.this.title"));
		setIconImage(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/tagreader/tiempometa_icon_large_alpha.png")).getImage());
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(importPanel, BorderLayout.CENTER);
		setSize(550, 315);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JImportBackupPanel importPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
