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
