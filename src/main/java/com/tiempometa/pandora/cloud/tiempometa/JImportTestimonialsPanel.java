/**
 * 
 */
package com.tiempometa.pandora.cloud.tiempometa;

import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.tiempometa.pandora.foxberry.JImportBackupPanel;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class JImportTestimonialsPanel extends JImportBackupPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7730212147488424674L;

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
		List<RawChipRead> chipReads = tableModel.getChipReads();
		for (RawChipRead rawChipRead : chipReads) {
			rawChipRead.setCheckPoint("finish");
		}
		importButton.setEnabled(true);
	}

}
