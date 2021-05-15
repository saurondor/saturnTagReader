/*
 * Copyright (c) 2019 Gerardo Esteban Tasistro Giubetic
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.tiempometa.pandora.macsha;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.tiempometa.pandora.macsha.commands.MacshaCommand;
import com.tiempometa.pandora.macsha.commands.one4all.StartCommand;
import com.tiempometa.pandora.macsha.commands.one4all.StopCommand;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class Ocelot implements Runnable {

	private static final Logger logger = Logger.getLogger(Ocelot.class);
	public static final String COMMAND_START = "Start";
	public static final String COMMAND_STOP = "Stop";
	public static final String COMMAND_CONNECTED = "CONECTADO";
	public static final String COMMAND_STARTED = "START-OK";
	public static final String COMMAND_OPERATION_STARTED = "OPERATION-MODE-STARTED";
	public static final String COMMAND_STOPPED = "STOP-OK";
	public static final String COMMAND_REMOTE_OFF = "MODO-REMOTE-OFF";

	private int port = 10002; // use default port
	private String hostname = "";
	private Socket readerSocket = null;
	private InputStream dataInputStream = null;
	private OutputStream dataOutputStream = null;
	private boolean doReadings = true; // flag indicating continue reading tags
	private TagReadListener tagReadListener;
	private CommandResponseHandler commandResponseHandler;
	private Integer keepAliveCounter = 0;
	private KeepAlive keepAlive = new KeepAlive();

	private class KeepAlive implements Runnable {
		private boolean runMe = true;

		public void start() {
			runMe = true;
		}

		public void stop() {
			logger.info("SIGNAL STOP KEEPALIVE");
			synchronized (this) {
				runMe = false;
			}
		}

		@Override
		public void run() {
			boolean run = true;

			logger.debug("Starting keepalive worker thread");
			while (run) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				synchronized (this) {
					run = runMe;
				}
				if (run) {
					synchronized (keepAlive) {
						keepAliveCounter++;
						if (keepAliveCounter >= 30) {
							logger.debug("TIMEOUT!");
							(new Thread(new Runnable() {
								@Override
								public void run() {
									notifyTimeOut();

								}
							})).start();
						}
						logger.debug("Increased keepalive counter to " + keepAliveCounter);
					}
				}
			}
			logger.debug("ENDED KEEPALIVE!!");
			runMe = true;
		}

	}

	public boolean isConnected() {
		if ((dataInputStream == null) || (dataOutputStream == null)) {
			return false;
		} else {
			return true;
		}
	}

	public void notifyTimeOut() {
		logger.error("TIMEOUT OCELOT " + (new Date()));
		commandResponseHandler.notifyTimeout();
	}

	public void sendCommand(MacshaCommand command) {
		try {
			command.sendCommand(dataOutputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connect(String hostName, Integer port) throws UnknownHostException, IOException {
		this.hostname = hostName;
		this.port = port;
		logger.info("Opening socket");
		openSocket();
		logger.info("Socket opened");
		startKeepAlive();
		logger.info("Notify successful connect");
	}

	/**
	 * 
	 */
	private void startKeepAlive() {
//		keepAlive = new KeepAlive();
//		keepAlive.start();
		Thread thread = new Thread(keepAlive);
		thread.start();
	}

	public void connect(String hostName) throws UnknownHostException, IOException {
		connect(hostName, port);
	}

	private void openSocket() throws UnknownHostException, IOException {
		readerSocket = new Socket(hostname, port);
		dataInputStream = readerSocket.getInputStream();
		dataOutputStream = readerSocket.getOutputStream();
	}

	public void disconnect() throws IOException {
		keepAlive.stop();
		doReadings = false;
		readerSocket.close();
		dataInputStream = null;
		dataOutputStream = null;
	}

	@Override
	public void run() {
		do {

//			try {
			boolean read = true;
			while ((read) && (this.isConnected())) {
				synchronized (this) {
					logger.debug("DO READINGS " + doReadings);
					read = doReadings;
				}
				logger.info("Socket is open");
				while (this.isConnected()) {
					if (readerSocket.isClosed()) {
						logger.warn("Socket is closed!");
					}
					int dataInStream;
					try {
						dataInStream = dataInputStream.available();
						if (dataInStream > 0) {
							byte[] b = new byte[dataInStream];
							dataInputStream.read(b);
							String dataString = new String(b);
							if (logger.isDebugEnabled()) {
								logger.debug("IN BUFFER>\n\t\t\t\t\t\t" + dataString + "\n\t\t\t\t\t\tLEN>"
										+ dataString.length());
							}
							parsePayload(dataString);
						} else {
						}
					} catch (IOException e1) {
						logger.error("Fault getting available bytes " + e1.getMessage());
						e1.printStackTrace();
//							try {
//								disconnect();
//								connect(hostname);
//								dataInputStream = readerSocket.getInputStream();
//								dataOutputStream = readerSocket.getOutputStream();
//							} catch (IOException e2) {
//								e2.printStackTrace();
//							}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
//			logger.warn("STOPPING AND DISCONNECT!");
//			disconnect();
//			} catch (IOException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}
			logger.debug("Waiting to connect");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		} while (true);
	}

	private void parsePayload(String dataString) {
		List<MacshaTagRead> readings = new ArrayList<MacshaTagRead>();
//		if (logger.isDebugEnabled()) {
//			logger.debug("PARSE>\n" + dataString + "\nLEN:" + dataString.length());
//		}
		String[] dataRows = dataString.split("\\|");
		if (logger.isDebugEnabled()) {
			logger.debug("DATAROWS>\t:" + dataRows.length);
		}
		for (int i = 0; i < dataRows.length; i++) {
			String[] row = dataRows[i].split(";");
			if (logger.isDebugEnabled()) {
				logger.debug("ROW " + i + " HEADER: " + row[0]);
			}
			MacshaTagRead tagRead = parseRow(row);
			if (tagRead != null) {
				readings.add(tagRead);
			}
		}
		if (readings.size() > 0) {
			notifyTagReads(readings);
		}
	}

	private void notifyTagReads(List<MacshaTagRead> readings) {
		List<RawChipRead> chipReadList = new ArrayList<RawChipRead>();
		for (MacshaTagRead macshaTagRead : readings) {
			RawChipRead chipRead = macshaTagRead.toRawChipRead();
			chipReadList.add(chipRead);
		}
		tagReadListener.notifyTagReads(chipReadList);
	}

	private MacshaTagRead parseRow(String[] row) {
		if (row[0].startsWith(COMMAND_CONNECTED)) {
			synchronized (keepAlive) {
				keepAliveCounter = 0;
			}
			logger.debug("GOT Connected. Keepalive now :" + keepAliveCounter);
			return null;
		} else if (row[0].startsWith(COMMAND_STARTED)) {
			logger.debug("GOT Started");
			return null;
		} else if (row[0].startsWith(COMMAND_OPERATION_STARTED)) {
			logger.debug("GOT Operation STARTED");
			return null;
		} else if (row[0].startsWith(COMMAND_STOPPED)) {
			logger.debug("GOT Stop");
			return null;
		} else if (row[0].startsWith(COMMAND_REMOTE_OFF)) {
			logger.debug("GOT Remote off");
			return null;
		} else {
			try {
				logger.debug("GOT TAG READ ROW");
				MacshaTagRead tagRead = MacshaTagRead.row(row[0], Context.getZoneId());
				logger.debug("TAG: " + tagRead);
				return tagRead;
			} catch (NumberFormatException e) {
				logger.debug("GOT COMMAND RESPONSE");
				parseCommandResponse(row);
				// TODO notify handler
				return null;
			}
		}
	}

	private void parseCommandResponse(String[] row) {
		MacshaCommand command = null;
		switch (row[0]) {
		case COMMAND_START:
			logger.debug("GOT START COMMAND");
			command = new StartCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_STOP:
			logger.debug("GOT STOP COMMAND");
			command = new StopCommand();
			command.parseCommandRow(row);
			break;
		default:
			break;
		}
		handleCommandResponse(command);
	}

	private void handleCommandResponse(MacshaCommand command) {
		if (command == null) {

		} else {
			commandResponseHandler.handleCommandResponse(command);
		}
	}

	public TagReadListener getTagReadListener() {
		return tagReadListener;
	}

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;
	}

	public CommandResponseHandler getCommandResponseHandler() {
		return commandResponseHandler;
	}

	public void setCommandResponseHandler(CommandResponseHandler commandResponseHandler) {
		this.commandResponseHandler = commandResponseHandler;
	}

	public void stop() throws IOException {
		disconnect();
//		keepAlive.stop();
	}

}
