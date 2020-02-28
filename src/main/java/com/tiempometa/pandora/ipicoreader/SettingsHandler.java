/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author gtasi
 *
 */
public class SettingsHandler {

	public static final String PANDORA_SERVER_PROPERTIES = "/ipicoreader.properties";
	private Logger logger = Logger.getLogger(SettingsHandler.class);
	private Properties properties = new Properties();
	private String propertiesPath = System.getProperty("user.home") + "/.tiempometa";

	public String getSetting(String setting) {
		return properties.getProperty(setting);
	}

	public String getSetting(String setting, String defaultValue) {
		return properties.getProperty(setting, defaultValue);
	}

	public void init() throws IOException {
		File propertiesPathFile = new File(propertiesPath);
		if (!propertiesPathFile.exists()) {
			propertiesPathFile.mkdir();
		}
		String propertiesFilename = propertiesPath + PANDORA_SERVER_PROPERTIES;
		File propertiesFile = new File(propertiesFilename);
		if (!propertiesFile.exists()) {
			propertiesFile.createNewFile();
		}
		FileReader propertiesReader = new FileReader(propertiesFile);
		properties.load(propertiesReader);
		propertiesReader.close();
		logger.debug("Loaded property names " + properties.stringPropertyNames());
	}

	public void setSetting(String setting, String value) {
		if (value == null) {
			properties.setProperty(setting, "");
		} else {
			properties.setProperty(setting, value);
		}
	}

	public void flush() throws IOException {
		String propertiesFilename = propertiesPath + PANDORA_SERVER_PROPERTIES;
		FileWriter writer = new FileWriter(new File(propertiesFilename));
		properties.store(writer, "----");
		writer.close();
	}

}
