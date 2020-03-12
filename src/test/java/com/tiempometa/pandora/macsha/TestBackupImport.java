/**
 * 
 */
package com.tiempometa.pandora.macsha;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiempometa.pandora.tagreader.BackupImporter;

/**
 * @author gtasi
 *
 */
public class TestBackupImport {
	private static final Logger logger = Logger.getLogger(TestBackupImport.class);

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCloudImport() {
		String cwd = System.getProperty("user.dir");
		logger.info("Current working directory : " + cwd);
		BackupImporter importer = new MacshaCloudBackupImporter();
		String fileName = cwd + "/src/test/resources/backups/macsha/cloud.csv";
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(fileName);
			MacshaCSVParser parser = new MacshaCSVParser();
			List<CSVRecord> list = parser.parse(inputStream);
			logger.info("List size " + list.size());
			for (CSVRecord csvRecord : list) {
				logger.info("\t" + csvRecord);
				MacshaCloudRead cloudRead = MacshaCloudRead.parseRecord(csvRecord);
				logger.info("\t"+cloudRead);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Context.setZoneId(ZoneId.of("GMT-5"));
//		try {
//			importer.load(fileName);
//			List<RawChipRead> readList = importer.getChipReads();
//			logger.debug("Read list size " + readList.size());
//			for (RawChipRead rawChipRead : readList) {
//				logger.debug("Tag read " + rawChipRead);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
