/**
 * Copyright (c) 2008, TARAMSA SA de CV
 * All rights reserved. Todos los derechos reservados.
 */
package com.tiempometa.pandora.ipicoreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * Handles the decoding of each chip read line.
 * 
 * @author Gerardo Tasistro gtasistro@deuxbits.com
 * 
 */
public class IPICOChipReadDecoder implements ChipReadDecoder {

	private static Logger log = Logger.getLogger(IPICOChipReadDecoder.class);
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

	/**
	 * Processes a chip read string. It does not process the date values and is
	 * used generally for the portable reader
	 * 
	 * @param chipRead
	 * @return
	 */
	@Override
	public IpicoRead readChip(String chipRead) {
		IpicoRead reading = new IpicoRead();
		if (chipRead.length() >= 36) {
			try {
				if (chipRead.substring(0, 2).equals(DATA_LINE_HEADER)) {
					reading.setReader(chipRead.substring(FRAME_READER_START,
							FRAME_READER_END));
					reading.setRfid(chipRead.substring(FRAME_TAG_START,
							FRAME_TAG_END));
					reading.setAntenna(chipRead.substring(
							FRAME_I_CHANNEL_START, FRAME_Q_CHANNEL_END));
					if (log.isDebugEnabled()) {
						log.debug("box:" + reading.getReader() + "@"
								+ reading.getAntenna());
						log.debug("rfid:" + reading.getRfid());
						log.debug("date:" + reading.getClockTime());
						log.debug("millis:" + reading.getRunTime());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			reading = null;
		}
		return reading;
	}

	/**
	 * Processes a chip read string. Handles the date values and sets the load
	 * properties.
	 * 
	 * @param chipRead
	 * @param loadProperties
	 * @return
	 */
	@Override
	@SuppressWarnings("deprecation")
	public IpicoRead readChip(String chipRead, DataLoadProperties loadProperties) {
		IpicoRead reading = new IpicoRead();
		if (chipRead.length() >= 36) {
			if ((chipRead.length() >= 38)
					&& (chipRead.substring(0, 2).equals(DATA_LINE_HEADER) && chipRead
							.substring(36, 38).equals(FIRST_SEEN_TAIL))
					|| (chipRead.substring(0, 2).equals(DATA_LINE_HEADER))) {
				reading.setReader(chipRead.substring(FRAME_READER_START,
						FRAME_READER_END));
				reading.setRfid(chipRead.substring(FRAME_TAG_START,
						FRAME_TAG_END));
				int year = Integer.valueOf(chipRead.substring(FRAME_DATE_START,
						FRAME_DATE_START + 2));
				int month = Integer.valueOf(chipRead.substring(
						FRAME_DATE_START + 2, FRAME_DATE_START + 4));
				int day = Integer.valueOf(chipRead.substring(
						FRAME_DATE_START + 4, FRAME_DATE_START + 6));
				int hour = Integer.valueOf(chipRead.substring(FRAME_TIME_START,
						FRAME_TIME_START + 2));
				int minute = Integer.valueOf(chipRead.substring(
						FRAME_TIME_START + 2, FRAME_TIME_START + 4));
				int second = Integer.valueOf(chipRead.substring(
						FRAME_TIME_START + 4, FRAME_TIME_START + 6));
				int millis = Integer.parseInt(chipRead.substring(
						FRAME_MILLIS_START, FRAME_MILLIS_END), 16) * 10;
				Date chipDate = new Date(100 + year, month - 1, day, hour,
						minute, second);
				chipDate.setTime(chipDate.getTime() + millis);
				reading.setClockTime(chipDate);
				reading.setRunTime(chipDate.getTime());
				reading.setAntenna(chipRead.substring(FRAME_I_CHANNEL_START,
						FRAME_Q_CHANNEL_END));
				if (log.isDebugEnabled()) {
					log.debug("box:" + reading.getReader() + "@"
							+ reading.getAntenna());
					log.debug("rfid:" + reading.getRfid());
					log.debug("date:" + reading.getClockTime());
					log.debug("millis:" + reading.getRunTime());
				}
			} else {
				// is not FS read, ignore
				reading = null;
			}
		} else {
			log.debug("Line is invalid");
			reading = null;
		}
		return reading;
	}

	/**
	 * Processes a file with chip read data. One read per row.
	 * 
	 * @param fileName
	 * @param loadProperties
	 */
	@Override
	public List<IpicoRead> readFile(Reader fileReader, DataLoadProperties loadProperties) {
		List<IpicoRead> readings = new ArrayList<IpicoRead>();
		try {
			BufferedReader bReader = new BufferedReader(fileReader);
			String dataLine;
			do {
				dataLine = bReader.readLine();
				if (dataLine != null) {
					if (dataLine.length() == 38) {
						if (dataLine.substring(0, 2).equals(DATA_LINE_HEADER)
								&& dataLine.substring(36, 38).equals(
										FIRST_SEEN_TAIL)) {
							if (log.isDebugEnabled()) {
								log.debug(dataLine);
							}
							IpicoRead read = readChip(dataLine, loadProperties);
							readings.add(read);
						}
					}
				}
			} while (dataLine != null);
			bReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		return readings;
	}

	/**
	 * 
	 */
	public IPICOChipReadDecoder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Integer dataPort() {
		return 10200;
	}

	@Override
	public String lineDelimiter() {
		return "\\n";
	}

}
