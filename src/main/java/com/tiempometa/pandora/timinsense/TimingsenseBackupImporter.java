/**
 * 
 */
package com.tiempometa.pandora.timinsense;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.tiempometa.pandora.ipicoreader.IpicoRead;
import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class TimingsenseBackupImporter implements BackupImporter {
	List<RawChipRead> chipReads = new ArrayList<RawChipRead>();

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
		BufferedReader br = new BufferedReader(new FileReader(dataFile));
		chipReads = new ArrayList<RawChipRead>();
		String dataLine;
		Reader in = new FileReader(dataFile);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
		for (CSVRecord record : records) {
			TimingsenseTagRead tagRead = TimingsenseTagRead.parseRecord(record, Context.getZoneId());
			chipReads.add(tagRead.toRawChipRead());
		}
		br.close();
	}

}
