/**
 * 
 */
package com.tiempometa.pandora.tagreader;

import java.awt.Frame;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.tiempometa.pandora.checatuchip.preview.JPreviewFrame;
import com.tiempometa.pandora.checatuchip.preview.TagReading;
import com.tiempometa.webservice.model.ParticipantRegistration;
import com.tiempometa.webservice.model.Registration;


/**
 * @author gtasi
 *
 */
public class PreviewHelper {

	private static final Logger logger = Logger.getLogger(PreviewHelper.class);
	JPreviewFrame frame = new JPreviewFrame();
	private boolean maximized = false;
	private boolean visible = false;
	private boolean decorated = true;
	private Registration registration = null;
	private PreviewFrameSettings settings = new PreviewFrameSettings();

	public void showPreviewWindow() {
		frame.setVisible(true);
		visible = true;
		frame.prepareView();
		frame.loadImages();
	}

	public void hidePreviewWindow() {
		frame.setVisible(false);
		visible = false;
	}

	public void deocratePreviewWindow() {
		frame.setUndecorated(false);
		decorated = true;
	}

	public void maximizePreviewWindow() {
		frame.setExtendedState(frame.getExtendedState() | frame.MAXIMIZED_BOTH);
		maximized = true;
	}

	public void restorePreviewWindow() {
		frame.setState(Frame.NORMAL);
		maximized = false;
	}

	public void undeocratePreviewWindow() {
		frame.dispose();
		frame = new JPreviewFrame();
		frame.setUndecorated(true);
		frame.setVisible(true);
		decorated = false;
	}

	public boolean isMaximized() {
		return maximized;
	}

	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isDecorated() {
		return decorated;
	}

	public void setDecorated(boolean decorated) {
		this.decorated = decorated;
	}

	public Registration getRegistration() {
		return registration;
	}

	public void setRegistration(Registration registration) {
		logger.debug("Setting registration to " + registration);
		this.registration = registration;
		frame.setRegistration(registration);
	}

	public PreviewFrameSettings getSettings() {
		return settings;
	}

	public void setSettings(PreviewFrameSettings settings) {
		this.settings = settings;
	}

	public void setTagReading(TagReading tagReading) {
		frame.setTagReading(tagReading);
		
	}

	public void setRegistration(ParticipantRegistration participantRegistration) {
		frame.setRegistration(participantRegistration);
		
	}

}
