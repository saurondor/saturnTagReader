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
package com.tiempometa.pandora.macsha.commands.ocelot;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.macsha.commands.MacshaCommand;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class StopCommand extends MacshaCommand {

	private static final Logger logger = LogManager.getLogger(StopCommand.class);

//	Para dar comienzo con la lectura de chips, debemos enviar la siguiente cadena:
//	> “Start:”
//	Posibles respuestas del One4All:
//	> “MODO-REMOTE-OFF_[ID]”: El One4All no se encuentra en modo operación. No fue
//	posible dar Start al sistema.
//	> “OPERATION-MODE-STARTED_[ID]”: El One4All ya se encuentra en Start.
//	> “START-OK_[ID]”: Start OK.
//	Donde:
//	[ID] = Se corresponde con el ID configurado en el sistema.
//	Para dar fin a la lectura de chips, debemos enviar la siguiente cadena:
//	> “Stop:”
//	Posibles respuestas del One4All:
//	> “STOP-OK_[ID]”: Stop OK.

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
			case RESPONSE_OK:
				setStatus(STATUS_OK);
				break;
			case RESPONSE_ERRCLOSE:
				setErrorCode(RESPONSE_ERRCLOSE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERRSTOP:
				setErrorCode(RESPONSE_ERRSTOP);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
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
		dataOutputStream.write("Stop:\r\n".getBytes());
		dataOutputStream.flush();
	}

}
