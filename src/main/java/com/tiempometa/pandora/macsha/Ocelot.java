/**
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

import org.apache.log4j.Logger;

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

	private int port = 10002; // use default port
	private String hostname = "";
	private Socket readerSocket = null;
	private InputStream dataInputStream = null;
	private OutputStream dataOutputStream = null;
	private boolean doReadings = true; // flag indicating continue reading tags
	private TagReadListener tagReadListener;
//	private String checkPoint;
	private CommandResponseHandler commandResponseHandler;
//	private KeepAlive keepAlive;

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
//		startKeepAlive();
//		notifyConnected();
		logger.info("Notify successful connect");
	}

	public void connect(String hostName) throws UnknownHostException, IOException {
		this.hostname = hostName;
		logger.info("Opening socket");
		openSocket();
		logger.info("Socket opened");
//		startKeepAlive();
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
								logger.debug("IN BUFFER>\n" + dataString + "\nLEN:" + dataString.length());
							}
							parsePayload(dataString);
//							dataString = dataString.replace("CONECTADO_4", "");

//							String[] dataRows = dataString.split("\\|");

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
//		String[] dataRows = dataString.split("\\r\\n");
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
		if (row[0].startsWith(COMMAND_CONNECTED)) {
			logger.debug("GOT Connected");
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
//		keepAlive.stop();
	}

}
