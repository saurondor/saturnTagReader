/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import java.io.Reader;
import java.util.List;


/**
 * Defines interface that decoders must implement to process a plain text datafeed through a TCP/IP port
 * 
 * @author Gerardo Tasistro
 *
 */
public interface ChipReadDecoder {

	/**
	 * Indicates which port to connect for data feed
	 * 
	 * @return
	 */
	Integer dataPort();
	
	/**
	 * Indicates the character to use as line delimiter, usually \n
	 * @return
	 */
	String lineDelimiter();

	
	List<IpicoRead> readFile(Reader fileReader, DataLoadProperties loadProperties);
	
	IpicoRead readChip(String chipReadRow, DataLoadProperties loadProperties);

	IpicoRead readChip(String chipReadRow);

}
