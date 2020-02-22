/**
 * Copyright (c) 2008, TARAMSA SA de CV
 * All rights reserved. Todos los derechos reservados.
 */
package com.tiempometa.pandora.ipicoreader;

/**
 * @author Gerardo Tasistro gtasistro@deuxbits.com
 * 
 */
public interface ReadParser {
	void addListener(ReadListener listener);

	void removeListener(ReadListener listener);

	void addCharacters(int numBytes, byte[] buffer);

}
