/*
 * Created by JFormDesigner on Tue May 28 09:12:44 EDT 2019
 */

package com.tiempometa.pandora.checatuchip.preview;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.gui.*;
import com.tiempometa.pandora.ipicoreader.Context;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JPreviewConfig extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1614167937739307441L;
	private File topBannerFile;
	private File bottomBannerFile;
	private File leftBannerFile;
	private File rightBannerFile;

	private void initConfiguration() {
		Context.previewHelper.getSettings().loadSettings();
		firstNamePositionComboBox.setSelectedIndex(Context.previewHelper.getSettings().getFieldNameLocation());
		subeventPositionComboBox.setSelectedIndex(Context.previewHelper.getSettings().getFieldSubeventLocation());
		categoryPositionComboBox.setSelectedIndex(Context.previewHelper.getSettings().getFieldCategoryLocation());
		agePositionComboBox.setSelectedIndex(Context.previewHelper.getSettings().getFieldAgeLocation());
		genderPositionComboBox.setSelectedIndex(Context.previewHelper.getSettings().getFieldGenderLocation());
		birthdatePositionComboBox.setSelectedIndex(Context.previewHelper.getSettings().getFieldBirthdateLocation());
		labelColorPositionComboBox.setSelectedIndex(Context.previewHelper.getSettings().getFieldLabelColorLocation());
		teamPositionComboBox.setSelectedIndex(Context.previewHelper.getSettings().getFieldTeamLocation());
		procedencePositionComboBox.setSelectedIndex(Context.previewHelper.getSettings().getFieldProcedenceLocation());
		if (Context.previewHelper.getSettings().getFieldTopBanner() != null) {
			topBannerFile = new File(Context.previewHelper.getSettings().getFieldTopBanner());
			if (topBannerFile.exists()) {
				topBannerTextField.setText(topBannerFile.getName());
			}
		}
		if (Context.previewHelper.getSettings().getFieldBottomBanner() != null) {
			bottomBannerFile = new File(Context.previewHelper.getSettings().getFieldBottomBanner());
			if (bottomBannerFile.exists()) {
				bottomBannerTextField.setText(bottomBannerFile.getName());
			}
		}
		if (Context.previewHelper.getSettings().getFieldLeftBanner() != null) {
			leftBannerFile = new File(Context.previewHelper.getSettings().getFieldLeftBanner());
			if (leftBannerFile.exists()) {
				leftBannerTextField.setText(leftBannerFile.getName());
			}
		}
		if (Context.previewHelper.getSettings().getFieldRightBanner() != null) {
			rightBannerFile = new File(Context.previewHelper.getSettings().getFieldRightBanner());
			if (rightBannerFile.exists()) {
				rightBannerTextField.setText(rightBannerFile.getName());
			}
		}
	}

	public JPreviewConfig(Frame owner) {
		super(owner);
		initComponents();
		initConfiguration();
	}

	public JPreviewConfig(Dialog owner) {
		super(owner);
		initComponents();
		initConfiguration();
	}

	private void topBannerSelectButtonActionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(topBannerFile);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagen (.jpg, .png)", "jpg", "png");
		fc.setFileFilter(filter);
		int response = fc.showOpenDialog(this);
		if (response == JFileChooser.APPROVE_OPTION) {
			topBannerFile = fc.getSelectedFile();
			topBannerTextField.setText(topBannerFile.getName());
		}
	}

	private void leftBannerSelectButtonActionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(leftBannerFile);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagen (.jpg, .png)", "jpg", "png");
		fc.setFileFilter(filter);
		int response = fc.showOpenDialog(this);
		if (response == JFileChooser.APPROVE_OPTION) {
			leftBannerFile = fc.getSelectedFile();
			leftBannerTextField.setText(leftBannerFile.getName());
		}
	}

	private void rightBannerSelectButtonActionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(rightBannerFile);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagen (.jpg, .png)", "jpg", "png");
		fc.setFileFilter(filter);
		int response = fc.showOpenDialog(this);
		if (response == JFileChooser.APPROVE_OPTION) {
			rightBannerFile = fc.getSelectedFile();
			rightBannerTextField.setText(rightBannerFile.getName());
		}
	}

	private void bottomBannerSelectButtonActionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(bottomBannerFile);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagen (.jpg, .png)", "jpg", "png");
		fc.setFileFilter(filter);
		int response = fc.showOpenDialog(this);
		if (response == JFileChooser.APPROVE_OPTION) {
			bottomBannerFile = fc.getSelectedFile();
			bottomBannerTextField.setText(bottomBannerFile.getName());
		}
	}

	private void applySettingsButtonActionPerformed(ActionEvent e) {
	}

	/**
	 * 
	 */
	private void applySettings() {
		Context.previewHelper.getSettings().setFieldNameLocation(firstNamePositionComboBox.getSelectedIndex());
		Context.previewHelper.getSettings().setFieldSubeventLocation(subeventPositionComboBox.getSelectedIndex());
		Context.previewHelper.getSettings().setFieldCategoryLocation(categoryPositionComboBox.getSelectedIndex());
		Context.previewHelper.getSettings().setFieldAgeLocation(agePositionComboBox.getSelectedIndex());
		Context.previewHelper.getSettings().setFieldGenderLocation(genderPositionComboBox.getSelectedIndex());
		Context.previewHelper.getSettings().setFieldBirthdateLocation(birthdatePositionComboBox.getSelectedIndex());
		Context.previewHelper.getSettings().setFieldLabelColorLocation(labelColorPositionComboBox.getSelectedIndex());
		Context.previewHelper.getSettings().setFieldTeamLocation(teamPositionComboBox.getSelectedIndex());
		Context.previewHelper.getSettings().setFieldProcedenceLocation(procedencePositionComboBox.getSelectedIndex());
		if (topBannerFile == null) {
			Context.previewHelper.getSettings().setFieldTopBanner(null);
		} else {
			Context.previewHelper.getSettings().setFieldTopBanner(topBannerFile.getAbsolutePath());
		}
		if (bottomBannerFile == null) {
			Context.previewHelper.getSettings().setFieldBottomBanner(null);
		} else {
			Context.previewHelper.getSettings().setFieldBottomBanner(bottomBannerFile.getAbsolutePath());
		}
		if (leftBannerFile == null) {
			Context.previewHelper.getSettings().setFieldLeftBanner(null);
		} else {
			Context.previewHelper.getSettings().setFieldLeftBanner(leftBannerFile.getAbsolutePath());
		}
		if (rightBannerFile == null) {
			Context.previewHelper.getSettings().setFieldRightBanner(null);
		} else {
			Context.previewHelper.getSettings().setFieldRightBanner(rightBannerFile.getAbsolutePath());
		}
		try {
			Context.previewHelper.getSettings().saveSettings();
			JOptionPane.showMessageDialog(this, "La configuración fue guaradada con éxito.", "Guardar configuración",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, "No se pudo guardar la configuración. " + e1.getMessage(),
					"Error de configuración", JOptionPane.ERROR_MESSAGE);
		}
		this.dispose();
	}

	private void okButtonActionPerformed(ActionEvent e) {
		applySettings();
	}

	private void cancelButtonActionPerformed(ActionEvent e) {
		int response = JOptionPane.showConfirmDialog(this, "Se perderán todos los cambios no guardados.\n¿Cerrar?",
				"Confirmar cerrar.", JOptionPane.YES_NO_OPTION);
		if (response == JOptionPane.YES_OPTION) {
			this.dispose();
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.checatuchip.preview.confguration");
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		firstNamePositionComboBox = new JPreviewFieldSettingComboBox();
		label2 = new JLabel();
		birthdatePositionComboBox = new JPreviewFieldSettingComboBox();
		label3 = new JLabel();
		agePositionComboBox = new JPreviewFieldSettingComboBox();
		label6 = new JLabel();
		genderPositionComboBox = new JPreviewFieldSettingComboBox();
		label4 = new JLabel();
		categoryPositionComboBox = new JPreviewFieldSettingComboBox();
		label5 = new JLabel();
		subeventPositionComboBox = new JPreviewFieldSettingComboBox();
		label7 = new JLabel();
		labelColorPositionComboBox = new JPreviewFieldSettingComboBox();
		label8 = new JLabel();
		teamPositionComboBox = new JPreviewFieldSettingComboBox();
		label9 = new JLabel();
		procedencePositionComboBox = new JPreviewFieldSettingComboBox();
		label12 = new JLabel();
		topBannerTextField = new JTextField();
		topBannerSelectButton = new JButton();
		label13 = new JLabel();
		leftBannerTextField = new JTextField();
		leftBannerSelectButton = new JButton();
		label14 = new JLabel();
		rightBannerTextField = new JTextField();
		rightBannerSelectButton = new JButton();
		label15 = new JLabel();
		bottomBannerTextField = new JTextField();
		bottomBannerSelectButton = new JButton();
		label10 = new JLabel();
		label11 = new JLabel();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle(bundle.getString("JPreviewConfig.this.title"));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"2*(default, $lcgap), default",
					"13*(default, $lgap), default"));

				//---- label1 ----
				label1.setText(bundle.getString("JPreviewConfig.label1.text"));
				contentPanel.add(label1, CC.xy(1, 3));

				//---- firstNamePositionComboBox ----
				firstNamePositionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"No mostrar",
					"En posici\u00f3n 1",
					"En posici\u00f3n 2",
					"En posici\u00f3n 3",
					"En posici\u00f3n 4",
					"En posici\u00f3n 5",
					"En posici\u00f3n 6"
				}));
				contentPanel.add(firstNamePositionComboBox, CC.xywh(3, 3, 3, 1));

				//---- label2 ----
				label2.setText(bundle.getString("JPreviewConfig.label2.text"));
				contentPanel.add(label2, CC.xy(1, 5));

				//---- birthdatePositionComboBox ----
				birthdatePositionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"No mostrar",
					"En posici\u00f3n 1",
					"En posici\u00f3n 2",
					"En posici\u00f3n 3",
					"En posici\u00f3n 4",
					"En posici\u00f3n 5",
					"En posici\u00f3n 6"
				}));
				contentPanel.add(birthdatePositionComboBox, CC.xywh(3, 5, 3, 1));

				//---- label3 ----
				label3.setText(bundle.getString("JPreviewConfig.label3.text"));
				contentPanel.add(label3, CC.xy(1, 7));

				//---- agePositionComboBox ----
				agePositionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"No mostrar",
					"En posici\u00f3n 1",
					"En posici\u00f3n 2",
					"En posici\u00f3n 3",
					"En posici\u00f3n 4",
					"En posici\u00f3n 5",
					"En posici\u00f3n 6"
				}));
				contentPanel.add(agePositionComboBox, CC.xywh(3, 7, 3, 1));

				//---- label6 ----
				label6.setText(bundle.getString("JPreviewConfig.label6.text"));
				contentPanel.add(label6, CC.xy(1, 9));

				//---- genderPositionComboBox ----
				genderPositionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"No mostrar",
					"En posici\u00f3n 1",
					"En posici\u00f3n 2",
					"En posici\u00f3n 3",
					"En posici\u00f3n 4",
					"En posici\u00f3n 5",
					"En posici\u00f3n 6"
				}));
				contentPanel.add(genderPositionComboBox, CC.xywh(3, 9, 3, 1));

				//---- label4 ----
				label4.setText(bundle.getString("JPreviewConfig.label4.text"));
				contentPanel.add(label4, CC.xy(1, 11));

				//---- categoryPositionComboBox ----
				categoryPositionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"No mostrar",
					"En posici\u00f3n 1",
					"En posici\u00f3n 2",
					"En posici\u00f3n 3",
					"En posici\u00f3n 4",
					"En posici\u00f3n 5",
					"En posici\u00f3n 6"
				}));
				contentPanel.add(categoryPositionComboBox, CC.xywh(3, 11, 3, 1));

				//---- label5 ----
				label5.setText(bundle.getString("JPreviewConfig.label5.text"));
				contentPanel.add(label5, CC.xy(1, 13));

				//---- subeventPositionComboBox ----
				subeventPositionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"No mostrar",
					"En posici\u00f3n 1",
					"En posici\u00f3n 2",
					"En posici\u00f3n 3",
					"En posici\u00f3n 4",
					"En posici\u00f3n 5",
					"En posici\u00f3n 6"
				}));
				contentPanel.add(subeventPositionComboBox, CC.xywh(3, 13, 3, 1));

				//---- label7 ----
				label7.setText(bundle.getString("JPreviewConfig.label7.text"));
				contentPanel.add(label7, CC.xy(1, 15));

				//---- labelColorPositionComboBox ----
				labelColorPositionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"No mostrar",
					"En posici\u00f3n 1",
					"En posici\u00f3n 2",
					"En posici\u00f3n 3",
					"En posici\u00f3n 4",
					"En posici\u00f3n 5",
					"En posici\u00f3n 6"
				}));
				contentPanel.add(labelColorPositionComboBox, CC.xywh(3, 15, 3, 1));

				//---- label8 ----
				label8.setText(bundle.getString("JPreviewConfig.label8.text"));
				contentPanel.add(label8, CC.xy(1, 17));

				//---- teamPositionComboBox ----
				teamPositionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"No mostrar",
					"En posici\u00f3n 1",
					"En posici\u00f3n 2",
					"En posici\u00f3n 3",
					"En posici\u00f3n 4",
					"En posici\u00f3n 5",
					"En posici\u00f3n 6"
				}));
				contentPanel.add(teamPositionComboBox, CC.xywh(3, 17, 3, 1));

				//---- label9 ----
				label9.setText(bundle.getString("JPreviewConfig.label9.text"));
				contentPanel.add(label9, CC.xy(1, 19));

				//---- procedencePositionComboBox ----
				procedencePositionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"No mostrar",
					"En posici\u00f3n 1",
					"En posici\u00f3n 2",
					"En posici\u00f3n 3",
					"En posici\u00f3n 4",
					"En posici\u00f3n 5",
					"En posici\u00f3n 6"
				}));
				contentPanel.add(procedencePositionComboBox, CC.xywh(3, 19, 3, 1));

				//---- label12 ----
				label12.setText(bundle.getString("JPreviewConfig.label12.text"));
				contentPanel.add(label12, CC.xy(1, 21));
				contentPanel.add(topBannerTextField, CC.xy(3, 21));

				//---- topBannerSelectButton ----
				topBannerSelectButton.setText(bundle.getString("JPreviewConfig.topBannerSelectButton.text"));
				topBannerSelectButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						topBannerSelectButtonActionPerformed(e);
					}
				});
				contentPanel.add(topBannerSelectButton, CC.xy(5, 21));

				//---- label13 ----
				label13.setText(bundle.getString("JPreviewConfig.label13.text"));
				contentPanel.add(label13, CC.xy(1, 23));
				contentPanel.add(leftBannerTextField, CC.xy(3, 23));

				//---- leftBannerSelectButton ----
				leftBannerSelectButton.setText(bundle.getString("JPreviewConfig.leftBannerSelectButton.text"));
				leftBannerSelectButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						leftBannerSelectButtonActionPerformed(e);
					}
				});
				contentPanel.add(leftBannerSelectButton, CC.xy(5, 23));

				//---- label14 ----
				label14.setText(bundle.getString("JPreviewConfig.label14.text"));
				contentPanel.add(label14, CC.xy(1, 25));
				contentPanel.add(rightBannerTextField, CC.xy(3, 25));

				//---- rightBannerSelectButton ----
				rightBannerSelectButton.setText(bundle.getString("JPreviewConfig.rightBannerSelectButton.text"));
				rightBannerSelectButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						rightBannerSelectButtonActionPerformed(e);
					}
				});
				contentPanel.add(rightBannerSelectButton, CC.xy(5, 25));

				//---- label15 ----
				label15.setText(bundle.getString("JPreviewConfig.label15.text"));
				contentPanel.add(label15, CC.xy(1, 27));
				contentPanel.add(bottomBannerTextField, CC.xy(3, 27));

				//---- bottomBannerSelectButton ----
				bottomBannerSelectButton.setText(bundle.getString("JPreviewConfig.bottomBannerSelectButton.text"));
				bottomBannerSelectButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						bottomBannerSelectButtonActionPerformed(e);
					}
				});
				contentPanel.add(bottomBannerSelectButton, CC.xy(5, 27));

				//---- label10 ----
				label10.setText(bundle.getString("JPreviewConfig.label10.text"));
				label10.setFont(new Font("Tahoma", Font.BOLD, 12));
				label10.setHorizontalAlignment(SwingConstants.CENTER);
				contentPanel.add(label10, CC.xy(1, 1));

				//---- label11 ----
				label11.setText(bundle.getString("JPreviewConfig.label11.text"));
				label11.setFont(new Font("Tahoma", Font.BOLD, 12));
				label11.setHorizontalAlignment(SwingConstants.CENTER);
				contentPanel.add(label11, CC.xy(3, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setLayout(new FormLayout(
					"$glue, $button, $rgap, $button",
					"pref"));

				//---- okButton ----
				okButton.setText("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});
				buttonBar.add(okButton, CC.xy(2, 1));

				//---- cancelButton ----
				cancelButton.setText("Cancelar");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
					}
				});
				buttonBar.add(cancelButton, CC.xy(4, 1));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel label1;
	private JPreviewFieldSettingComboBox firstNamePositionComboBox;
	private JLabel label2;
	private JPreviewFieldSettingComboBox birthdatePositionComboBox;
	private JLabel label3;
	private JPreviewFieldSettingComboBox agePositionComboBox;
	private JLabel label6;
	private JPreviewFieldSettingComboBox genderPositionComboBox;
	private JLabel label4;
	private JPreviewFieldSettingComboBox categoryPositionComboBox;
	private JLabel label5;
	private JPreviewFieldSettingComboBox subeventPositionComboBox;
	private JLabel label7;
	private JPreviewFieldSettingComboBox labelColorPositionComboBox;
	private JLabel label8;
	private JPreviewFieldSettingComboBox teamPositionComboBox;
	private JLabel label9;
	private JPreviewFieldSettingComboBox procedencePositionComboBox;
	private JLabel label12;
	private JTextField topBannerTextField;
	private JButton topBannerSelectButton;
	private JLabel label13;
	private JTextField leftBannerTextField;
	private JButton leftBannerSelectButton;
	private JLabel label14;
	private JTextField rightBannerTextField;
	private JButton rightBannerSelectButton;
	private JLabel label15;
	private JTextField bottomBannerTextField;
	private JButton bottomBannerSelectButton;
	private JLabel label10;
	private JLabel label11;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
