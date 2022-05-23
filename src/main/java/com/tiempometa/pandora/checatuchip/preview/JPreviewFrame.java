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
package com.tiempometa.pandora.checatuchip.preview;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.webservice.model.ParticipantRegistration;
import com.tiempometa.webservice.model.Registration;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JPreviewFrame extends JFrame {
	/**
	 * 
	 */
	private static final Logger logger = LogManager.getLogger(JPreviewFrame.class);
	private static final long serialVersionUID = 7699464539178157614L;
	private Registration registration = null;

	public JPreviewFrame() {
		initComponents();
		contentPanel.enableAgeField();
		contentPanel.enableSubeventField();
		contentPanel.enableCategoryField();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.checatuchip.preview.jpreview");
		contentPanel = new JContentPreviewPanel();
		topPanel = new JTopPreviewPanel();
		bottomPanel = new JBottomPreviewPanel();
		leftPanel = new JLeftPreviewPanel();
		rightPanel = new JRightPreviewPanel();

		//======== this ========
		setTitle(bundle.getString("JPreviewFrame.this.title"));
		setIconImage(new ImageIcon(getClass().getResource("/tiempometa_icon_large_alpha.png")).getImage());
		setBackground(Color.white);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//---- contentPanel ----
		contentPanel.setBackground(Color.white);
		contentPane.add(contentPanel, BorderLayout.CENTER);

		//---- topPanel ----
		topPanel.setBackground(Color.white);
		contentPane.add(topPanel, BorderLayout.NORTH);

		//---- bottomPanel ----
		bottomPanel.setBackground(Color.white);
		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		//---- leftPanel ----
		leftPanel.setBackground(Color.white);
		contentPane.add(leftPanel, BorderLayout.WEST);

		//---- rightPanel ----
		rightPanel.setBackground(Color.white);
		contentPane.add(rightPanel, BorderLayout.EAST);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JContentPreviewPanel contentPanel;
	private JTopPreviewPanel topPanel;
	private JBottomPreviewPanel bottomPanel;
	private JLeftPreviewPanel leftPanel;
	private JRightPreviewPanel rightPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public Registration getRegistration() {
		return registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
		contentPanel.setRegistration(registration);
	}

	public void prepareView() {
		logger.debug("Preparing view");
		contentPanel.disableFields();
		contentPanel.enableFields();
		setSize(getPreferredSize());
	}

	public void loadImages() {
		logger.debug("Loading preview window banners");
		topPanel.loadImage(Context.previewHelper.getSettings().getFieldTopBanner());
//		leftPanel.loadImage(Context.previewHelper.getSettings().getFieldLeftBanner());
//		rightPanel.loadImage(Context.previewHelper.getSettings().getFieldRightBanner());
		bottomPanel.loadImage(Context.previewHelper.getSettings().getFieldBottomBanner());
	}

	public void setTagReading(TagReading tagReading) {
		contentPanel.setTagReading(tagReading);
	}

	public void setRegistration(ParticipantRegistration participantRegistration) {
		contentPanel.setRegistration(participantRegistration);
	}
}
