/**
 * Copyright (c) 2008, TARAMSA SA de CV
 * All rights reserved. Todos los derechos reservados.
 */
package com.tiempometa.pandora.ipicoreader;

/**
 * @author Gerardo Tasistro gtasistro@deuxbits.com
 * 
 */
public interface ReadListener {
	void notifyChipReads(String rfid);
}
