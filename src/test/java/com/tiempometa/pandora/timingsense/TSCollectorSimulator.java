/**
 * 
 */
package com.tiempometa.pandora.timingsense;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author gtasi
 *
 */
public class TSCollectorSimulator implements Runnable {
	private static final Logger logger = Logger.getLogger(TSCollectorSimulator.class);
	private ServerSocket serverSocket;
	private int port = 10200; // use default port
	private Socket clientSocket = null;
	private List<String> historyLog;

	OutputStream outputStream;

	public static void main(String[] args) {
		TSCollectorSimulator simulator = new TSCollectorSimulator();
		try {
			simulator.loadLogFile(simulator.getClass().getResourceAsStream("/backups/timingsense/sample_payload.json"));
			simulator.start();
			Thread thread = new Thread(simulator);
			thread.run();
			while (true) {
				try {
					thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	public void loadLogFile(InputStream inputStream) throws IOException {
		String line;
		historyLog = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		do {
			line = reader.readLine();
			if (line != null) {
				historyLog.add(line);
			}
		} while (line != null);
	}

	public void start() throws IOException {
		logger.info("Starting socket server");
		serverSocket = new ServerSocket(port);
		logger.info("Successfully started socket server on port :" + port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			clientSocket = serverSocket.accept();
			logger.info("Accepted connection");
			try {
				outputStream = clientSocket.getOutputStream();
				for (String line : historyLog) {
					logger.info("Writing log line " + line);
					outputStream.write(line.substring(0, 20).getBytes());
					outputStream.flush();
					try {
						Thread.sleep(1200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					outputStream.write(line.substring(20).getBytes());
					outputStream.write("\r\n".getBytes());
					outputStream.flush();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clientSocket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
