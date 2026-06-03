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
import com.tiempometa.timing.model.Bib;
import com.tiempometa.timing.model.Category;
import com.tiempometa.timing.model.Country;
import com.tiempometa.timing.model.Event;
import com.tiempometa.timing.model.Lap;
import com.tiempometa.timing.model.Participant;
import com.tiempometa.timing.model.Registration;
import com.tiempometa.timing.model.RfidTagEquivalence;
import com.tiempometa.timing.model.Route;
import com.tiempometa.timing.model.Subevent;
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

	/**
	 * Downloads the full event snapshot from saturnPandora and seeds local H2.
	 * Must be called after a successful {@link #initWebserviceClients()}.
	 * Runs in the calling thread — wrap in SwingWorker for UI responsiveness.
	 */
	public static void downloadEventSnapshot() {
		if (registrationWebservice == null) {
			logger.warn("downloadEventSnapshot called before webservice connection — skipping");
			return;
		}
		logger.info("Downloading event snapshot from Saturno...");

		// Event
		com.tiempometa.webservice.model.Event wsEvent = registrationWebservice.getEvent();
		Event modelEvent = wsEventToModel(wsEvent);
		LocalDataContext.replicateEvent(modelEvent);

		// Subevents
		List<com.tiempometa.webservice.model.Subevent> wsSubevents =
				registrationWebservice.getSubevents(wsEvent);
		List<Subevent> modelSubevents = new ArrayList<>();
		for (com.tiempometa.webservice.model.Subevent ws : wsSubevents) {
			modelSubevents.add(wsSubeventToModel(ws));
		}
		LocalDataContext.replicateSubevents(modelSubevents);

		// Categories
		List<com.tiempometa.webservice.model.Category> wsCategories =
				registrationWebservice.getAllCategories();
		List<Category> modelCategories = new ArrayList<>();
		for (com.tiempometa.webservice.model.Category ws : wsCategories) {
			modelCategories.add(wsCategoryToModel(ws));
		}
		LocalDataContext.replicateCategories(modelCategories);

		// Routes
		List<com.tiempometa.webservice.model.Route> wsRoutes = registrationWebservice.getRoutes();
		List<Route> modelRoutes = new ArrayList<>();
		for (com.tiempometa.webservice.model.Route ws : wsRoutes) {
			modelRoutes.add(wsRouteToModel(ws));
		}
		LocalDataContext.replicateRoutes(modelRoutes);

		// Laps
		List<com.tiempometa.webservice.model.Lap> wsLaps = registrationWebservice.getLaps();
		List<Lap> modelLaps = new ArrayList<>();
		for (com.tiempometa.webservice.model.Lap ws : wsLaps) {
			modelLaps.add(wsLapToModel(ws, modelRoutes));
		}
		LocalDataContext.replicateLaps(modelLaps);

		// Bibs
		List<com.tiempometa.webservice.model.Bib> wsBibs = registrationWebservice.getAllBibs();
		List<Bib> modelBibs = new ArrayList<>();
		for (com.tiempometa.webservice.model.Bib ws : wsBibs) {
			modelBibs.add(wsBibToModel(ws));
		}
		LocalDataContext.replicateBibs(modelBibs);

		// Registrations + Participants
		List<com.tiempometa.webservice.model.Registration> wsRegs =
				registrationWebservice.getSnapshotRegistrations();
		List<Registration> modelRegs = new ArrayList<>();
		for (com.tiempometa.webservice.model.Registration ws : wsRegs) {
			modelRegs.add(wsRegistrationToModel(ws, modelBibs));
		}
		LocalDataContext.replicateRegistrations(modelRegs);
		LocalDataContext.replicateParticipants(modelRegs);

		// RFID tag equivalences
		List<com.tiempometa.webservice.model.RfidTagEquivalence> wsEqs =
				registrationWebservice.getRfidTagEquivalences();
		List<RfidTagEquivalence> modelEqs = new ArrayList<>();
		for (com.tiempometa.webservice.model.RfidTagEquivalence ws : wsEqs) {
			RfidTagEquivalence eq = new RfidTagEquivalence();
			eq.setId(ws.getId());
			eq.setBib(ws.getBib());
			eq.setAltRfidString(ws.getAltRfidString());
			eq.setRfidString(ws.getRfidString());
			modelEqs.add(eq);
		}
		LocalDataContext.replicateRfidTagEquivalences(modelEqs);

		logger.info("Event snapshot downloaded: {} subevents, {} categories, {} routes, {} laps, "
				+ "{} bibs, {} registrations, {} rfid equivalences",
				modelSubevents.size(), modelCategories.size(), modelRoutes.size(), modelLaps.size(),
				modelBibs.size(), modelRegs.size(), modelEqs.size());
	}

	private static Event wsEventToModel(com.tiempometa.webservice.model.Event ws) {
		Event m = new Event();
		m.setId(ws.getId());
		m.setTitle(ws.getTitle());
		m.setApi_key(ws.getApi_key());
		m.setHash_id(ws.getHash_id());
		m.setQuota(ws.getQuota());
		m.setQuotaHash(ws.getQuotaHash());
		m.setPassphrase(ws.getPassphrase());
		m.setMacAddress(ws.getMacAddress());
		m.setStartTimeMillis(ws.getStartTimeMillis());
		m.setZoneIdString(ws.getZoneIdString());
		if (ws.getDateForAge() != null)
			m.setDateForAge(ws.getDateForAge().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		return m;
	}

	private static Subevent wsSubeventToModel(com.tiempometa.webservice.model.Subevent ws) {
		Subevent m = new Subevent();
		m.setId(ws.getId());
		m.setTitle(ws.getTitle());
		m.setSubTitle(ws.getSubTitle());
		m.setLocation(ws.getLocation());
		m.setStartMillis(ws.getStartMillis());
		m.setMinParticipants(ws.getMinParticipants());
		m.setMaxParticipants(ws.getMaxParticipants());
		m.setMinFemale(ws.getMinFemale());
		m.setMinMale(ws.getMinMale());
		m.setTeamMode(ws.getTeamMode());
		if (ws.getDateForAge() != null)
			m.setDateForAge(ws.getDateForAge().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		if (ws.getSubeventDate() != null)
			m.setSubeventDate(ws.getSubeventDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		return m;
	}

	private static Category wsCategoryToModel(com.tiempometa.webservice.model.Category ws) {
		Category m = new Category();
		m.setId(ws.getId());
		m.setTitle(ws.getTitle());
		m.setKey(ws.getKey());
		m.setGender(ws.getGender());
		m.setMinFemale(ws.getMinFemale());
		m.setMinMale(ws.getMinMale());
		m.setMinParticipants(ws.getMinParticipants());
		m.setMaxParticipants(ws.getMaxParticipants());
		m.setMinAge(ws.getMinAge());
		m.setMaxAge(ws.getMaxAge());
		m.setPriority(ws.getPriority());
		m.setStartMillis(ws.getStartMillis());
		m.setPricePositions(ws.getPricePositions());
		m.setIdField0(ws.getIdField0());
		m.setIdField1(ws.getIdField1());
		m.setIdField2(ws.getIdField2());
		m.setIdField3(ws.getIdField3());
		m.setIdField4(ws.getIdField4());
		if (ws.getSubevent() != null) {
			Subevent sub = new Subevent();
			sub.setId(ws.getSubevent().getId());
			m.setSubevent(sub);
		}
		return m;
	}

	private static Route wsRouteToModel(com.tiempometa.webservice.model.Route ws) {
		Route m = new Route();
		m.setId(ws.getId());
		m.setTitle(ws.getTitle());
		m.setDistance(ws.getDistance());
		m.setDistanceMinTolerance(ws.getDistanceMinTolerance());
		m.setDistanceMaxTolerance(ws.getDistanceMaxTolerance());
		if (ws.getDistanceHandling() != null) m.setDistanceHandling(ws.getDistanceHandling().byteValue());
		m.setMinActivityDistance(ws.getMinActivityDistance());
		m.setTotalTime(ws.getTotalTime());
		m.setTotalTimeMinTolerance(ws.getTotalTimeMinTolerance());
		m.setTotalTimeMaxTolerance(ws.getTotalTimeMaxTolerance());
		m.setMinActivityTime(ws.getMinActivityTime());
		m.setMinRunTimeMillis(ws.getMinRunTimeMillis());
		if (ws.getUnits() != null) m.setUnits(ws.getUnits().byteValue());
		m.setDistanceCoef(ws.getDistanceCoef());
		if (ws.getSpeedCalculation() != null) m.setSpeedCalculation(ws.getSpeedCalculation().byteValue());
		if (ws.getCalculationType() != null) m.setCalculationType(ws.getCalculationType().byteValue());
		m.setTrials(ws.getTrials());
		return m;
	}

	private static Lap wsLapToModel(com.tiempometa.webservice.model.Lap ws, List<Route> routes) {
		Lap m = new Lap();
		m.setId(ws.getId());
		if (ws.getRouteId() != null) {
			for (Route r : routes) {
				if (r.getId() != null && r.getId().longValue() == ws.getRouteId()) {
					m.setRoute(r);
					break;
				}
			}
		}
		m.setLapType(ws.getLapType());
		m.setPhaseTitle(ws.getPhaseTitle());
		m.setPhase(ws.getPhase());
		m.setLapTitle(ws.getLapTitle());
		m.setLap(ws.getLap());
		m.setCheckPoint(ws.getCheckPoint());
		m.setCheckPointCount(ws.getCheckPointCount());
		m.setSequenceId(ws.getSequenceId());
		m.setLapDistance(ws.getLapDistance());
		if (ws.getLapUnits() != null) m.setLapUnits(ws.getLapUnits().byteValue());
		m.setLapDistanceCoef(ws.getLapDistanceCoef());
		if (ws.getLapSpeedCalculation() != null) m.setLapSpeedCalculation(ws.getLapSpeedCalculation().byteValue());
		m.setPhaseDistance(ws.getPhaseDistance());
		m.setPhaseTotalTime(ws.getPhaseTotalTime());
		if (ws.getPhaseUnits() != null) m.setPhaseUnits(ws.getPhaseUnits().byteValue());
		m.setPhaseDistanceCoef(ws.getPhaseDistanceCoef());
		if (ws.getPhaseSpeedCalculation() != null) m.setPhaseSpeedCalculation(ws.getPhaseSpeedCalculation().byteValue());
		if (ws.getObserve() != null) m.setObserve(ws.getObserve().byteValue());
		if (ws.getDisplayable() != null) m.setDisplayable(ws.getDisplayable().byteValue());
		m.setMinPhaseTimeMillis(ws.getMinPhaseTimeMillis());
		m.setMinLapTimeMillis(ws.getMinLapTimeMillis());
		m.setMaxPhaseTimeMillis(ws.getMaxPhaseTimeMillis());
		m.setMaxLapTimeMillis(ws.getMaxLapTimeMillis());
		m.setMetering(ws.getMetering());
		return m;
	}

	private static Bib wsBibToModel(com.tiempometa.webservice.model.Bib ws) {
		Bib m = new Bib();
		m.setId(ws.getId());
		m.setBib(ws.getBib());
		m.setBibId(ws.getBibId());
		m.setRfidString(ws.getRfidString());
		m.setAssignedStatus(ws.getAssignedStatus());
		m.setPaymentStatus(ws.getPaymentStatus());
		m.setPrepaidToken(ws.getPrepaidToken());
		return m;
	}

	private static Registration wsRegistrationToModel(
			com.tiempometa.webservice.model.Registration ws, List<Bib> bibs) {
		Registration m = new Registration();
		m.setId(ws.getId());
		m.setGender(ws.getGender());
		m.setConfirmationCode(ws.getConfirmationCode());
		m.setPassCode(ws.getPassCode());
		m.setPassType(ws.getPassType());
		m.setInvoiceType(ws.getInvoiceType());
		m.setRegistrationStatus(ws.getRegistrationStatus());
		m.setPassPrintCount(ws.getPassPrintCount());
		m.setCertificatePrintCount(ws.getCertificatePrintCount());
		m.setExtra1(ws.getExtra1());
		m.setExtra2(ws.getExtra2());
		m.setExtra3(ws.getExtra3());
		m.setExtra4(ws.getExtra4());
		m.setExtra5(ws.getExtra5());
		m.setCertificateType(ws.getCertificateType());
		m.setTotalPaid(ws.getTotalPaid());
		m.setVatApplied(ws.getVatApplied());
		m.setRemoteId(ws.getRemoteId());
		m.setPassPhrase(ws.getPassPhrase());
		m.setUpdatedAt(ws.getUpdatedAt());
		m.setCreatedAt(ws.getCreatedAt());
		m.setUploadedAt(ws.getUploadedAt());
		m.setRemoteUpdatedAt(ws.getRemoteUpdatedAt());
		m.setRemoteCreatedAt(ws.getRemoteCreatedAt());
		m.setTeam(ws.getTeam());
		m.setDq(ws.getDq());
		m.setDqReason(ws.getDqReason());
		m.setLapSequence(ws.getLapSequence());
		m.setCourseStatus(ws.getCourseStatus());
		m.setObserveCount(ws.getObserveCount());
		m.setLapCount(ws.getLapCount());
		m.setLapAverage(ws.getLapAverage());
		m.setLapStdDev(ws.getLapStdDev());
		m.setChipResult(ws.getChipResult());
		m.setChipResultMillis(ws.getChipResultMillis());
		m.setChipPace(ws.getChipPace());
		m.setChipGeneralPos(ws.getChipGeneralPos());
		m.setChipCategoryPos(ws.getChipCategoryPos());
		m.setChipGenderPos(ws.getChipGenderPos());
		m.setOfficialResult(ws.getOfficialResult());
		m.setOfficialResultMillis(ws.getOfficialResultMillis());
		m.setOfficialPace(ws.getOfficialPace());
		m.setOfficialGeneralPos(ws.getOfficialGeneralPos());
		m.setOfficialCategoryPos(ws.getOfficialCategoryPos());
		m.setOfficialGenderPos(ws.getOfficialGenderPos());
		m.setStartTimeMillis(ws.getStartTimeMillis());
		m.setResultStatus(ws.getResultStatus());
		m.setRegistrationCategoryId(ws.getRegistrationCategoryId());
		m.setRegistrationSubeventId(ws.getRegistrationSubeventId());
		if (ws.getCategory() != null) {
			Category cat = new Category();
			cat.setId(ws.getCategory().getId());
			m.setCategory(cat);
		}
		List<Participant> modelParticipants = new ArrayList<>();
		if (ws.getParticipants() != null) {
			for (com.tiempometa.webservice.model.Participant wsp : ws.getParticipants()) {
				modelParticipants.add(wsParticipantToModel(wsp, bibs));
			}
		}
		m.setParticipants(modelParticipants);
		return m;
	}

	private static Participant wsParticipantToModel(
			com.tiempometa.webservice.model.Participant ws, List<Bib> bibs) {
		Participant m = new Participant();
		m.setId(ws.getId());
		m.setFirstName(ws.getFirstName());
		m.setLastName(ws.getLastName());
		m.setMiddleName(ws.getMiddleName());
		m.setGender(ws.getGender());
		m.setAge(ws.getAge());
		m.setEmail(ws.getEmail());
		m.setCedula(ws.getCedula());
		m.setCompanyName(ws.getCompanyName());
		m.setNameHash(ws.getNameHash());
		m.setJobTitle(ws.getJobTitle());
		m.setTitle(ws.getTitle());
		m.setExtra1(ws.getExtra1());
		m.setExtra2(ws.getExtra2());
		m.setExtra3(ws.getExtra3());
		m.setExtra4(ws.getExtra4());
		m.setExtra5(ws.getExtra5());
		if (ws.getBirthDate() != null)
			m.setBirthDate(ws.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		if (ws.getAddress() != null) {
			m.setAddress1(ws.getAddress1());
			m.setAddress2(ws.getAddress2());
			m.setCity(ws.getCity());
			m.setState(ws.getState());
			m.setCountry(ws.getCountry());
			m.setDistrict(ws.getDistrict());
			m.setZipCode(ws.getZipCode());
			m.setHomePhone(ws.getHomePhone());
			m.setWorkPhone(ws.getWorkPhone());
			m.setCellPhone(ws.getCellPhone());
		}
		if (ws.getBib() != null) {
			for (Bib b : bibs) {
				if (b.getId() != null && b.getId().equals(ws.getBib().getId())) {
					m.setBib(b);
					break;
				}
			}
		}
		return m;
	}

//	public static ZoneId getZoneId() {
//		return zoneId;
//	}
//
//	public static void setZoneId(ZoneId zoneId) {
//		Context.zoneId = zoneId;
//	}

}
