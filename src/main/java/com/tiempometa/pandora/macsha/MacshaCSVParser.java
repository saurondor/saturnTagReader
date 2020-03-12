/**
 * 
 */
package com.tiempometa.pandora.macsha;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * @author gtasi
 *
 */
public class MacshaCSVParser {
	private CSVFormat format;
	private CSVParser parser;

	public MacshaCSVParser() {
		super();
		format = CSVFormat.newFormat(";".toCharArray()[0]).withQuote("\"".toCharArray()[0]).withIgnoreEmptyLines(true)
				.withFirstRecordAsHeader();
	}

	public List<CSVRecord> parse(InputStream inputStream) throws IOException {
		parser = CSVParser.parse(inputStream, Charset.defaultCharset(), format);
		return parser.getRecords();
	}

}
