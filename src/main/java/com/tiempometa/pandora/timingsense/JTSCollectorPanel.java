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
package com.tiempometa.pandora.timingsense;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.ipicoreader.CommandResponseHandler;
import com.tiempometa.pandora.ipicoreader.JIpicoReaderPanel;
import com.tiempometa.pandora.ipicoreader.commands.GetTimeCommand;
import com.tiempometa.pandora.ipicoreader.commands.IpicoCommand;
import com.tiempometa.pandora.ipicoreader.commands.SetTimeCommand;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.JReaderListPanel;
import com.tiempometa.pandora.tagreader.JReaderPanel;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JTSCollectorPanel extends JReaderPanel implements CommandResponseHandler, TagReadListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1234969021040337654L;
	private static final Logger logger = Logger.getLogger(JTSCollectorPanel.class);
	private JReaderListPanel listPanel;
	private TSCollectorClient reader = new TSCollectorClient();
//	private String checkPoint1 = null;
	private TagReadListener tagReadListener;
	private Integer tagCount = 0;
	private JSocketMonitor socketMonitor = new JSocketMonitor();
//
//	public void setTerminal(String terminal) {
//		terminalTextField.setText(terminal);
//	}

	public JTSCollectorPanel(JReaderListPanel listPanel) {
		initComponents();
		this.listPanel = listPanel;
//		reader.setCommandResponseHandler(this);
//		reader.registerTagReadListener(this);
		reader.setTagReadListener(this);
		reader.setSocketMonitor(socketMonitor);
//		loadCheckPoints();
	}

	/**
	 * 
	 */
//	private void loadCheckPoints() {
//		List<String> checkPoints = Context.getResultsWebservice().getCheckPointNames();
//		logger.debug("Available checkpoints ");
//		for (String string : checkPoints) {
//			logger.debug(string);
//		}
//		String[] checkPointArray = new String[checkPoints.size()];
////		checkPointComboBox1.setModel(new DefaultComboBoxModel<String>(checkPoints.toArray(checkPointArray)));
////		checkPointComboBox2.setModel(new DefaultComboBoxModel<String>(checkPoints.toArray(checkPointArray)));
//	}

//	private void checkPointComboBoxItemStateChanged(ItemEvent e) {
//		// TODO add your code here
//	}

	private void connectButtonActionPerformed(ActionEvent e) {
		if (reader.isConnected()) {
			try {
				reader.disconnect();
				setDisconnected();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
//			if (checkPoint1 == null) {
//				JOptionPane.showMessageDialog(this, "Se debe fijar un punto antes de conectar",
//						"Error de configuración", JOptionPane.ERROR_MESSAGE);
//			} else {
			reader.setHostname(readerAddressTextField.getText());
			try {
				reader.setPort(Integer.valueOf(readerPortTextField.getText()));
			} catch (NumberFormatException e2) {
				JOptionPane.showMessageDialog(this, "El valor de puerto debe ser númerico. Usando valor default 10200",
						"Error de puerto", JOptionPane.ERROR_MESSAGE);
				reader.setPort(10200);
			}
			try {
				reader.connect();
				Thread thread = new Thread(reader);
				thread.start();
				setConnected();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "No se pudo conectar. " + e1.getMessage(), "Error de conexión",
						JOptionPane.ERROR_MESSAGE);
			}
//			}
		}
	}

	private void setConnected() {
		connectButton.setText("Desconectar");
		connectButton.setBackground(Color.GREEN);
//		setTimeButton.setEnabled(true);
		removeReaderButton.setEnabled(false);
	}

	private void setDisconnected() {
		connectButton.setText("Conectar");
		connectButton.setBackground(Color.RED);
//		setTimeButton.setEnabled(false);
		removeReaderButton.setEnabled(true);
	}

	private void applyCheckpointButtonActionPerformed(ActionEvent e) {
	}

	private void setTimeButtonActionPerformed(ActionEvent e) {
	}

	private void removeReaderButtonActionPerformed(ActionEvent e) {
		listPanel.removeReader(this);
	}

