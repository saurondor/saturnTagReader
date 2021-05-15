/*
 * Copyright (c) 2019 Gerardo Esteban Tasistro Giubetic
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.tiempometa.pandora.ipicoreader.commands;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Gerardo Esteban Tasistro Giubetic
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
