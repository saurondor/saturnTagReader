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
package com.tiempometa.pandora.foxberry.tcpip;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.ipicoreader.CommandResponseHandler;
import com.tiempometa.pandora.ipicoreader.commands.IpicoCommand;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.JReaderListPanel;
import com.tiempometa.pandora.tagreader.JReaderPanel;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JFoxberryReaderPanel extends JReaderPanel implements CommandResponseHandler, TagReadListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1234969021040337654L;
	private static final Logger logger = Logger.getLogger(JFoxberryReaderPanel.class);
	private JReaderListPanel listPanel;
	private FoxberryClient reader = new FoxberryClient();
	private String checkPoint1 = null;
	private TagReadListener tagReadListener;
	private Integer tagCount;

	public void setTerminal(String terminal) {
		terminalTextField.setText(terminal);
	}

	public JFoxberryReaderPanel(JReaderListPanel listPanel) {
		initComponents();
		this.listPanel = listPanel;
		reader.setCommandResponseHandler(this);
		reader.registerTagReadListener(this);
		loadCheckPoints();
	}

	/**
	 * 
	 */
	private void loadCheckPoints() {
		List<String> checkPoints = Context.getResultsWebservice().getCheckPointNames();
		logger.debug("Available checkpoints ");
		for (String string : checkPoints) {
			logger.debug(string);
		}
		String[] checkPointArray = new String[checkPoints.size()];
		checkPointComboBox1.setModel(new DefaultComboBoxModel<String>(checkPoints.toArray(checkPointArray)));
		checkPointComboBox2.setModel(new DefaultComboBoxModel<String>(checkPoints.toArray(checkPointArray)));
	}

