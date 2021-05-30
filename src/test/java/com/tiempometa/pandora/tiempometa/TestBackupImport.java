/**
 * 
 */
package com.tiempometa.pandora.tiempometa;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiempometa.pandora.cloud.tiempometa.TestimonialBackupImporter;
import com.tiempometa.pandora.cloud.tiempometa.VirtualTagBackupImporter;
import com.tiempometa.webservice.model.CookedChipRead;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * Tests the importing of virtual tag reads and testimonials
 * 
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
	public void testVirtualTagBackupImport() {
		String cwd = System.getProperty("user.dir");
		logger.info("Current working directory : " + cwd);
		VirtualTagBackupImporter importer = new VirtualTagBackupImporter();
		String fileName = cwd + "/src/test/resources/backups/tiempometa/vtagreads.xls";
		try {
			importer.load(fileName);
			importer.selectSheet(0);
			List<RawChipRead> readList = importer.getChipReads();
			assertEquals(48, readList.size());
			RawChipRead chipRead = readList.get(0);
			logger.debug("First chip read " + chipRead);
			assertEquals(1605565141401l, chipRead.getTimeMillis().longValue());
			assertEquals(CookedChipRead.TYPE_TAG, chipRead.getReadType().intValue());
			assertEquals("start", chipRead.getCheckPoint());
			assertEquals("vtag00099", chipRead.getRfidString());
			assertEquals("Android", chipRead.getDevice());
			assertEquals("com.tiempometa.timing.virtual", chipRead.getMobileApp());
			assertEquals(0, chipRead.getSteps().intValue());
			assertEquals(0d, chipRead.getDistance().doubleValue(), 0.01d);
			assertNull(chipRead.getCalories());
			chipRead = readList.get(1);
			logger.debug("Second chip read " + chipRead);
			assertEquals(1605567896479l, chipRead.getTimeMillis().longValue());
			assertEquals(CookedChipRead.TYPE_TAG, chipRead.getReadType().intValue());
			assertEquals("finish", chipRead.getCheckPoint());
			assertEquals("vtag00099", chipRead.getRfidString());
			assertEquals("Android", chipRead.getDevice());
			assertEquals("com.tiempometa.timing.virtual", chipRead.getMobileApp());
			assertEquals(3680, chipRead.getSteps().intValue());
			assertEquals(2521.07763671875d, chipRead.getDistance().doubleValue(), 0.01d);
			assertNull(chipRead.getCalories());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Exceptciont " + e.getMessage());
		}
	}

	@Test
	public void testTestimonialTagBackupImport() {
		String cwd = System.getProperty("user.dir");
		logger.info("Current working directory : " + cwd);
		TestimonialBackupImporter importer = new TestimonialBackupImporter();
		String fileName = cwd + "/src/test/resources/backups/tiempometa/testimonials.xls";
		try {
			importer.load(fileName);
			importer.selectSheet(0);
			List<RawChipRead> readList = importer.getChipReads();
			assertEquals(3, readList.size());
			RawChipRead chipRead = readList.get(0);
			logger.debug("First chip read " + chipRead);
			assertEquals(CookedChipRead.TYPE_TESTIMONIAL, chipRead.getReadType().intValue());
			assertEquals("vtag00100", chipRead.getRfidString());
			assertEquals(2.5d, chipRead.getDistance().doubleValue(), 0.01d);
			assertEquals(1439l, chipRead.getRunTime().longValue());
			chipRead = readList.get(1);
			logger.debug("Second chip read " + chipRead);
			assertEquals(CookedChipRead.TYPE_TESTIMONIAL, chipRead.getReadType().intValue());
			assertEquals("vtag00001", chipRead.getRfidString());
			assertEquals(5d, chipRead.getDistance().doubleValue(), 0.01d);
			assertEquals(1591, chipRead.getRunTime().longValue());
			assertNull(chipRead.getCalories());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Exceptciont " + e.getMessage());
		}
	}

}
