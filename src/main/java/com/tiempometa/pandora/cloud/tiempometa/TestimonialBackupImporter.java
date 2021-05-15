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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.tiempometa.data.ExcelImporter;
import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.webservice.model.CookedChipRead;
import com.tiempometa.webservice.model.RawChipRead;

import jxl.Cell;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Implements loading information from web based XLS representing the
 * testimonials uploaded by the virtual timing app.
 * 
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class TestimonialBackupImporter extends ExcelImporter implements BackupImporter {
	Map<String, Integer> colMap = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tiempometa.pandora.tagreader.BackupImporter#load(java.lang.String)
	 */
	@Override
	public void load(String fileName) throws IOException {
		load(new File(fileName));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tiempometa.pandora.tagreader.BackupImporter#getChipReads()
	 */
	@Override
	public List<RawChipRead> getChipReads() {
		selectSheet(0);
		List<Object> list = importData(colMap);
		List<RawChipRead> rawReadList = new ArrayList<RawChipRead>(list.size());
		for (int i = 0; i < list.size(); i++) {
			RawChipRead chipRead = (RawChipRead) list.get(i);
			chipRead.setLoadName(String.valueOf(i));
			rawReadList.add(chipRead);
			
		}
		for (Object rawChipRead : list) {
		}
		return rawReadList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tiempometa.pandora.tagreader.BackupImporter#load(java.io.File)
	 */
	@Override
	public void load(File dataFile) throws IOException {
		try {
			workbook = Workbook.getWorkbook(dataFile);
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}

	}

	@Override
	protected Object importRow(Cell[] cell) {
		// 0 Tag - tag is vtag activation code
		// 1 Número - must convert bib no vtag
		// 2 Tiempo

		// 3 Tiempo Segundos
		// 4 Distancia
		// 5 Actividad
		// 6 Testimonial

		RawChipRead chipRead = new RawChipRead();
		chipRead.setReadType(CookedChipRead.TYPE_TESTIMONIAL);
		chipRead.setRfidString("vtag" + StringUtils.leftPad(cell[1].getContents(), 5, "0"));
		chipRead.setCheckPoint(null);
		try {
			chipRead.setRunTime(Long.valueOf(cell[3].getContents()));
		} catch (NumberFormatException e) {
//			e.printStackTrace();
		}
		try {
			chipRead.setDistance(Double.valueOf(cell[4].getContents()));
		} catch (NumberFormatException e) {
//			e.printStackTrace();
		}
		chipRead.setTestimonialUrl(cell[6].getContents());
		return chipRead;
	}

}
