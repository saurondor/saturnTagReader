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
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.ipicoreader.IpicoBackupImporter;
import com.tiempometa.pandora.macsha.MacshaBackupImporter;
import com.tiempometa.pandora.macsha.MacshaCloudBackupImporter;
import com.tiempometa.pandora.macsha.MacshaOcelotBackupImporter;
import com.tiempometa.pandora.rfidtiming.UltraBackupImporter;
import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.RawChipReadTableModel;
import com.tiempometa.pandora.timingsense.TimingsenseBackupImporter;
import com.tiempometa.timing.model.CookedChipRead;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JImportBackupPanel extends JPanel {
	BackupImporter importer;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7408634299336269973L;
	private static final Logger logger = Logger.getLogger(JImportBackupPanel.class);
	protected RawChipReadTableModel tableModel = new RawChipReadTableModel();
	private File dataFile;

	public JImportBackupPanel() {
		initComponents();
		tagReadsTable.setModel(tableModel);
	}

	/**
	 * 
	 */
	private void loadCheckPoints() {
		List<String> checkPoints = Context.getResultsWebservice().getCheckPointNames();
		String[] checkPointArray = new String[checkPoints.size()];
		if (checkPointArray.length > 0) {
			checkPointComboBox.setEnabled(true);
		} else {
			checkPointComboBox.setEnabled(false);
		}
		checkPointComboBox.setModel(new DefaultComboBoxModel<String>(checkPoints.toArray(checkPointArray)));
	}

	private void fileOpenButtonActionPerformed(ActionEvent e) {
		final JFileChooser fc = new JFileChooser();
		setFileFormat(fc);
		try {
			fc.setCurrentDirectory(Context.getWorkingDirectory());
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dataFile = fc.getSelectedFile();
				fileLabel.setText(dataFile.getName());
				importButton.setEnabled(false);
				try {
					loadDataFile();
					Context.saveWorkingDirectory(dataFile.getPath());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(this, "Error cargando los datos " + e1.getMessage(),
							"Error de importación", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(this, "Error cargando los datos " + e2.getMessage(), "Error de importación",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * @param fc
	 */
	protected void setFileFormat(final JFileChooser fc) {
		fc.setFileFilter(new FileNameExtensionFilter("CSV/TXT/LOG (.csv, .txt, .log)", "csv", "txt", "log"));
	}

	protected void loadDataFile() throws IOException {
		importer.load(dataFile);
		List<RawChipRead> chipReads;
		if (importer instanceof MacshaCloudBackupImporter) {
			// convert bib to rfid
			chipReads = Context.getResultsWebservice().populateRfidByChipNumber(importer.getChipReads());
		} else {
			chipReads = importer.getChipReads();
		}
		boolean missingCheckpoint = false;
		for (RawChipRead rawChipRead : chipReads) {
			if (rawChipRead.getCheckPoint() == null) {
				logger.debug("Chip read missing checkpoint " + rawChipRead);
				missingCheckpoint = true;
			}
		}
		applyCheckPointButton.setEnabled(true);
		if (missingCheckpoint) {
			importButton.setEnabled(false);
		} else {
			importButton.setEnabled(true);
		}
		tableModel.setChipReads(chipReads);
//		importButton.setEnabled(true);
		tableModel.fireTableDataChanged();
		loadCheckPoints();
	}

	private void applyCheckPointButtonActionPerformed(ActionEvent e) {
		List<RawChipRead> chipReads = tableModel.getChipReads();
		for (RawChipRead rawChipRead : chipReads) {
			rawChipRead.setCheckPoint((String) checkPointComboBox.getSelectedItem());
			if (rawChipRead.getReadType() == null) {
				rawChipRead.setReadType(CookedChipRead.TYPE_BACKUP);
			}
		}
		tableModel.fireTableDataChanged();
		importButton.setEnabled(true);
	}

	private void importButtonActionPerformed(ActionEvent e) {
		int listSize = tableModel.getChipReads().size();
		for (int i = 0; i < listSize; i += 2000) {
			List<RawChipRead> readings = tableModel.getChipReads().subList(i, Math.min(listSize, i + 2000));
			Context.getResultsWebservice().batchSaveRawReads(readings);
		}
		JOptionPane.showMessageDialog(this, "Se guardaron " + tableModel.getChipReads().size() + " lecturas",
				"Importación exitosa", JOptionPane.INFORMATION_MESSAGE);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.tagreader.tagreader");
		label1 = new JLabel();
		fileLabel = new JLabel();
		fileOpenButton = new JButton();
		scrollPane1 = new JScrollPane();
		tagReadsTable = new JTable();
		checkPointComboBox = new JComboBox();
		applyCheckPointButton = new JButton();
		importButton = new JButton();

		// ======== this ========
		setLayout(new FormLayout("8dlu, $lcgap, 334dlu",
				"8dlu, $lgap, default, $lgap, 12dlu, $lgap, default, $lgap, 58dlu, $lgap, 15dlu, 2*($lgap, default)"));

		// ---- label1 ----
		label1.setText(bundle.getString("JImportBackupPanel.label1.text"));
		label1.setFont(new Font("Tahoma", Font.BOLD, 12));
		add(label1, CC.xy(3, 3));
		add(fileLabel, CC.xy(3, 5));

		// ---- fileOpenButton ----
		fileOpenButton.setText(bundle.getString("JImportBackupPanel.fileOpenButton.text"));
		fileOpenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileOpenButtonActionPerformed(e);
			}
		});
		add(fileOpenButton, CC.xy(3, 7));

		// ======== scrollPane1 ========
		{
			scrollPane1.setViewportView(tagReadsTable);
		}
		add(scrollPane1, CC.xy(3, 9));

		// ---- checkPointComboBox ----
		checkPointComboBox.setEnabled(false);
		add(checkPointComboBox, CC.xy(3, 11));

		// ---- applyCheckPointButton ----
		applyCheckPointButton.setText(bundle.getString("JImportBackupPanel.applyCheckPointButton.text"));
		applyCheckPointButton.setEnabled(false);
		applyCheckPointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyCheckPointButtonActionPerformed(e);
			}
		});
		add(applyCheckPointButton, CC.xy(3, 13));

		// ---- importButton ----
		importButton.setText(bundle.getString("JImportBackupPanel.importButton.text"));
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importButtonActionPerformed(e);
			}
		});
		add(importButton, CC.xy(3, 15));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel label1;
	private JLabel fileLabel;
	private JButton fileOpenButton;
	private JScrollPane scrollPane1;
	private JTable tagReadsTable;
	protected JComboBox checkPointComboBox;
	protected JButton applyCheckPointButton;
	protected JButton importButton;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setImporter(BackupImporter importer) {
		this.importer = importer;
		if (importer instanceof FoxberryBackupImporter) {

		} else if (importer instanceof IpicoBackupImporter) {

		} else if (importer instanceof MacshaCloudBackupImporter) {

		} else if (importer instanceof MacshaBackupImporter) {

		} else if (importer instanceof MacshaOcelotBackupImporter) {

		} else if (importer instanceof TimingsenseBackupImporter) {

		} else if (importer instanceof UltraBackupImporter) {

		}
	}
}
