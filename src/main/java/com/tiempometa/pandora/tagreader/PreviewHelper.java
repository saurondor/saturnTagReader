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

import java.awt.Frame;

import org.apache.log4j.Logger;

import com.tiempometa.pandora.checatuchip.preview.JPreviewFrame;
import com.tiempometa.pandora.checatuchip.preview.TagReading;
import com.tiempometa.webservice.model.ParticipantRegistration;
import com.tiempometa.webservice.model.Registration;


/**
 * @author Gerardo Esteban Tasistro Giubetic
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
