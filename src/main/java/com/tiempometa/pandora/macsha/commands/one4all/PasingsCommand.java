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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.macsha.commands.MacshaCommand;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class PasingsCommand extends MacshaCommand {

	private static final Logger logger = LogManager.getLogger(PasingsCommand.class);

	// Con el fin de recibir el n?mero de pasadas y los ?ltimos ocho chips le?dos de
	// la sesi?n de cronometraje actual, el host env?a PASSINGS<CrLf>.
	//
	// El One4All responde:
	// PASSINGS;<Number>;<Last eight><CrLf>
	//
	// Donde:
	// <Number>, es el n?mero de pasadas actuales de la sesi?n de cronometraje.
	// <Last eight>, es:
	// los ?ltimos ocho chips le?dos con el formato: MC00001;MC00002;MC00003;?
	// ERR, si ocurre alg?n error durante el proceso.
	//
	// Ejemplo:
	// < PASSINGS<CrLf>
	// >
	// PASSINGS;190;MC00001;MC00002;MC00003;MC00017;MC00022;MC00033;MC00012;MC00190<CrLf>

	private String[] passings;
	private String passCount;

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
			passCount = row[1];
			passings = new String[row.length - 2];
			for (int i = 2; i < row.length; i++) {
				logger.debug("Passing " + row[i]);
				passings[i - 1] = row[i];
			}
			setStatus(STATUS_OK);
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
		dataOutputStream.write("PASSINGS\r\n".getBytes());
		dataOutputStream.flush();
	}

	public String[] getPassings() {
		return passings;
	}

	public String getPassCount() {
		return passCount;
	}

}
