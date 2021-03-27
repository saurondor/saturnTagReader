/**
 * 
 */
package com.tiempometa.pandora.timingsense;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZoneId;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiempometa.pandora.tagreader.Context;

/**
 * @author gtasi
 *
 */
public class TestTcpIp {

	private static final Logger logger = Logger.getLogger(TestTcpIp.class);
	TSCollectorSimulator simulator;

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
	public void testParsePayload() {
		String cwd = System.getProperty("user.dir");
//		logger.info("Current working directory : " + cwd);
		String fileName = cwd + "/src/test/resources/backups/timingsense/sample_payload.json";
		Context.setZoneId(ZoneId.of("GMT-5"));

		BufferedReader objReader = null;
		StringBuffer fileBuffer = new StringBuffer();
		try {
			String strCurrentLine;
			objReader = new BufferedReader(new FileReader(fileName));
			while ((strCurrentLine = objReader.readLine()) != null) {
				System.out.println(strCurrentLine);
				fileBuffer.append(strCurrentLine);
			}
			List<TimingsenseTagRead> tsReads = TimingsenseTagRead.parseJson(fileBuffer.toString());
//			List<RawChipRead> readList = importer.getChipReads();
			logger.debug("Read list size " + tsReads.size());
			for (TimingsenseTagRead rawChipRead : tsReads) {
				logger.debug("Tag read " + rawChipRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objReader != null)
					objReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Test
	public void testSocket() {
		simulator = new TSCollectorSimulator();
		try {
			simulator.loadLogFile(simulator.getClass().getResourceAsStream("/backups/timingsense/sample_payload.json"));
			simulator.start();
			Thread thread = new Thread(simulator);
			thread.start();
			logger.debug("Server is running, starting tests");
			TSCollectorClient client = new TSCollectorClient();
			client.setHostname("127.0.0.1");
			Thread clientThread = new Thread(client);
			clientThread.start();
			client.connect();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
