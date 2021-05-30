/**
 * 
 */
package com.tiempometa.pandora.macsha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.webservice.model.RawChipRead;

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
	public void testOcelotImport() {
		String cwd = System.getProperty("user.dir");
		logger.info("Current working directory : " + cwd);
		BackupImporter importer = new MacshaOcelotBackupImporter();
		String fileName = cwd + "/src/test/resources/backups/macsha/azul-15032020_062119.csv";
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(fileName);
			MacshaCSVParser parser = new MacshaCSVParser();
			List<CSVRecord> list = parser.parse(inputStream);
			logger.info("List size " + list.size());
			for (CSVRecord csvRecord : list) {
				if (csvRecord.size() > 2) {
					logger.info("\t" + csvRecord);
					MacshaOcelotRead cloudRead = MacshaOcelotRead.parseRecord(csvRecord,
							MacshaOcelotRead.EXIT_READ_TIME);
					logger.info("\t" + cloudRead);
				}
			}
			inputStream.close();
			importer.load(new File(fileName));
			List<RawChipRead> reads = importer.getChipReads();
			logger.info("\t CHIP READS>>>");
			for (RawChipRead rawChipRead : reads) {
				logger.info("\t" + rawChipRead);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				logger.info("\t" + cloudRead);
			}
			inputStream.close();
			importer.load(new File(fileName));
			List<RawChipRead> reads = importer.getChipReads();
			logger.info("\t CHIP READS>>>");
			for (RawChipRead rawChipRead : reads) {
				logger.info("\t" + rawChipRead);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
