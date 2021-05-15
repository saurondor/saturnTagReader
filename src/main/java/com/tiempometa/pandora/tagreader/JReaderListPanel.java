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
import javax.swing.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.cloud.tiempometa.JTiempoMetaCloudPanel;
import com.tiempometa.pandora.foxberry.tcpip.JFoxberryReaderPanel;
import com.tiempometa.pandora.foxberry.usb.JFoxberryUsbReaderPanel;
import com.tiempometa.pandora.ipicoreader.JIpicoReaderPanel;
import com.tiempometa.pandora.ipicoreader.tcpip.JIpicoEliteReaderPanel;
import com.tiempometa.pandora.ipicoreader.usb.JIpicoUsbReaderPanel;
import com.tiempometa.pandora.macsha.JMacshaOcelotPanel;
import com.tiempometa.pandora.macsha.JMacshaReaderPanel;
import com.tiempometa.pandora.rfidtiming.JUltraReaderPanel;
import com.tiempometa.pandora.timingsense.JTSCollectorPanel;

import org.apache.log4j.Logger;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JReaderListPanel extends JPanel {

	private static final Logger logger = Logger.getLogger(JReaderListPanel.class);

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

	public void removeReader(JReaderPanel reader) {
		listPanel.remove(reader);
		listPanel.validate();
		listPanel.repaint();
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JPanel listPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;
	}

	public void addIpicoUsbReader() {
		JIpicoUsbReaderPanel reader = new JIpicoUsbReaderPanel(this);
		reader.setTagReadListener(tagReadListener);
		reader.setListPanel(this);
		logger.debug("Adding reader...");
		logger.debug("Component count " + listPanel.getComponentCount() + " before add");
		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Component count " + listPanel.getComponentCount() + " after add");
		logger.debug("Added reader!");
//		validate();
//		repaint();
	}

	public void addIpicoEliteReader() {
		JIpicoEliteReaderPanel reader = new JIpicoEliteReaderPanel(this);
		reader.setTagReadListener(tagReadListener);
		reader.setListPanel(this);
		logger.debug("Adding reader...");
		logger.debug("Component count " + listPanel.getComponentCount() + " before add");
		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Component count " + listPanel.getComponentCount() + " after add");
		logger.debug("Added reader!");
//		validate();
//		repaint();
		
	}

	public void addOne4AllReader() {
		JMacshaReaderPanel reader = new JMacshaReaderPanel(this);
		reader.setTagReadListener(tagReadListener);
//		reader.setListPanel(this);
		logger.debug("Adding reader...");
		logger.debug("Component count " + listPanel.getComponentCount() + " before add");
//		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Component count " + listPanel.getComponentCount() + " after add");
		logger.debug("Added reader!");
		
	}

	public void addUltraReader() {
		JUltraReaderPanel reader = new JUltraReaderPanel(this);
		reader.setTagReadListener(tagReadListener);
//		reader.setListPanel(this);
		logger.debug("Adding reader...");
		logger.debug("Component count " + listPanel.getComponentCount() + " before add");
//		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Component count " + listPanel.getComponentCount() + " after add");
		logger.debug("Added reader!");
	}

	public void addFoxberryReader() {
		JFoxberryReaderPanel reader = new JFoxberryReaderPanel(this);
		reader.setTagReadListener(tagReadListener);
//		reader.setListPanel(this);
		logger.debug("Adding reader...");
		logger.debug("Component count " + listPanel.getComponentCount() + " before add");
//		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Component count " + listPanel.getComponentCount() + " after add");
		logger.debug("Added reader!");
		
	}

	public void addFoxberryUSBReader() {
		JFoxberryUsbReaderPanel reader = new JFoxberryUsbReaderPanel(this);
		reader.setTagReadListener(tagReadListener);
//		reader.setListPanel(this);
		logger.debug("Adding reader...");
		logger.debug("Component count " + listPanel.getComponentCount() + " before add");
//		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Component count " + listPanel.getComponentCount() + " after add");
		logger.debug("Added reader!");
		
	}

	public void addTSCollector() {
		JTSCollectorPanel reader = new JTSCollectorPanel(this);
		reader.setTagReadListener(tagReadListener);
//		reader.setListPanel(this);
		logger.debug("Adding reader...");
		logger.debug("Component count " + listPanel.getComponentCount() + " before add");
//		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Component count " + listPanel.getComponentCount() + " after add");
		logger.debug("Added reader!");
		
	}

	public void addOcelot() {
		JMacshaOcelotPanel reader = new JMacshaOcelotPanel(this);
		reader.setTagReadListener(tagReadListener);
//		reader.setListPanel(this);
		logger.debug("Adding reader...");
		logger.debug("Component count " + listPanel.getComponentCount() + " before add");
//		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Component count " + listPanel.getComponentCount() + " after add");
		logger.debug("Added reader!");
		
	}

	public void addTiempoMetaCloud() {
		JTiempoMetaCloudPanel reader = new JTiempoMetaCloudPanel(this);
		reader.setTagReadListener(tagReadListener);
//		reader.setListPanel(this);
		logger.debug("Adding reader...");
		logger.debug("Component count " + listPanel.getComponentCount() + " before add");
//		reader.setTerminal(Integer.valueOf(1 + listPanel.getComponentCount()).toString());
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Component count " + listPanel.getComponentCount() + " after add");
		logger.debug("Added reader!");
		
	}
}
