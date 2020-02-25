/**
 * Copyright (c) 2008, TARAMSA SA de CV
 * All rights reserved. Todos los derechos reservados.
 */
package com.tiempometa.pandora.ipicoreader;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Gerardo Tasistro gtasistro@deuxbits.com
 *
 */
public class RegistrationReadParser implements ReadParser {
	private static final Logger log = Logger.getLogger(RegistrationReadParser.class);
	private StringBuffer dataBuffer = new StringBuffer();
	private ChipReaderIPICO chipReader = new ChipReaderIPICO();
	private List<ReadListener> readListeners = new ArrayList<ReadListener>();

	@Override
	public void addListener(ReadListener listener) {
		readListeners.add(listener);
	}

	@Override
	public void removeListener(ReadListener listener) {
		readListeners.remove(listener);
	}

	private void signalListeners(String rfid) {
		for (ReadListener listener : readListeners) {
			listener.notifyChipReads(rfid);
		}
	}

	public String crc(String dataString) {
		int checkSum = 0;
		for (int i = 2; i < dataString.length() - 2; i++) {
			checkSum = (checkSum + (dataString.getBytes()[i]));
		}
		String crc = Integer.toHexString(checkSum);
		crc = crc.substring(crc.length() - 2);
		return crc;
	}

	public void parseBuffer() {
		if (log.isDebugEnabled()) {
			log.debug("Buffer " + dataBuffer.toString());
		}
		if (dataBuffer.length() >= 36) {
			// we have enough characters to process
			do {
				String data = dataBuffer.substring(0, 36);
				if (log.isDebugEnabled()) {
					log.debug("Data Length is " + data.length());
				}
				if (data.substring(0, 2).equals("aa")) {
					if (log.isDebugEnabled()) {
						log.debug("Proper start characters [aa]");
					}
					String crc = crc(data);
					if (log.isDebugEnabled()) {
						log.debug("CRC is " + crc + " vs " + data.substring(34));
					}
					if (crc.equals(data.substring(34))) {
						log.info("Proper string " + data);
						IpicoRead read = chipReader.readChip(data);
						if (log.isInfoEnabled()) {
							log.info("Chip ID " + read.getRfid());
						}
						signalListeners(read.getRfid());
					}
				}
				dataBuffer.delete(0, 36);
				if (log.isInfoEnabled()) {
					log.info("Deleteting 36 chars");
				}
			} while (dataBuffer.length() >= 36);
		}
		dataBuffer = new StringBuffer();
	}

	@Override
	public void addCharacters(int numBytes, byte[] dataBytes) {
		boolean carriageReturn = false;
		for (int i = 0; i < numBytes; i++) {
			String addValue = Integer.toHexString(dataBytes[i]);
			if (addValue.equals("d")) {
				if (log.isDebugEnabled()) {
					log.debug("Carriage Return");
				}
				carriageReturn = true;
			} else if (addValue.equals("a")) {
				if (log.isDebugEnabled()) {
					log.debug("Line Feed");
				}
				if (carriageReturn) {
					parseBuffer();
					carriageReturn = false;
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("DATA:" + dataBytes[i]);
				}

				dataBuffer.append((char) dataBytes[i]);
			}
		}

	}

	/**
	 * 
	 */
	public RegistrationReadParser() {
		// TODO Auto-generated constructor stub
	}

	public RegistrationReadParser(ReadListener listener) {
		readListeners.add(listener);
	}

//	@Override
//	public void addCharacters(int numBytes, byte[] buffer) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public void addListener(ReadListener listener) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public Object popRecording() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public void pushRecording(Object recording) {
//		// TODO Auto-generated method stub
//		
//	}

}
