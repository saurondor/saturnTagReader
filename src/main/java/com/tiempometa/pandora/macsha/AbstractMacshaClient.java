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
package com.tiempometa.pandora.macsha;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.macsha.commands.MacshaCommand;
import com.tiempometa.pandora.tagreader.AbstractReadWriteTcpReaderClient;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * Shared infrastructure for all Macsha readers (One4All, Ocelot).
 * Handles bidirectional TCP, keep-alive lifecycle, and tag-read notification.
 * Subclasses supply the KeepAlive strategy and payload parsing.
 *
 * @author Gerardo Esteban Tasistro Giubetic
 */
public abstract class AbstractMacshaClient extends AbstractReadWriteTcpReaderClient {

    private static final Logger logger = LogManager.getLogger(AbstractMacshaClient.class);

    protected CommandResponseHandler commandResponseHandler;
    protected boolean doReadings = true;

    public AbstractMacshaClient() {
        setPort(10002);
    }

    @Override
    public boolean isConnected() {
        return inputStream != null && outputStream != null;
    }

    public void connect(String hostName, Integer port) throws UnknownHostException, IOException {
        setHostname(hostName);
        setPort(port);
        logger.info("Opening socket to " + hostName + ":" + port);
        super.connect();
        logger.info("Socket opened");
        startKeepAlive();
        logger.info("Notify successful connect");
    }

    public void connect(String hostName) throws UnknownHostException, IOException {
        setHostname(hostName);
        logger.info("Opening socket to " + hostName);
        super.connect();
        logger.info("Socket opened");
        startKeepAlive();
        logger.info("Notify successful connect");
    }

    @Override
    public void disconnect() {
        stopKeepAlive();
        doReadings = false;
        super.disconnect();
        inputStream = null;
    }

    public void stop() {
        disconnect();
    }

    public void sendCommand(MacshaCommand command) {
        try {
            command.sendCommand(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void notifyTagReads(List<MacshaTagRead> readings) {
        List<RawChipRead> chipReadList = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (MacshaTagRead macshaTagRead : readings) {
            String key = macshaTagRead.getRfidString() + ":" + macshaTagRead.getTimeMillis();
            if (seen.add(key)) {
                chipReadList.add(macshaTagRead.toRawChipRead());
            } else {
                logger.debug("Duplicate read filtered: {}", macshaTagRead);
            }
        }
        if (!chipReadList.isEmpty() && tagReadListener != null) {
            tagReadListener.notifyTagReads(chipReadList);
        }
    }

    protected void handleCommandResponse(MacshaCommand command) {
        if (command != null && commandResponseHandler != null) {
            commandResponseHandler.handleCommandResponse(command);
        }
    }

    public void setTagReadListener(TagReadListener listener) {
        tagReadListener = listener;
    }

    public TagReadListener getTagReadListener() {
        return tagReadListener;
    }

    public CommandResponseHandler getCommandResponseHandler() {
        return commandResponseHandler;
    }

    public void setCommandResponseHandler(CommandResponseHandler commandResponseHandler) {
        this.commandResponseHandler = commandResponseHandler;
    }

    protected abstract void startKeepAlive();

    protected abstract void stopKeepAlive();

    protected abstract void parsePayload(String dataString);
}
