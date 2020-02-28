/**
 * 
 */
package com.tiempometa.pandora.tagreader;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;

import org.apache.log4j.Logger;

import com.tiempometa.pandora.ipicoreader.JPandoraApplication;
import com.tiempometa.pandora.ipicoreader.PandoraSettings;
import com.tiempometa.pandora.ipicoreader.PreviewHelper;
import com.tiempometa.pandora.ipicoreader.SettingsHandler;
import com.tiempometa.timing.model.Country;
import com.tiempometa.timing.model.Event;

/**
 * @author gtasi
 *
 */
public class Context extends com.tiempometa.timing.Context {
	private static final Logger logger = Logger.getLogger(Context.class);
	public static PreviewHelper previewHelper = new PreviewHelper();
	public static SettingsHandler settings = null;
	private static JPandoraApplication application;

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
		String address = Context.loadSetting("serverAddress", "127.0.0.1");
		return address;
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

}
