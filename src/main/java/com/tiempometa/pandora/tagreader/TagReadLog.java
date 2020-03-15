/**
 * 
 */
package com.tiempometa.pandora.tagreader;

import java.time.LocalDateTime;

import com.tiempometa.webservice.model.RawChipRead;
import com.tiempometa.webservice.model.ParticipantRegistration;

/**
 * @author gtasi
 *
 */
public class TagReadLog {

	private String tag;
	private LocalDateTime time;
	private String checkPoint;
	private String bib;
	private String name;
	private String category;

	public static TagReadLog fromRawRead(RawChipRead rawChipRead) {
		TagReadLog log = new TagReadLog();
		log.setTag(rawChipRead.getRfidString());
		log.setTime(rawChipRead.getTime());
		log.setCheckPoint(rawChipRead.getCheckPoint());
		return log;
	}

	public static TagReadLog fromRawRead(RawChipRead rawChipRead, ParticipantRegistration registration) {
		TagReadLog log = new TagReadLog();
		log.setTag(rawChipRead.getRfidString());
		log.setTime(rawChipRead.getTime());
		log.setCheckPoint(rawChipRead.getCheckPoint());
		log.setCategory(registration.getCategoryTitle());
		log.setName(registration.getFullName());
		log.setBib(registration.getBib());
		return log;
	}

	public String getTag() {
		return tag;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public String getCheckPoint() {
		return checkPoint;
	}

	public String getBib() {
		return bib;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public void setCheckPoint(String checkPoint) {
		this.checkPoint = checkPoint;
	}

	public void setBib(String bib) {
		this.bib = bib;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
