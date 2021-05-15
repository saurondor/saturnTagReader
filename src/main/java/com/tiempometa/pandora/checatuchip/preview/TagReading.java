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
package com.tiempometa.pandora.checatuchip.preview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Hex;

/**
 * @author Gerardo Tasistro gtasistro@tiempometa.com Copyright 2015 Gerardo
 *         Tasistro Licensed under the Mozilla Public License, v. 2.0
 * 
 */
public class TagReading {
	public static final String KEEP_ALIVE = "*";
	public static final String TYPE_TAG = "tag";
	public static final String TYPE_COMMAND_RESPONSE = "response";
	public static final String TYPE_KEEP_ALIVE = "keepalive";

	private String reader;
	private String antenna;
	private String epc;
	private String tid;
	private String userData;
	private String bib;
	private Long timeMillis;
	private Date time;
	private Long processedMillis;
	private Integer peakRssi;
	private Boolean valid = false;
	private String stringData = null;
	private String readingType = null;
	private String firstName = null;
	private String lastName = null;
	private String middleName = null;
	private String eventTitle = null;
	private String categoryTitle = null;
	private Float readerTemperature = null;
	private Float cpuTemperature = null;

	public TagReading() {
		super();
	}

	// “MC00001D20150414T120755416T120758010A1R034TV”
	// 012345678901234567890123456789012345678901

