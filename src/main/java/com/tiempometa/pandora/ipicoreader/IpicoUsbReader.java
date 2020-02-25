/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author gtasi
 *
 */
public class IpicoUsbReader implements Runnable {
	private static final Logger logger = Logger.getLogger(IpicoUsbReader.class);
	TagReadListener tagReadListener;
	private SerialReader serialReader = new SerialReader();
	private String checkPointOne;
	private String checkPointTwo;
	private String terminal;
	
	public List<String> getSerialPorts() {
		return serialReader.getPorts();
	}

	public void setCommandResponseHandler(JIpicoUsbReaderPanel jIpicoReaderPanel) {
		// TODO Auto-generated method stub

	}

	public void registerTagReadListener(TagReadListener listener) {
		tagReadListener = listener;
	}

	public TagReadListener getTagReadListener() {
		return tagReadListener;
	}

	public String getCheckPointOne() {
		return checkPointOne;
	}

	public String getCheckPointTwo() {
		return checkPointTwo;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;
	}

	public void setCheckPointOne(String checkPointOne) {
		this.checkPointOne = checkPointOne;
	}

	public void setCheckPointTwo(String checkPointTwo) {
		this.checkPointTwo = checkPointTwo;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	public void connect(String commPort) throws Exception {
		serialReader.openPort(commPort);
		
	}

}
