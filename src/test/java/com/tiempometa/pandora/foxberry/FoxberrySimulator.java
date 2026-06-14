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
package com.tiempometa.pandora.foxberry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simulates the Foxberry reader data port by replaying a CSV backup file
 * over a ServerSocket.
 *
 * Each line is sent in two fragments with a short delay between them to
 * exercise the FoxberryCsvClient's stream-buffer reassembly logic — the
 * same technique used by EliteSimulator and TSCollectorSimulator.
 *
 * Can be run standalone via main() for manual testing, or started
 * programmatically in integration tests via start()/stop().
 *
 * Default backup: backups/foxberry/arranque_5_10.txt
 * Default port  : 10201
 */
public class FoxberrySimulator implements Runnable {

    private static final Logger logger = LogManager.getLogger(FoxberrySimulator.class);

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private OutputStream outputStream;
    private List<String> historyLog;

    private int port = 10201;
    private int lineDelayMillis = 200;
    private int fragmentDelayMillis = 20;

    public static void main(String[] args) throws IOException {
        FoxberrySimulator simulator = new FoxberrySimulator();
        simulator.loadLogFile(simulator.getClass().getResourceAsStream(
                "/backups/foxberry/arranque_5_10.txt"));
        simulator.start();
        Thread thread = new Thread(simulator);
        thread.start();
        logger.info("Foxberry simulator running. Press Ctrl+C to stop.");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void loadLogFile(InputStream inputStream) throws IOException {
        historyLog = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                historyLog.add(line);
            }
        }
        logger.info("Loaded " + historyLog.size() + " lines");
    }

    public void start() throws IOException {
        logger.info("Starting Foxberry simulator on port " + port);
        serverSocket = new ServerSocket(port);
        logger.info("Listening on port " + port);
    }

    public void stop() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            logger.warn("Error stopping simulator: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            clientSocket = serverSocket.accept();
            logger.info("Client connected from " + clientSocket.getRemoteSocketAddress());
            outputStream = clientSocket.getOutputStream();

            for (String line : historyLog) {
                logger.debug("Sending: " + line);
                // Split mid-line to exercise the client's stream-buffer reassembly
                int split = Math.min(20, line.length());
                outputStream.write(line.substring(0, split).getBytes());
                outputStream.flush();
                Thread.sleep(fragmentDelayMillis);
                outputStream.write(line.substring(split).getBytes());
                outputStream.write("\n\r".getBytes());
                outputStream.flush();
                Thread.sleep(lineDelayMillis);
            }
            logger.info("Replay complete (" + historyLog.size() + " lines sent)");
            clientSocket.close();
        } catch (IOException e) {
            logger.warn("Simulator connection closed: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public int getLineDelayMillis() { return lineDelayMillis; }
    public void setLineDelayMillis(int lineDelayMillis) { this.lineDelayMillis = lineDelayMillis; }
    public int getFragmentDelayMillis() { return fragmentDelayMillis; }
    public void setFragmentDelayMillis(int fragmentDelayMillis) { this.fragmentDelayMillis = fragmentDelayMillis; }
}
