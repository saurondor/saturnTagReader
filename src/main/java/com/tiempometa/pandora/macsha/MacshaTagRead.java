/**
 * 
 */
package com.tiempometa.pandora.macsha;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class MacshaTagRead {

	private static final String VOID_TAG = "VOIDTAG";
	private String reader;
	private String rfidString;
	private LocalDateTime time;
	private Long timeMillis;
	private String antenna;
	private static DateTimeFormatter version4DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	private static DateTimeFormatter version3DateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMdd HHmmssSSS");
	// 20200313141812293

	// “MC00001D20150414T120755416T120758010A1R034TV”
	// 012345678901234567890123456789012345678901

	public static MacshaTagRead parseVersion3(String data, ZoneId zoneId) {
		data = data.replaceAll("\n", "");
		data = data.replaceAll("\r", "");
		MacshaTagRead reading = new MacshaTagRead();
		String tag = data.substring(0, 7);
		String dateString = data.substring(8, 16);
		String timeString = data.substring(27, 36);
		String epc = tag;
		if (tag.equals(VOID_TAG)) {
			return null;
		} else {
			try {
				System.out.println("----");
				System.out.println(dateString);
				System.out.println(timeString);
				System.out.println(dateString + timeString);
				System.out.println("----");
				reading.setTime(LocalDateTime.parse(dateString + " " + timeString, version3DateTimeFormatter));
				reading.setTimeMillis(Long.valueOf(reading.getTime().atZone(zoneId).toInstant().toEpochMilli()));
				reading.setRfidString(epc);
			} catch (DateTimeParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return reading;
	}

	public static MacshaTagRead parseVersion4(String data, ZoneId zoneId) {
		data = data.replaceAll("\n", "");
		data = data.replaceAll("\r", "");
		MacshaTagRead reading = new MacshaTagRead();
		String[] fields = data.split(";");
		String epc = fields[1] + fields[2];
		try {
			reading.setTime(LocalDateTime.parse(fields[3] + " " + fields[4], version4DateTimeFormatter));
			reading.setTimeMillis(Long.valueOf(reading.getTime().atZone(zoneId).toInstant().toEpochMilli()));
			reading.setRfidString(epc);
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			return null;
		}
		return reading;
	}

	public static MacshaTagRead parseString(String data, ZoneId zoneId) {
		if (data.length() > 40) {
			if (data.indexOf(";") > 0) {
				return parseVersion4(data, zoneId);
			} else {
				return parseVersion3(data, zoneId);
			}
		} else {
			return null;
		}
	}

	public String getReader() {
		return reader;
	}

	public String getRfidString() {
		return rfidString;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public Long getTimeMillis() {
		return timeMillis;
	}

	public String getAntenna() {
		return antenna;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public void setRfidString(String rfidString) {
		this.rfidString = rfidString;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public void setAntenna(String antenna) {
		this.antenna = antenna;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MacshaTagRead [rfidString=");
		builder.append(rfidString);
		builder.append(", time=");
		builder.append(time);
		builder.append(", timeMillis=");
		builder.append(timeMillis);
		builder.append("]");
		return builder.toString();
	}

	public static MacshaTagRead row(String data, ZoneId zoneId) {
		return MacshaTagRead.parseVersion3(data, zoneId);
	}

	public static MacshaTagRead row(String[] row, ZoneId zoneId) {
		MacshaTagRead reading = new MacshaTagRead();
		String epc = row[1] + row[2];
		reading.setTime(LocalDateTime.parse(row[3] + " " + row[4], version4DateTimeFormatter));
		reading.setTimeMillis(Long.valueOf(reading.getTime().atZone(zoneId).toInstant().toEpochMilli()));
		reading.setRfidString(epc);
		return reading;
	}

	public RawChipRead toRawChipRead() {
		RawChipRead chipRead = new RawChipRead();
		chipRead.setRfidString(getRfidString());
		chipRead.setTimeMillis(getTimeMillis());
		chipRead.setTime(getTime());
		return chipRead;
	}

}
