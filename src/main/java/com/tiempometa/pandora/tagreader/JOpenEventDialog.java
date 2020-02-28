/*
 * Created by JFormDesigner on Fri May 24 10:18:31 EDT 2019
 */

package com.tiempometa.pandora.tagreader;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

import javax.swing.*;


import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.timing.model.Event;

import org.hibernate.HibernateException;
/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JOpenEventDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8785031554321403118L;

	public JOpenEventDialog(Frame owner, boolean modal) {
		super(owner, modal);
		initComponents();
		try {
			List<String> databases = Context.listDatabases();
			databaseTextField.setOptions(databases.toArray(new String[databases.size()]));
			databaseTextField.setSelectedItem(Context.getDatabaseName());
		} catch (HibernateException e) {
			JOptionPane.showMessageDialog(this,
					"No se pudo acceder a la base de datos. ¿Se ha borrado?\nError es: " + e.getMessage(),
					"Error de base de datos", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public JOpenEventDialog(Frame owner) {
		super(owner);
		initComponents();
		try {
			List<String> databases = Context.listDatabases();
			databaseTextField.setOptions(databases.toArray(new String[databases.size()]));
			databaseTextField.setSelectedItem(Context.getDatabaseName());
		} catch (HibernateException e) {
			JOptionPane.showMessageDialog(this,
					"No se pudo acceder a la base de datos. ¿Se ha borrado?\nError es: " + e.getMessage(),
					"Error de base de datos", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public JOpenEventDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void okButtonActionPerformed(ActionEvent e) {
		String databaseName = (String) databaseTextField.getSelectedItem();
		if (!databaseName.matches("[a-zA-Z0-9_]*")) {
			JOptionPane.showMessageDialog(this, "Solo se permiten letras sin acentos, números y guiones bajos.", "Caracteres inválidos", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			Context.connectDatabase(databaseName, Context.getDatabaseUsername(), Context.getDatabasePassword());
			saveSettings();
			JOptionPane.showMessageDialog(this, "Se abrió con éxito el evento: " + databaseName, "Abrir evento",
					JOptionPane.INFORMATION_MESSAGE);
			Context.setApplicationTitle(Context.registrationHelper.getEvent().getTitle());
			Context.notifyDatabaseChanged();
			this.dispose();
		} catch (Exception e1) {
			int response = JOptionPane.showConfirmDialog(this,
					"No existe la base de datos " + databaseName + "\n ¿Deseas crear la base?", "Nueva base de datos",
					JOptionPane.INFORMATION_MESSAGE);
			if (response == JOptionPane.YES_OPTION) {
				Context.exportSchema(databaseName, Context.getDatabaseUsername(), Context.getDatabasePassword());
				try {
					Context.populateCountryCatalogue();
					saveSettings();
					Context.eventHelper.initVersion();
					com.tiempometa.timing.model.Event event = new Event();
					event.setTitle("Nuevo evento");
					event.setZoneIdString(Context.loadSetting(PandoraSettings.PREFERRED_TIMEZONE, ZoneId.systemDefault().toString()));
					Context.eventHelper.save(event);
					JOptionPane.showMessageDialog(this, "Se creó con éxito el evento: " + databaseName,
							"Crear nuevo evento", JOptionPane.INFORMATION_MESSAGE);
					Context.setApplicationTitle(Context.registrationHelper.getEvent().getTitle());
					Context.notifyDatabaseChanged();
				} catch (IOException e2) {
					JOptionPane.showMessageDialog(this,
							"Error creando el catálogo de paises y estados " + e2.getMessage(), "Error de catálogo",
							JOptionPane.ERROR_MESSAGE);

				}
			}
		}
		this.dispose();
	}

	/**
	 * 
	 */
	private void saveSettings() {
		try {
			Context.setDatabaseNameSetting((String) databaseTextField.getSelectedItem());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "No se pudo guardar la configuración " + e.getMessage(),
					"Error de configuración", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void cancelButtonActionPerformed(ActionEvent e) {
		this.dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.macshareader.macshareader");
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		databaseTextField = new JAutocompleteComboBox();
		label2 = new JLabel();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle(bundle.getString("JOpenEventDialog.this.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"10dlu, $lcgap, default, $lcgap, 140dlu",
					"10dlu, 2*($lgap, default)"));

				//---- label1 ----
				label1.setText(bundle.getString("JOpenEventDialog.label1.text"));
				contentPanel.add(label1, CC.xy(3, 3));
				contentPanel.add(databaseTextField, CC.xy(5, 3));

				//---- label2 ----
				label2.setText(bundle.getString("JOpenEventDialog.label2.text"));
				label2.setFont(new Font("Tahoma", Font.PLAIN, 10));
				label2.setHorizontalAlignment(SwingConstants.CENTER);
				contentPanel.add(label2, CC.xywh(3, 5, 3, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setLayout(new FormLayout(
					"$glue, $button, $rgap, $button",
					"pref"));

				//---- okButton ----
				okButton.setText("Abrir");
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
		setSize(400, 165);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel label1;
	private JAutocompleteComboBox databaseTextField;
	private JLabel label2;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
