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
package com.tiempometa.pandora.tagreader;

import java.time.LocalDateTime;

import com.tiempometa.webservice.model.RawChipRead;
import com.tiempometa.webservice.model.ParticipantRegistration;

/**
 * @author Gerardo Esteban Tasistro Giubetic
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
