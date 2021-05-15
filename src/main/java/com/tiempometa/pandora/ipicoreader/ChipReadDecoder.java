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
