/**
 * 
 */
package com.tiempometa.pandora.macsha;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class MacshaBackupImporter implements BackupImporter {
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
		chipReads = new ArrayList<RawChipRead>();
		BufferedReader br = new BufferedReader(new FileReader(dataFile));
		String dataLine;
		while ((dataLine = br.readLine()) != null) {
			MacshaTagRead tagRead = MacshaTagRead.parseString(dataLine, Context.getZoneId());
			if (tagRead != null) {
				chipReads.add(tagRead.toRawChipRead());
			}
		}
		br.close();
	}

}
