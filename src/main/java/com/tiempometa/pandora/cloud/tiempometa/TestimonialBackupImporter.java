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
import org.apache.poi.ss.usermodel.Row;

import com.tiempometa.data.ExcelImporter;
import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.webservice.model.CookedChipRead;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * Implements loading information from web based XLS representing the
 * testimonials uploaded by the virtual timing app.
 *
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class TestimonialBackupImporter extends ExcelImporter implements BackupImporter {

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
		for (int i = 0; i < list.size(); i++) {
			RawChipRead chipRead = (RawChipRead) list.get(i);
			chipRead.setLoadName(String.valueOf(i));
			rawReadList.add(chipRead);
		}
		return rawReadList;
	}

	@Override
	protected Object importRow(Row row) {
		// Col 0 Tag      — vtag activation code
		// Col 1 Número   — bib number, convert to vtag rfid
		// Col 2 Tiempo
		// Col 3 Tiempo Segundos
		// Col 4 Distancia
		// Col 5 Actividad
		// Col 6 Testimonial

		RawChipRead chipRead = new RawChipRead();
		chipRead.setReadType(CookedChipRead.TYPE_TESTIMONIAL);

		String bib = getColumnContent(1, row);
		if (bib != null) {
			chipRead.setRfidString("vtag" + StringUtils.leftPad(bib.replaceAll("[^\\d]", ""), 5, "0"));
		}
		chipRead.setCheckPoint(null);

		try {
			String v = getColumnContent(3, row);
			if (v != null) chipRead.setRunTime(Long.parseLong(v.replaceAll("[^\\d]", "")));
		} catch (NumberFormatException e) { /* blank or non-numeric cell */ }

		try {
			String v = getColumnContent(4, row);
			if (v != null) chipRead.setDistance(Double.parseDouble(v.replaceAll("[^\\d.]", "")));
		} catch (NumberFormatException e) { /* blank or non-numeric cell */ }

		chipRead.setTestimonialUrl(getColumnContent(6, row));

		return chipRead;
	}
}
