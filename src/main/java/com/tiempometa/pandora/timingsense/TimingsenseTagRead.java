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
package com.tiempometa.pandora.timingsense;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class TimingsenseTagRead {
	// [{lectura},{lectura}]
	// {"Chip":2,"Moment":"2016-06-03T02:33:48.937842Z","TimingPoint":"a","Type":1}

	// Chip: c?digo del chip que origina la lectura.
	// - Moment: objeto datetime, con el momento en
	// el que se realiz? la lectura del chip, en el huso
	// horario del equipo que recibiera dicha lectura
	// de chip. En la recuperaci?n de lecturas
	// almacenadas puede traer el offset que el
	// cronometrador crea conveniente.
	// - TimingPoint: nombre del TimingPoint en el
	// que se origin? la lectura del chip
	// - Type: tipo de lectura. Puede ser ?1? (primera
	// lectura tomada) o ?2? (lectura m?s potente).
	// - Antenna: antena que ha le?do el chip.
	// - Source: equipo que ha le?do el chip.

	public static final int CHIP_COLUMN = 0;
	public static final int MOMENT_COLUMN = 1;
	public static final int TIMINGPOINT_COLUMN = 2;
	public static final int TYPE_COLUMN = 3;
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");

	private Integer Chip;
	private String Moment;
	private String TimingPoint;
	private Long timeMillis;
	private LocalDateTime time;
	private Integer Type;

	public Integer getChip() {
		return Chip;
	}

	public String getMoment() {
		return Moment;
	}

	public String getTimingPoint() {
		return TimingPoint;
	}

	public Integer getType() {
		return Type;
	}

	public void setChip(Integer chip) {
		Chip = chip;
	}

	public void setMoment(String moment) {
		Moment = moment;
	}

	public void setTimingPoint(String timingPoint) {
		TimingPoint = timingPoint;
	}

	public void setType(Integer type) {
		Type = type;
	}

	public RawChipRead toRawChipRead() {
		RawChipRead chipRead = new RawChipRead();
		chipRead.setRfidString(Chip.toString());
		chipRead.setTimeMillis(timeMillis);
		chipRead.setCheckPoint(TimingPoint);
		chipRead.setTime(time);
		return chipRead;
	}

	/**
	 * Converts Moment to timeMillis and time
	 */
	public void populateTimeFields() {
		LocalDateTime localDateTime = LocalDateTime.parse(Moment.substring(0, 26),
				DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		this.setTime(localDateTime);
		this.setTimeMillis(localDateTime.atZone((Context.getZoneId())).toInstant().toEpochMilli());
	}

	/**
	 * Converts a JSON text to a list of TimingsenseTagRead
	 * 
	 * @param json
	 * @return
	 */
	public static List<TimingsenseTagRead> parseJson(String json) {
		List<TimingsenseTagRead> readings = new ArrayList<TimingsenseTagRead>();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		readings = (List<TimingsenseTagRead>) gson.fromJson(json, new TypeToken<List<TimingsenseTagRead>>() {
		}.getType());
		for (TimingsenseTagRead timingsenseTagRead : readings) {
			timingsenseTagRead.populateTimeFields();
		}
		return readings;
	}

	public static List<RawChipRead> toRawChipReads(List<TimingsenseTagRead> tagReads) {
		List<RawChipRead> readings = new ArrayList<RawChipRead>();
		for (TimingsenseTagRead timingsenseTagRead : tagReads) {
			readings.add(timingsenseTagRead.toRawChipRead());
		}
		return readings;
	}

	public static TimingsenseTagRead parseRecord(CSVRecord record, ZoneId zoneId) {
		TimingsenseTagRead tagRead = new TimingsenseTagRead();
		try {
			tagRead.setChip(Integer.valueOf(record.get(CHIP_COLUMN)));
		} catch (NumberFormatException e) {
			tagRead.setChip(Integer.valueOf(record.get(CHIP_COLUMN)));
		}
		tagRead.setTimingPoint(record.get(TIMINGPOINT_COLUMN));
		tagRead.setMoment(record.get(MOMENT_COLUMN));
		try {
			tagRead.setType(Integer.valueOf(record.get(TYPE_COLUMN)));
		} catch (NumberFormatException e) {
			tagRead.setType(Integer.valueOf(record.get(TYPE_COLUMN)));
		}

		LocalDateTime time = LocalDateTime.parse(tagRead.getMoment(), dateTimeFormatter);
//		Instant instant = Instant.from(dateTimeFormatter.parse(tagRead.getMoment()));

//		Instant instant = Instant.parse(tagRead.getMoment());

		tagRead.setTimeMillis(time.atZone(zoneId).toInstant().toEpochMilli());
		tagRead.setTime(time);
		return tagRead;
	}

	public Long getTimeMillis() {
		return timeMillis;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimingsenseTagRead [Chip=");
		builder.append(Chip);
		builder.append(", Moment=");
		builder.append(Moment);
		builder.append(", TimingPoint=");
		builder.append(TimingPoint);
		builder.append(", timeMillis=");
		builder.append(timeMillis);
		builder.append(", time=");
		builder.append(time);
		builder.append(", Type=");
		builder.append(Type);
		builder.append("]");
		return builder.toString();
	}
}
