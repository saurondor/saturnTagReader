package com.tiempometa.pandora.raceresult.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.webservice.model.RawChipRead;

/**
 * Parsed representation of a single Race Result 4000/5000 passing line.
 *
 * Wire format (push):
 *   #P;<PassingNo>;<Bib/TranspCode>;<Date>;<Time>;<EventID>;<Hits>;<MaxRSSI>;
 *      <InternalData>;<IsActive>[;<Channel>;<LoopID>;<LoopOnly>;<WakeupCounter>;
 *      <Battery>;<Temperature>;<InternalActiveData>];<BoxName>;...
 *
 * The 7 optional active-transponder fields are always present as empty tokens
 * for passive reads, so BoxName is always at index 16 (after stripping the #P; prefix).
 */
public class RaceResultPassing {

    private static final Logger logger = LogManager.getLogger(RaceResultPassing.class);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final String ZERO_DATE = "0000-00-00";

    private long passingNo;
    private String transpCode;
    private String date;
    private String time;
    private int eventId;
    private int hits;
    private double maxRssi;
    private boolean isActive;
    private String boxName;
    private String checkPoint;
    private LocalDateTime dateTime;
    private long timeMillis;

    private RaceResultPassing() {}

    /**
     * Parses a push passing line ({@code #P;...}) or a bare pull passing line.
     * Returns {@code null} if the line cannot be parsed.
     */
    public static RaceResultPassing parse(String line) {
        if (line == null || line.isEmpty()) return null;
        String data = line.startsWith("#P;") ? line.substring(3) : line;
        String[] parts = data.split(";", -1);
        if (parts.length < 9) {
            logger.warn("Too few fields in passing line: " + line);
            return null;
        }
        try {
            RaceResultPassing p = new RaceResultPassing();
            p.passingNo  = Long.parseLong(parts[0].trim());
            p.transpCode = parts[1].trim();
            p.date       = parts[2].trim();
            p.time       = parts[3].trim();
            p.eventId    = parseIntSafe(parts[4]);
            p.hits       = parseIntSafe(parts[5]);
            p.maxRssi    = parseDoubleSafe(parts[6]);
            p.isActive   = "1".equals(parts[8].trim());
            // BoxName: index 16 when passive (7 empty optional tokens at [9]-[15]);
            // index 15 when active fields are omitted. Take whichever is last non-empty
            // before file/box-id tail fields, falling back gracefully.
            p.boxName = extractBoxName(parts);
            p.dateTime   = parseDateTime(p.date, p.time);
            p.timeMillis = p.dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return p;
        } catch (Exception e) {
            logger.error("Failed to parse passing line: " + line + " — " + e.getMessage());
            return null;
        }
    }

    private static String extractBoxName(String[] parts) {
        // Standard passive layout: 9 fixed + 7 empty optional = boxName at [16]
        if (parts.length > 16 && !parts[16].isEmpty()) return parts[16].trim();
        // Active layout with fewer trailing tokens
        if (parts.length > 15 && !parts[15].isEmpty()) return parts[15].trim();
        return "";
    }

    private static LocalDateTime parseDateTime(String date, String time) {
        LocalDate d;
        if (ZERO_DATE.equals(date)) {
            d = LocalDate.now();
        } else {
            try {
                d = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                d = LocalDate.now();
            }
        }
        LocalTime t = LocalTime.parse(time, TIME_FMT);
        return LocalDateTime.of(d, t);
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }

    public RawChipRead toRawChipRead() {
        RawChipRead read = new RawChipRead();
        read.setRfidString(transpCode);
        read.setTime(dateTime);
        read.setTimeMillis(timeMillis);
        read.setCheckPoint(checkPoint);
        read.setLoadName(boxName);
        return read;
    }

    public long getPassingNo()     { return passingNo; }
    public String getTranspCode()  { return transpCode; }
    public String getDate()        { return date; }
    public String getTime()        { return time; }
    public int getEventId()        { return eventId; }
    public int getHits()           { return hits; }
    public double getMaxRssi()     { return maxRssi; }
    public boolean isActive()      { return isActive; }
    public String getBoxName()     { return boxName; }
    public LocalDateTime getDateTime() { return dateTime; }
    public long getTimeMillis()    { return timeMillis; }

    public String getCheckPoint()  { return checkPoint; }
    public void setCheckPoint(String checkPoint) { this.checkPoint = checkPoint; }

    @Override
    public String toString() {
        return "RaceResultPassing[no=" + passingNo + ", chip=" + transpCode
                + ", time=" + time + ", box=" + boxName + ", checkpoint=" + checkPoint + "]";
    }
}
