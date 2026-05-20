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
 */
package com.tiempometa.pandora.tagreader;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.ipicoreader.CommandResponseHandler;
import com.tiempometa.pandora.ipicoreader.IpicoRead;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * Shared TCP connection logic for Ipico-protocol readers (IPICO Elite, Foxberry TCP).
 * Both devices use the same wire format, so all parsing lives here.
 *
 * @author Gerardo Esteban Tasistro Giubetic
 */
public abstract class AbstractTcpReaderClient implements Runnable {

    private static final Logger logger = LogManager.getLogger(AbstractTcpReaderClient.class);

    private int port = 10200;
    private String hostname = "";
    private boolean connected = false;
    private Socket readerSocket = null;
    private InputStream inputStream = null;
    private List<IpicoRead> readLog = new ArrayList<>();
    private StringBuffer streamBuffer = new StringBuffer();
    private TagReadListener tagReadListener;
    private String checkPointOne;
    private String checkPointTwo;
    private String terminal;

    public void registerTagReadListener(TagReadListener listener) {
        tagReadListener = listener;
    }

    public void setCommandResponseHandler(CommandResponseHandler handler) {
        // command response not yet implemented for TCP clients
    }

    public void connect() throws UnknownHostException, IOException {
        logger.debug("Connecting to reader at " + hostname + ":" + port);
        readerSocket = new Socket(hostname, port);
        inputStream = readerSocket.getInputStream();
        connected = true;
        logger.debug("Connected!");
    }

    public void disconnect() {
        synchronized (this) {
            connected = false;
        }
        try {
            if (readerSocket != null) {
                readerSocket.close();
            }
        } catch (IOException e) {
            logger.warn("Error closing socket: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                int dataInStream = inputStream.available();
                if (dataInStream > 0) {
                    byte[] b = new byte[dataInStream];
                    inputStream.read(b);
                    String dataString = new String(b);
                    logger.debug("BUFFER DATA LEN:" + dataString.length() + ">\n" + dataString);
                    append(dataString);
                }
            } catch (IOException e) {
                logger.warn("Connection lost: " + e.getMessage());
                synchronized (this) {
                    connected = false;
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        try {
            if (readerSocket != null && !readerSocket.isClosed()) {
                readerSocket.close();
            }
        } catch (IOException e) {
            logger.warn("Error closing socket on exit: " + e.getMessage());
        }
    }

    private void append(String dataString) {
        streamBuffer.append(dataString);
        if (streamBuffer.toString().contains("\n")) {
            processBuffer();
        }
    }

    private void processBuffer() {
        List<String> lines = new ArrayList<>();
        String[] dataRows = streamBuffer.toString().split("\\n");
        for (String row : dataRows) {
            if (row.length() >= IpicoRead.FRAME_LRC_END) {
                lines.add(row);
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
        List<RawChipRead> readings = new ArrayList<>();
        for (String line : lines) {
            logger.debug("Parsing string " + line);
            IpicoRead read = IpicoRead.parse(line.replace("\r", ""));
            read.setCheckPoint(checkPointOne);
            read.setTerminal(terminal);
            if (read == null) {
                logger.error("Invalid data string: " + line + ", length: " + line.length());
            } else {
                synchronized (this) {
                    readLog.add(read);
                }
                readings.add(read.toRawChipRead());
            }
        }
        tagReadListener.notifyTagReads(readings);
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
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

    public synchronized void clearLog() {
        readLog = new ArrayList<>();
    }

    public String getCheckPointOne() {
        return checkPointOne;
    }

    public void setCheckPointOne(String checkPointOne) {
        this.checkPointOne = checkPointOne;
    }

    public String getCheckPointTwo() {
        return checkPointTwo;
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
}