	public static TagReading parseVersion3(String data) {
		data = data.replaceAll("\n", "");
		data = data.replaceAll("\r", "");
		TagReading reading = null;
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String tag = data.substring(0, 7);
		if (tag.equals("VOIDTAG")) {
			return null;
		}
		String dateString = data.substring(8, 16);
		String timeString = data.substring(27, 36);
		try {
			String epc = tag;
			Date date = dateTimeFormat.parse(dateString + timeString);
			reading = new TagReading(date, epc);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reading;
	}

	public static TagReading parseVersion4(String data) {
		data = data.replaceAll("\n", "");
		data = data.replaceAll("\r", "");
		TagReading reading = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String[] fields = data.split(";");
		if (fields.length > 3) {
			try {
				String epc = fields[1] + fields[2];
				if (epc.equals("VOIDTAG")) {
					return null;
				}
				Date date = format.parse(fields[3] + " " + fields[4]);
				reading = new TagReading(date, epc);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return reading;
	}

	public static TagReading parseMacsha(String data) {
		if (data.length() > 40) {
			if (data.indexOf(";") > 0) {
				return parseVersion4(data);
			} else {
				return parseVersion3(data);
			}
		} else {
			return null;
		}
	}

	public TagReading(String data) {
		super();
		if (data.length() == 0) {
			valid = false;
			return;
		}
		stringData = data;
		if (data.startsWith("#")) {
			readingType = TagReading.TYPE_COMMAND_RESPONSE;
			// command response
			String[] fields = data.replaceAll("\\r", "").replaceAll("\\n", "").split(",");
			switch (fields.length) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				Double millis = Double.valueOf(fields[2]) * 1000;
				time = new Date(millis.longValue());
				Long diff = (new Date()).getTime() - time.getTime();
				epc = diff.toString();
				readingType = TagReading.TYPE_COMMAND_RESPONSE;
				timeMillis = time.getTime();
				break;
			}
		} else {
			readingType = TagReading.TYPE_TAG;
			String[] fields = data.replaceAll("\\r", "").split(",");
			switch (fields.length) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				valid = true;
				// single field data packet is keep alive
				readingType = TagReading.TYPE_KEEP_ALIVE;
				reader = fields[0];
				antenna = fields[1];
				break;
			case 4:
				valid = true;
				// single field data packet is keep alive
				readingType = TagReading.TYPE_KEEP_ALIVE;
				reader = fields[0];
				antenna = fields[1];
				try {
					readerTemperature = Float.valueOf(fields[2]);
					cpuTemperature = Float.valueOf(fields[3]);
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
				break;
			case 7:
				// six field data packet includes user data
				userData = fields[6];
			case 6:
				tid = fields[5];
			case 5:
				// five field data packet lacks user data
				valid = true;
				reader = fields[0];
				antenna = fields[1];
				epc = fields[2];
				try {
					timeMillis = Long.valueOf(fields[3]);
					time = new Date(timeMillis / 1000);
					peakRssi = Integer.parseInt(fields[4]);
				} catch (NumberFormatException e) {
					valid = false;
				}
				break;
			default:
			}
		}
	}

	public TagReading(Date date, String epc) {
		this.epc = epc;
		this.peakRssi = null;
		this.antenna = null;
		this.time = date;
		this.timeMillis = time.getTime();
		this.readingType = TagReading.TYPE_TAG;
		valid = true;
	}

	/**
	 * Returns true if reading is a keepalive package
	 * 
	 * @return
	 */
	public boolean isKeepAlive() {
		if (antenna == null) {
			return false;
		}
		return antenna.equals(KEEP_ALIVE);
	}

	/**
	 * @return the reader
	 */
	public String getReader() {
		return reader;
	}

	/**
	 * @param reader the reader to set
	 */
	public void setReader(String reader) {
		this.reader = reader;
	}

	/**
	 * @return the antenna
	 */
	public String getAntenna() {
		return antenna;
	}

	/**
	 * @param antenna the antenna to set
	 */
	public void setAntenna(String antenna) {
		this.antenna = antenna;
	}

	/**
	 * @return the epc
	 */
	public String getEpc() {
		return epc;
	}

	/**
	 * @param epc the epc to set
	 */
	public void setEpc(String epc) {
		this.epc = epc;
	}

	/**
	 * @return the tid
	 */
	public String getTid() {
		return tid;
	}

	/**
	 * @param tid the tid to set
	 */
	public void setTid(String tid) {
		this.tid = tid;
	}

	/**
	 * @return the userData
	 */
	public String getUserData() {
		return userData;
	}

	/**
	 * @param userData the userData to set
	 */
	public void setUserData(String userData) {
		this.userData = userData;
	}

	/**
	 * @return the timeMillis
	 */
	public Long getTimeMillis() {
		return timeMillis;
	}

	/**
	 * @param timeMillis the timeMillis to set
	 */
	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * @return the peakRssi
	 */
	public Integer getPeakRssi() {
		return peakRssi;
	}

	/**
	 * @param peakRssi the peakRssi to set
	 */
	public void setPeakRssi(Integer peakRssi) {
		this.peakRssi = peakRssi;
	}

	/**
	 * @return the valid
	 */
	public Boolean isValid() {
		return valid;
	}

	/**
	 * @return the readingType
	 */
	public String getReadingType() {
		return readingType;
	}

	/**
	 * @return the bib
	 */
	public String getBib() {
		return bib;
	}

	/**
	 * @param bib the bib to set
	 */
	public void setBib(String bib) {
		this.bib = bib;
	}

	/**
	 * @return the processedMillis
	 */
	public Long getProcessedMillis() {
		return processedMillis;
	}

	/**
	 * @param processedMillis the processedMillis to set
	 */
	public void setProcessedMillis(Long processedMillis) {
		this.processedMillis = processedMillis;
	}

	private String prettyName(String nameValue) {
		if (nameValue == null) {
			return "";
		}
		return nameValue.trim();
	}

	public String getParticipantFullName() {
		return (prettyName(firstName) + " " + prettyName(lastName) + " " + prettyName(middleName)).trim();
	}

	/**
	 * @return the firstName
	 */
	public synchronized String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public synchronized void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public synchronized String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public synchronized void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the middleName
	 */
	public synchronized String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName the middleName to set
	 */
	public synchronized void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the eventTitle
	 */
	public synchronized String getEventTitle() {
		return eventTitle;
	}

	/**
	 * @param eventTitle the eventTitle to set
	 */
	public synchronized void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	/**
	 * @return the categoryTitle
	 */
	public synchronized String getCategoryTitle() {
		return categoryTitle;
	}

	/**
	 * @param categoryTitle the categoryTitle to set
	 */
	public synchronized void setCategoryTitle(String categoryTitle) {
		this.categoryTitle = categoryTitle;
	}

	/**
	 * @return the readerTemperature
	 */
	public Float getReaderTemperature() {
		return readerTemperature;
	}

	/**
	 * @param readerTemperature the readerTemperature to set
	 */
	public void setReaderTemperature(Float readerTemperature) {
		this.readerTemperature = readerTemperature;
	}

	/**
	 * @return the cpuTemperature
	 */
	public Float getCpuTemperature() {
		return cpuTemperature;
	}

	/**
	 * @param cpuTemperature the cpuTemperature to set
	 */
	public void setCpuTemperature(Float cpuTemperature) {
		this.cpuTemperature = cpuTemperature;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TagReading [epc=");
		builder.append(epc);
		builder.append(", time=");
		builder.append(time);
		builder.append("]");
		return builder.toString();
	}

}
