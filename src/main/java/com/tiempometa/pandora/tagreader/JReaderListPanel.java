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

import java.awt.*;
import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tiempometa.pandora.cloud.tiempometa.JTiempoMetaCloudPanel;
import com.tiempometa.pandora.foxberry.tcpip.JFoxberryReaderPanel;
import com.tiempometa.pandora.foxberry.usb.JFoxberryUsbReaderPanel;
import com.tiempometa.pandora.ipicoreader.tcpip.JIpicoEliteReaderPanel;
import com.tiempometa.pandora.ipicoreader.usb.JIpicoUsbReaderPanel;
import com.tiempometa.pandora.macsha.JMacshaOcelotPanel;
import com.tiempometa.pandora.macsha.JMacshaReaderPanel;
import com.tiempometa.pandora.rfidtiming.JUltraReaderPanel;
import com.tiempometa.pandora.tagreader.config.FoxberryReaderPanelConfig;
import com.tiempometa.pandora.tagreader.config.FoxberryUsbReaderPanelConfig;
import com.tiempometa.pandora.tagreader.config.IpicoEliteReaderPanelConfig;
import com.tiempometa.pandora.tagreader.config.IpicoUsbReaderPanelConfig;
import com.tiempometa.pandora.tagreader.config.MacshaReaderPanelConfig;
import com.tiempometa.pandora.tagreader.config.ReaderPanelConfig;
import com.tiempometa.pandora.tagreader.config.TSCollectorReaderPanelConfig;
import com.tiempometa.pandora.tagreader.config.UltraReaderPanelConfig;
import com.tiempometa.pandora.timingsense.JTSCollectorPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JReaderListPanel extends JPanel {

	private static final Logger logger = LogManager.getLogger(JReaderListPanel.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 8216390465500099596L;
	private TagReadListener tagReadListener;

	public JReaderListPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		listPanel = new JPanel();

		//======== this ========
		setLayout(new BorderLayout());

		//======== listPanel ========
		{
			listPanel.setAutoscrolls(true);
			listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		}
		add(listPanel, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	public void removeReader(IReader reader) {
		listPanel.remove((Component) reader);
		listPanel.validate();
		listPanel.repaint();
	}

	public void disconnectAll() {
		for (Component c : listPanel.getComponents()) {
			if (c instanceof IReader) {
				((IReader) c).disconnect();
			}
		}
	}

	private void addReader(JReaderPanel reader) {
		reader.setTagReadListener(tagReadListener);
		logger.debug("Adding reader, before: " + listPanel.getComponentCount());
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Added reader, after: " + listPanel.getComponentCount());
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JPanel listPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;
		for (Component c : listPanel.getComponents()) {
			if (c instanceof JReaderPanel) {
				((JReaderPanel) c).setTagReadListener(tagReadListener);
			}
		}
	}

	public void addIpicoUsbReader() {
		JIpicoUsbReaderPanel reader = new JIpicoUsbReaderPanel(this);
		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		addReader(reader);
	}

	public void addIpicoEliteReader() {
		JIpicoEliteReaderPanel reader = new JIpicoEliteReaderPanel(this);
		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		addReader(reader);
	}

	public void addOne4AllReader() {
		addReader(new JMacshaReaderPanel(this));
	}

	public void addUltraReader() {
		addReader(new JUltraReaderPanel(this));
	}

	public void addFoxberryReader() {
		addReader(new JFoxberryReaderPanel(this));
	}

	public void addFoxberryUSBReader() {
		addReader(new JFoxberryUsbReaderPanel(this));
	}

	public void addTSCollector() {
		addReader(new JTSCollectorPanel(this));
	}

	public void addOcelot() {
		addReader(new JMacshaOcelotPanel(this));
	}

	public void addTiempoMetaCloud() {
		addReader(new JTiempoMetaCloudPanel(this));
	}

	private static final String READERS_CONFIG_PATH =
			System.getProperty("user.home") + "/.tiempometa/readers.json";

	public void saveReaderConfigs() {
		List<ReaderPanelConfig> configs = new ArrayList<>();
		for (Component c : listPanel.getComponents()) {
			if (c instanceof PersistableReaderPanel) {
				configs.add(((PersistableReaderPanel) c).getConfig());
			}
		}
		try (FileWriter w = new FileWriter(READERS_CONFIG_PATH)) {
			new Gson().toJson(configs, w);
			logger.info("Saved {} reader config(s)", configs.size());
		} catch (IOException e) {
			logger.warn("Could not save reader configs: {}", e.getMessage());
		}
	}

	public int restoreReaderConfigs() {
		File f = new File(READERS_CONFIG_PATH);
		if (!f.exists()) return 0;
		Gson gson = new Gson();
		int restored = 0;
		try (FileReader r = new FileReader(f)) {
			JsonArray arr = JsonParser.parseReader(r).getAsJsonArray();
			for (JsonElement el : arr) {
				String type = el.getAsJsonObject().get("type").getAsString();
				ReaderPanelConfig cfg = deserializeConfig(gson, el, type);
				if (cfg == null) continue;
				JReaderPanel panel = createPanel(cfg);
				if (panel == null) continue;
				((PersistableReaderPanel) panel).applyConfig(cfg);
				addReader(panel);
				restored++;
			}
			logger.info("Restored {} reader(s) from config", restored);
		} catch (Exception e) {
			logger.warn("Could not restore reader configs: {}", e.getMessage());
		}
		return restored;
	}

	private ReaderPanelConfig deserializeConfig(Gson gson, JsonElement el, String type) {
		switch (type) {
			case MacshaReaderPanelConfig.TYPE:
				return gson.fromJson(el, MacshaReaderPanelConfig.class);
			case UltraReaderPanelConfig.TYPE:
				return gson.fromJson(el, UltraReaderPanelConfig.class);
			case FoxberryReaderPanelConfig.TYPE:
				return gson.fromJson(el, FoxberryReaderPanelConfig.class);
			case FoxberryUsbReaderPanelConfig.TYPE:
				return gson.fromJson(el, FoxberryUsbReaderPanelConfig.class);
			case IpicoEliteReaderPanelConfig.TYPE:
				return gson.fromJson(el, IpicoEliteReaderPanelConfig.class);
			case IpicoUsbReaderPanelConfig.TYPE:
				return gson.fromJson(el, IpicoUsbReaderPanelConfig.class);
			case TSCollectorReaderPanelConfig.TYPE:
				return gson.fromJson(el, TSCollectorReaderPanelConfig.class);
			default:
				logger.warn("Unknown reader type '{}' in config — skipped", type);
				return null;
		}
	}

	private JReaderPanel createPanel(ReaderPanelConfig cfg) {
		switch (cfg.type) {
			case MacshaReaderPanelConfig.TYPE:
				return new JMacshaReaderPanel(this);
			case UltraReaderPanelConfig.TYPE:
				return new JUltraReaderPanel(this);
			case FoxberryReaderPanelConfig.TYPE:
				return new JFoxberryReaderPanel(this);
			case FoxberryUsbReaderPanelConfig.TYPE:
				return new JFoxberryUsbReaderPanel(this);
			case IpicoEliteReaderPanelConfig.TYPE:
				return new JIpicoEliteReaderPanel(this);
			case IpicoUsbReaderPanelConfig.TYPE:
				return new JIpicoUsbReaderPanel(this);
			case TSCollectorReaderPanelConfig.TYPE:
				return new JTSCollectorPanel(this);
			default:
				return null;
		}
	}
}
