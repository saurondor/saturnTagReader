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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Pure TCP infrastructure base for all TCP-connected readers.
 * Manages socket lifecycle only — no protocol logic.
 *
 * @author Gerardo Esteban Tasistro Giubetic
 */
public abstract class AbstractTcpReaderClient implements Runnable {

    private static final Logger logger = LogManager.getLogger(AbstractTcpReaderClient.class);

    private String hostname = "";
    private int port = 10200;
    private boolean connected = false;

    protected Socket readerSocket;
    protected InputStream inputStream;
    protected TagReadListener tagReadListener;

    public void registerTagReadListener(TagReadListener listener) {
        tagReadListener = listener;
    }

    public void connect() throws UnknownHostException, IOException {
        logger.debug("Connecting to " + hostname + ":" + port);
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

    public boolean isConnected() {
        return connected;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
