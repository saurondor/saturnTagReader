/**
 * 
 */
package com.tiempometa.pandora.ipicoreader.commands;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author gtasi
 *
 */
public class ClearHistoryCommand extends IpicoCommand {

	/* (non-Javadoc)
	 * @see com.tiempometa.pandora.ipicoreader.commands.IpicoCommand#parseCommandRow(java.lang.String[])
	 */
	@Override
	public void parseCommandRow(String[] row) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.tiempometa.pandora.ipicoreader.commands.IpicoCommand#sendCommand(java.io.OutputStream)
	 */
	@Override
	public void sendCommand(OutputStream dataOutputStream) throws IOException {
		dataOutputStream.write((byte) 10);
		String markerCommand = "clear_history";
		System.out.println("CLEAR command :" + markerCommand);
		dataOutputStream.write(markerCommand.getBytes());
		dataOutputStream.write((byte) 10);
		dataOutputStream.flush();
	}

}
