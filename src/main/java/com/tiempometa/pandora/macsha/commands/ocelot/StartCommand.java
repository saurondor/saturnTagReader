/**
 * 
 */
package com.tiempometa.pandora.macsha.commands.ocelot;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.tiempometa.pandora.macsha.commands.MacshaCommand;

/**
 * @author gtasi
 *
 */
public class StartCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(StartCommand.class);

//	Para dar comienzo con la lectura de chips, debemos enviar la siguiente cadena:
//		> “Start:”
//		Posibles respuestas del One4All:
//		> “MODO-REMOTE-OFF_[ID]”: El One4All no se encuentra en modo operación. No fue
//		posible dar Start al sistema.
//		> “OPERATION-MODE-STARTED_[ID]”: El One4All ya se encuentra en Start.
//		> “START-OK_[ID]”: Start OK.
//		Donde:
//		[ID] = Se corresponde con el ID configurado en el sistema.
//		Para dar fin a la lectura de chips, debemos enviar la siguiente cadena:
//		> “Stop:”
//		Posibles respuestas del One4All:
//		> “STOP-OK_[ID]”: Stop OK.

	private String logFileName;

	@Override
	public void parseCommandRow(String[] row) {
		if (row.length > 1) {
			switch (row[1]) {
			case RESPONSE_STARTMODE:
				setErrorCode(RESPONSE_STARTMODE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERRANTENNAS:
				setErrorCode(RESPONSE_ERRANTENNAS);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERRCONNECT:
				setErrorCode(RESPONSE_STARTMODE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERRSTART:
				setErrorCode(RESPONSE_ERRSTART);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				logFileName = row[1];
				setStatus(STATUS_OK);
				break;
			}
		} else {
			setErrorCode(RESPONSE_LENGTH_ERROR);
			setStatus(STATUS_ERROR);
		}
	}

	@Override
	public void sendCommand(OutputStream dataOutputStream) throws IOException {
		dataOutputStream.write("Start:\r\n".getBytes());
		dataOutputStream.flush();
	}

	public String getLogFileName() {
		return logFileName;
	}

}
