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
package com.tiempometa.pandora.ipicoreader;

/**
 * Configuration of a particular data load.  Indicates location at which the file-data was adquired.
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class DataLoadProperties {

	/**
	 * Box in which the chip read was made
	 */
	private String checkPoint="";
	
	/**
	 * User set lap value.
	 */
	private String uLap="";
	/**
	 * User set phase value.
	 */
	private String uPhase="";
	/**
	 * User set stage value.
	 */
	private String uStage="";
	/**
	 * Identifier for load made.
	 */
	private Integer loadCount=0;

	public DataLoadProperties(String checkPoint, String lap,
			String phase, String stage, Integer loadCount) {
		super();
		this.checkPoint = checkPoint;
		this.uLap = lap;
		this.uPhase = phase;
		this.uStage = stage;
		this.loadCount = loadCount;
	}

	/**
	 * 
	 */
	public DataLoadProperties() {
		// TODO Auto-generated constructor stub
	}

	public String getCheckPoint() {
		return checkPoint;
	}

	public void setCheckPoint(String checkPoint) {
		this.checkPoint = checkPoint;
	}

	public String getULap() {
		return uLap;
	}

	public void setULap(String lap) {
		uLap = lap;
	}

	public String getUPhase() {
		return uPhase;
	}

	public void setUPhase(String phase) {
		uPhase = phase;
	}

	public String getUStage() {
		return uStage;
	}

	public void setUStage(String stage) {
		uStage = stage;
	}

	public Integer getLoadCount() {
		return loadCount;
	}

	public void setLoadCount(Integer loadCount) {
		this.loadCount = loadCount;
	}

}
