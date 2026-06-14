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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.timing.local.InfoDto;
import com.tiempometa.pandora.webservice.api.ParticipantDetailDto;
import com.tiempometa.timing.SettingsHandler;
import com.tiempometa.timing.local.LocalDataContext;
import com.tiempometa.timing.local.SnapshotDto;
import com.tiempometa.timing.local.SnapshotSeeder;
import com.tiempometa.timing.model.Country;
import com.tiempometa.timing.model.Event;
import com.tiempometa.timing.model.RawChipRead;

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
	private static boolean restConnected = false;
	private static String serverAddress = null;

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
	 * Connects to saturnPandora's REST API, validates event pairing, and sets
	 * the zone ID. Replaces the old SOAP proxy initialisation.
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
		restConnected = false;
		logger.info("Connecting to Saturn REST API at {}:9001", serverAddress);
		InfoDto info = SaturnRestClient.getInfo(serverAddress);
		if (info == null) {
			throw new Exception("No se pudo conectar a Saturno en " + serverAddress + ":9001");
		}
		setZoneId(ZoneId.of(info.getZoneId()));
		logger.info("Zone ID: {}", info.getZoneId());

		String pandoraDbName = info.getDatabaseName();
		String localBaseDb   = LocalDataContext.getBaseDbName();
		logger.info("Pandora db='{}', local base_db_name='{}'", pandoraDbName, localBaseDb);

		if (localBaseDb == null) {
			throw new DbAuthorizationRequiredException(pandoraDbName);
		}
		if (!localBaseDb.equals(pandoraDbName)) {
			throw new DbNameMismatchException(localBaseDb, pandoraDbName);
		}

		restConnected = true;
		logger.info("Connected to Saturn db '{}' via REST", pandoraDbName);
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
		return restConnected;
	}

	/**
	 * Returns checkpoint names for reader panel combos. Always uses local H2
	 * (seeded at connect time from the snapshot), which avoids a round-trip
	 * per panel open and works offline.
	 */
	public static List<String> getCheckPointNames() {
		return LocalDataContext.getCheckPointNames();
	}

	/**
	 * Looks up participants by RFID tag. Uses the live REST API when connected
	 * (checa tu chip / letterboard use case requires current data); falls back
	 * to the local H2 snapshot when offline.
	 * Sets restConnected=false and refreshes the title if REST is unreachable.
	 */
	public static List<ParticipantDetailDto> findParticipantByRfid(String rfidString) {
		if (restConnected) {
			List<ParticipantDetailDto> result =
					SaturnRestClient.findParticipantByRfid(serverAddress, rfidString);
			if (result != null) return result;
			markRestDisconnected();
		}
		return lookupFromLocalH2(rfidString);
	}

	/**
	 * Looks up participants by bib number. REST-first when connected; falls back
	 * to the local H2 snapshot when offline or when REST returns null (error).
	 * Sets restConnected=false and refreshes the title if REST is unreachable.
	 */
	public static List<ParticipantDetailDto> findParticipantByBib(String bib) {
		if (restConnected) {
			List<ParticipantDetailDto> result = SaturnRestClient.findParticipantByBib(serverAddress, bib);
			if (result != null) return result;
			markRestDisconnected();
		}
		return lookupFromLocalH2ByBib(bib);
	}

	/**
	 * Pushes a batch of model reads to saturnPandora via REST POST /api/reads.
	 * Sets restConnected=false and refreshes the title on failure.
	 *
	 * @return {@code true} if the push succeeded
	 */
	public static boolean pushRawReads(List<RawChipRead> reads) {
		boolean ok = SaturnRestClient.pushRawReads(serverAddress, reads);
		if (!ok) markRestDisconnected();
		return ok;
	}

	/**
	 * Closes the current local H2 and opens a new one named after {@code dbName}.
	 * The old file is left intact on disk — its reads are preserved for audit.
	 * Also records {@code dbName} as the pairing in the new H2.
	 */
	public static void reinitLocalH2(String dbName) throws IOException {
		LocalDataContext.close();
		String newPath = System.getProperty("user.home") + "/.tiempometa/databases/" + dbName;
		int port = Integer.parseInt(loadSetting(PandoraSettings.LOCAL_H2_PORT,
				String.valueOf(PandoraSettings.LOCAL_H2_PORT_DEFAULT)));
		LocalDataContext.init(port, newPath);
		LocalDataContext.setBaseDbName(dbName);
		logger.info("Local H2 switched to {}.mv.db", newPath);
	}

	private static void markRestDisconnected() {
		if (restConnected) {
			restConnected = false;
			logger.warn("REST connection lost during operation — switching to offline mode");
			if (application != null) {
				SwingUtilities.invokeLater(application::refreshTitle);
			}
		}
	}

	private static List<ParticipantDetailDto> lookupFromLocalH2(String rfidString) {
		List<Object[]> rows = LocalDataContext.findParticipantRowsByRfid(rfidString);
		logger.info("H2 rfid lookup '{}' → {} row(s)", rfidString, rows.size());
		return rowsToDto(rows);
	}

	private static List<ParticipantDetailDto> lookupFromLocalH2ByBib(String bib) {
		List<Object[]> rows = LocalDataContext.findParticipantRowsByBib(bib);
		logger.info("H2 bib lookup '{}' → {} row(s)", bib, rows.size());
		return rowsToDto(rows);
	}

	private static List<ParticipantDetailDto> rowsToDto(List<Object[]> rows) {
		if (rows.isEmpty()) return Collections.emptyList();
		List<ParticipantDetailDto> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			ParticipantDetailDto dto = new ParticipantDetailDto();
			dto.setQrCode(row[0] != null ? row[0].toString() : null);
			dto.setRfidString(row[1] != null ? row[1].toString() : null);
			dto.setNumber(row[2] != null ? row[2].toString() : null);
			dto.setFullName(buildFullName(row[3], row[4]));
			dto.setCategory(row[5] != null ? row[5].toString() : null);
			dto.setSubeventTitle(row[6] != null ? row[6].toString() : null);
			dto.setGender(row[8] != null ? row[8].toString() : null);
			dto.setAge(row[9] != null ? Integer.valueOf(row[9].toString()) : null);
			dto.setProvince(row[10] != null ? row[10].toString() : null);
			dto.setTeam(row[11] != null ? row[11].toString() : null);
			dto.setIdField0(row[12] != null ? row[12].toString() : null);
			dto.setBirthDateString(row[13] != null ? row[13].toString() : null);
			result.add(dto);
		}
		return result;
	}

	private static String buildFullName(Object first, Object last) {
		String f = first != null ? first.toString().trim() : "";
		String l = last  != null ? last.toString().trim()  : "";
		return (f + " " + l).trim();
	}

	public static void setServerAddress(String serverAddress) throws IOException {
		Context.serverAddress = serverAddress;
		Context.saveSetting(PandoraSettings.SERVER_IP, Context.serverAddress);
		Context.flushSettings();
	}

	/**
	 * Downloads the full event snapshot from saturnPandora's REST API and seeds
	 * local H2 via {@link SnapshotSeeder}.
	 * Must be called after a successful {@link #initWebserviceClients()}.
	 * Runs in the calling thread — wrap in SwingWorker for UI responsiveness.
	 */
	public static void downloadEventSnapshot() {
		if (!restConnected) {
			logger.warn("downloadEventSnapshot called before REST connection — skipping");
			return;
		}
		logger.info("Downloading event snapshot from Saturno...");
		SnapshotDto snap = SaturnRestClient.downloadSnapshot(serverAddress);
		if (snap == null) {
			logger.error("Snapshot download failed");
			return;
		}
		SnapshotSeeder.seed(snap);
	}

//	public static ZoneId getZoneId() { ... }
//	public static void setZoneId(ZoneId zoneId) { ... }

}
