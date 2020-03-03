/*
 * Created by JFormDesigner on Sun Nov 17 07:55:43 CST 2019
 */

package com.tiempometa.pandora.tagreader;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.ipicoreader.JIpicoReaderPanel;
import com.tiempometa.pandora.ipicoreader.TagReadListener;
import com.tiempometa.pandora.ipicoreader.tcpip.JIpicoEliteReaderPanel;
import com.tiempometa.pandora.ipicoreader.usb.JIpicoUsbReaderPanel;
import com.tiempometa.pandora.macsha.JMacshaReaderPanel;

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
		// TODO Auto-generated method stub
		
	}

	public void addFoxberryReader() {
		// TODO Auto-generated method stub
		
	}

	public void addFoxberryUSBReader() {
		// TODO Auto-generated method stub
		
	}

	public void addTSCollector() {
		// TODO Auto-generated method stub
		
	}
}
