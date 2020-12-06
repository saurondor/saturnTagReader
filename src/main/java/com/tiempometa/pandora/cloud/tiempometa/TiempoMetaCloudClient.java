/**
 * 
 */
package com.tiempometa.pandora.cloud.tiempometa;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.tiempometa.api.DataRequestException;
import com.tiempometa.api.DataUploadException;
import com.tiempometa.api.TiempoMetaClient;
import com.tiempometa.api.TiempoMetaClientImpl;
import com.tiempometa.api.model.Event;
import com.tiempometa.api.model.TiempoMetaReading;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class TiempoMetaCloudClient implements Runnable {
	private static final Logger logger = Logger.getLogger(TiempoMetaCloudClient.class);
	private TagReadListener tagReadListener;
	private boolean connected = false;

	private String apiKey;
	private String passPhrase;
	private boolean runMe = true;
	private Date lastCreateTime = new Date(74, 0, 1);
	private Event event;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private Long delayMillis = 5000l;

	private TiempoMetaClient client;

	public boolean connect(String apiKey, String passPhrase)
			throws ClientProtocolException, IOException, DataRequestException {
		client = new TiempoMetaClientImpl(apiKey, passPhrase);
		event = client.getEvent();
		if (event == null) {
			return false;
		}
		this.apiKey = apiKey;
		this.passPhrase = passPhrase;
		return true;
	}

	public boolean disconnect() {
		runMe = false;
		return true;
	}

	public void registerTagReadListener(TagReadListener listener) {
		tagReadListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (runMe) {
			try {
				logger.debug("Getting reads from " + dateFormat.format(lastCreateTime));
				List<TiempoMetaReading> readings = null;
				readings = client.getTagReads(dateFormat.format(lastCreateTime));
				logger.debug("Got tag reads. Count: " + readings.size());
				List<RawChipRead> rawReads = new ArrayList<RawChipRead>();
				if (readings.size() > 0) {
					for (TiempoMetaReading cloudRead : readings) {
						RawChipRead reading = new RawChipRead();
						reading.setCheckPoint(cloudRead.getCheckpoint());
						reading.setTimeMillis(cloudRead.getTime());
						reading.setRfidString(cloudRead.getRfid_string());
						if (reading.getTimeMillis() == null) {

						} else {
							reading.setTime(Instant.ofEpochMilli(reading.getTimeMillis()).atZone(Context.getZoneId())
									.toLocalDateTime());
						}
						rawReads.add(reading);
						try {
							synchronized (this) {
								lastCreateTime = new Date(dateFormat.parse(cloudRead.getCreated_at()).getTime() + 1000);
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
//					lastCreateTime = new Date(rawReads.get(rawReads.size()-1).getTimeMillis());
					logger.debug("Current last create time: " + lastCreateTime.toGMTString());
				}
				tagReadListener.notifyTagReads(rawReads);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.getMessage());
			} catch (DataRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	protected Event getEvent() {
		return event;
	}

	public void rewindAll() {
		synchronized (this) {
			lastCreateTime = new Date(74, 0, 1);
		}

	}

	public void clearTags() {
//		try {
//			client.clearTags();
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (DataUploadException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
