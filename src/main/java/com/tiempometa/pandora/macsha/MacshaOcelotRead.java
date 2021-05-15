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
package com.tiempometa.pandora.macsha;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.CSVRecord;

import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class MacshaOcelotRead {
	// ID2-20200315-062123- 123-e: 512_1
	// DM00328;2020/03/15;06:02:55.029;06:02:55.311;3;006
	// DM00227;2020/03/15;06:02:55.665;06:02:57.568;2;040
	// DM00016;2020/03/15;06:02:54.451;06:02:55.621;4;025
	// DM00198;2020/03/15;06:02:57.385;06:02:57.534;4;004
	// DM00519;2020/03/15;16:31:06.994;16:31:07.209;4;008
	// DM00293;2020/03/15;16:32:16.481;16:32:16.784;3;012
	// 20200315-163625-695

	public static final int ENTRY_READ_TIME = 0;
	public static final int EXIT_READ_TIME = 1;
	public static String VOID_TAG = "VOIDTAG";

	private String rfid;
	private String readDate;
	private String entryDateTime;
	private String exitDateTime;
	private Integer antenna;
	private Integer readCount;
	private LocalDateTime time;
	private Long timeMillis;

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

	public static MacshaOcelotRead parseRecord(CSVRecord record, Integer readTime) {
		if (record.get(0).equals(VOID_TAG)) {
			return null;
		}
		MacshaOcelotRead read = new MacshaOcelotRead();
		// DM00328;2020/03/15;06:02:55.029;06:02:55.311;3;006
		read.setRfid(record.get(0));
		read.setReadDate(record.get(1));
		read.setEntryDateTime(record.get(2));
		read.setExitDateTime(record.get(3));
		try {
			read.setAntenna(Integer.valueOf(record.get(4)));
		} catch (Exception e) {
			read.setAntenna(null);
		}
		try {
			read.setReadCount(Integer.valueOf(record.get(5)));
		} catch (Exception e) {
			read.setReadCount(null);
		}
		String dateTime = null;
		switch (readTime) {
		case ENTRY_READ_TIME:
			dateTime = read.getReadDate() + " " + read.getEntryDateTime();
			break;
		case EXIT_READ_TIME:
			dateTime = read.getReadDate() + " " + read.getExitDateTime();
			break;
		default:
			dateTime = read.getReadDate() + " " + read.getExitDateTime();
			break;
		}
		LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
		read.setTime(localDateTime);
		read.setTimeMillis(localDateTime.atZone((Context.getZoneId())).toInstant().toEpochMilli());
		return read;
	}

	public RawChipRead toRawChipRead() {
		RawChipRead chipRead = new RawChipRead();
		chipRead.setRfidString(getRfid());
		chipRead.setTimeMillis(getTimeMillis());
		chipRead.setTime(getTime());
		return chipRead;
	}

	public String getRfid() {
		return rfid;
	}

	public String getReadDate() {
		return readDate;
	}

	public String getEntryDateTime() {
		return entryDateTime;
	}

	public String getExitDateTime() {
		return exitDateTime;
	}

	public Integer getAntenna() {
		return antenna;
	}

	public Integer getReadCount() {
		return readCount;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public Long getTimeMillis() {
		return timeMillis;
	}

	public static DateTimeFormatter getDateTimeFormatter() {
		return dateTimeFormatter;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public void setReadDate(String readDate) {
		this.readDate = readDate;
	}

	public void setEntryDateTime(String entryDateTime) {
		this.entryDateTime = entryDateTime;
	}

	public void setExitDateTime(String exitDateTime) {
		this.exitDateTime = exitDateTime;
	}

	public void setAntenna(Integer antenna) {
		this.antenna = antenna;
	}

	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public static void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
		MacshaOcelotRead.dateTimeFormatter = dateTimeFormatter;
	}

}
