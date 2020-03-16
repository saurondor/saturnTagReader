/*
 * Created by JFormDesigner on Sat Feb 22 15:31:19 CST 2020
 */

package com.tiempometa.pandora.tagreader;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.foxberry.FoxberryBackupImporter;
import com.tiempometa.pandora.ipicoreader.IpicoBackupImporter;
import com.tiempometa.pandora.ipicoreader.IpicoRead;
import com.tiempometa.pandora.macsha.MacshaBackupImporter;
import com.tiempometa.pandora.macsha.MacshaCloudBackupImporter;
import com.tiempometa.pandora.macsha.MacshaOcelotBackupImporter;
import com.tiempometa.pandora.rfidtiming.UltraBackupImporter;
import com.tiempometa.pandora.timinsense.TimingsenseBackupImporter;
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
	private RawChipReadTableModel tableModel = new RawChipReadTableModel();
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
		fc.setFileFilter(new FileNameExtensionFilter("CSV/TXT/LOG (.csv, .txt, .log)", "csv", "txt", "log"));
		try {
			fc.setCurrentDirectory(Context.getWorkingDirectory());
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dataFile = fc.getSelectedFile();
				fileLabel.setText(dataFile.getName());
				try {
					loadDataFile();
					Context.saveWorkingDirectory(dataFile.getPath());
					importButton.setEnabled(false);
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

	private void loadDataFile() throws IOException {
		importer.load(dataFile);
		if (importer instanceof MacshaCloudBackupImporter) {
			// convert bib to rfid
			List<RawChipRead> chipReads = Context.getResultsWebservice()
					.populateRfidByChipNumber(importer.getChipReads());
			tableModel.setChipReads(chipReads);
		} else {
			tableModel.setChipReads(importer.getChipReads());
		}
		tableModel.fireTableDataChanged();
		loadCheckPoints();
	}

	private void applyCheckPointButtonActionPerformed(ActionEvent e) {
		List<RawChipRead> chipReads = tableModel.getChipReads();
		for (RawChipRead rawChipRead : chipReads) {
			rawChipRead.setCheckPoint((String) checkPointComboBox.getSelectedItem());
		}
		tableModel.fireTableDataChanged();
		importButton.setEnabled(true);
	}

	private void importButtonActionPerformed(ActionEvent e) {
		Context.getResultsWebservice().batchSaveRawReads(tableModel.getChipReads());
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
		applyCheckPointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyCheckPointButtonActionPerformed(e);
			}
		});
		add(applyCheckPointButton, CC.xy(3, 13));

		// ---- importButton ----
		importButton.setText(bundle.getString("JImportBackupPanel.importButton.text"));
		importButton.setEnabled(false);
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
	private JComboBox checkPointComboBox;
	private JButton applyCheckPointButton;
	private JButton importButton;
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
