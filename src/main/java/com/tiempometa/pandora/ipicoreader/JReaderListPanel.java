/*
 * Created by JFormDesigner on Sun Nov 17 07:55:43 CST 2019
 */

package com.tiempometa.pandora.ipicoreader;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

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

		// ======== this ========
		setLayout(new BorderLayout());

		// ======== listPanel ========
		{
			listPanel.setAutoscrolls(true);
			listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		}
		add(listPanel, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	public void addReader() {
		JIpicoReaderPanel reader = new JIpicoReaderPanel();
		reader.setTagReadListener(tagReadListener);
		logger.debug("Adding reader...");
		logger.debug("Component count " + listPanel.getComponentCount() + " before add");
		listPanel.add(reader);
		listPanel.validate();
		listPanel.repaint();
		logger.debug("Component count " + listPanel.getComponentCount() + " after add");
		logger.debug("Added reader!");
//		validate();
//		repaint();
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JPanel listPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;
	}
}