//	private void checkPointComboBoxItemStateChanged(ItemEvent e) {
//		// TODO add your code here
//	}

	private void connectButtonActionPerformed(ActionEvent e) {
		if (reader.isConnected()) {
			reader.disconnect();
			setDisconnected();
		} else {
			if (checkPoint1 == null) {
				JOptionPane.showMessageDialog(this, "Se debe fijar un punto antes de conectar",
						"Error de configuración", JOptionPane.ERROR_MESSAGE);
			} else {
				reader.setHostname(readerAddressTextField.getText());
				try {
					reader.connect();
					Thread thread = new Thread(reader);
					thread.start();
					setConnected();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(this, "No se pudo conectar. " + e1.getMessage(), "Error de conexión",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private void setConnected() {
		connectButton.setText("Desconectar");
		connectButton.setBackground(Color.GREEN);
		setTimeButton.setEnabled(true);
		removeReaderButton.setEnabled(false);
	}

	private void setDisconnected() {
		connectButton.setText("Conectar");
		connectButton.setBackground(Color.RED);
		setTimeButton.setEnabled(false);
		removeReaderButton.setEnabled(true);
	}

	private void applyCheckpointButtonActionPerformed(ActionEvent e) {
		checkPoint1 = (String) checkPointComboBox1.getSelectedItem();
		reader.setCheckPointOne(checkPoint1);
		reader.setTerminal(terminalTextField.getText());
//		reader.setCheckPoint(checkPoint1);
		applyCheckpointButton.setBackground(Color.GREEN);
	}

	private void setTimeButtonActionPerformed(ActionEvent e) {
//		IpicoTcpClient client = new IpicoTcpClient();
//		logger.info("Connecting to host");
////		client.setHostname("127.0.0.1");
//		try {
//			client.connect("10.19.1.101");
//			Thread thread = new Thread(client);
//			thread.start();
////			client.sendCommand(new GetTimeCommand());
//			client.sendCommand(new SetTimeCommand());
////			client.sendCommand(new GetTimeCommand());
//			JOptionPane.showMessageDialog(this, "Se fijó la hora", "Fijar hora", JOptionPane.INFORMATION_MESSAGE);
//		} catch (InvalidTelnetOptionException | IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	}

	private void removeReaderButtonActionPerformed(ActionEvent e) {
		listPanel.removeReader(this);
	}

	private void checkPointComboBox1ItemStateChanged(ItemEvent e) {
		applyCheckpointButton.setBackground(Color.RED);
	}

	private void checkPointComboBox2ItemStateChanged(ItemEvent e) {
		applyCheckpointButton.setBackground(Color.RED);
	}

	private void multipointComboBoxItemStateChanged(ItemEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.foxberry.foxberry");
		label1 = new JLabel();
		readerAddressTextField = new JTextField();
		connectButton = new JButton();
		setTimeButton = new JButton();
		removeReaderButton = new JButton();
		label2 = new JLabel();
		multipointComboBox = new JComboBox<>();
		textField1 = new JTextField();
		textField2 = new JTextField();
		checkPointComboBox1 = new JComboBox();
		label4 = new JLabel();
		terminalTextField = new JTextField();
		label3 = new JLabel();
		textField3 = new JTextField();
		textField4 = new JTextField();
		checkPointComboBox2 = new JComboBox();
		applyCheckpointButton = new JButton();
		label5 = new JLabel();
		tagsReadLabel = new JLabel();

		//======== this ========
		setBorder(new TitledBorder(bundle.getString("JIpicoReaderPanel.this.border")));
		setInheritsPopupMenu(true);
		setMaximumSize(new Dimension(550, 120));
		setMinimumSize(new Dimension(550, 120));
		setPreferredSize(new Dimension(550, 120));
		setLayout(new FormLayout(
			"5dlu, $lcgap, default, $lcgap, 57dlu, 2*($lcgap, 15dlu), $lcgap, 84dlu, $lcgap, default, $lcgap, 41dlu, $lcgap, 22dlu",
			"5dlu, 3*($lgap, default)"));

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

		//---- setTimeButton ----
		setTimeButton.setText(bundle.getString("JIpicoReaderPanel.setTimeButton.text"));
		setTimeButton.setEnabled(false);
		setTimeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTimeButtonActionPerformed(e);
			}
		});
		add(setTimeButton, CC.xy(13, 3));

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

		//---- multipointComboBox ----
		multipointComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
			"Todos",
			"RX 1 (Izq.)"
		}));
		multipointComboBox.setEnabled(false);
		multipointComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				multipointComboBoxItemStateChanged(e);
			}
		});
		add(multipointComboBox, CC.xy(5, 5));

		//---- textField1 ----
		textField1.setBackground(Color.green);
		textField1.setEnabled(false);
		textField1.setEditable(false);
		add(textField1, CC.xy(7, 5));

		//---- textField2 ----
		textField2.setBackground(Color.red);
		textField2.setEnabled(false);
		textField2.setEditable(false);
		add(textField2, CC.xy(9, 5));

		//---- checkPointComboBox1 ----
		checkPointComboBox1.setBackground(Color.red);
		checkPointComboBox1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkPointComboBox1ItemStateChanged(e);
			}
		});
		add(checkPointComboBox1, CC.xy(11, 5));

		//---- label4 ----
		label4.setText(bundle.getString("JIpicoReaderPanel.label4.text"));
		add(label4, CC.xy(13, 5));
		add(terminalTextField, CC.xywh(15, 5, 3, 1));

		//---- label3 ----
		label3.setText(bundle.getString("JIpicoReaderPanel.label3.text"));
		label3.setEnabled(false);
		add(label3, CC.xy(5, 7));

		//---- textField3 ----
		textField3.setBackground(Color.blue);
		textField3.setEnabled(false);
		textField3.setEditable(false);
		add(textField3, CC.xy(7, 7));

		//---- textField4 ----
		textField4.setBackground(Color.yellow);
		textField4.setEnabled(false);
		textField4.setEditable(false);
		add(textField4, CC.xy(9, 7));

		//---- checkPointComboBox2 ----
		checkPointComboBox2.setBackground(Color.red);
		checkPointComboBox2.setEnabled(false);
		checkPointComboBox2.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkPointComboBox2ItemStateChanged(e);
			}
		});
		add(checkPointComboBox2, CC.xy(11, 7));

		//---- applyCheckpointButton ----
		applyCheckpointButton.setText(bundle.getString("JIpicoReaderPanel.applyCheckpointButton.text"));
		applyCheckpointButton.setBackground(Color.red);
		applyCheckpointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyCheckpointButtonActionPerformed(e);
			}
		});
		add(applyCheckpointButton, CC.xy(13, 7));

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
	private JButton setTimeButton;
	private JButton removeReaderButton;
	private JLabel label2;
	private JComboBox<String> multipointComboBox;
	private JTextField textField1;
	private JTextField textField2;
	private JComboBox checkPointComboBox1;
	private JLabel label4;
	private JTextField terminalTextField;
	private JLabel label3;
	private JTextField textField3;
	private JTextField textField4;
	private JComboBox checkPointComboBox2;
	private JButton applyCheckpointButton;
	private JLabel label5;
	private JLabel tagsReadLabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;

	}

	@Override
	public void notifyTagReads(List<RawChipRead> chipReadList) {
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
