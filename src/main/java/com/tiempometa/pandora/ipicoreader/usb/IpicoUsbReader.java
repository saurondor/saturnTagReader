/**
 * 
 */
package com.tiempometa.pandora.ipicoreader.usb;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.tiempometa.Utils;
import com.tiempometa.pandora.ipicoreader.ReadListener;
import com.tiempometa.pandora.ipicoreader.TagReadListener;
import com.tiempometa.timing.model.CookedChipRead;
import com.tiempometa.timing.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class IpicoUsbReader implements Runnable, ReadListener {
	private static final Logger logger = Logger.getLogger(IpicoUsbReader.class);
	TagReadListener tagReadListener;
	private SerialReader serialReader = new SerialReader(this);
//	private String checkPointOne;
//	private String checkPointTwo;
//	private String terminal;
	private String lastRfid;
//	private Integer mode = JIpicoUsbReaderPanel.MODE_CHECA_TU_CHIP;

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

//	public String getCheckPointOne() {
//		return checkPointOne;
//	}
//
//	public String getCheckPointTwo() {
//		return checkPointTwo;
//	}
//
//	public String getTerminal() {
//		return terminal;
//	}

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;
	}

//	public void setCheckPointOne(String checkPointOne) {
//		this.checkPointOne = checkPointOne;
//	}
//
//	public void setCheckPointTwo(String checkPointTwo) {
//		this.checkPointTwo = checkPointTwo;
//	}
//
//	public void setTerminal(String terminal) {
//		this.terminal = terminal;
//	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	public void connect(String commPort) throws Exception {
		serialReader.openPort(commPort);
	}

	public boolean isConnected() {
		return serialReader.isConnected();
	}

	public void disconnect() {
		serialReader.closePort();
	}

	@Override
	public void notifyChipReads(String rfid) {
		String lastTag = null;
		synchronized (this) {
			lastTag = lastRfid;
		}
		if ((lastTag == null) || (!rfid.equals(lastTag))) {
			lastRfid = rfid;
			RawChipRead chipRead = new RawChipRead();
			chipRead.setRfidString(rfid);
			LocalDateTime time = LocalDateTime.now();
			chipRead.setReadTime(time);
			chipRead.setTimeMillis(Utils.localDateTimeToMillis(time));
			List<RawChipRead> chipReadList = new ArrayList<RawChipRead>();
			chipReadList.add(chipRead);
			tagReadListener.notifyTagReads(chipReadList);
			Thread clearThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					synchronized (this) {
						lastRfid = null;
					}
				}
			});
			clearThread.start();
		}
	}
//
//	public Integer getMode() {
//		return mode;
//	}
//
//	public void setMode(Integer mode) {
//		this.mode = mode;
//	}

}
