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
public class SetTimeCommand extends IpicoCommand {
	SimpleDateFormat dFormat = new SimpleDateFormat("yyMMddHH:mm:ss");


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
		dataOutputStream.write("getdate".getBytes());
		dataOutputStream.write((byte) 10);
		dataOutputStream.flush();
		Date currentDate = new Date();
		long delay = currentDate.getTime() % 1000;
		System.out.println("DELAY :" + delay);
		System.out.println(currentDate.getTime());
		try {
			Thread.sleep(1000 - delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		currentDate = new Date();
		currentDate.setTime(currentDate.getTime() + 1000);
		System.out.println(currentDate.getTime());
		String newDate = "setdate." + dFormat.format(currentDate);
		dataOutputStream.write(newDate.getBytes());
		dataOutputStream.write((byte) 10);
		dataOutputStream.flush();
		dataOutputStream.write("getdate".getBytes());
		dataOutputStream.write((byte) 10);
		dataOutputStream.flush();

	}

}
