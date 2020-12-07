/**
 * 
 */
package com.tiempometa.pandora.cloud.tiempometa;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tiempometa.data.ExcelImporter;
import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.webservice.model.CookedChipRead;
import com.tiempometa.webservice.model.RawChipRead;

import jxl.Cell;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * @author gtasi
 *
 */
public class VirtualTagBackupImporter extends ExcelImporter implements BackupImporter {
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

		// 0 Tag
		// 1 Punto
		// 2 Tiempo UTC Milis
		// 3 Fecha y Hora UTC
		// 4 Fecha y Hora Local Texto
		// 5 Pasos
		// 6 Distancia
		// 7 Calorias
		// 8 Fuente
		// 9 App

		RawChipRead chipRead = new RawChipRead();
		chipRead.setReadType(CookedChipRead.TYPE_TAG);
		chipRead.setRfidString(cell[0].getContents());
		chipRead.setCheckPoint(cell[1].getContents());
		try {
			chipRead.setTimeMillis(Long.valueOf(cell[2].getContents()));
		} catch (NumberFormatException e) {
//			e.printStackTrace();
		}
		try {
			chipRead.setSteps(Integer.valueOf(cell[5].getContents()));
		} catch (NumberFormatException e) {
//			e.printStackTrace();
		}
		try {
			chipRead.setCalories(Integer.valueOf(cell[7].getContents()));
		} catch (NumberFormatException e) {
//			e.printStackTrace();
		}
		try {
			chipRead.setDistance(Double.valueOf(cell[6].getContents()) / 1000d);
		} catch (NumberFormatException e) {
//			e.printStackTrace();
		}
		chipRead.setMobileApp(cell[9].getContents());
		chipRead.setDevice(cell[8].getContents());
		chipRead.setTime(Instant.ofEpochMilli(chipRead.getTimeMillis()).atZone(Context.getZoneId()).toLocalDateTime());
		return chipRead;
	}

}
