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
package com.tiempometa.pandora.tagreader;

import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tiempometa.timing.model.Country;
import com.tiempometa.timing.model.Event;
import com.tiempometa.webservice.RegistrationWebservice;
import com.tiempometa.webservice.ResultsWebservice;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class Context extends com.tiempometa.timing.Context {
//	private static ApplicationContext ctx = null;
	private static final Logger logger = Logger.getLogger(Context.class);
	public static PreviewHelper previewHelper = new PreviewHelper();
	public static SettingsHandler settings = null;
	private static JPandoraApplication application;
	private static RegistrationWebservice registrationWebservice;
	private static ResultsWebservice resultsWebservice;
	private static String serverAddress = null;
//	private static ZoneId zoneId = null;

	public static void saveWorkingDirectory(String workingDirectory) throws IOException {
		Context.saveSetting(PandoraSettings.EVENT_PATH, workingDirectory);
		Context.flushSettings();
	}

	public static File getWorkingDirectory() throws IOException {
		String path = Context.loadSetting(PandoraSettings.EVENT_PATH, null);
		if (path == null) {
			return null;
		}
		return new File(path);
	}

	public static void initWebserviceClients() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(RegistrationWebservice.class);
		String wsAddress = "http://" + serverAddress + ":9000/registrationClient";
		logger.info("Connecting webservice to " + wsAddress);
		factory.setAddress(wsAddress);
		registrationWebservice = (RegistrationWebservice) factory.create();
		logger.info("Registration client created");
		registrationWebservice.findByTag("TAG");
		String zoneIdString = registrationWebservice.getZoneId();
		logger.info("Setting zone id to " + zoneIdString);
		setZoneId(ZoneId.of(zoneIdString));
		logger.info("Set timezone to " + zoneIdString);
		factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(ResultsWebservice.class);
		wsAddress = "http://" + serverAddress + ":9000/resultsClient";
		logger.info("Connecting webservice to " + wsAddress);
		factory.setAddress(wsAddress);
		resultsWebservice = (ResultsWebservice) factory.create();
		logger.info("Results client created");
	}

	public static void setApplication(JReaderFrame app) {
		application = app;
	}

	public static void openEvent() throws Exception {
		logger.info("!!! OPEN EVENT");
		connectDatabase(databaseName, databaseUsername, databasePassword);
		// open version table and validate version
		if (Context.validateVersion()) {
			List<Country> countries = Context.registrationHelper.getCountryList();
			if (countries.size() == 0) {
				populateCountryCatalogue();
			}
			Event event = Context.registrationHelper.getEvent();
			if (event == null) {
				logger.warn("Event is null. Will init event");
				event = new Event();
				event.setTitle("Nuevo evento");
				event.setZoneIdString(ZoneId.systemDefault().toString());
				Context.registrationHelper.save(event);
			}
			try {
				logger.info("Setting Zone ID to:" + event.getZoneIdString());
				if (event.getZoneIdString() == null) {
					Context.setZoneId(ZoneId.of(Context.loadSetting(PandoraSettings.PREFERRED_TIMEZONE,
							ZoneId.systemDefault().toString())));
				} else {
					Context.setZoneId(ZoneId.of(event.getZoneIdString()));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw e;
			}
			setApplicationTitle(event.getTitle());
			logger.info("Opened event " + event.getTitle());
		}
	}

	public static void setApplicationTitle(String title) {
		application.setEventTitle(title);
		application.refreshTitle();
	}

	public static void loadSettings() throws IOException {
		settings = new SettingsHandler();
		settings.init();
		serverAddress = Context.loadSetting(PandoraSettings.SERVER_IP, "127.0.0.1");
//		databaseName = Context.loadSetting(PandoraSettings.DB_NAME, "pandora_test");
//		databasePassword = Context.loadSetting(PandoraSettings.DB_PASSWORD, "");
//		databaseUsername = Context.loadSetting(PandoraSettings.DB_USERNAME, "root");
	}

	public static void saveReaderAddress(String address) throws IOException {
		Context.saveSetting("readerAddress", address);
		Context.flushSettings();
	}

	public static String loadReaderAddress() {
		String address = Context.loadSetting("readerAddress", "192.168.1.10");
		return address;
	}

	public static void saveServerAddress(String address) throws IOException {
		Context.saveSetting("serverAddress", address);
		Context.flushSettings();
	}

	public static String loadServerAddress() {
		serverAddress = Context.loadSetting(PandoraSettings.SERVER_IP, "127.0.0.1");
		return serverAddress;
	}

	public static String loadSetting(String setting, String defaultValue) {
		return settings.getSetting(Context.class.getPackage().getName() + "." + setting, defaultValue);
	}

	public static void saveSetting(String setting, String value) {
		settings.setSetting(Context.class.getPackage().getName() + "." + setting, value);
	}

	public static void flushSettings() throws IOException {
		settings.flush();
	}

	public static boolean isApplicationCloseable() {
		return true;
	}

	private static boolean validateVersion() {
		// TODO Auto-generated method stub
		return true;
	}

	public static void notifyDatabaseChanged() {
		// TODO Auto-generated method stub

	}

	public static void setDatabaseNameSetting(String selectedItem) throws IOException {
		// TODO Auto-generated method stub

	}

	public static String getServerAddress() {
		return serverAddress;
	}

	public static ResultsWebservice getResultsWebservice() {
		return resultsWebservice;
	}

	public static RegistrationWebservice getRegistrationWebservice() {
		return registrationWebservice;
	}

	public static void setServerAddress(String serverAddress) throws IOException {
		Context.serverAddress = serverAddress;
		Context.saveSetting(PandoraSettings.SERVER_IP, Context.serverAddress);
		Context.flushSettings();
	}

//	public static ZoneId getZoneId() {
//		return zoneId;
//	}
//
//	public static void setZoneId(ZoneId zoneId) {
//		Context.zoneId = zoneId;
//	}

}
