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
import java.io.OutputStream;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extends AbstractTcpReaderClient with bidirectional I/O (adds OutputStream).
 * Base for all readers that send commands back to the device.
 *
 * @author Gerardo Esteban Tasistro Giubetic
 */
public abstract class AbstractReadWriteTcpReaderClient extends AbstractTcpReaderClient {

    private static final Logger logger = LogManager.getLogger(AbstractReadWriteTcpReaderClient.class);

    protected OutputStream outputStream;

    @Override
    public void connect() throws UnknownHostException, IOException {
        super.connect();
        outputStream = readerSocket.getOutputStream();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        outputStream = null;
    }
}
