/*
 * Created by JFormDesigner on Sat Feb 22 15:31:19 CST 2020
 */

package com.tiempometa.pandora.ipicoreader;

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
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.timing.model.RawChipRead;
import com.tiempometa.timing.model.dao.RawChipReadDao;
import com.tiempometa.timing.model.dao.RouteDao;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JImportBackupPanel extends JPanel {
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
		RouteDao rDao = (RouteDao) Context.getCtx().getBean("routeDao");
		List<String> checkPoints = rDao.getCheckPointNames();
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
		String path = Context.loadSetting(PandoraSettings.EVENT_PATH, null);
		if (path == null) {
			fc.setCurrentDirectory(null);
		} else {
			fc.setCurrentDirectory(new File(path));
		}
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			dataFile = fc.getSelectedFile();
			fileLabel.setText(dataFile.getName());
			try {
				loadDataFile();
				importButton.setEnabled(false);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "Error cargando los datos " + e1.getMessage(),
						"Error de importación", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void loadDataFile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(dataFile));
		List<RawChipRead> chipReads = new ArrayList<RawChipRead>();
		String dataLine;
		while ((dataLine = br.readLine()) != null) {
			IpicoRead tagRead = IpicoRead.parseString(dataLine, Context.getZoneId());
			chipReads.add(tagRead.toRawChipRead());
		}
		logger.info("Read log count " + chipReads.size());
		tableModel.setChipReads(chipReads);
		tableModel.fireTableDataChanged();
		loadCheckPoints();
		br.close();
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
		RawChipReadDao chipDao = (RawChipReadDao) Context.getCtx().getBean("rawChipReadDao");
		chipDao.batchSave(tableModel.getChipReads());
		JOptionPane.showMessageDialog(this, "Se guardaron " + tableModel.getChipReads().size() + " lecturas",
				"Importación exitosa", JOptionPane.INFORMATION_MESSAGE);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.ipicoreader.ipicoreader");
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
}
