/*
 * Created by JFormDesigner on Sat Feb 22 14:40:34 CST 2020
 */

package com.tiempometa.pandora.cloud.tiempometa;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.api.DataRequestException;
import com.tiempometa.pandora.ipicoreader.CommandResponseHandler;
import com.tiempometa.pandora.ipicoreader.JIpicoReaderPanel;
import com.tiempometa.pandora.ipicoreader.commands.GetTimeCommand;
import com.tiempometa.pandora.ipicoreader.commands.IpicoCommand;
import com.tiempometa.pandora.ipicoreader.commands.SetTimeCommand;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.JReaderListPanel;
import com.tiempometa.pandora.tagreader.JReaderPanel;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.timing.model.dao.RouteDao;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JTiempoMetaCloudPanel extends JReaderPanel implements CommandResponseHandler, TagReadListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1234969021040337654L;
	private static final Logger logger = Logger.getLogger(JTiempoMetaCloudPanel.class);
	private JReaderListPanel listPanel;
	private TiempoMetaCloudClient reader = new TiempoMetaCloudClient();
	private TagReadListener tagReadListener;
	private Integer tagCount = 0;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public JTiempoMetaCloudPanel(JReaderListPanel listPanel) {
		initComponents();
		this.listPanel = listPanel;
		reader.registerTagReadListener(this);
	}

	private void connectButtonActionPerformed(ActionEvent e) {
		try {
			if (reader.connect(apiKeyTextField.getText(), null)) {
				eventTitleLabel.setText(reader.getEvent().getTitle());
				Thread thread = new Thread(reader);
				thread.start();
			} else {
				eventTitleLabel.setText("Error conectando");
			}
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "No se pudo conectar. " + e1.getMessage(), "Error de comunicación",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "No se pudo conectar. " + e1.getMessage(), "Error de comunicación",
					JOptionPane.ERROR_MESSAGE);
		} catch (DataRequestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "No se pudo conectar. " + e1.getMessage(), "Error de comunicación",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void removeReaderButtonActionPerformed(ActionEvent e) {
		listPanel.removeReader(this);
	}

	private void rewindTagReadsButtonActionPerformed(ActionEvent e) {
		int response = JOptionPane.showConfirmDialog(this,
				"Esta acción descargará todas las lecturas nuevamente. ¿Deseas continuar?", "Rebobinar lecturas",
				JOptionPane.YES_NO_OPTION);
		if (response == JOptionPane.YES_OPTION) {
			reader.rewindAll();
		}
	}

	private void clearTagReadsButtonActionPerformed(ActionEvent e) {
		String confirmation = UUID.randomUUID().toString().substring(9, 13);
		String response = JOptionPane.showInputDialog(this,
				"Esto borrará todas las lecturas en la nube. Esta operación no se puede deshacer.\n"
						+ "Para continuar por favor digita los siguientes caracteres para confirmar:" + confirmation,
				null);
		if ((response != null) && (response.equals(confirmation))) {
			reader.clearTags();
			JOptionPane.showMessageDialog(this, "Se borraron todos los tags.");
		} else {
			JOptionPane.showMessageDialog(this, "Operación cancelada.");
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.foxberry.foxberry");
		label1 = new JLabel();
		apiKeyTextField = new JTextField();
		removeReaderButton = new JButton();
		eventTitleLabel = new JLabel();
		connectButton = new JButton();
		label2 = new JLabel();
		lastRequestLabel = new JLabel();
		label3 = new JLabel();
		lastDownloadLabel = new JLabel();
		rewindTagReadsButton = new JButton();
		label5 = new JLabel();
		tagsReadLabel = new JLabel();
		clearTagReadsButton = new JButton();

		// ======== this ========
		setBorder(new TitledBorder(bundle.getString("JIpicoReaderPanel.this.border")));
		setInheritsPopupMenu(true);
		setMaximumSize(new Dimension(550, 130));
		setMinimumSize(new Dimension(550, 130));
		setPreferredSize(new Dimension(550, 130));
		setLayout(new FormLayout(
				"5dlu, $lcgap, default, $lcgap, 57dlu, $lcgap, 15dlu, $lcgap, 78dlu, $lcgap, 51dlu, $lcgap, 57dlu, $lcgap, 22dlu",
				"3*(default, $lgap), default"));

		// ---- label1 ----
		label1.setText(bundle.getString("JIpicoReaderPanel.label1.text"));
		add(label1, CC.xy(3, 1));
		add(apiKeyTextField, CC.xywh(5, 1, 9, 1));

		// ---- removeReaderButton ----
		removeReaderButton
				.setIcon(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/tagreader/x-remove.png")));
		removeReaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeReaderButtonActionPerformed(e);
			}
		});
		add(removeReaderButton, CC.xy(15, 1));

		// ---- eventTitleLabel ----
		eventTitleLabel.setText(bundle.getString("JIpicoReaderPanel.eventTitleLabel.text"));
		eventTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(eventTitleLabel, CC.xywh(3, 3, 9, 1));

		// ---- connectButton ----
		connectButton.setText(bundle.getString("JIpicoReaderPanel.connectButton.text"));
		connectButton.setBackground(Color.red);
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectButtonActionPerformed(e);
			}
		});
		add(connectButton, CC.xywh(13, 3, 3, 1));

		// ---- label2 ----
		label2.setText(bundle.getString("JIpicoReaderPanel.label2.text"));
		add(label2, CC.xy(3, 5));

		// ---- lastRequestLabel ----
		lastRequestLabel.setText(bundle.getString("JIpicoReaderPanel.lastRequestLabel.text"));
		lastRequestLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(lastRequestLabel, CC.xy(5, 5));

		// ---- label3 ----
		label3.setText(bundle.getString("JIpicoReaderPanel.label3.text"));
		add(label3, CC.xy(9, 5));

		// ---- lastDownloadLabel ----
		lastDownloadLabel.setText(bundle.getString("JIpicoReaderPanel.lastDownloadLabel.text"));
		lastDownloadLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(lastDownloadLabel, CC.xy(11, 5));

		// ---- rewindTagReadsButton ----
		rewindTagReadsButton.setText(bundle.getString("JIpicoReaderPanel.rewindTagReadsButton.text"));
		rewindTagReadsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rewindTagReadsButtonActionPerformed(e);
			}
		});
		add(rewindTagReadsButton, CC.xywh(13, 5, 3, 1));

		// ---- label5 ----
		label5.setText(bundle.getString("JIpicoReaderPanel.label5.text"));
		add(label5, CC.xy(9, 7));

		// ---- tagsReadLabel ----
		tagsReadLabel.setText(bundle.getString("JIpicoReaderPanel.tagsReadLabel.text"));
		add(tagsReadLabel, CC.xy(11, 7));

		// ---- clearTagReadsButton ----
		clearTagReadsButton.setText(bundle.getString("JIpicoReaderPanel.clearTagReadsButton.text"));
		clearTagReadsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearTagReadsButtonActionPerformed(e);
			}
		});
		add(clearTagReadsButton, CC.xywh(13, 7, 3, 1));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel label1;
	private JTextField apiKeyTextField;
	private JButton removeReaderButton;
	private JLabel eventTitleLabel;
	private JButton connectButton;
	private JLabel label2;
	private JLabel lastRequestLabel;
	private JLabel label3;
	private JLabel lastDownloadLabel;
	private JButton rewindTagReadsButton;
	private JLabel label5;
	private JLabel tagsReadLabel;
	private JButton clearTagReadsButton;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;

	}

	@Override
	public void notifyTagReads(List<RawChipRead> chipReadList) {
		lastRequestLabel.setText(dateFormat.format(new Date()));
		if (chipReadList.size() > 0) {
			lastDownloadLabel.setText(dateFormat.format(new Date()));
			logger.debug("Notified tag reads " + chipReadList.size());
			for (RawChipRead rawChipRead : chipReadList) {
				logger.debug("TAG READ " + rawChipRead.getRfidString());
			}
			tagReadListener.notifyTagReads(chipReadList);
			tagCount = tagCount + chipReadList.size();
			tagsReadLabel.setText(tagCount.toString());
		}
	}

	@Override
	public void handleCommandResponse(IpicoCommand command) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyCommException(IOException e) {
		// TODO Auto-generated method stub

	}

	public JReaderListPanel getListPanel() {
		return listPanel;
	}

	public void setListPanel(JReaderListPanel listPanel) {
		this.listPanel = listPanel;
	}

	public TagReadListener getTagReadListener() {
		return tagReadListener;
	}
}
