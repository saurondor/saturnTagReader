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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;

import com.tiempometa.data.ExcelImporter;
import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.webservice.model.CookedChipRead;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class VirtualTagBackupImporter extends ExcelImporter implements BackupImporter {

	Map<String, Integer> colMap = null;

	@Override
	public void load(String fileName) throws IOException {
		load(new File(fileName));
	}

	@Override
	public void load(File dataFile) throws IOException {
		openWorkbook(dataFile);
	}

	@Override
	public List<RawChipRead> getChipReads() {
		selectSheet(0);
		List<Object> list = importData(colMap);
		List<RawChipRead> rawReadList = new ArrayList<>(list.size());
		for (Object item : list) {
			rawReadList.add((RawChipRead) item);
		}
		return rawReadList;
	}

	@Override
	protected Object importRow(Row row) {
		// Col 0 Tag
		// Col 1 Punto
		// Col 2 Tiempo UTC Milis
		// Col 3 Fecha y Hora UTC
		// Col 4 Fecha y Hora Local Texto
		// Col 5 Pasos
		// Col 6 Distancia
		// Col 7 Calorias
		// Col 8 Fuente
		// Col 9 App

		RawChipRead chipRead = new RawChipRead();
		chipRead.setReadType(CookedChipRead.TYPE_TAG);
		chipRead.setRfidString(getColumnContent(0, row));
		chipRead.setCheckPoint(getColumnContent(1, row));

		try {
			String v = getColumnContent(2, row);
			if (v != null) chipRead.setTimeMillis(Long.parseLong(v.replaceAll("[^\\d]", "")));
		} catch (NumberFormatException e) { /* blank or non-numeric cell */ }

		try {
			String v = getColumnContent(5, row);
			if (v != null) chipRead.setSteps(Integer.parseInt(v.replaceAll("[^\\d]", "")));
		} catch (NumberFormatException e) { /* blank or non-numeric cell */ }

		try {
			String v = getColumnContent(6, row);
			if (v != null) chipRead.setDistance(Double.parseDouble(v.replaceAll("[^\\d.]", "")) / 1000d);
		} catch (NumberFormatException e) { /* blank or non-numeric cell */ }

		try {
			String v = getColumnContent(7, row);
			if (v != null) chipRead.setCalories(Integer.parseInt(v.replaceAll("[^\\d]", "")));
		} catch (NumberFormatException e) { /* blank or non-numeric cell */ }

		chipRead.setDevice(getColumnContent(8, row));
		chipRead.setMobileApp(getColumnContent(9, row));

		if (chipRead.getTimeMillis() != null) {
			chipRead.setTime(
				Instant.ofEpochMilli(chipRead.getTimeMillis())
					.atZone(Context.getZoneId())
					.toLocalDateTime()
			);
		}

		return chipRead;
	}
}
