/**
 * 
 */
package com.tiempometa.pandora.cloud.tiempometa;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.tiempometa.api.DataRequestException;
import com.tiempometa.api.TiempoMetaClient;
import com.tiempometa.api.TiempoMetaClientImpl;
import com.tiempometa.api.model.TiempoMetaReading;
import com.tiempometa.pandora.tagreader.TagReadListener;

/**
 * @author gtasi
 *
 */
public class TiempoMetaCloudClient implements Runnable {
	private TagReadListener tagReadListener;
	private boolean connected = false;

	private String apiKey;
	private String passPhrase;
	private boolean runMe = true;
	private Date lastCreateTime;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	private TiempoMetaClient client;

	public boolean connect(String apiKey, String passPhrase) {
		client = new TiempoMetaClientImpl(apiKey, passPhrase);
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
		while(runMe){
			try {
				List<TiempoMetaReading> readings = client.getTagReads(dateFormat.format(lastCreateTime));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DataRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
