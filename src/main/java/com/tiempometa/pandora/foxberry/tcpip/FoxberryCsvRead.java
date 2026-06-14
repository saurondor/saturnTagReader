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

import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * CSV wire-format read DTO for Foxberry/R700 readers.
 *
 * Wire format (one line per read, newline-terminated):
 *   reader,antenna,epc,time_microseconds,rssi,tid,user_data\n\r
 *
 * Keepalive sentinel: second field is "*"
 *   reader,*,epc_or_empty,user_data_or_empty\n\r
 *
 * Timestamps are epoch microseconds from the Impinj Octane SDK;
 * toRawChipRead() converts to milliseconds for the rest of the pipeline.
 *
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class FoxberryCsvRead {

    private static final Logger logger = LogManager.getLogger(FoxberryCsvRead.class);

    public static final String KEEPALIVE_SENTINEL = "*";

    private String reader;
    private String antenna;
    private String epc;
    private long timeMillis;
    private int rssi;
    private String tid;
    private String userData;
    private String checkPoint;
    private String terminal;

    public static boolean isKeepalive(String line) {
        if (line == null || line.trim().isEmpty()) return false;
        String[] fields = line.split(",", -1);
        return fields.length >= 2 && KEEPALIVE_SENTINEL.equals(fields[1].trim());
    }

    public static FoxberryCsvRead parse(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String trimmed = line.trim();
        String[] fields = trimmed.split(",", -1);
        if (fields.length < 5) {
            logger.warn("CSV line too short (" + fields.length + " fields): " + trimmed);
            return null;
        }
        if (KEEPALIVE_SENTINEL.equals(fields[1].trim())) {
            return null;
        }
        FoxberryCsvRead read = new FoxberryCsvRead();
        try {
            read.reader = fields[0].trim();
            read.antenna = fields[1].trim();
            read.epc = fields[2].trim();
            read.timeMillis = Long.parseLong(fields[3].trim()) / 1000;
            read.rssi = Integer.parseInt(fields[4].trim());
            read.tid = toNullIfEmpty(fields.length > 5 ? fields[5].trim() : null);
            read.userData = toNullIfEmpty(fields.length > 6 ? fields[6].trim() : null);
        } catch (NumberFormatException e) {
            logger.error("Unparseable CSV line: " + trimmed, e);
            return null;
        }
        return read;
    }

    private static String toNullIfEmpty(String s) {
        if (s == null || s.isEmpty() || "None".equals(s)) return null;
        return s;
    }

    public RawChipRead toRawChipRead() {
        RawChipRead raw = new RawChipRead();
        raw.setRfidString(epc);
        raw.setTimeMillis(timeMillis);
        raw.setTime(Instant.ofEpochMilli(timeMillis).atZone(Context.getZoneId()).toLocalDateTime());
        raw.setCheckPoint(checkPoint);
        raw.setLoadName(terminal);
        return raw;
    }

    public String getReader() { return reader; }
    public void setReader(String reader) { this.reader = reader; }
    public String getAntenna() { return antenna; }
    public void setAntenna(String antenna) { this.antenna = antenna; }
    public String getEpc() { return epc; }
    public void setEpc(String epc) { this.epc = epc; }
    public long getTimeMillis() { return timeMillis; }
    public void setTimeMillis(long timeMillis) { this.timeMillis = timeMillis; }
    public int getRssi() { return rssi; }
    public void setRssi(int rssi) { this.rssi = rssi; }
    public String getTid() { return tid; }
    public void setTid(String tid) { this.tid = tid; }
    public String getUserData() { return userData; }
    public void setUserData(String userData) { this.userData = userData; }
    public String getCheckPoint() { return checkPoint; }
    public void setCheckPoint(String checkPoint) { this.checkPoint = checkPoint; }
    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }

    @Override
    public String toString() {
        return "FoxberryCsvRead [reader=" + reader + ", antenna=" + antenna
                + ", epc=" + epc + ", timeMillis=" + timeMillis
                + ", rssi=" + rssi + ", tid=" + tid + ", userData=" + userData + "]";
    }
}
