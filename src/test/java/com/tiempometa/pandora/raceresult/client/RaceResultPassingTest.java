package com.tiempometa.pandora.raceresult.client;

import com.tiempometa.webservice.model.RawChipRead;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.*;

public class RaceResultPassingTest {

    // Passive transponder: 9 fixed fields + 7 empty optional tokens + BoxName
    private static final String PASSIVE_LINE =
            "#P;1234;A0B1C2;2026-06-17;10:30:00.123;1;3;-75.5;0;0;;;;;;;;BOX1";

    // Active transponder: 9 fixed + 7 filled optional tokens + BoxName
    private static final String ACTIVE_LINE =
            "#P;5678;B2C3D4;2026-06-17;10:31:05.789;1;5;-65.0;1;1;2;1;3;90;22.5;INTERNAL;BOX2";

    // Push format without #P; prefix (pull response)
    private static final String BARE_LINE =
            "9999;CC1122;2026-06-17;11:00:00.000;2;1;-80.0;0;0;;;;;;;;BOXBARE";

    @Test
    public void parse_passiveLine_parsesAllFields() {
        RaceResultPassing p = RaceResultPassing.parse(PASSIVE_LINE);

        assertNotNull(p);
        assertEquals(1234L, p.getPassingNo());
        assertEquals("A0B1C2", p.getTranspCode());
        assertEquals("2026-06-17", p.getDate());
        assertEquals("10:30:00.123", p.getTime());
        assertEquals(1, p.getEventId());
        assertEquals(3, p.getHits());
        assertEquals(-75.5, p.getMaxRssi(), 0.001);
        assertFalse(p.isActive());
        assertEquals("BOX1", p.getBoxName());
    }

    @Test
    public void parse_passiveLine_parsesDateTime() {
        RaceResultPassing p = RaceResultPassing.parse(PASSIVE_LINE);

        assertNotNull(p);
        LocalDateTime dt = p.getDateTime();
        assertEquals(2026, dt.getYear());
        assertEquals(6, dt.getMonthValue());
        assertEquals(17, dt.getDayOfMonth());
        assertEquals(10, dt.getHour());
        assertEquals(30, dt.getMinute());
        assertEquals(0, dt.getSecond());
        assertEquals(123_000_000, dt.getNano());
    }

    @Test
    public void parse_passiveLine_timeMillisConsistentWithDateTime() {
        RaceResultPassing p = RaceResultPassing.parse(PASSIVE_LINE);

        assertNotNull(p);
        long expected = p.getDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        assertEquals(expected, p.getTimeMillis());
    }

    @Test
    public void parse_activeLine_parsesBoxNameAndIsActiveFlag() {
        RaceResultPassing p = RaceResultPassing.parse(ACTIVE_LINE);

        assertNotNull(p);
        assertEquals(5678L, p.getPassingNo());
        assertEquals("B2C3D4", p.getTranspCode());
        assertTrue(p.isActive());
        assertEquals("BOX2", p.getBoxName());
        assertEquals(5, p.getHits());
        assertEquals(-65.0, p.getMaxRssi(), 0.001);
    }

    @Test
    public void parse_bareLineWithoutPrefix_parsesCorrectly() {
        RaceResultPassing p = RaceResultPassing.parse(BARE_LINE);

        assertNotNull(p);
        assertEquals(9999L, p.getPassingNo());
        assertEquals("CC1122", p.getTranspCode());
        assertEquals("BOXBARE", p.getBoxName());
    }

    @Test
    public void parse_zeroDate_fallsBackToToday() {
        String line = "#P;1;CHIP1;0000-00-00;09:00:00.000;1;1;-70.0;0;0;;;;;;;;BOX";
        RaceResultPassing p = RaceResultPassing.parse(line);

        assertNotNull(p);
        assertEquals(LocalDate.now(), p.getDateTime().toLocalDate());
    }

    @Test
    public void parse_invalidDate_fallsBackToToday() {
        String line = "#P;1;CHIP1;not-a-date;09:00:00.000;1;1;-70.0;0;0;;;;;;;;BOX";
        RaceResultPassing p = RaceResultPassing.parse(line);

        assertNotNull(p);
        assertEquals(LocalDate.now(), p.getDateTime().toLocalDate());
    }

    @Test
    public void parse_nullLine_returnsNull() {
        assertNull(RaceResultPassing.parse(null));
    }

    @Test
    public void parse_emptyLine_returnsNull() {
        assertNull(RaceResultPassing.parse(""));
    }

    @Test
    public void parse_tooFewFields_returnsNull() {
        assertNull(RaceResultPassing.parse("#P;1;CHIP1;2026-06-17;10:00:00.000;1;1"));
    }

    @Test
    public void parse_exactlyNineFields_parsesWithoutBoxName() {
        // 9 fields — valid minimum, box name absent
        String line = "#P;42;RFID99;2026-06-17;08:00:00.000;1;2;-55.0;0;0";
        RaceResultPassing p = RaceResultPassing.parse(line);

        assertNotNull(p);
        assertEquals(42L, p.getPassingNo());
        assertEquals("RFID99", p.getTranspCode());
        assertEquals("", p.getBoxName());
    }

    @Test
    public void toRawChipRead_mapsFieldsCorrectly() {
        RaceResultPassing p = RaceResultPassing.parse(PASSIVE_LINE);
        assertNotNull(p);
        p.setCheckPoint("META");

        RawChipRead read = p.toRawChipRead();

        assertEquals("A0B1C2", read.getRfidString());
        assertEquals("META", read.getCheckPoint());
        assertEquals("BOX1", read.getLoadName());
        assertEquals(p.getDateTime(), read.getTime());
        assertEquals(p.getTimeMillis(), (long) read.getTimeMillis());
    }

    @Test
    public void toRawChipRead_withNullCheckpoint_setsNullCheckpoint() {
        RaceResultPassing p = RaceResultPassing.parse(PASSIVE_LINE);
        assertNotNull(p);
        // checkPoint is null by default (never set)

        RawChipRead read = p.toRawChipRead();

        assertNull(read.getCheckPoint());
    }
}
