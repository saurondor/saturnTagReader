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
package com.tiempometa.pandora.macsha;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class MacshaCloudBackupImporter implements BackupImporter {
	List<RawChipRead> chipReads = new ArrayList<RawChipRead>();
	MacshaCSVParser parser = new MacshaCSVParser();

	@Override
	public void load(String fileName) throws IOException {
		load(new File(fileName));
	}

	@Override
	public List<RawChipRead> getChipReads() {
		return chipReads;
	}

	@Override
	public void load(File dataFile) throws IOException {
		chipReads = new ArrayList<RawChipRead>();
		List<CSVRecord> list = parser.parse(new FileInputStream(dataFile));
		for (CSVRecord csvRecord : list) {
			MacshaCloudRead reading = MacshaCloudRead.parseRecord(csvRecord);
			if (reading.getBib() != null) {
				chipReads.add(reading.toRawChipRead());
			}
		}
	}

}
