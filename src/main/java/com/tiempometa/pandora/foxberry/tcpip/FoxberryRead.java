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

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.Utils;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * TCP wire-format read DTO for Foxberry readers.
 * Fully independent of the ipicoreader package; kept separate so the
 * Foxberry protocol can evolve (e.g. toward Octane/R700) without touching
 * IPICO code.
 *
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class FoxberryRead {

    private static final Logger logger = LogManager.getLogger(FoxberryRead.class);

    public static final int FRAME_PAYLOAD_START = 0;
    public static final int FRAME_PAYLOAD_END = 36;
    public static final int FRAME_HEADER_START = 0;
    public static final int FRAME_HEADER_END = 2;
    public static final int FRAME_READER_START = 2;
    public static final int FRAME_READER_END = 4;
    public static final int FRAME_TAG_START = 4;
    public static final int FRAME_TAG_END = 16;
    public static final int FRAME_I_CHANNEL_START = 16;
    public static final int FRAME_I_CHANNEL_END = 18;
    public static final int FRAME_Q_CHANNEL_START = 18;
    public static final int FRAME_Q_CHANNEL_END = 20;
    public static final int FRAME_DATE_START = 20;
    public static final int FRAME_DATE_END = 26;
    public static final int FRAME_TIME_START = 26;
    public static final int FRAME_TIME_END = 32;
    public static final int FRAME_MILLIS_START = 32;
    public static final int FRAME_MILLIS_END = 34;
    public static final int FRAME_LRC_START = 34;
    public static final int FRAME_LRC_END = 36;
    public static final int FRAME_SEEN_START = 36;
    public static final int FRAME_SEEN_END = 38;
    public static final String DATA_LINE_HEADER = "aa";
    public static final String FIRST_SEEN_TAIL = "FS";
    public static final String LAST_SEEN_TAIL = "LS";
    public static final int FIRST_SEEN = 1;
    public static final int LAST_SEEN = -1;

    private String rfid;
    private String reader;
    private String antenna;
    private Date clockTime;
    private Long runTime;
    private Integer seenStatus;
    private String crc;
    private String checkPoint;
    private String terminal;

    public static String crc(String dataString) {
        int checkSum = 0;
        for (int i = 2; i < dataString.length() - 2; i++) {
            checkSum = (checkSum + (dataString.getBytes()[i]));
        }
        String crc = Integer.toHexString(checkSum);
        crc = crc.substring(crc.length() - 2);
        return crc.toLowerCase();
    }

    public static FoxberryRead parse(String line) {
        if (line == null) {
            logger.error("Parse line is NULL");
            return null;
        }
        FoxberryRead reading = new FoxberryRead();
        if (line.length() >= FRAME_LRC_END) {
            if (line.substring(0, 2).equals(DATA_LINE_HEADER)) {
                String crc = crc(line.substring(FRAME_PAYLOAD_START, FRAME_PAYLOAD_END));
                String checkSum = line.substring(FRAME_LRC_START, FRAME_LRC_END);
                if (!crc.equals(checkSum)) {
                    logger.error("LRC is invalid");
                    return null;
                }
                reading.setReader(line.substring(FRAME_READER_START, FRAME_READER_END));
                reading.setRfid(line.substring(FRAME_TAG_START, FRAME_TAG_END));
                int year = Integer.valueOf(line.substring(FRAME_DATE_START, FRAME_DATE_START + 2));
                int month = Integer.valueOf(line.substring(FRAME_DATE_START + 2, FRAME_DATE_START + 4));
                int day = Integer.valueOf(line.substring(FRAME_DATE_START + 4, FRAME_DATE_START + 6));
                int hour = Integer.valueOf(line.substring(FRAME_TIME_START, FRAME_TIME_START + 2));
                int minute = Integer.valueOf(line.substring(FRAME_TIME_START + 2, FRAME_TIME_START + 4));
                int second = Integer.valueOf(line.substring(FRAME_TIME_START + 4, FRAME_TIME_START + 6));
                int millis = Integer.parseInt(line.substring(FRAME_MILLIS_START, FRAME_MILLIS_END), 16) * 10;
                Date chipDate = new Date(100 + year, month - 1, day, hour, minute, second);
                chipDate.setTime(chipDate.getTime() + millis);
                if (line.length() >= FRAME_SEEN_END) {
                    if (line.substring(36, 38).equals(FIRST_SEEN_TAIL)) {
                        reading.setSeenStatus(FIRST_SEEN);
                    }
                    if (line.substring(36, 38).equals(LAST_SEEN_TAIL)) {
                        reading.setSeenStatus(LAST_SEEN);
                    }
                }
                reading.setClockTime(chipDate);
                reading.setRunTime(chipDate.getTime());
                reading.setAntenna(line.substring(FRAME_I_CHANNEL_START, FRAME_Q_CHANNEL_END));
                reading.setCrc(crc);
            } else {
                logger.error("Invalid length or start frame " + line.length() + " " + line.substring(0, 2));
                reading = null;
            }
        } else {
            logger.error("Invalid length " + line.length());
            reading = null;
        }
        return reading;
    }

    public RawChipRead toRawChipRead() {
        RawChipRead read = new RawChipRead();
        read.setRfidString(rfid);
        read.setTime(Utils.dateToLocalDateTime(clockTime));
        read.setTimeMillis(runTime);
        read.setCheckPoint(checkPoint);
        read.setLoadName(terminal);
        return read;
    }

    public String getRfid() { return rfid; }
    public void setRfid(String rfid) { this.rfid = rfid; }
    public String getReader() { return reader; }
    public void setReader(String reader) { this.reader = reader; }
    public String getAntenna() { return antenna; }
    public void setAntenna(String antenna) { this.antenna = antenna; }
    public Date getClockTime() { return clockTime; }
    public void setClockTime(Date clockTime) { this.clockTime = clockTime; }
    public Long getRunTime() { return runTime; }
    public void setRunTime(Long runTime) { this.runTime = runTime; }
    public Integer getSeenStatus() { return seenStatus; }
    public void setSeenStatus(Integer seenStatus) { this.seenStatus = seenStatus; }
    public String getCrc() { return crc; }
    public void setCrc(String crc) { this.crc = crc; }
    public String getCheckPoint() { return checkPoint; }
    public void setCheckPoint(String checkPoint) { this.checkPoint = checkPoint; }
    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }

    @Override
    public String toString() {
        return "FoxberryRead [rfid=" + rfid + ", checkpoint=" + checkPoint
                + ", reader=" + reader + ", antenna=" + antenna
                + ", clockTime=" + clockTime + ", runTime=" + runTime
                + ", seenStatus=" + seenStatus + "]";
    }
}
