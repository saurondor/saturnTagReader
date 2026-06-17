package com.tiempometa.pandora.raceresult.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.tagreader.AbstractTcpReaderClient;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * TCP client for Race Result 4000/5000 decoders.
 *
 * Protocol summary (port 3601, CRLF-terminated text):
 *   1. Connect → send SETPROTOCOL;2.0 → echo back
 *   2. STARTOPERATION → "OPERATIONSTARTED" or similar
 *   3. SETPUSHPASSINGS;1;0 → decoder pushes "#P;..." lines as they arrive
 *   4. STOPOPERATION before disconnect
 *
 * All reads delivered to the registered {@link com.tiempometa.pandora.tagreader.TagReadListener}.
 */
public class RaceResultClient extends AbstractTcpReaderClient {

    private static final Logger logger = LogManager.getLogger(RaceResultClient.class);

    public static final int DEFAULT_PORT = 3601;
    private static final int HANDSHAKE_TIMEOUT_MS = 5000;

    private BufferedReader reader;
    private PrintWriter writer;
    private String checkPoint = "";

    public RaceResultClient() {
        setPort(DEFAULT_PORT);
    }

    public String getCheckPoint() {
        return checkPoint;
    }

    public void setCheckPoint(String checkPoint) {
        this.checkPoint = checkPoint != null ? checkPoint : "";
    }

    @Override
    public void connect() throws UnknownHostException, IOException {
        super.connect();
        try {
            readerSocket.setSoTimeout(HANDSHAKE_TIMEOUT_MS);
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));
            writer = new PrintWriter(readerSocket.getOutputStream(), false);

            sendCommand("SETPROTOCOL;2.0");
            String protocolAck = reader.readLine();
            logger.info("SETPROTOCOL ack: " + protocolAck);

            sendCommand("STARTOPERATION");
            String startAck = reader.readLine();
            logger.info("STARTOPERATION ack: " + startAck);

            sendCommand("SETPUSHPASSINGS;1;0");
            String pushAck = reader.readLine();
            logger.info("SETPUSHPASSINGS ack: " + pushAck);

            readerSocket.setSoTimeout(0);
        } catch (IOException e) {
            // Clean up streams if handshake fails so caller can retry safely
            super.disconnect();
            throw e;
        }
    }

    @Override
    public void run() {
        logger.info("RaceResultClient read loop started, checkpoint=" + checkPoint);
        while (isConnected()) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    logger.info("RaceResultClient: connection closed by decoder");
                    break;
                }
                logger.debug("RR< " + line);
                if (line.startsWith("#P;")) {
                    RaceResultPassing passing = RaceResultPassing.parse(line);
                    if (passing != null && tagReadListener != null) {
                        passing.setCheckPoint(checkPoint);
                        RawChipRead read = passing.toRawChipRead();
                        tagReadListener.notifyTagReads(Collections.singletonList(read));
                    }
                }
            } catch (IOException e) {
                if (isConnected()) {
                    logger.error("RaceResultClient read error: " + e.getMessage());
                }
                break;
            }
        }
        logger.info("RaceResultClient read loop ended");
    }

    @Override
    public void disconnect() {
        if (isConnected() && writer != null) {
            try {
                sendCommand("STOPOPERATION");
                String stopAck = reader.readLine();
                logger.info("STOPOPERATION ack: " + stopAck);
            } catch (Exception e) {
                logger.warn("Could not send STOPOPERATION: " + e.getMessage());
            }
        }
        super.disconnect();
    }

    private void sendCommand(String cmd) {
        writer.print(cmd + "\r\n");
        writer.flush();
        logger.debug("RR> " + cmd);
    }
}
