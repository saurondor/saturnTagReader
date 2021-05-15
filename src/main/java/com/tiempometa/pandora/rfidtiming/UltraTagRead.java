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
 * 
 */
package com.tiempometa.pandora.rfidtiming;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.csv.CSVRecord;

import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class UltraTagRead {

	// 0,ChipCode,Seconds,Milliseconds,AntennaNo,RSSI,IsRewind,ReaderNo,UltraID,
	public static final int CHIP_CODE_COLUMN = 1;
	public static final int SECONDS_COLUMN = 2;
	public static final int MILLISECONDS_COLUMN = 3;
	public static final int ANTENNA_NO_COLUMN = 4;
	public static final int RSSI_COLUMN = 5;
	public static final int READER_NO_COLUMN = 6;
	public static final int ULTRA_ID_COLUMN = 7;
	public static final long DATE_FROM_1980 = 315532800000l;

	// These will be in the following format:
	//
	// 0,ChipCode,Seconds,Milliseconds,AntennaNo,RSSI,IsRewind,ReaderNo,UltraID,
	// ReaderTime,StartTime,LogID
	// It is a comma-delimited string, terminated with a line feed character (ASCII
	// 10, 0x0A).
	// Description of fields, in the above order:
	// 0 Zero (unused at present)
	// ChipCode Could be the chip code decimal or hexadecimal value, depending on
	// current setting in Ultra (see section 3.8)
	// Seconds Integer value representing the number of seconds after 01/01/1980
	// Milliseconds Integer value representing the millisecond portion of the time.
	// AntennaNo Integer value from 1 to 4 representing the antenna number the chip
	// was detected on.
	// RSSI Negative integer value. This is the signal strength for the chip read.
	// IsRewind 0 or 1. A value of 1 means the data is being transmitted from a
	// rewind command, in other words it is not a ‘live’ read. Live and rewound data
	// will be mixed up in between each other if you do a ‘rewind while reading’.
	// ReaderNo Integer value of from 1 to 3 representing the reader number. There
	// are 2 readers in an Ultra. A reader number of 3 is used for MTB downhill
	// start times.
	// UltraID Integer value. See section 3.1 ReaderTime 8 characters representing
	// the 64-bit time recorded by the UHF readers. Not available for some Ultra
	// models – please speak to your supplier for more information.
	// StartTime For MTB downhill racing. Integer value representing the number of
	// seconds after 01/01/1980
	// LogID Integer value representing the record’s position in the log (starting
	// at one)

	private String chipCode;
	private Long seconds;
	private Long milliseconds;
	private Integer antennaNo;
	private Integer rssi;
	private Integer isRewind;
	private Integer readerNo;
	private Integer ultraID;
	private LocalDateTime time;
	private Long timeMillis;

	public String getChipCode() {
		return chipCode;
	}

	public Long getSeconds() {
		return seconds;
	}

	public Long getMilliseconds() {
		return milliseconds;
	}

	public Integer getAntennaNo() {
		return antennaNo;
	}

	public Integer getRssi() {
		return rssi;
	}

	public Integer getIsRewind() {
		return isRewind;
	}

	public Integer getReaderNo() {
		return readerNo;
	}

	public Integer getUltraID() {
		return ultraID;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public Long getTimeMillis() {
		return timeMillis;
	}

	public void setChipCode(String chipCode) {
		this.chipCode = chipCode;
	}

	public void setSeconds(Long seconds) {
		this.seconds = seconds;
	}

	public void setMilliseconds(Long milliseconds) {
		this.milliseconds = milliseconds;
	}

	public void setAntennaNo(Integer antennaNo) {
		this.antennaNo = antennaNo;
	}

	public void setRssi(Integer rssi) {
		this.rssi = rssi;
	}

	public void setIsRewind(Integer isRewind) {
		this.isRewind = isRewind;
	}

	public void setReaderNo(Integer readerNo) {
		this.readerNo = readerNo;
	}

	public void setUltraID(Integer ultraID) {
		this.ultraID = ultraID;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public static UltraTagRead parseRecord(CSVRecord record, ZoneId zoneId) {
		UltraTagRead tagRead = new UltraTagRead();
		tagRead.setChipCode(record.get(CHIP_CODE_COLUMN));
		try {
			tagRead.setSeconds(Long.valueOf(record.get(SECONDS_COLUMN)));
		} catch (NumberFormatException e) {
			tagRead.setSeconds(null);
		}
		try {
			tagRead.setMilliseconds(Long.valueOf(record.get(MILLISECONDS_COLUMN)));
		} catch (NumberFormatException e) {
			tagRead.setMilliseconds(null);
		}
		try {
			tagRead.setAntennaNo(Integer.valueOf(record.get(ANTENNA_NO_COLUMN)));
		} catch (NumberFormatException e) {
			tagRead.setAntennaNo(null);
		}
		try {
			tagRead.setRssi(Integer.valueOf(record.get(RSSI_COLUMN)));
		} catch (NumberFormatException e) {
			tagRead.setRssi(null);
		}
		try {
			tagRead.setReaderNo(Integer.valueOf(record.get(READER_NO_COLUMN)));
		} catch (NumberFormatException e) {
			tagRead.setReaderNo(null);
		}
		try {
			tagRead.setUltraID(Integer.valueOf(record.get(ULTRA_ID_COLUMN)));
		} catch (NumberFormatException e) {
			tagRead.setUltraID(null);
		}
		if ((tagRead.getSeconds() != null) && (tagRead.getMilliseconds() != null)) {
			Long milliseconds = tagRead.getSeconds() * 1000 + tagRead.getMilliseconds() + DATE_FROM_1980;
			tagRead.setTimeMillis(milliseconds);
			tagRead.setTime(Instant.ofEpochMilli(tagRead.getTimeMillis()).atZone(zoneId).toLocalDateTime());
		}
		return tagRead;
	}

	public RawChipRead toRawChipRead() {
		RawChipRead chipRead = new RawChipRead();
		chipRead.setTime(time);
		chipRead.setTimeMillis(timeMillis);
		chipRead.setRfidString(chipCode);
		return chipRead;
	}
}
