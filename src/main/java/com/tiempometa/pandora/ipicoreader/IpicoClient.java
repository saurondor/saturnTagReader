/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.tiempometa.timing.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class IpicoClient implements Runnable {
	private static final Logger logger = Logger.getLogger(IpicoClient.class);

	private DataLoadProperties loadProperties = null;
	SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private int port = 10200; // use default port
	private String hostname = "";
	private boolean connected = true;
	private Socket readerSocket = null;
	private InputStream inputStream = null;
	private List<IpicoRead> readLog = new ArrayList<IpicoRead>();
	StringBuffer streamBuffer = new StringBuffer();
	TagReadListener tagReadListener;

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
	 * Issues a disconnect signal to terminate connection to reader.
	 */
	public void disconnect() {
		synchronized (this) {
			connected = false;
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			readerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			if (dataRows[i].length() >= IpicoRead.FRAME_LRC_END) {
				lines.add(dataRows[i]);
			}
		}
		if ((dataRows[dataRows.length - 1].startsWith(IpicoRead.DATA_LINE_HEADER))
				&& (dataRows[dataRows.length - 1].length() < IpicoRead.FRAME_SEEN_END)) {
			logger.debug("Keeping last line " + dataRows[dataRows.length - 1]);
			streamBuffer = new StringBuffer(dataRows[dataRows.length - 1]);
		} else {
			logger.debug("Dropping last line");
			streamBuffer = new StringBuffer();
		}
		List<RawChipRead> readings = new ArrayList<RawChipRead>();
		for (String line : lines) {
			logger.debug("Parsing string " + line);
			IpicoRead read = IpicoRead.parse(line.replace("\r", ""));
			if (read == null) {
				logger.error("Invalid data string :" + line + ", length:" + line.length());
			} else {
				this.saveChip(read);
				readings.add(read.toRawChipRead());
			}
		}
		tagReadListener.notifyTagReads(readings);
	}

	private void saveChip(IpicoRead read) {
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

	public List<IpicoRead> getReadLog() {
		return readLog;
	}

	public void clearLog() {
		synchronized (this) {
			readLog = new ArrayList<IpicoRead>();
		}
	}

	public void setCommandResponseHandler(JIpicoReaderPanel jIpicoReaderPanel) {
		// TODO Auto-generated method stub

	}
}
