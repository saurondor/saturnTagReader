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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class One4All extends AbstractMacshaClient {

    public static final String COMMAND_GETPROTOCOL = "GETPROTOCOL";
    public static final String COMMAND_SETPROTOCOL = "SETPROTOCOL";
    public static final String COMMAND_START = "START";
    public static final String COMMAND_STOP = "STOP";
    public static final String COMMAND_PUSHTAGS = "PUSHTAGS";
    public static final String COMMAND_SETBOUNCE = "SETBOUNCE";
    public static final String COMMAND_SETTIME = "SETTIME";
    public static final String COMMAND_GETTIME = "GETTIME";
    public static final String COMMAND_SETBUZZER = "SETBUZZER";
    public static final String COMMAND_NEWFILE = "NEWFILE";
    public static final String COMMAND_PASSINGS = "PASSINGS";
    public static final String COMMAND_GETPASSINGS = "GETPASSINGS";
    public static final String COMMAND_READBATTERY = "READBATTERY";
    public static final String COMMAND_PING = "PING";
    public static final String COMMAND_LISTFILES = "LISTFILES";
    public static final String COMMAND_GETFILEINFO = "GETFILEINFO";
    public static final String COMMAND_GETFILE = "GETFILE";
    public static final String COMMAND_CLEARFILES = "CLEARFILES";
    private static final String COMMAND_HELLO = "HELLO";

    private static final Logger logger = LogManager.getLogger(One4All.class);

    private KeepAlive keepAlive;

    private class KeepAlive implements Runnable {
        private boolean runMe = true;

        public void stop() {
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
                    ReadBatteryCommand command = new ReadBatteryCommand();
                    command.sendCommand(outputStream);
                    Thread.sleep(10000);
                    logger.debug("Send battery keepalive");
                    synchronized (this) {
                        run = runMe;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    commandResponseHandler.notifyCommException(e);
                    e.printStackTrace();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void startKeepAlive() {
        keepAlive = new KeepAlive();
        Thread thread = new Thread(keepAlive);
        thread.start();
    }

    @Override
    protected void stopKeepAlive() {
        if (keepAlive != null) {
            keepAlive.stop();
        }
    }

    @Override
    public void run() {
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
                            logger.debug("PARSE>\n" + dataString + "\nLEN:" + dataString.length());
                        }
                        parsePayload(dataString);
                        // filter out CONECTADO_4 that sometimes appears prior to valid payload
                        dataString = dataString.replace("CONECTADO_4", "");
                        String[] dataRows = dataString.split("\\|");
                    } else {
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                    try {
                        disconnect();
                        connect(getHostname());
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.warn("STOPPING AND DISCONNECT!");
        disconnect();
    }

    @Override
    protected void parsePayload(String dataString) {
        List<MacshaTagRead> readings = new ArrayList<MacshaTagRead>();
        if (logger.isDebugEnabled()) {
            logger.debug("PARSE>\n" + dataString + "\nLEN:" + dataString.length());
        }
        // HW specific split rows: \r\n for generation 5, pipe for version 4 and prior
        String[] dataRows = dataString.split("\\r\\n");
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
        if (row[0].startsWith(COMMAND_HELLO)) {
            logger.debug("GOT HELLO");
            return null;
        } else {
            try {
                Integer readId = Integer.valueOf(row[0]);
                logger.debug("GOT TAG READ ROW");
                MacshaTagRead tagRead = MacshaTagRead.row(row, Context.getZoneId());
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
        case COMMAND_CLEARFILES:
            logger.debug("GOT CLEARFILES COMMAND");
            command = new ClearFilesCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_GETFILE:
            logger.debug("GOT GETFILE COMMAND");
            command = new GetFileCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_GETFILEINFO:
            logger.debug("GOT GETFILEINFO COMMAND");
            command = new GetFileInfoCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_GETPASSINGS:
            logger.debug("GOT GETPASSINGS COMMAND");
            command = new GetPassingsCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_GETPROTOCOL:
            logger.debug("GOT GETPROTOCOL COMMAND");
            command = new GetProtocolCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_GETTIME:
            logger.debug("GOT GETTIME COMMAND");
            command = new GetTimeCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_HELLO:
            logger.debug("GOT HELLO COMMAND");
            break;
        case COMMAND_LISTFILES:
            logger.debug("GOT LISTFILES COMMAND");
            command = new ListFilesCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_NEWFILE:
            logger.debug("GOT NEWFILE COMMAND");
            command = new NewFileCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_PASSINGS:
            logger.debug("GOT PASSINGS COMMAND");
            command = new StartCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_PING:
            logger.debug("GOT PING COMMAND");
            command = new PingCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_PUSHTAGS:
            logger.debug("GOT PUSHTAGS COMMAND");
            command = new PushTagsCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_READBATTERY:
            logger.debug("GOT READBATTERY COMMAND");
            command = new ReadBatteryCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_SETBOUNCE:
            logger.debug("GOT SETBOUNCE COMMAND");
            command = new SetBounceCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_SETBUZZER:
            logger.debug("GOT SETBUZZER COMMAND");
            command = new SetBuzzerCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_SETPROTOCOL:
            logger.debug("GOT SETPROTOCOL COMMAND");
            command = new SetProtocolCommand();
            command.parseCommandRow(row);
            break;
        case COMMAND_SETTIME:
            logger.debug("GOT SETTIME COMMAND");
            command = new SetTimeCommand();
            command.parseCommandRow(row);
            break;
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
