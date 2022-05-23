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
package com.tiempometa.pandora.foxberry;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class FoxberryTagRead {

	private static final Logger logger = LogManager.getLogger(FoxberryTagRead.class);

	// reader_2,4,BF65451ABE0F622D4A180ED3,1467464368979179,-63,,
	public static final int READER_COLUMN = 0;
	public static final int ANTENNA_COLUMN = 1;
	public static final int RFID_COLUMN = 2;
	public static final int TIME_MILLIS_COLUMN = 3;
	public static final int SIGNAL_STRENGTH_COLUMN = 4;
	public static final int CPU_ONE_TEMPERATURE_COLUMN = 5;
	public static final int CPU_TWO_TEMPERATURE_COLUMN = 6;

	private String reader;
	private Integer antenna;
	private String rfidId;
	private Long timeMillis;
	private Integer signalStrength;
	private Float cpuOneTemperature;
	private Float cpuTwoTemperature;
	private LocalDateTime time;

	public static FoxberryTagRead parseRecord(CSVRecord record, ZoneId zoneId) {
		FoxberryTagRead tagRead = new FoxberryTagRead();
		tagRead.setReader(record.get(READER_COLUMN));
		try {

			try {
				tagRead.setAntenna(Integer.valueOf(record.get(ANTENNA_COLUMN)));
			} catch (NumberFormatException e) {
				tagRead.setAntenna(null);
			}
			tagRead.setRfidId(record.get(RFID_COLUMN));
			try {
				tagRead.setTimeMillis(Long.valueOf(record.get(TIME_MILLIS_COLUMN)) / 1000);
			} catch (NumberFormatException e) {
				tagRead.setTimeMillis(null);
			}
			try {
				tagRead.setSignalStrength(Integer.valueOf(record.get(SIGNAL_STRENGTH_COLUMN)));
			} catch (NumberFormatException e) {
				tagRead.setSignalStrength(null);
			}
			try {
				tagRead.setCpuOneTemperature(Float.valueOf(record.get(CPU_ONE_TEMPERATURE_COLUMN)));
			} catch (NumberFormatException e) {
				tagRead.setCpuOneTemperature(null);
			}
			try {
				tagRead.setCpuTwoTemperature(Float.valueOf(record.get(CPU_TWO_TEMPERATURE_COLUMN)));
			} catch (NumberFormatException e) {
				tagRead.setCpuTwoTemperature(null);
			}
			tagRead.setTime(Instant.ofEpochMilli(tagRead.getTimeMillis()).atZone(zoneId).toLocalDateTime());
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("Error parsing record " + record.getRecordNumber() + " @column:" + e.getMessage()
					+ " - payload: " + record.toString());
		}
		return tagRead;
	}

	public RawChipRead toRawChipRead() {
		RawChipRead chipRead = new RawChipRead();
		chipRead.setTime(time);
		chipRead.setTimeMillis(timeMillis);
		chipRead.setRfidString(rfidId);
		return chipRead;
	}

	public String getReader() {
		return reader;
	}

	public Integer getAntenna() {
		return antenna;
	}

	public String getRfidId() {
		return rfidId;
	}

	public Long getTimeMillis() {
		return timeMillis;
	}

	public Integer getSignalStrength() {
		return signalStrength;
	}

	public Float getCpuOneTemperature() {
		return cpuOneTemperature;
	}

	public Float getCpuTwoTemperature() {
		return cpuTwoTemperature;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public void setAntenna(Integer antenna) {
		this.antenna = antenna;
	}

	public void setRfidId(String rfidId) {
		this.rfidId = rfidId;
	}

	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public void setSignalStrength(Integer signalStrength) {
		this.signalStrength = signalStrength;
	}

	public void setCpuOneTemperature(Float cpuOneTemperature) {
		this.cpuOneTemperature = cpuOneTemperature;
	}

	public void setCpuTwoTemperature(Float cpuTwoTemperature) {
		this.cpuTwoTemperature = cpuTwoTemperature;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

}
