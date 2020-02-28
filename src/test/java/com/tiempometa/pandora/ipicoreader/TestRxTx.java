/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiempometa.pandora.tagreader.SerialReader;

/**
 * @author gtasi
 *
 */
public class TestRxTx {
	private static final Logger logger = Logger.getLogger(TestRxTx.class);

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
	public void testListPort() {
		logger.info("Listing ports:");
		List<String> ports;
		SerialReader reader = new SerialReader();
		ports = reader.getPorts();
		logger.info("Port list length " + ports.size());
		for (String port : ports) {
			logger.info("Port " + port);
		}
		fail("Not yet implemented");
	}

	@Test
	public void testPort() {
		fail("Not yet implemented");
	}

}
