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
public abstract class IpicoCommand {

	public abstract void parseCommandRow(String[] row);

	public abstract void sendCommand(OutputStream dataOutputStream) throws IOException;
}
