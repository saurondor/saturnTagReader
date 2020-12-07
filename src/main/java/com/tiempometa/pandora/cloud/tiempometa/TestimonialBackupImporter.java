/**
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
 * @author gtasi
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
		for (Object rawChipRead : list) {
			rawReadList.add((RawChipRead) rawChipRead);
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
