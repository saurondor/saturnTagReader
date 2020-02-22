/**
 * Copyright (c) 2008, TARAMSA SA de CV
 * All rights reserved. Todos los derechos reservados.
 */
package com.tiempometa.pandora.ipicoreader;

/**
 * Configuration of a particular data load.  Indicates location at which the file-data was adquired.
 * @author Gerardo Tasistro gtasistro@deuxbits.com
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
