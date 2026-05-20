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
package com.tiempometa.pandora.foxberry.tcpip;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.foxberry.FoxberryCommandResponseHandler;
import com.tiempometa.pandora.tagreader.AbstractTcpReaderClient;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * TCP client for Foxberry/R700 readers using the CSV data protocol.
 *
 * Consumes the read-only data port stream emitted by foxberry-reader:
 *   reader,antenna,epc,time_microseconds,rssi,tid,user_data\n\r
 *
 * Keepalive frames (second field = "*") are silently acknowledged and
 * not forwarded to the TagReadListener.
 *
 * Zero dependency on the ipicoreader package — kept separate so this
 * client can evolve with the R700/Octane protocol independently.
 *
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class FoxberryCsvClient extends AbstractTcpReaderClient {

    private static final Logger logger = LogManager.getLogger(FoxberryCsvClient.class);

    private final StringBuffer streamBuffer = new StringBuffer();
    private String checkPoint;
    private String terminal;
    private FoxberryCommandResponseHandler commandResponseHandler;

    public void connect(String hostname, int port) throws UnknownHostException, IOException {
        setHostname(hostname);
        setPort(port);
        connect();
    }

    @Override
    public void run() {
        while (isConnected()) {
            try {
                int available = inputStream.available();
                if (available > 0) {
                    byte[] buf = new byte[available];
                    inputStream.read(buf);
                    String data = new String(buf);
                    logger.debug("Received " + data.length() + " bytes");
                    streamBuffer.append(data);
                    processBuffer();
                }
            } catch (IOException e) {
                logger.error("IO error reading from reader", e);
                if (commandResponseHandler != null) {
                    commandResponseHandler.notifyCommException(e);
                }
                disconnect();
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

    private void processBuffer() {
        String content = streamBuffer.toString();
        if (!content.contains("\n")) return;

        String[] lines = content.split("\n", -1);
        // Keep the last (possibly incomplete) fragment in the buffer
        String incomplete = lines[lines.length - 1];
        streamBuffer.setLength(0);
        if (!incomplete.isEmpty()) {
            streamBuffer.append(incomplete);
        }

        List<RawChipRead> reads = new ArrayList<>();
        for (int i = 0; i < lines.length - 1; i++) {
            String line = lines[i].replace("\r", "").trim();
            if (line.isEmpty()) continue;
            if (FoxberryCsvRead.isKeepalive(line)) {
                logger.debug("Keepalive received from reader");
                continue;
            }
            FoxberryCsvRead csvRead = FoxberryCsvRead.parse(line);
            if (csvRead == null) {
                logger.warn("Unparseable line skipped: " + line);
                continue;
            }
            csvRead.setCheckPoint(checkPoint);
            csvRead.setTerminal(terminal);
            reads.add(csvRead.toRawChipRead());
        }
        if (!reads.isEmpty() && tagReadListener != null) {
            tagReadListener.notifyTagReads(reads);
        }
    }

    public void setCommandResponseHandler(FoxberryCommandResponseHandler handler) {
        this.commandResponseHandler = handler;
    }

    public FoxberryCommandResponseHandler getCommandResponseHandler() {
        return commandResponseHandler;
    }

    public String getCheckPoint() { return checkPoint; }
    public void setCheckPoint(String checkPoint) { this.checkPoint = checkPoint; }
    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }
}
