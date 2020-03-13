/**
 * 
 */
package com.tiempometa.pandora.macsha.commands.one4all;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.tiempometa.pandora.macsha.commands.MacshaCommand;

/**
 * @author gtasi
 *
 */
public class GetPassingsCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(GetPassingsCommand.class);

	// Con el fin de recibir los datos completos de las pasadas del archivo de
	// sesi�n actual, el host env�a GETPASSINGS;<StartPassing>;<EndPassing><CrLf>.
	//
	// Donde:
	// <StartPassing>, es el n�mero inicial de las pasadas a enviar.
	// <EndPassing>, es el n�mero final de las pasadas a enviar.
	//
	// Ejemplo:
	// < GETPASSINGS;15;15<CrLf>
	// > 15;MC;00001;2017-04-23;13:30:45.492;3;1;Z1y39<CrLf>
	// 16;MC;00002;2017-04-23;13:30:46.543;4;1;O90ut<CrLf>
	// 17;MC;00015;2017-04-23;13:30:46.993;4;1;kle23<CrLf>
	// <CrLf>

	private Integer firstPassing;
	private Integer lastPassing;

	public GetPassingsCommand(Integer firstPassing, Integer lastPassing) {
		super();
		this.firstPassing = firstPassing;
		this.lastPassing = lastPassing;
	}

	public GetPassingsCommand() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.macshareader.commands.MacshaCommand#parseCommandRow(
	 * java.lang.String[])
	 */
	@Override
	public void parseCommandRow(String[] row) {
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
		String payload = "GETPASSINGS;" + firstPassing + ";" + lastPassing + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

}
