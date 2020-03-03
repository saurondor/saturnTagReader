/**
 * 
 */
package com.tiempometa.pandora.timinsense;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.csv.CSVRecord;

import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class TimingsenseTagRead {
	// [{lectura},{lectura}]
	// {"Chip":2,"Moment":"2016-06-03T02:33:48.937842Z","TimingPoint":"a","Type":1}

	// Chip: código del chip que origina la lectura.
	// - Moment: objeto datetime, con el momento en
	// el que se realizó la lectura del chip, en el huso
	// horario del equipo que recibiera dicha lectura
	// de chip. En la recuperación de lecturas
	// almacenadas puede traer el offset que el
	// cronometrador crea conveniente.
	// - TimingPoint: nombre del TimingPoint en el
	// que se originó la lectura del chip
	// - Type: tipo de lectura. Puede ser “1” (primera
	// lectura tomada) o “2” (lectura más potente).
	// - Antenna: antena que ha leído el chip.
	// - Source: equipo que ha leído el chip.

	public static final int CHIP_COLUMN = 0;
	public static final int MOMENT_COLUMN = 1;
	public static final int TIMINGPOINT_COLUMN = 2;
	public static final int TYPE_COLUMN = 3;

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
		chipRead.setTime(time);
		return chipRead;
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
		Instant instant = Instant.parse(tagRead.getMoment());
		
		tagRead.setTimeMillis(instant.toEpochMilli());
		tagRead.setTime(instant.atZone(zoneId).toLocalDateTime());
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
}
