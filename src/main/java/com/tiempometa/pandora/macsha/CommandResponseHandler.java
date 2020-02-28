/**
 * 
 */
package com.tiempometa.pandora.macsha;

import java.io.IOException;

import com.tiempometa.pandora.macsha.commands.MacshaCommand;

/**
 * @author gtasi
 *
 */
public interface CommandResponseHandler {

	void handleCommandResponse(MacshaCommand command);

	void notifyCommException(IOException e);

}
