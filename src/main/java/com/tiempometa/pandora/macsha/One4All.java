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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.macsha.commands.MacshaCommand;
import com.tiempometa.pandora.macsha.commands.one4all.ClearFilesCommand;
import com.tiempometa.pandora.macsha.commands.one4all.GetFileCommand;
import com.tiempometa.pandora.macsha.commands.one4all.GetFileInfoCommand;
import com.tiempometa.pandora.macsha.commands.one4all.GetPassingsCommand;
import com.tiempometa.pandora.macsha.commands.one4all.GetProtocolCommand;
import com.tiempometa.pandora.macsha.commands.one4all.GetTimeCommand;
import com.tiempometa.pandora.macsha.commands.one4all.ListFilesCommand;
import com.tiempometa.pandora.macsha.commands.one4all.NewFileCommand;
import com.tiempometa.pandora.macsha.commands.one4all.PingCommand;
import com.tiempometa.pandora.macsha.commands.one4all.PushTagsCommand;
import com.tiempometa.pandora.macsha.commands.one4all.ReadBatteryCommand;
import com.tiempometa.pandora.macsha.commands.one4all.SetBounceCommand;
import com.tiempometa.pandora.macsha.commands.one4all.SetBuzzerCommand;
import com.tiempometa.pandora.macsha.commands.one4all.SetProtocolCommand;
import com.tiempometa.pandora.macsha.commands.one4all.SetTimeCommand;
import com.tiempometa.pandora.macsha.commands.one4all.StartCommand;
import com.tiempometa.pandora.macsha.commands.one4all.StopCommand;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class One4All implements Runnable {

	public static final String COMMAND_GETPROTOCOL = "GETPROTOCOL";
	public static final String COMMAND_SETPROTOCOL = "SETPROTOCOL";
	public static final String COMMAND_START = "START";
	public static final String COMMAND_STOP = "STOP";
	public static final String COMMAND_PUSHTAGS = "PUSHTAGS";
	public static final String COMMAND_SETBOUNCE = "SETBOUNCE";
	public static final String COMMAND_SETTIME = "SETTIME";
	public static final String COMMAND_GETTIME = "GETTIME";
	public static final String COMMAND_SETBUZZER = "SETBUZZER";
	public static final String COMMAND_NEWFILE = "NEWFILE";
	public static final String COMMAND_PASSINGS = "PASSINGS";
	public static final String COMMAND_GETPASSINGS = "GETPASSINGS";
	public static final String COMMAND_READBATTERY = "READBATTERY";
	public static final String COMMAND_PING = "PING";
	public static final String COMMAND_LISTFILES = "LISTFILES";
	public static final String COMMAND_GETFILEINFO = "GETFILEINFO";
	public static final String COMMAND_GETFILE = "GETFILE";
	public static final String COMMAND_CLEARFILES = "CLEARFILES";
	private static final String COMMAND_HELLO = "HELLO";

	private static final Logger logger = LogManager.getLogger(One4All.class);

	private int port = 10002; // use default port
	private String hostname = "";
	private Socket readerSocket = null;
	private InputStream dataInputStream = null;
	private OutputStream dataOutputStream = null;
	private boolean doReadings = true; // flag indicating continue reading tags
	private TagReadListener tagReadListener;
//	private String checkPoint;
	private CommandResponseHandler commandResponseHandler;
	private KeepAlive keepAlive;

	private class KeepAlive implements Runnable {
		private boolean runMe = true;

		public void stop() {
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
					ReadBatteryCommand command = new ReadBatteryCommand();
					command.sendCommand(dataOutputStream);
					Thread.sleep(10000);
					logger.debug("Send battery keepalive");
					synchronized (this) {
						run = runMe;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					commandResponseHandler.notifyCommException(e);
					e.printStackTrace();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}

	}

	public boolean isConnected() {
		if ((dataInputStream == null) || (dataOutputStream == null)) {
			return false;
		} else {
			return true;
		}
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
//		notifyConnected();
		logger.info("Notify successful connect");
	}

	/**
	 * 
	 */
	private void startKeepAlive() {
		keepAlive = new KeepAlive();
		Thread thread = new Thread(keepAlive);
		thread.start();
	}

	public void connect(String hostName) throws UnknownHostException, IOException {
		this.hostname = hostName;
		logger.info("Opening socket");
		openSocket();
		logger.info("Socket opened");
		startKeepAlive();
//		notifyConnected();
		logger.info("Notify successful connect");
	}

	private void openSocket() throws UnknownHostException, IOException {
		readerSocket = new Socket(hostname, port);
		dataInputStream = readerSocket.getInputStream();
		dataOutputStream = readerSocket.getOutputStream();
	}

	public void disconnect() throws IOException {
		doReadings = false;
		readerSocket.close();
		dataInputStream = null;
		dataOutputStream = null;
//		notifyDisconnected();
	}

	@Override
	public void run() {
		try {
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
								logger.debug("PARSE>\n" + dataString + "\nLEN:" + dataString.length());
							}
							parsePayload(dataString);
//							if (dataString.indexOf("CONECTADO_4") >= 0) {
//								logListener.log(new String[] { "KEEPALIVE - CONECTADO_4" });
//							}
							// filter out CONECTADO_4 that sometimes appears prior to valid payload
							dataString = dataString.replace("CONECTADO_4", "");
//							StringBuffer buffer = new StringBuffer((new Date()) + " " + dataString);
//							String[] dataRows = dataString.split("\\n");
							// HW specific split rows? pipe for version 4 and prior, \n\r for generation 5
							String[] dataRows = dataString.split("\\|");
//							if (logListener != null) {
//								logListener.log(dataRows);
//							}

//							List<TagReading> readings = new ArrayList<TagReading>();
//							for (String string : dataRows) {
//								logger.debug(">>> DATA ROW (" + string.length() + ")");
//								logger.debug(string);
//								if (string.length() > 0) {
//									logger.debug(Integer.toHexString(string.toCharArray()[0]));
//									if (string.replaceAll("\\r", "").length() > 0) {
////									TagReading reading = new TagReading(string);
//										TagReading reading = TagReading.parseMacsha(string);
//										if ((reading != null) && (reading.isValid())) {
//											logger.debug(reading);
//											readings.add(reading);
//										} else {
//											logger.warn("Invalid reading - " + reading);
//										}
//									}
//								}
//							}
//							notifyListeners(readings);
						} else {
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						try {
							disconnect();
							connect(hostname);
							dataInputStream = readerSocket.getInputStream();
							dataOutputStream = readerSocket.getOutputStream();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			logger.warn("STOPPING AND DISCONNECT!");
			disconnect();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	private void parsePayload(String dataString) {
		List<MacshaTagRead> readings = new ArrayList<MacshaTagRead>();
		if (logger.isDebugEnabled()) {
			logger.debug("PARSE>\n" + dataString + "\nLEN:" + dataString.length());
		}
		String[] dataRows = dataString.split("\\r\\n");
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
//		RawChipReadDao chipDao = (RawChipReadDao) Context.getCtx().getBean("rawChipReadDao");
		List<RawChipRead> chipReadList = new ArrayList<RawChipRead>();
		for (MacshaTagRead macshaTagRead : readings) {
			RawChipRead chipRead = macshaTagRead.toRawChipRead();
//			synchronized (this) {
//				chipRead.setCheckPoint(checkPoint);
//			}
			chipReadList.add(chipRead);
		}
//		chipDao.batchSave(chipReadList);
		tagReadListener.notifyTagReads(chipReadList);
	}

	private MacshaTagRead parseRow(String[] row) {
		if (row[0].startsWith(COMMAND_HELLO)) {
			logger.debug("GOT HELLO");
			return null;
		} else {
			try {
				Integer readId = Integer.valueOf(row[0]);
				logger.debug("GOT TAG READ ROW");
				MacshaTagRead tagRead = MacshaTagRead.row(row, Context.getZoneId());
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
		case COMMAND_CLEARFILES:
			logger.debug("GOT CLEARFILES COMMAND");
			command = new ClearFilesCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_GETFILE:
			logger.debug("GOT GETFILE COMMAND");
			command = new GetFileCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_GETFILEINFO:
			logger.debug("GOT GETFILEINFO COMMAND");
			command = new GetFileInfoCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_GETPASSINGS:
			logger.debug("GOT GETPASSINGS COMMAND");
			command = new GetPassingsCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_GETPROTOCOL:
			logger.debug("GOT GETPROTOCOL COMMAND");
			command = new GetProtocolCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_GETTIME:
			logger.debug("GOT GETTIME COMMAND");
			command = new GetTimeCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_HELLO:
			logger.debug("GOT HELLO COMMAND");
			break;
		case COMMAND_LISTFILES:
			logger.debug("GOT LISTFILES COMMAND");
			command = new ListFilesCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_NEWFILE:
			logger.debug("GOT NEWFILE COMMAND");
			command = new NewFileCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_PASSINGS:
			logger.debug("GOT PASSINGS COMMAND");
			command = new StartCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_PING:
			logger.debug("GOT PING COMMAND");
			command = new PingCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_PUSHTAGS:
			logger.debug("GOT PUSHTAGS COMMAND");
			command = new PushTagsCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_READBATTERY:
			logger.debug("GOT READBATTERY COMMAND");
			command = new ReadBatteryCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_SETBOUNCE:
			logger.debug("GOT SETBOUNCE COMMAND");
			command = new SetBounceCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_SETBUZZER:
			logger.debug("GOT SETBUZZER COMMAND");
			command = new SetBuzzerCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_SETPROTOCOL:
			logger.debug("GOT SETPROTOCOL COMMAND");
			command = new SetProtocolCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_SETTIME:
			logger.debug("GOT SETTIME COMMAND");
			command = new SetTimeCommand();
			command.parseCommandRow(row);
			break;
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

//	public String getCheckPoint() {
//		return checkPoint;
//	}
//
//	public void setCheckPoint(String checkPoint) {
//		synchronized (this) {
//			this.checkPoint = checkPoint;
//		}
//	}

	public CommandResponseHandler getCommandResponseHandler() {
		return commandResponseHandler;
	}

	public void setCommandResponseHandler(CommandResponseHandler commandResponseHandler) {
		this.commandResponseHandler = commandResponseHandler;
	}

	public void stop() throws IOException {
		disconnect();
		keepAlive.stop();
	}

}
