/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

import com.tiempometa.pandora.ipicoreader.commands.IpicoCommand;

/**
 * @author gtasi
 *
 */
public class IpicoTcpClient implements Runnable, TelnetNotificationHandler {

	TelnetClient telnetClient = new TelnetClient();
	FTPClient ftpClient;
	OutputStream outStream;
	InputStream inStream;
	String ipAddress;
	Integer port = 9999;

	public void connect(String ipAddress) throws InvalidTelnetOptionException, IOException {
		this.ipAddress = ipAddress;

		TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
		EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
		SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);

		telnetClient.registerNotifHandler(this);
		telnetClient.addOptionHandler(ttopt);
		telnetClient.addOptionHandler(echoopt);
		telnetClient.addOptionHandler(gaopt);
		telnetClient.connect(ipAddress, this.port);
		outStream = telnetClient.getOutputStream();
		inStream = telnetClient.getInputStream();

	}

	public void disconnect() throws IOException {
		if (telnetClient.isConnected()) {
			telnetClient.disconnect();
		}
	}

	/**
	 * Sends IPICO command
	 * 
	 * @param command
	 * @throws IOException
	 */
	public void sendCommand(IpicoCommand command) throws IOException {
		command.sendCommand(outStream);
	}

	/**
	 * Returns the directory listing
	 * 
	 * @return
	 */
	public String[] listFiles() {
		return null;
	}

	/**
	 * Returns the content of the FS_LS log file
	 * 
	 * @return
	 */
	public String getLogFile() {
		return null;

	}

	/**
	 * Returns the content of the specified file
	 * 
	 * @param fileName
	 * @return
	 */
	public String getFile(String fileName) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.net.telnet.TelnetNotificationHandler#receivedNegotiation(
	 * int, int)
	 */
	public void receivedNegotiation(int negotiation_code, int option_code) {
		String command = null;
		if (negotiation_code == TelnetNotificationHandler.RECEIVED_DO) {
			command = "DO";
		} else if (negotiation_code == TelnetNotificationHandler.RECEIVED_DONT) {
			command = "DONT";
		} else if (negotiation_code == TelnetNotificationHandler.RECEIVED_WILL) {
			command = "WILL";
		} else if (negotiation_code == TelnetNotificationHandler.RECEIVED_WONT) {
			command = "WONT";
		}
		System.out.println("Received " + command + " for option code " + option_code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("Init listen backgroun process");
//		do {
//			
//		} while (ru)
		try {
			byte[] buff = new byte[1024];
			int ret_read = 0;

			do {
				ret_read = inStream.read(buff);
				if (ret_read > 0) {
					System.out.print(new String(buff, 0, ret_read));
				}
			} while (ret_read >= 0);
		} catch (IOException e) {
			System.err.println("Exception while reading socket:" + e.getMessage());
		}

		try {
			System.out.println("Disconnect");
			telnetClient.disconnect();
		} catch (IOException e) {
			System.err.println("Exception while closing telnet:" + e.getMessage());
		}

	}

}
