/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import java.io.IOException;

import com.tiempometa.pandora.ipicoreader.commands.IpicoCommand;


/**
 * @author gtasi
 *
 */
public interface CommandResponseHandler {

	void handleCommandResponse(IpicoCommand command);

	void notifyCommException(IOException e);

}
