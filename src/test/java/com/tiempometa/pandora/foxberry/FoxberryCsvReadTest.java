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

import static org.junit.Assert.*;

import org.junit.Test;

import com.tiempometa.pandora.foxberry.tcpip.FoxberryCsvRead;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * Unit tests for FoxberryCsvRead.parse().
 * Uses lines taken directly from the arranque_5_10.txt backup so the
 * expected values are ground-truth from real hardware captures.
 */
public class FoxberryCsvReadTest {

    // Line 1 from arranque_5_10.txt
    private static final String VALID_LINE =
            "reader_2,4,BF65451ABE0F622D4A180ED3,1467464368979179,-63,,";

    @Test
    public void parsesReaderAndAntenna() {
        FoxberryCsvRead read = FoxberryCsvRead.parse(VALID_LINE);
        assertNotNull(read);
        assertEquals("reader_2", read.getReader());
        assertEquals("4", read.getAntenna());
    }

    @Test
    public void parsesEpc() {
        FoxberryCsvRead read = FoxberryCsvRead.parse(VALID_LINE);
        assertNotNull(read);
        assertEquals("BF65451ABE0F622D4A180ED3", read.getEpc());
    }

    @Test
    public void convertsMicrosecondsToMillis() {
        FoxberryCsvRead read = FoxberryCsvRead.parse(VALID_LINE);
        assertNotNull(read);
        // 1467464368979179 µs / 1000 = 1467464368979 ms
        assertEquals(1467464368979L, read.getTimeMillis());
    }

    @Test
    public void parsesNegativeRssi() {
        FoxberryCsvRead read = FoxberryCsvRead.parse(VALID_LINE);
        assertNotNull(read);
        assertEquals(-63, read.getRssi());
    }

    @Test
    public void emptyTidAndUserDataBecomeNull() {
        FoxberryCsvRead read = FoxberryCsvRead.parse(VALID_LINE);
        assertNotNull(read);
        assertNull(read.getTid());
        assertNull(read.getUserData());
    }

    @Test
    public void parsesPopulatedOptionalFields() {
        String line = "reader_1,2,AABBCCDDEEFF,1467464368979179,-50,TID123,mydata";
        FoxberryCsvRead read = FoxberryCsvRead.parse(line);
        assertNotNull(read);
        assertEquals("TID123", read.getTid());
        assertEquals("mydata", read.getUserData());
    }

    @Test
    public void treatsNoneLiteralAsNull() {
        String line = "reader_1,2,AABBCCDDEEFF,1467464368979179,-50,None,None";
        FoxberryCsvRead read = FoxberryCsvRead.parse(line);
        assertNotNull(read);
        assertNull(read.getTid());
        assertNull(read.getUserData());
    }

    @Test
    public void returnsNullForNullInput() {
        assertNull(FoxberryCsvRead.parse(null));
    }

    @Test
    public void returnsNullForEmptyLine() {
        assertNull(FoxberryCsvRead.parse(""));
        assertNull(FoxberryCsvRead.parse("   "));
    }

    @Test
    public void returnsNullForTooFewFields() {
        assertNull(FoxberryCsvRead.parse("reader_1,2,AABBCC"));
    }

    @Test
    public void returnsNullForUnparseableTimestamp() {
        assertNull(FoxberryCsvRead.parse("reader_1,2,AABBCC,not_a_number,-50,,"));
    }

    @Test
    public void returnsNullForUnparseableRssi() {
        assertNull(FoxberryCsvRead.parse("reader_1,2,AABBCC,1467464368979179,bad,,"));
    }

    @Test
    public void detectsKeepalive() {
        assertTrue(FoxberryCsvRead.isKeepalive("reader_1,*,,"));
        assertTrue(FoxberryCsvRead.isKeepalive("reader_2,*,AABBCC,"));
    }

    @Test
    public void normalReadIsNotKeepalive() {
        assertFalse(FoxberryCsvRead.isKeepalive(VALID_LINE));
    }

    @Test
    public void nullAndEmptyAreNotKeepalive() {
        assertFalse(FoxberryCsvRead.isKeepalive(null));
        assertFalse(FoxberryCsvRead.isKeepalive(""));
    }

    @Test
    public void keepaliveLineIsNotParsedAsRead() {
        assertNull(FoxberryCsvRead.parse("reader_1,*,,"));
    }

    @Test
    public void toRawChipReadMapsEpcAndTimeMillis() {
        FoxberryCsvRead read = FoxberryCsvRead.parse(VALID_LINE);
        assertNotNull(read);
        RawChipRead raw = read.toRawChipRead();
        assertEquals("BF65451ABE0F622D4A180ED3", raw.getRfidString());
        assertEquals(Long.valueOf(1467464368979L), raw.getTimeMillis());
        assertNotNull(raw.getTime());
    }

    @Test
    public void toRawChipReadAppliesCheckpointAndTerminal() {
        FoxberryCsvRead read = FoxberryCsvRead.parse(VALID_LINE);
        assertNotNull(read);
        read.setCheckPoint("meta-21k");
        read.setTerminal("1");
        RawChipRead raw = read.toRawChipRead();
        assertEquals("meta-21k", raw.getCheckPoint());
        assertEquals("1", raw.getLoadName());
    }

    @Test
    public void parsesAllLinesFromBackupSample() {
        String[] backupLines = {
            "reader_2,4,BF65451ABE0F622D4A180ED3,1467464368979179,-63,,",
            "reader_2,4,F9E34C658069C7B6F5920399,1467464369973268,-62,,",
            "reader_2,4,697B4E7A9393B01AFBC436A4,1467464372595005,-58,,",
            "reader_1,2,52F1428EB40F10B5C872DEAE,1467464383655857,-65,,"
        };
        for (String line : backupLines) {
            FoxberryCsvRead read = FoxberryCsvRead.parse(line);
            assertNotNull("Failed to parse: " + line, read);
            assertNotNull(read.getEpc());
            assertTrue(read.getTimeMillis() > 0);
        }
    }
}
