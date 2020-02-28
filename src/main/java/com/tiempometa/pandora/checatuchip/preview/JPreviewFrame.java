/*
 * Created by JFormDesigner on Sun Apr 07 10:53:18 CDT 2019
 */

package com.tiempometa.pandora.checatuchip.preview;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.*;
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
	private static final Logger logger = Logger.getLogger(JPreviewFrame.class);
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
