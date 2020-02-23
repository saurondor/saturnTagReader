/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiempometa.pandora.ipicoreader.commands.GetTimeCommand;
import com.tiempometa.pandora.ipicoreader.commands.SetTimeCommand;

/**
 * @author gtasi
 *
 */
public class TestTcpIp {
	private static final Logger logger = Logger.getLogger(TestTcpIp.class);
	EliteSimulator simulator = new EliteSimulator();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
//		simulator.loadLogFile(simulator.getClass().getResourceAsStream("/FS_LS.log.txt"));
//		simulator.start();
//		Thread thread = new Thread(simulator);
//		thread.run();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadStream() {
		try {
			logger.info("Emulator started");
			IpicoClient client = new IpicoClient();
			logger.info("Connecting to host");
			client.setHostname("127.0.0.1");
			client.connect();
			logger.info("Connected to host!");
			Thread thread = new Thread(client);
			thread.start();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("Read log size " + client.getReadLog().size());
			for (IpicoRead line : client.getReadLog()) {
				logger.info(line);
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		fail("Not yet implemented");
	}

	@Test
	public void testSetTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTime() {
		try {
			logger.info("Emulator started");
			IpicoTcpClient client = new IpicoTcpClient();
			logger.info("Connecting to host");
//			client.setHostname("127.0.0.1");
			client.connect("10.19.1.101");
			Thread thread = new Thread(client);
			thread.start();
			client.sendCommand(new GetTimeCommand());
			client.sendCommand(new SetTimeCommand());
			client.sendCommand(new GetTimeCommand());
			Thread.sleep(10000);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InvalidTelnetOptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fail("Not yet implemented");
	}

	@Test
	public void testClearHistory() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetMarker() {
		fail("Not yet implemented");
	}

}
