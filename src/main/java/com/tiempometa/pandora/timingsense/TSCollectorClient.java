/**
 * 
 */
package com.tiempometa.pandora.timingsense;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.tiempometa.pandora.tagreader.TagReadListener;

/**
 * @author gtasi
 *
 */
public class TSCollectorClient implements Runnable {
	private static final Logger logger = Logger.getLogger(TSCollectorClient.class);

	private int port = 10200; // use default port
	private String hostname = "";
	private Socket readerSocket = null;
	private TagReadListener tagReadListener;
	private InputStream dataInputStream = null;
	private OutputStream dataOutputStream = null;
	private boolean doReadings = true; // flag indicating continue reading tags
	private StringBuffer buffer = new StringBuffer();
	JsonParser parser = new JsonParser();
	private SocketMonitor socketMonitor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
//		try {
		boolean read = true;
		while (read) {
			synchronized (this) {
				logger.debug("DO READINGS " + doReadings);
				read = doReadings;
			}
			if (this.isConnected()) {
				logger.info("Socket is open");
				if (readerSocket.isClosed()) {
					logger.warn("Socket is closed!");
				}

				int dataInStream;
				try {
					dataInStream = dataInputStream.available();
					logger.info("Available data " + dataInStream);
					if (dataInStream > 0) {
						byte[] b = new byte[dataInStream];
						dataInputStream.read(b);
						String dataString = new String(b);
//						if (logger.isDebugEnabled()) {
						logger.debug("PARSE>\n" + dataString + "\nLEN:" + dataString.length());
						buffer.append(dataString);
						if (socketMonitor != null) {
							socketMonitor.appendText(dataString);
						}
						try {
							String json = buffer.toString();
							parser.parse(json);
							List<TimingsenseTagRead> tagReads = TimingsenseTagRead.parseJson(json);
							tagReadListener.notifyTagReads(TimingsenseTagRead.toRawChipReads(tagReads));
							buffer = new StringBuffer();
							logger.info("*** VALID JSON");
							socketMonitor.appendText("\n\rVALID PAYLOAD, parsing...\n\r");
						} catch (JsonParseException e) {
							logger.warn("Invalid JSON");
//							e.printStackTrace();
						}
//						}
//							parsePayload(dataString);
//							dataString = dataString.replace("CONECTADO_4", "");
//							String[] dataRows = dataString.split("\\|");
					} else {
						logger.info("No data in stream");
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					try {
						disconnect();
						connect();
//							dataInputStream = readerSocket.getInputStream();
//							dataOutputStream = readerSocket.getOutputStream();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.warn("STOPPING AND DISCONNECT!");
//		disconnect();
	}

//	public void setCommandResponseHandler(JTSCollectorPanel jUltraReaderPanel) {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void registerTagReadListener(JTSCollectorPanel jUltraReaderPanel) {
//		tagReadListener
//
//	}

	public boolean isConnected() {
		if ((dataInputStream == null) || (dataOutputStream == null)) {
			return false;
		} else {
			return true;
		}
	}

	public void disconnect() throws IOException {
		doReadings = false;
		readerSocket.close();
		dataInputStream = null;
		dataOutputStream = null;

	}

	public void setHostname(String hostname) {
		this.hostname = hostname;

	}

	private void openSocket() throws UnknownHostException, IOException {
		readerSocket = new Socket(hostname, port);
		dataInputStream = readerSocket.getInputStream();
		dataOutputStream = readerSocket.getOutputStream();
	}

	public void connect() throws UnknownHostException, IOException {
//		this.hostname = hostName;
//		this.port = port;
		logger.info("Opening socket");
		doReadings = true;
		openSocket();
		logger.info("Notify successful connect");
	}

	public TagReadListener getTagReadListener() {
		return tagReadListener;
	}

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;
	}

	public SocketMonitor getSocketMonitor() {
		return socketMonitor;
	}

	public void setSocketMonitor(SocketMonitor socketMonitor) {
		this.socketMonitor = socketMonitor;
	}

}
