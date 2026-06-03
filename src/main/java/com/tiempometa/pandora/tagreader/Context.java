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
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.timing.SettingsHandler;
import com.tiempometa.timing.local.LocalDataContext;
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
	private static final Logger logger = LogManager.getLogger(Context.class);
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

	/**
	 * Connects to saturnPandora's SOAP webservices, validates event pairing, and
	 * injects the X-Base-DB header on both proxies.
	 *
	 * @throws DbAuthorizationRequiredException if the local H2 has no base_db_name
	 *         yet — caller must show the authorisation dialog and call
	 *         {@link LocalDataContext#setBaseDbName(String)} on acceptance
	 * @throws DbNameMismatchException if the local base_db_name does not match
	 *         the database name reported by saturnPandora
	 * @throws Exception on connectivity failures
	 */
	public static void initWebserviceClients()
			throws DbAuthorizationRequiredException, DbNameMismatchException, Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(RegistrationWebservice.class);
		String wsAddress = "http://" + serverAddress + ":9000/registrationClient";
		logger.info("Connecting webservice to {}", wsAddress);
		factory.setAddress(wsAddress);
		registrationWebservice = (RegistrationWebservice) factory.create();
		registrationWebservice.findByTag("TAG"); // connectivity probe
		String zoneIdString = registrationWebservice.getZoneId();
		logger.info("Setting zone id to {}", zoneIdString);
		setZoneId(ZoneId.of(zoneIdString));

		factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(ResultsWebservice.class);
		wsAddress = "http://" + serverAddress + ":9000/resultsClient";
		logger.info("Connecting webservice to {}", wsAddress);
		factory.setAddress(wsAddress);
		resultsWebservice = (ResultsWebservice) factory.create();
		logger.info("Webservice clients created");

		// Event pairing check — must happen before any data exchange
		String pandoraDbName = registrationWebservice.getDatabaseName();
		String localBaseDb   = LocalDataContext.getBaseDbName();
		logger.info("Pandora db='{}', local base_db_name='{}'", pandoraDbName, localBaseDb);

		if (localBaseDb == null) {
			// Not paired yet — caller shows authorisation popup
			throw new DbAuthorizationRequiredException(pandoraDbName);
		}
		if (!localBaseDb.equals(pandoraDbName)) {
			// Paired to a different database — reject sync
			throw new DbNameMismatchException(localBaseDb, pandoraDbName);
		}

		// Inject X-Base-DB on both proxies so every call carries the token
		injectBaseDbHeader(registrationWebservice, localBaseDb);
		injectBaseDbHeader(resultsWebservice, localBaseDb);
		logger.info("Connected to Pandora db '{}', X-Base-DB header set", pandoraDbName);
	}

	@SuppressWarnings("unchecked")
	private static void injectBaseDbHeader(Object proxy, String baseDbName) {
		Map<String, List<String>> headers = new HashMap<>();
		headers.put("X-Base-DB", Collections.singletonList(baseDbName));
		((BindingProvider) proxy).getRequestContext()
				.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
	}

	public static void setApplication(JPandoraApplication app) {
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
		settings = new SettingsHandler("/tagreader.properties");
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

	public static boolean isWebserviceConnected() {
		return resultsWebservice != null;
	}

	public static ResultsWebservice getResultsWebservice() {
		return resultsWebservice;
	}

	public static RegistrationWebservice getRegistrationWebservice() {
		return registrationWebservice;
	}

	/**
	 * Returns checkpoint names for reader panel combos. Uses the webservice when
	 * connected; falls back to distinct checkPoint values from the local H2 laps
	 * table when offline.
	 */
	public static List<String> getCheckPointNames() {
		if (isWebserviceConnected()) {
			return resultsWebservice.getCheckPointNames();
		}
		return LocalDataContext.getCheckPointNames();
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
