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
public class StartCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(StartCommand.class);

	// Start.
	// Para comenzar la lectura de los chips, el host env�a START<CrLf>
	//
	// El One4All responde:
	// START;<Response><CrLf>
	//
	// Donde <Response> es:
	// En el �xito, la fecha y hora del inicio de la sesi�n de cronometraje y el
	// nombre del archivo de backup de la sesi�n con el siguiente formato:
	// yyyyMMdd-HHmmss.csv
	// STARTMODE, si el sistema ya se encuentra en Start mode.
	// ERRANTENNAS, si no hay antenas conectadas al sistema.
	// ERRCONNECT, ERRSTART, ERR, si ocurre alg�n otro error durante el proceso de
	// inicio.
	//
	// Ejemplo:
	// < START<CrLf>
	// > START;20171223-153055.csv<CrLf>

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
		dataOutputStream.write("START\r\n".getBytes());
		dataOutputStream.flush();
	}

	public String getLogFileName() {
		return logFileName;
	}

}
