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
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.webservice.model.Bib;
import com.tiempometa.webservice.model.Participant;
import com.tiempometa.webservice.model.ParticipantRegistration;
import com.tiempometa.webservice.model.Registration;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JContentPreviewPanel extends JPanel {
	/**
	 * 
	 */
	private static final Logger logger = Logger.getLogger(JContentPreviewPanel.class);
	private static final long serialVersionUID = 5064577426057648244L;
	public static final Integer FIELD_NAME = 1;
	public static final Integer FIELD_SUBEVENT = 2;
	public static final Integer FIELD_CATEGORY = 3;
	public static final Integer FIELD_AGE = 4;
	public static final Integer FIELD_GENDER = 5;
	public static final Integer FIELD_BIRTHDATE = 6;
	public static final Integer FIELD_LABEL_COLOR = 7;
	public static final Integer FIELD_TEAM = 8;
	public static final Integer FIELD_PROCEDENCE = 9;
	private SimpleDateFormat dFormat = new SimpleDateFormat("d MMM yyyy");
	private Map<Integer, JFieldPanel> panelMap = new HashMap<Integer, JFieldPanel>();
	private Registration registration;

	public void enableNameField() {
		JFieldPanel panel = new JFieldPanel("Nombre");
		fieldsPanel.add(panel);
		panelMap.put(FIELD_NAME, panel);
	}

	public void enableSubeventField() {
		JFieldPanel panel = new JFieldPanel("Distancia");
		fieldsPanel.add(panel);
		panelMap.put(FIELD_SUBEVENT, panel);
	}

	public void enableCategoryField() {
		JFieldPanel panel = new JFieldPanel("Categoría");
		fieldsPanel.add(panel);
		panelMap.put(FIELD_CATEGORY, panel);
	}

	public void enableAgeField() {
		JFieldPanel panel = new JFieldPanel("Edad");
		fieldsPanel.add(panel);
		panelMap.put(FIELD_AGE, panel);
	}

	public void enableGenderField() {
		JFieldPanel panel = new JFieldPanel("Género");
		fieldsPanel.add(panel);
		panelMap.put(FIELD_GENDER, panel);
	}

	public void enableBirthdateField() {
		JFieldPanel panel = new JFieldPanel("Nacimiento");
		fieldsPanel.add(panel);
		panelMap.put(FIELD_BIRTHDATE, panel);
	}

	public void enableLabelColorField() {
		JFieldPanel panel = new JFieldPanel("Etiqueta");
		fieldsPanel.add(panel);
		panelMap.put(FIELD_LABEL_COLOR, panel);
	}

	public void enableTeamField() {
		JFieldPanel panel = new JFieldPanel("Equipo");
		fieldsPanel.add(panel);
		panelMap.put(FIELD_TEAM, panel);
	}

	public void enableProcedenceField() {
		JFieldPanel panel = new JFieldPanel("Procedencia");
		fieldsPanel.add(panel);
		panelMap.put(FIELD_PROCEDENCE, panel);
	}

	public void disableNameField() {
		JFieldPanel panel = panelMap.get(FIELD_NAME);
		removeField(panel);
	}

	/**
	 * @param panel
	 */
	private void removeField(JFieldPanel panel) {
		if (panel != null) {
			fieldsPanel.remove(panel);
		}
	}

	public void disableSubeventField() {
		JFieldPanel panel = panelMap.get(FIELD_SUBEVENT);
		removeField(panel);
	}

	public void disableCategoryField() {
		JFieldPanel panel = panelMap.get(FIELD_CATEGORY);
		removeField(panel);
	}

	public void disableAgeField() {
		JFieldPanel panel = panelMap.get(FIELD_AGE);
		removeField(panel);
	}

	public void disableGenderField() {
		JFieldPanel panel = panelMap.get(FIELD_GENDER);
		removeField(panel);
	}

	public void disableBirthdateField() {
		JFieldPanel panel = panelMap.get(FIELD_BIRTHDATE);
		removeField(panel);
	}

	public void disableLabelColorField() {
		JFieldPanel panel = panelMap.get(FIELD_LABEL_COLOR);
		removeField(panel);
	}

	public void disableTeamField() {
		JFieldPanel panel = panelMap.get(FIELD_TEAM);
		removeField(panel);
	}

	public void disableProcedenceField() {
		JFieldPanel panel = panelMap.get(FIELD_PROCEDENCE);
		removeField(panel);
	}

	public JContentPreviewPanel() {
		initComponents();
	}

	public void disableFields() {
		logger.debug("Disabling fields");
		Set<Integer> keys = panelMap.keySet();
		for (Integer key : keys) {
			logger.debug("Disabling field " + key);
			removeField(panelMap.get(key));
		}
		panelMap = new HashMap<Integer, JFieldPanel>();

		repaint();
		revalidate();
//		panelMap.remove(FIELD_NAME);
	}

	public void enableFields() {
		logger.debug("Enabling fields");
		for (int i = 1; i < 10; i++) {
			if (Context.previewHelper.getSettings().getFieldAgeLocation() == i) {
				logger.debug("Enabling age field at location " + i);
				enableAgeField();
			}
			if (Context.previewHelper.getSettings().getFieldBirthdateLocation() == i) {
				logger.debug("Enabling birthdate field at location " + i);
				enableBirthdateField();
			}
			if (Context.previewHelper.getSettings().getFieldCategoryLocation() == i) {
				logger.debug("Enabling category field at location " + i);
				enableCategoryField();
			}
			if (Context.previewHelper.getSettings().getFieldGenderLocation() == i) {
				logger.debug("Enabling gender field at location " + i);
				enableGenderField();
			}
			if (Context.previewHelper.getSettings().getFieldLabelColorLocation() == i) {
				logger.debug("Enabling label color field at location " + i);
				enableLabelColorField();
			}
			if (Context.previewHelper.getSettings().getFieldNameLocation() == i) {
				logger.debug("Enabling name field at location " + i);
				enableNameField();
			}
			if (Context.previewHelper.getSettings().getFieldProcedenceLocation() == i) {
				logger.debug("Enabling procedence field at location " + i);
				enableProcedenceField();
			}
			if (Context.previewHelper.getSettings().getFieldSubeventLocation() == i) {
				logger.debug("Enabling subevent field at location " + i);
				enableSubeventField();
			}
			if (Context.previewHelper.getSettings().getFieldTeamLocation() == i) {
				logger.debug("Enabling team field at location " + i);
				enableTeamField();
			}
		}
		repaint();
		revalidate();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.checatuchip.preview.jpreview");
		bibLabel = new JLabel();
		fieldsPanel = new JPanel();
		panel2 = new JFieldPanel();
		panel3 = new JFieldPanel();

		// ======== this ========
		setBackground(Color.white);
		setLayout(new FormLayout("5dlu, $lcgap, default, $lcgap, 159dlu, $lcgap, default:grow",
				"5dlu, 2*($lgap, default), $lgap, 89dlu:grow"));

		// ---- bibLabel ----
		bibLabel.setText(bundle.getString("JContentPreviewPanel.bibLabel.text"));
		bibLabel.setFont(new Font("Tahoma", Font.BOLD, 72));
		bibLabel.setForeground(Color.red);
		bibLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(bibLabel, CC.xywh(3, 3, 5, 1));

		// ======== fieldsPanel ========
		{
			fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));

			// ---- panel2 ----
			panel2.setBackground(Color.white);
			fieldsPanel.add(panel2);

			// ---- panel3 ----
			panel3.setBackground(Color.white);
			fieldsPanel.add(panel3);
		}
		add(fieldsPanel, CC.xywh(3, 5, 5, 1));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel bibLabel;
	private JPanel fieldsPanel;
	private JFieldPanel panel2;
	private JFieldPanel panel3;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public Registration getRegistration() {
		return registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
		populateFields();
	}

	private void populateFields() {
		if (registration == null) {
			bibLabel.setText("Sin datos");
			JFieldPanel panel = panelMap.get(FIELD_NAME);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_AGE);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_CATEGORY);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_GENDER);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_LABEL_COLOR);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_PROCEDENCE);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_SUBEVENT);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_TEAM);
			setFieldValue(panel, "");

		} else {
			if (registration.getParticipants().get(0).getBib() == null) {
				bibLabel.setText("-- Sin Número --");
			} else {
				bibLabel.setText(registration.getParticipants().get(0).getBib().getBib());
			}
			JFieldPanel panel = panelMap.get(FIELD_NAME);
			setFieldValue(panel, registration.getParticipants().get(0).getFullName());
			panel = panelMap.get(FIELD_AGE);
			if (registration.getParticipants().get(0).getAge() == null) {
				setFieldValue(panel, "No disponible");
			} else {
				setFieldValue(panel, registration.getParticipants().get(0).getAge().toString());
			}
			panel = panelMap.get(FIELD_BIRTHDATE);
			if (registration.getParticipants().get(0).getBirthDate() == null) {
				setFieldValue(panel, "No disponible");
			} else {
				setFieldValue(panel, dFormat.format(registration.getParticipants().get(0).getBirthDate()));
			}
			panel = panelMap.get(FIELD_CATEGORY);
			setFieldValue(panel, registration.getCategory().getTitle());
			panel = panelMap.get(FIELD_GENDER);
			setFieldValue(panel, registration.getParticipants().get(0).getGender());
			panel = panelMap.get(FIELD_LABEL_COLOR);
			setFieldValue(panel, registration.getCategory().getIdField0());
			panel = panelMap.get(FIELD_PROCEDENCE);
			setFieldValue(panel, registration.getParticipants().get(0).getState());
			panel = panelMap.get(FIELD_SUBEVENT);
			setFieldValue(panel, registration.getCategory().getSubevent().getTitle());
			panel = panelMap.get(FIELD_TEAM);
			setFieldValue(panel, registration.getTeam());

		}

	}

	/**
	 * @param panel
	 * @param value
	 */
	private void setFieldValue(JFieldPanel panel, String value) {
		if (panel != null) {
			if (value == null) {
				panel.setValue("");
			} else {
				panel.setValue(value);
			}
		}
	}

	public void setTagReading(TagReading tagReading) {
		Registration registration = new Registration();
		Participant participant = new Participant();
		participant.setFirstName(tagReading.getFirstName());
		participant.setLastName(tagReading.getLastName());
		participant.setMiddleName(tagReading.getMiddleName());

		Bib bib = new Bib();
		bib.setBib(tagReading.getBib());
		participant.setBib(bib);
		registration.getParticipants().add(participant);
		this.registration = registration;
	}

	public void setRegistration(ParticipantRegistration participantRegistration) {
		populateFields(participantRegistration);
	}

	private void populateFields(ParticipantRegistration participantRegistration) {
		if (participantRegistration == null) {
			logger.warn("Participan is null");
			bibLabel.setText("Sin datos");
			JFieldPanel panel = panelMap.get(FIELD_NAME);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_AGE);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_CATEGORY);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_GENDER);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_LABEL_COLOR);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_PROCEDENCE);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_SUBEVENT);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_TEAM);
			setFieldValue(panel, "");
			panel = panelMap.get(FIELD_BIRTHDATE);
			setFieldValue(panel, "");
		} else {
			logger.info("Participan is " + participantRegistration);
			if (participantRegistration.getBib() == null) {
				bibLabel.setText("-- Sin Número --");
			} else {
				bibLabel.setText(participantRegistration.getBib());
			}
			JFieldPanel panel = panelMap.get(FIELD_NAME);
			setFieldValue(panel, participantRegistration.getFullName());
			panel = panelMap.get(FIELD_AGE);
			if (participantRegistration.getAge() == null) {
				setFieldValue(panel, "No disponible");
			} else {
				setFieldValue(panel, participantRegistration.getAge().toString());
			}
			panel = panelMap.get(FIELD_BIRTHDATE);
			if (participantRegistration.getBirthDateString() == null) {
				setFieldValue(panel, "No disponible");
			} else {
				setFieldValue(panel, participantRegistration.getBirthDateString());
			}
			panel = panelMap.get(FIELD_CATEGORY);
			setFieldValue(panel, participantRegistration.getCategoryTitle());
			panel = panelMap.get(FIELD_GENDER);
			setFieldValue(panel, participantRegistration.getGender());
			panel = panelMap.get(FIELD_LABEL_COLOR);
			setFieldValue(panel, participantRegistration.getIdField0());
			panel = panelMap.get(FIELD_PROCEDENCE);
			setFieldValue(panel, participantRegistration.getProvince());
			panel = panelMap.get(FIELD_SUBEVENT);
			setFieldValue(panel, participantRegistration.getSubeventTitle());
			panel = panelMap.get(FIELD_TEAM);
			setFieldValue(panel, participantRegistration.getTeam());
		}
	}
}
