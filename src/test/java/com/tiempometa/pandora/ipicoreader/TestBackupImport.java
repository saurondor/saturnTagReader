/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
	public void testBackupImporter() {
		BackupImporter importer = new IpicoBackupImporter();
		String fileName = "/backups/ipico/FS_LS.log.txt";
		try {
			importer.load(fileName);
			List<RawChipRead> readList = importer.getChipReads();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testParseLine() {

		String line = "aa0105800312d92501011810131148093183FS";
		logger.info(line);
		IpicoRead read = IpicoRead.parse(line);
		assertNotNull(read);
		String crc = IpicoRead.crc(line.substring(IpicoRead.FRAME_PAYLOAD_START, IpicoRead.FRAME_PAYLOAD_END));
		logger.info(crc);
		logger.info(read);
		assertEquals("0101", read.getAntenna());
		assertEquals("Sat Oct 13 11:48:09 CDT 2018", read.getClockTime().toString());
		assertEquals("01", read.getReader());
		assertEquals("05800312d925", read.getRfid());
		assertEquals(Long.valueOf(1539449289490l), read.getRunTime());
		assertEquals(Integer.valueOf(1), read.getSeenStatus());
		line = "aa0205800312d9250100181013114822317eLS";
		logger.info(line);
		read = IpicoRead.parse(line);
		crc = IpicoRead.crc(line.substring(IpicoRead.FRAME_PAYLOAD_START, IpicoRead.FRAME_PAYLOAD_END));
		assertNotNull(read);
		logger.info(crc);
		logger.info(read);
		assertEquals("0100", read.getAntenna());
		assertEquals("Sat Oct 13 11:48:22 CDT 2018", read.getClockTime().toString());
		assertEquals("02", read.getReader());
		assertEquals("05800312d925", read.getRfid());
		assertEquals(Long.valueOf(1539449302490l), read.getRunTime());
		assertEquals(Integer.valueOf(-1), read.getSeenStatus());

		line = "aa0105800312d92501011810131148093182FS";
		read = IpicoRead.parse(line);
		assertNull(read);
	}

	@Test
	public void testParseLineFromFile() {
//		this.getClass().getResourceAsStream("FS_LS.log.txt");
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/backups/ipico/FS_LS.log.txt")));
		String line = null;
		do {
			try {
				line = reader.readLine();
				logger.info(line);
				IpicoRead read = IpicoRead.parse(line);
				logger.info(read);
			} catch (IOException e) {
				fail(e.getMessage());
			}
		} while (line != null);
	}

}