//	private void checkPointComboBox1ItemStateChanged(ItemEvent e) {
//		applyCheckpointButton.setBackground(Color.RED);
//	}
//
//	private void checkPointComboBox2ItemStateChanged(ItemEvent e) {
//		applyCheckpointButton.setBackground(Color.RED);
//	}

	private void multipointComboBoxItemStateChanged(ItemEvent e) {
		// TODO add your code here
	}

	private void socketMonitorButtonActionPerformed(ActionEvent e) {
		if (socketMonitor.isVisible()) {
			socketMonitor.setVisible(false);
		} else {
			socketMonitor.setVisible(true);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.timingsense.tscollector");
		label1 = new JLabel();
		readerAddressTextField = new JTextField();
		connectButton = new JButton();
		socketMonitorButton = new JButton();
		removeReaderButton = new JButton();
		label2 = new JLabel();
		readerPortTextField = new JTextField();
		label5 = new JLabel();
		tagsReadLabel = new JLabel();

		//======== this ========
		setBorder(new TitledBorder(bundle.getString("JIpicoReaderPanel.this.border")));
		setInheritsPopupMenu(true);
		setMaximumSize(new Dimension(550, 120));
		setMinimumSize(new Dimension(550, 120));
		setPreferredSize(new Dimension(550, 120));
		setLayout(new FormLayout(
			"5dlu, $lcgap, default, $lcgap, 57dlu, 2*($lcgap, 15dlu), $lcgap, 84dlu, $lcgap, 52dlu, $lcgap, 41dlu, $lcgap, 22dlu",
			"5dlu, 2*($lgap, 17dlu), $lgap, default"));

		//---- label1 ----
		label1.setText(bundle.getString("JIpicoReaderPanel.label1.text"));
		add(label1, CC.xy(3, 3));

		//---- readerAddressTextField ----
		readerAddressTextField.setText(bundle.getString("JIpicoReaderPanel.readerAddressTextField.text"));
		add(readerAddressTextField, CC.xywh(5, 3, 5, 1));

		//---- connectButton ----
		connectButton.setText(bundle.getString("JIpicoReaderPanel.connectButton.text"));
		connectButton.setBackground(Color.red);
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectButtonActionPerformed(e);
			}
		});
		add(connectButton, CC.xy(11, 3));

		//---- socketMonitorButton ----
		socketMonitorButton.setText(bundle.getString("JIpicoReaderPanel.socketMonitorButton.text"));
		socketMonitorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				socketMonitorButtonActionPerformed(e);
			}
		});
		add(socketMonitorButton, CC.xywh(13, 3, 3, 1));

		//---- removeReaderButton ----
		removeReaderButton.setIcon(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/tagreader/x-remove.png")));
		removeReaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeReaderButtonActionPerformed(e);
			}
		});
		add(removeReaderButton, CC.xy(17, 3));

		//---- label2 ----
		label2.setText(bundle.getString("JIpicoReaderPanel.label2.text"));
		add(label2, CC.xy(3, 5));

		//---- readerPortTextField ----
		readerPortTextField.setText(bundle.getString("JIpicoReaderPanel.readerPortTextField.text"));
		add(readerPortTextField, CC.xywh(5, 5, 5, 1));

		//---- label5 ----
		label5.setText(bundle.getString("JIpicoReaderPanel.label5.text"));
		add(label5, CC.xy(15, 7));

		//---- tagsReadLabel ----
		tagsReadLabel.setText(bundle.getString("JIpicoReaderPanel.tagsReadLabel.text"));
		add(tagsReadLabel, CC.xy(17, 7));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel label1;
	private JTextField readerAddressTextField;
	private JButton connectButton;
	private JButton socketMonitorButton;
	private JButton removeReaderButton;
	private JLabel label2;
	private JTextField readerPortTextField;
	private JLabel label5;
	private JLabel tagsReadLabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;

	}

	@Override
	public void notifyTagReads(List<RawChipRead> chipReadList) {
		if (chipReadList == null) {
			return;
		}
		// TODO Auto-generated method stub
		logger.debug("Notified tag reads " + chipReadList.size());
		for (RawChipRead rawChipRead : chipReadList) {
			logger.debug("TAG READ " + rawChipRead.getRfidString());
		}
		tagReadListener.notifyTagReads(chipReadList);
		tagCount = tagCount + chipReadList.size();
		tagsReadLabel.setText(tagCount.toString());
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
