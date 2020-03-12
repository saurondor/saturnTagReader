/**
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
