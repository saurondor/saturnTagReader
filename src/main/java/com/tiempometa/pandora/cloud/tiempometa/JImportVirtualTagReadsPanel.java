/**
 * 
 */
package com.tiempometa.pandora.cloud.tiempometa;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.tiempometa.pandora.foxberry.JImportBackupPanel;

/**
 * @author gtasi
 *
 */
public class JImportVirtualTagReadsPanel extends JImportBackupPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7269105912588457916L;

	@Override
	protected void setFileFormat(JFileChooser fc) {
		fc.setFileFilter(new FileNameExtensionFilter("Excel 97-2003 (.xls)", "xls"));
	}

	@Override
	protected void loadDataFile() throws IOException {
		// TODO Auto-generated method stub
		super.loadDataFile();
		applyCheckPointButton.setEnabled(false);
		checkPointComboBox.setEnabled(false);
		
	}

}
