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
public class MacshaOcelotBackupImporter implements BackupImporter {
	List<RawChipRead> chipReads = new ArrayList<RawChipRead>();
	MacshaCSVParser parser = new MacshaCSVParser();
	private Integer readType = MacshaOcelotRead.EXIT_READ_TIME;

	public MacshaOcelotBackupImporter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MacshaOcelotBackupImporter(Integer readType) {
		super();
		this.readType = readType;
	}

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
			if (csvRecord.size() > 2) {
				MacshaOcelotRead reading = MacshaOcelotRead.parseRecord(csvRecord, readType);
				if (reading != null) {
					chipReads.add(reading.toRawChipRead());
				}
			}
		}
	}

	public Integer getReadType() {
		return readType;
	}

	public void setReadType(Integer readType) {
		this.readType = readType;
	}

}
