/**
 * Copyright (c) 2008, TARAMSA SA de CV
 * All rights reserved. Todos los derechos reservados.
 */
package com.tiempometa.pandora.ipicoreader;

import java.time.ZoneId;
import java.util.Date;

import org.apache.log4j.Logger;

import com.tiempometa.Utils;
import com.tiempometa.timing.model.RawChipRead;

/**
 * Raw Chip read data structure to represent each individual time data.
 * 
 * @author Gerardo Tasistro gtasistro@deuxbits.com
 * 
 */
public class IpicoRead {

	private static final Logger logger = Logger.getLogger(IpicoRead.class);
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

	/**
	 * RFID identifier string
	 */
	private String rfid;
	/**
	 * The reader ID where the chip was read
	 */
	private String reader;
	/**
	 * The antenna in the reader where the chip was read
	 */
	private String antenna;
	/**
	 * Time chip read was made
	 */
	private Date clockTime;
	/**
	 * Time chip read was made, in milliseconds.
	 */
	private Long runTime;
	/**
	 * Chip seen status Some systems can notify if this read is the first time or
	 * last time the chip was read.
	 */
	private Integer seenStatus;
	/**
	 * Payload CRC
	 */
	private String crc;
	/**
	 * Checkpoint at which this reading was made
	 */
	private String checkPoint;
	/**
	 * Equipment this read was made on
	 */
	private String terminal;

	/**
	 * 
	 */
	public IpicoRead() {
		// TODO Auto-generated constructor stub
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public Date getClockTime() {
		return clockTime;
	}

	public void setClockTime(Date clockTime) {
		this.clockTime = clockTime;
	}

	public Long getRunTime() {
		return runTime;
	}

	public void setRunTime(Long runTime) {
		this.runTime = runTime;
	}

	public String getReader() {
		return reader;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public String getAntenna() {
		return antenna;
	}

	public void setAntenna(String antenna) {
		this.antenna = antenna;
	}

	/**
	 * @return the seenStatus
	 */
	public Integer getSeenStatus() {
		return seenStatus;
	}

	/**
	 * @param seenStatus the seenStatus to set
	 */
	public void setSeenStatus(Integer seenStatus) {
		this.seenStatus = seenStatus;
	}

	public static String crc(String dataString) {
		int checkSum = 0;
		for (int i = 2; i < dataString.length() - 2; i++) {
			checkSum = (checkSum + (dataString.getBytes()[i]));
		}
		String crc = Integer.toHexString(checkSum);
		crc = crc.substring(crc.length() - 2);
		return crc.toLowerCase();
	}

	public static IpicoRead parse(String line) {
		if (line == null) {
			logger.error("Parse line is NULL");
			return null;
		}
		IpicoRead reading = new IpicoRead();
		if (line.length() >= FRAME_LRC_END) {
			if ((line.length() >= FRAME_SEEN_END) && (line.substring(0, 2).equals(DATA_LINE_HEADER))) {

				String crc = crc(line.substring(IpicoRead.FRAME_PAYLOAD_START, IpicoRead.FRAME_PAYLOAD_END));
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
				if (line.substring(36, 38).equals(FIRST_SEEN_TAIL)) {
					reading.setSeenStatus(FIRST_SEEN);
				}
				if (line.substring(36, 38).equals(LAST_SEEN_TAIL)) {
					reading.setSeenStatus(LAST_SEEN);
				}
				reading.setClockTime(chipDate);
				reading.setRunTime(chipDate.getTime());
				reading.setAntenna(line.substring(FRAME_I_CHANNEL_START, FRAME_Q_CHANNEL_END));
				reading.setCrc(crc);
			} else {
				// is not FS read, ignore
				logger.error("Invalid length or start frame " + line.length() + " " + line.substring(0, 2));
				reading = null;
			}
		} else {
			logger.error("Invalid length " + line.length());
			reading = null;
		}
		return reading;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IpicoRead [rfid=");
		builder.append(rfid);
		builder.append(", checkpoint=");
		builder.append(checkPoint);
		builder.append(", reader=");
		builder.append(reader);
		builder.append(", antenna=");
		builder.append(antenna);
		builder.append(", clockTime=");
		builder.append(clockTime);
		builder.append(", runTime=");
		builder.append(runTime);
		builder.append(", seenStatus=");
		builder.append(seenStatus);
		builder.append("]");
		return builder.toString();
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
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

	public static IpicoRead parseString(String dataLine, ZoneId zoneId) {
		return parse(dataLine);
	}

	public String getCheckPoint() {
		return checkPoint;
	}

	public void setCheckPoint(String checkPoint) {
		this.checkPoint = checkPoint;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

}
