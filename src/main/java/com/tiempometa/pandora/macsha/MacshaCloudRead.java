/**
 * 
 */
package com.tiempometa.pandora.macsha;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.CSVRecord;

import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class MacshaCloudRead {
	// passing;bib;date_time;latitude;longitude;status;chip;participant_id;timezone
	// 3;02;2020-03-11 12:25:01.649;16.7578038;-93.116542;Sent;;;GMT-06:00
	// 2;701;2020-03-11 12:24:24.255;16.7578038;-93.116542;Sent;;;GMT-06:00
	// 1;800;2020-03-11 12:24:17.131;16.7578038;-93.116542;Sent;;;GMT-06:00

	private Integer passing;
	private Integer bib;
	private String date_time;
	private Double latitude;
	private Double longitude;
	private String chip;
	private String status;
	private String participant_id;
	private String timezone;
	private LocalDateTime time;
	private Long timeMillis;

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	public static MacshaCloudRead parseRecord(CSVRecord record) {
		MacshaCloudRead read = new MacshaCloudRead();
		// passing;bib;date_time;latitude;longitude;status;chip;participant_id;timezone
		// 3;02;2020-03-11 12:25:01.649;16.7578038;-93.116542;Sent;;;GMT-06:00
		try {
			read.setPassing(Integer.valueOf(record.get(0)));
		} catch (NumberFormatException e) {
			read.setPassing(null);
		}
		try {
			read.setBib(Integer.valueOf(record.get(1)));
		} catch (Exception e) {
			read.setBib(null);
		}
		read.setDate_time(record.get(2));
		try {
			read.setLatitude(Double.valueOf(record.get(3)));
		} catch (NumberFormatException e) {
			read.setLatitude(null);
		}
		try {
			read.setLongitude(Double.valueOf(record.get(4)));
		} catch (NumberFormatException e) {
			read.setLongitude(Double.valueOf(null));
		}
		read.setStatus(record.get(5));
		read.setChip(record.get(6));
		read.setParticipant_id(record.get(7));
		read.setTimezone(record.get(8));
		LocalDateTime dateTime = LocalDateTime.parse(read.getDate_time(), dateTimeFormatter);
		read.setTime(dateTime);
		read.setTimeMillis(dateTime.atZone(ZoneId.of(read.getTimezone())).toInstant().toEpochMilli());
		return read;
	}

	public RawChipRead toRawChipRead() {
		RawChipRead chipRead = new RawChipRead();
		chipRead.setRfidString(getChip());
		chipRead.setTimeMillis(getTimeMillis());
		chipRead.setTime(getTime());
		chipRead.setChipNumber(bib);
		return chipRead;
	}

	public Integer getPassing() {
		return passing;
	}

	public Integer getBib() {
		return bib;
	}

	public String getDate_time() {
		return date_time;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String getChip() {
		return chip;
	}

	public String getStatus() {
		return status;
	}

	public String getParticipant_id() {
		return participant_id;
	}

	public String getTimezone() {
		return timezone;
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

	public void setPassing(Integer passing) {
		this.passing = passing;
	}

	public void setBib(Integer bib) {
		this.bib = bib;
	}

	public void setDate_time(String date_time) {
		this.date_time = date_time;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setChip(String chip) {
		this.chip = chip;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setParticipant_id(String participant_id) {
		this.participant_id = participant_id;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public static void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
		MacshaCloudRead.dateTimeFormatter = dateTimeFormatter;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MacshaCloudRead [passing=");
		builder.append(passing);
		builder.append(", bib=");
		builder.append(bib);
		builder.append(", date_time=");
		builder.append(date_time);
		builder.append(", latitude=");
		builder.append(latitude);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", chip=");
		builder.append(chip);
		builder.append(", status=");
		builder.append(status);
		builder.append(", participant_id=");
		builder.append(participant_id);
		builder.append(", timezone=");
		builder.append(timezone);
		builder.append(", time=");
		builder.append(time);
		builder.append(", timeMillis=");
		builder.append(timeMillis);
		builder.append("]");
		return builder.toString();
	}

}
