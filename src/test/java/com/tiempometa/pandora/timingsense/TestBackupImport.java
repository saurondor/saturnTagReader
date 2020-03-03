/**
 * 
 */
package com.tiempometa.pandora.timingsense;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiempometa.pandora.ipicoreader.IpicoBackupImporter;
import com.tiempometa.pandora.tagreader.BackupImporter;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.timinsense.TimingsenseBackupImporter;
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
	public void testBackupImporter() {
		String cwd = System.getProperty("user.dir");
		logger.info("Current working directory : " + cwd);
		BackupImporter importer = new TimingsenseBackupImporter();
		String fileName = cwd + "/src/test/resources/backups/timingsense/backup.csv";
		Context.setZoneId(ZoneId.of("GMT-5"));
		try {
			importer.load(fileName);
			List<RawChipRead> readList = importer.getChipReads();
			logger.debug("Read list size " + readList.size());
			for (RawChipRead rawChipRead : readList) {
				logger.debug("Tag read " + rawChipRead);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
