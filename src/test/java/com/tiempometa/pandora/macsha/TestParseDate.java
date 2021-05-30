/**
 * 
 */
package com.tiempometa.pandora.macsha;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author gtasi
 *
 */
public class TestParseDate {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String date3 = "20200313 142424194";
		DateTimeFormatter version3DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmssSSS");
		DateTimeFormatter version4DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		String date4 = "2020-03-13T14:24:24.194";
		LocalDateTime.parse(date4, version4DateTimeFormatter);
		LocalDateTime.parse(date3, version3DateTimeFormatter);
	}

}
