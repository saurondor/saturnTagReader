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
import javax.swing.*;
import com.tiempometa.pandora.cloud.tiempometa.JTiempoMetaCloudPanel;
import com.tiempometa.pandora.foxberry.tcpip.JFoxberryReaderPanel;
import com.tiempometa.pandora.foxberry.usb.JFoxberryUsbReaderPanel;
import com.tiempometa.pandora.ipicoreader.tcpip.JIpicoEliteReaderPanel;
import com.tiempometa.pandora.ipicoreader.usb.JIpicoUsbReaderPanel;
import com.tiempometa.pandora.macsha.JMacshaOcelotPanel;
import com.tiempometa.pandora.macsha.JMacshaReaderPanel;
import com.tiempometa.pandora.rfidtiming.JUltraReaderPanel;
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
}
