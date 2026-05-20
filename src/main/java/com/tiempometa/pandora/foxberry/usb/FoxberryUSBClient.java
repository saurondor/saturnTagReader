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
package com.tiempometa.pandora.foxberry.usb;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.foxberry.FoxberryCommandResponseHandler;
import com.tiempometa.pandora.foxberry.tcpip.FoxberryRead;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class FoxberryUSBClient implements Runnable {
	private static final Logger logger = LogManager.getLogger(FoxberryUSBClient.class);

	private int port = 10200; // use default port
	private String hostname = "";
	private boolean connected = false;
	private Socket readerSocket = null;
	private InputStream inputStream = null;
	private List<FoxberryRead> readLog = new ArrayList<FoxberryRead>();
	StringBuffer streamBuffer = new StringBuffer();
	TagReadListener tagReadListener;
	private FoxberryCommandResponseHandler commandResponseHandler;
	private String checkPointOne;
	private String checkPointTwo;
	private String terminal;

	public void registerTagReadListener(TagReadListener listener) {
		tagReadListener = listener;
	}

	/**
	 * Connects to the reader
	 *
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect() throws UnknownHostException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Connecting to reader on port :" + port);
		}
		readerSocket = new Socket(hostname, port);
		inputStream = readerSocket.getInputStream();
		connected = true;
		if (logger.isDebugEnabled()) {
			logger.debug("Connected!");
		}
	}

	/**
	 * Issues a disconnect signal and closes the underlying socket.
	 */
	public void disconnect() {
		synchronized (this) {
			connected = false;
		}
		if (readerSocket != null && !readerSocket.isClosed()) {
			try {
				readerSocket.close();
			} catch (IOException e) {
				logger.warn("Error closing socket", e);
			}
		}
	}

	public void run() {
		while (connected) {
			int dataInStream;
			try {
				dataInStream = inputStream.available();
				if (dataInStream > 0) {
					byte[] b = new byte[dataInStream];
					inputStream.read(b);
					String dataString = new String(b);
					if (logger.isDebugEnabled()) {
						logger.debug("BUFFER DATA LEN:" + dataString.length() + ">\n" + dataString);
					}
					append(dataString);
				}
			} catch (IOException e1) {
				logger.error("IO error reading from reader", e1);
				disconnect();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void append(String dataString) {
		streamBuffer.append(dataString);
		if (streamBuffer.toString().contains("\n")) {
			processBuffer();
		}
	}

	private void processBuffer() {
		String[] dataRows;
		List<String> lines = new ArrayList();
		dataRows = streamBuffer.toString().split("\\n");
		for (int i = 0; i < dataRows.length; i++) {
			if (dataRows[i].length() >= FoxberryRead.FRAME_LRC_END) {
				lines.add(dataRows[i]);
			}
		}
		if ((dataRows[dataRows.length - 1].startsWith(FoxberryRead.DATA_LINE_HEADER))
				&& (dataRows[dataRows.length - 1].length() < FoxberryRead.FRAME_SEEN_END)) {
			logger.debug("Keeping last line " + dataRows[dataRows.length - 1]);
			streamBuffer = new StringBuffer(dataRows[dataRows.length - 1]);
		} else {
			logger.debug("Dropping last line");
			streamBuffer = new StringBuffer();
		}
		List<RawChipRead> readings = new ArrayList<RawChipRead>();
		for (String line : lines) {
			logger.debug("Parsing string " + line);
			FoxberryRead read = FoxberryRead.parse(line.replace("\r", ""));
			if (read == null) {
				logger.error("Invalid data string :" + line + ", length:" + line.length());
			} else {
				read.setCheckPoint(checkPointOne);
				read.setTerminal(terminal);
				this.saveChip(read);
				readings.add(read.toRawChipRead());
			}
		}
		tagReadListener.notifyTagReads(readings);
	}

	private void saveChip(FoxberryRead read) {
		synchronized (this) {
			readLog.add(read);
		}
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public List<FoxberryRead> getReadLog() {
		return readLog;
	}

	public void clearLog() {
		synchronized (this) {
			readLog = new ArrayList<FoxberryRead>();
		}
	}

	public void setCommandResponseHandler(FoxberryCommandResponseHandler handler) {
		this.commandResponseHandler = handler;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public String getCheckPointOne() {
		return checkPointOne;
	}

	public String getCheckPointTwo() {
		return checkPointTwo;
	}

	public void setCheckPointOne(String checkPointOne) {
		this.checkPointOne = checkPointOne;
	}

	public void setCheckPointTwo(String checkPointTwo) {
		this.checkPointTwo = checkPointTwo;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public List<String> getSerialPorts() {
		List<String> ports = new ArrayList<String>();
		return ports;
	}

	public void connect(String selectedItem) {
		// TODO: serial port connect not implemented
	}
}
