/**
 * 
 */
package com.tiempometa.pandora.ipicoreader.commands;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gtasi
 *
 */
public class GetTimeCommand extends IpicoCommand {
	SimpleDateFormat dFormat = new SimpleDateFormat("yyMMddHH:mm:ss");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.ipicoreader.commands.IpicoCommand#parseCommandRow(java
	 * .lang.String[])
	 */
	@Override
	public void parseCommandRow(String[] row) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.ipicoreader.commands.IpicoCommand#sendCommand(java.io.
	 * OutputStream)
	 */
	@Override
	public void sendCommand(OutputStream dataOutputStream) throws IOException {
		dataOutputStream.write((byte) 10);
		String markerCommand = "getdate";
		System.out.println("CLEAR command :" + markerCommand);
		dataOutputStream.write(markerCommand.getBytes());
		dataOutputStream.write((byte) 10);
		dataOutputStream.flush();
	}

}
