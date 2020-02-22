/**
 * Copyright (c) 2008, TARAMSA SA de CV
 * All rights reserved. Todos los derechos reservados.
 */
package com.tiempometa.pandora.ipicoreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * @author Gerardo Tasistro gtasistro@deuxbits.com
 * 
 */
public class SerialReader implements Runnable, SerialPortEventListener {

	private static final Logger log = Logger.getLogger(SerialReader.class);
	private String commPort = "";

	private int msgStatus = 1;

	static final char SOH = 1;
	static final char ENQ = 5;
	static final char EOT = 4;
	static final char ETX = 3;
	static final char ACK = 6;
	static final char NAK = 21;

	private CommPortIdentifier portId;
	private CommPortIdentifier saveportId;
	private Enumeration portList;
	private InputStream inputStream;
	private OutputStream outputStream;

	private SerialPort serialPort;
	private boolean runme = false;
	private Thread readThread;
	// private WTDataParser parser=new WTDataParser("A","1");
	private ReadParser parser = new RegistrationReadParser();

	/**
	 * @return the commPort
	 */
	public String getCommPort() {
		return commPort;
	}

	public List<String> getPorts() {
		List<String> ports = new ArrayList<String>();
		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				ports.add(portId.getName());
			}
		}
		return ports;
	}

	/**
	 * @param commPort the commPort to set
	 */
	public void setCommPort(String commPort) {
		this.commPort = commPort;
	}

	public void addDataListener(ReadListener listener) {
		parser.addListener(listener);
	}

	public void removeDataListener(ReadListener listener) {
		parser.removeListener(listener);
	}

	public boolean testPost(String commPort) {
		boolean portFound = false;
		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(commPort)) {
					portFound = true;
				}
			}
		}
		return portFound;
	}

	public boolean openPort(String commPort) throws Exception {
		boolean portOpened = false;
		// parse ports and if the default port is found, initialized the reader
		try {
			System.out.println("getting portlist");
			portList = CommPortIdentifier.getPortIdentifiers();
			System.out.println(portList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Missing rxtx Serial");
		}
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			System.out.println("Checking port " + portId.getName() + " - " + portId.getPortType());
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(commPort)) {
					if (log.isInfoEnabled()) {
						log.info("Found port: " + commPort);
					}
					portOpened = true;
					break;
				}
			}
		}
		if (portOpened) {
			try {
				serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
				inputStream = serialPort.getInputStream();
				serialPort.addEventListener(this);
				// activate the DATA_AVAILABLE notifier
				serialPort.notifyOnDataAvailable(true);
				// set port parameters
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
				// get the outputstream
				outputStream = serialPort.getOutputStream();
				// activate the OUTPUT_BUFFER_EMPTY notifier
				serialPort.notifyOnOutputEmpty(true);
			} catch (IOException e) {
				e.printStackTrace();
				portOpened = false;
			} catch (PortInUseException e) {
				e.printStackTrace();
				portOpened = false;
			} catch (TooManyListenersException e) {
				e.printStackTrace();
				portOpened = false;
			} catch (UnsupportedCommOperationException e) {
				e.printStackTrace();
				portOpened = false;
			} catch (Exception e) {
				e.printStackTrace();
				portOpened = false;
			}

		} else {
			log.error("port " + commPort + " not found.");
		}
		if (portOpened) {
			log.info("Creating thread...");
			readThread = new Thread(this);
			runme = true;
			readThread.start();
		}
		return portOpened;
	}

	public void closePort() {
		runme = false;
		serialPort.removeEventListener();
		serialPort.close();
	}

	/**
	 * 
	 */
	public SerialReader() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		log.info("Starting thread...");
		while (runme) {
			// String data=String.valueOf(SOH)+"A1"+String.valueOf(ENQ);
			// String ack=String.valueOf(SOH)+"A1"+String.valueOf(ACK);
			// String resetCommand="A1010"+String.valueOf(ETX);
			// String
			// reset=String.valueOf(SOH)+resetCommand+String.valueOf(crc(resetCommand));
			// String eot=String.valueOf(EOT);

			// try {
			// // write string to serial port
			// if (msgStatus==1) {
			// outputStream.write(data.getBytes());
			// msgStatus=0;
			// } else {
			// outputStream.write(ack.getBytes());
			// msgStatus=1;
			// }
			// } catch (IOException e) {}
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		log.info("Serial event");
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			// we get here if data has been received
			byte[] readBuffer = new byte[1024];
			try {
				readBuffer = new byte[1024];
				// read data
				int numBytes = 0;
				// while (inputStream.available() > 0) {
				numBytes = inputStream.read(readBuffer);
				// }
				// print data
				String result = new String(readBuffer);
				System.out.println("Read: " + numBytes);
				for (int counter = 0; counter < numBytes; counter++) {
					System.out.print(Integer.toHexString(Byte.valueOf(readBuffer[counter]).intValue()) + "-");
				}
				System.out.println("");
				parser.addCharacters(numBytes, readBuffer);
			} catch (IOException e) {
			}

			break;
		}
	}

}
