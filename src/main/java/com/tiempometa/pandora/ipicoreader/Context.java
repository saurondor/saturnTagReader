/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import java.io.IOException;

/**
 * @author gtasi
 *
 */
public class Context {
	public static PreviewHelper previewHelper = new PreviewHelper();
	public static SettingsHandler settings = null;

	public static void loadSettings() throws IOException {
		settings = new SettingsHandler();
		settings.init();
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
}
