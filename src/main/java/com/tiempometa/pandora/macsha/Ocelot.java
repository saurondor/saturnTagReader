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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.macsha.commands.MacshaCommand;
import com.tiempometa.pandora.macsha.commands.one4all.StartCommand;
import com.tiempometa.pandora.macsha.commands.one4all.StopCommand;
import com.tiempometa.pandora.tagreader.Context;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class Ocelot extends AbstractMacshaClient {

    private static final Logger logger = LogManager.getLogger(Ocelot.class);

    public static final String COMMAND_START = "Start";
    public static final String COMMAND_STOP = "Stop";
    public static final String COMMAND_CONNECTED = "CONECTADO";
    public static final String COMMAND_STARTED = "START-OK";
    public static final String COMMAND_OPERATION_STARTED = "OPERATION-MODE-STARTED";
    public static final String COMMAND_STOPPED = "STOP-OK";
    public static final String COMMAND_REMOTE_OFF = "MODO-REMOTE-OFF";

    private Integer keepAliveCounter = 0;
    private KeepAlive keepAlive = new KeepAlive();

    private class KeepAlive implements Runnable {
        private boolean runMe = true;

        public void start() {
            runMe = true;
        }

        public void stop() {
            logger.info("SIGNAL STOP KEEPALIVE");
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
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                synchronized (this) {
                    run = runMe;
                }
                if (run) {
                    synchronized (keepAlive) {
                        keepAliveCounter++;
                        if (keepAliveCounter >= 30) {
                            logger.debug("TIMEOUT!");
                            (new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyTimeOut();
                                }
                            })).start();
                        }
                        logger.debug("Increased keepalive counter to " + keepAliveCounter);
                    }
                }
            }
            logger.debug("ENDED KEEPALIVE!!");
            runMe = true;
        }
    }

    @Override
    protected void startKeepAlive() {
        Thread thread = new Thread(keepAlive);
        thread.start();
    }

    @Override
    protected void stopKeepAlive() {
        keepAlive.stop();
    }

    public void notifyTimeOut() {
        logger.error("TIMEOUT OCELOT " + (new Date()));
        commandResponseHandler.notifyTimeout();
    }

    @Override
    public void run() {
        do {
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
                        dataInStream = inputStream.available();
                        if (dataInStream > 0) {
                            byte[] b = new byte[dataInStream];
                            inputStream.read(b);
                            String dataString = new String(b);
                            if (logger.isDebugEnabled()) {
                                logger.debug("IN BUFFER>\n\t\t\t\t\t\t" + dataString + "\n\t\t\t\t\t\tLEN>"
                                        + dataString.length());
                            }
                            parsePayload(dataString);
                        } else {
                        }
                    } catch (IOException e1) {
                        logger.error("Fault getting available bytes " + e1.getMessage());
                        e1.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
            logger.debug("Waiting to connect");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        } while (true);
    }

    @Override
    protected void parsePayload(String dataString) {
        List<MacshaTagRead> readings = new ArrayList<MacshaTagRead>();
        // Ocelot uses pipe '|' as row delimiter
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

    private MacshaTagRead parseRow(String[] row) {
        if (row[0].startsWith(COMMAND_CONNECTED)) {
            synchronized (keepAlive) {
                keepAliveCounter = 0;
            }
            logger.debug("GOT Connected. Keepalive now :" + keepAliveCounter);
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
        } else if (row[0].startsWith(COMMAND_REMOTE_OFF)) {
            logger.debug("GOT Remote off");
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
}
