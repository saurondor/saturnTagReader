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
package com.tiempometa.pandora.macsha.commands.one4all;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.macsha.commands.MacshaCommand;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class SetTimeCommand extends MacshaCommand {

	private static final Logger logger = LogManager.getLogger(SetTimeCommand.class);
	
	// Con el fin de configurar la fecha y hora interna del sistema, el host env�a
	// SETTIME;yyyy-MM-dd;HH:mm:ss<CrLf>. Esta operaci�n puede ser realizada
	// solamente en Stop mode.
	//
	// El One4All responde:
	// SETTIME;<Response><CrLf>
	//
	// Donde <Response> es:
	// En el �xito, la fecha y hora configurada con el siguiente formato:
	// yyyy-MM-dd;HH:mm:ss
	// STARTMODE, si el sistema esta en Start mode.
	// ERR, si ocurri� alg�n error.
	//
	// Ejemplo:
	// < SETTIME;2017-12-23;15:30:55<CrLf>
	// > SETTIME;2017-12-23;15:30:55<CrLf>

	private String timeString;

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.macshareader.commands.MacshaCommand#parseCommandRow(
	 * java.lang.String[])
	 */
	@Override
	public void parseCommandRow(String[] row) {
		if (row.length > 1) {

			switch (row[1]) {
			case RESPONSE_STARTMODE:
				setErrorCode(RESPONSE_STARTMODE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				timeString = row[1];
				setStatus(STATUS_OK);
				logger.debug("Reader time is :" + timeString);
				break;
			}
		} else {
			setErrorCode(RESPONSE_LENGTH_ERROR);
			setStatus(STATUS_ERROR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.macshareader.commands.MacshaCommand#sendCommand(java.
	 * io.OutputStream)
	 */
	@Override
	public void sendCommand(OutputStream dataOutputStream) throws IOException {
		Date dateTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		String date = dateFormat.format(dateTime);
		String time = timeFormat.format(dateTime);
		String payload = "SETTIME;" + date + ";" + time + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

	public String getTimeString() {
		return timeString;
	}

}
