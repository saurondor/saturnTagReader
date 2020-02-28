/*
 * Created by JFormDesigner on Sun Nov 17 07:54:32 CST 2019
 */

package com.tiempometa.pandora.macsha;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.ipicoreader.TagReadListener;
import com.tiempometa.pandora.macsha.commands.MacshaCommand;
import com.tiempometa.pandora.macsha.commands.PushTagsCommand;
import com.tiempometa.pandora.macsha.commands.ReadBatteryCommand;
import com.tiempometa.pandora.macsha.commands.SetBuzzerCommand;
import com.tiempometa.pandora.macsha.commands.SetTimeCommand;
import com.tiempometa.pandora.macsha.commands.StartCommand;
import com.tiempometa.pandora.macsha.commands.StopCommand;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.JReaderListPanel;
import com.tiempometa.timing.model.RawChipRead;
import com.tiempometa.timing.model.dao.RouteDao;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JMacshaReaderPanel extends JPanel implements CommandResponseHandler, TagReadListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2774027271874159969L;
	private static final Logger logger = Logger.getLogger(JMacshaReaderPanel.class);

	private JReaderListPanel listPanel;
	private TagReadListener tagReadListener;
	One4All reader = new One4All();
	Thread workerThread = null;
	private String checkPoint = null;
	private boolean started = false;
	private boolean buzzerStatus = true;
	private Integer tagsRead = 0;

	public JMacshaReaderPanel(JReaderListPanel listPanel) {
		super();
		this.listPanel = listPanel;
		initComponents();
		reader.setCommandResponseHandler(this);
		loadCheckPoints();
	}

	/**
	 * 
	 */
	private void loadCheckPoints() {
		RouteDao rDao = (RouteDao) Context.getCtx().getBean("routeDao");
		List<String> checkPoints = rDao.getCheckPointNames();
		logger.debug("Available checkpoints ");
		for (String string : checkPoints) {
			logger.debug(string);
		}
		String[] checkPointArray = new String[checkPoints.size()];
		checkPointComboBox.setModel(new DefaultComboBoxModel<String>(checkPoints.toArray(checkPointArray)));
	}

	public JMacshaReaderPanel() {
		initComponents();
		reader.setCommandResponseHandler(this);
		loadCheckPoints();
	}

	private void connectButtonActionPerformed(ActionEvent e) {
		if (reader.isConnected()) {
			try {
				reader.disconnect();
				connectButton.setText("Conectar");
				connectButton.setBackground(Color.RED);
				startReadingButton.setEnabled(false);
				setTimeButton.setEnabled(false);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "No se pudo desconectar. " + e1.getMessage(), "Error de conexión",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			try {
				Thread workerThread = new Thread(reader);
				if (ipHasPort()) {
					connectByIpPort();
				} else {
					connectByIp();
				}
				workerThread.start();
				connectButton.setText("Desconectar");
				connectButton.setBackground(Color.GREEN);
				startReadingButton.setEnabled(true);
				setTimeButton.setEnabled(true);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "No se pudo conectar. " + e1.getMessage(), "Error de conexión",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean ipHasPort() {
		return readerAddressTextField.getText().contains(":");
	}

	private void connectByIpPort() throws UnknownHostException, IOException {
		String[] readerIpPort = readerAddressTextField.getText().split(":");
		String ipAddress = readerIpPort[0];
		Integer port = Integer.valueOf(readerIpPort[1]);
		logger.info("Connecting to " + ipAddress + ":" + port);
		reader.connect(ipAddress, port);
	}

	private void connectByIp() throws UnknownHostException, IOException {
		String ipAddress = readerAddressTextField.getText();
		logger.info("Connecting to " + ipAddress + " with default port");
		reader.connect(ipAddress);
	}

	private void startReadingButtonActionPerformed(ActionEvent e) {
		if (started) {
			reader.sendCommand(new StopCommand());
			started = false;
			startReadingButton.setText("Iniciar Lectura");
		} else {
			reader.sendCommand(new StartCommand());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			reader.sendCommand(new PushTagsCommand());
			started = true;
			startReadingButton.setText("Detener Lectura");
		}
	}

	private void setTimeButtonActionPerformed(ActionEvent e) {
		reader.sendCommand(new SetTimeCommand());
	}

	private void setBuzzerButtonActionPerformed(ActionEvent e) {
		if (buzzerStatus) {
			buzzerStatus = false;
			setBuzzerButton.setText("Encender Buzzer");
		} else {
			buzzerStatus = true;
			setBuzzerButton.setText("Apagar Buzzer");
		}
		reader.sendCommand(new SetBuzzerCommand(buzzerStatus));
	}

	private void openBackupsButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void removeReaderButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void applyCheckpointButtonActionPerformed(ActionEvent e) {
		checkPoint = (String) checkPointComboBox.getSelectedItem();
		reader.setCheckPoint(checkPoint);
		applyCheckpointButton.setBackground(Color.GREEN);
	}

	private void checkPointComboBoxItemStateChanged(ItemEvent e) {
		applyCheckpointButton.setBackground(Color.RED);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.macsha.macsha");
		label1 = new JLabel();
		readerAddressTextField = new JTextField();
		connectButton = new JButton();
		startReadingButton = new JButton();
		removeReaderButton = new JButton();
		label2 = new JLabel();
		checkPointComboBox = new JComboBox();
		applyCheckpointButton = new JButton();
		newFileButton = new JButton();
		acPowerRadioButton = new JLabel();
		batteryLabel = new JLabel();
		label5 = new JLabel();
		tagsReadLabel = new JLabel();
		openBackupsButton = new JButton();
		setTimeButton = new JButton();
		setBuzzerButton = new JButton();

		//======== this ========
		setBorder(new TitledBorder("Macsha One4All"));
		setMaximumSize(new Dimension(550, 120));
		setMinimumSize(new Dimension(550, 120));
		setPreferredSize(new Dimension(550, 120));
		setLayout(new FormLayout(
			"11dlu, $lcgap, default, $lcgap, 65dlu, $lcgap, 70dlu, $lcgap, 75dlu, $lcgap, 35dlu, $lcgap, 18dlu",
			"3*(default, $lgap), default"));

		//---- label1 ----
		label1.setText(bundle.getString("JMacshaReaderPanel.label1.text"));
		add(label1, CC.xy(3, 1));
		add(readerAddressTextField, CC.xy(5, 1));

		//---- connectButton ----
		connectButton.setText(bundle.getString("JMacshaReaderPanel.connectButton.text"));
		connectButton.setBackground(Color.red);
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectButtonActionPerformed(e);
			}
		});
		add(connectButton, CC.xy(7, 1));

		//---- startReadingButton ----
		startReadingButton.setText(bundle.getString("JMacshaReaderPanel.startReadingButton.text"));
		startReadingButton.setEnabled(false);
		startReadingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startReadingButtonActionPerformed(e);
			}
		});
		add(startReadingButton, CC.xy(9, 1));

		//---- removeReaderButton ----
		removeReaderButton.setIcon(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/tagreader/x-remove.png")));
		removeReaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeReaderButtonActionPerformed(e);
			}
		});
		add(removeReaderButton, CC.xy(13, 1));

		//---- label2 ----
		label2.setText(bundle.getString("JMacshaReaderPanel.label2.text"));
		add(label2, CC.xy(3, 3));

		//---- checkPointComboBox ----
		checkPointComboBox.setBackground(Color.red);
		checkPointComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkPointComboBoxItemStateChanged(e);
			}
		});
		add(checkPointComboBox, CC.xy(5, 3));

		//---- applyCheckpointButton ----
		applyCheckpointButton.setText(bundle.getString("JMacshaReaderPanel.applyCheckpointButton.text"));
		applyCheckpointButton.setBackground(Color.red);
		applyCheckpointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyCheckpointButtonActionPerformed(e);
			}
		});
		add(applyCheckpointButton, CC.xy(7, 3));

		//---- newFileButton ----
		newFileButton.setText(bundle.getString("JMacshaReaderPanel.newFileButton.text"));
		newFileButton.setEnabled(false);
		add(newFileButton, CC.xy(9, 3));

		//---- acPowerRadioButton ----
		acPowerRadioButton.setText(bundle.getString("JMacshaReaderPanel.acPowerRadioButton.text"));
		acPowerRadioButton.setFocusable(false);
		acPowerRadioButton.setForeground(Color.red);
		acPowerRadioButton.setEnabled(false);
		add(acPowerRadioButton, CC.xy(3, 5));

		//---- batteryLabel ----
		batteryLabel.setText(bundle.getString("JMacshaReaderPanel.batteryLabel.text"));
		batteryLabel.setEnabled(false);
		add(batteryLabel, CC.xywh(5, 5, 3, 1));

		//---- label5 ----
		label5.setText(bundle.getString("JMacshaReaderPanel.label5.text"));
		add(label5, CC.xy(9, 5));

		//---- tagsReadLabel ----
		tagsReadLabel.setText(bundle.getString("JMacshaReaderPanel.tagsReadLabel.text"));
		add(tagsReadLabel, CC.xy(11, 5));

		//---- openBackupsButton ----
		openBackupsButton.setText(bundle.getString("JMacshaReaderPanel.openBackupsButton.text"));
		openBackupsButton.setEnabled(false);
		openBackupsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openBackupsButtonActionPerformed(e);
			}
		});
		add(openBackupsButton, CC.xywh(3, 7, 3, 1));

		//---- setTimeButton ----
		setTimeButton.setText(bundle.getString("JMacshaReaderPanel.setTimeButton.text"));
		setTimeButton.setEnabled(false);
		setTimeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTimeButtonActionPerformed(e);
			}
		});
		add(setTimeButton, CC.xy(7, 7));

		//---- setBuzzerButton ----
		setBuzzerButton.setText(bundle.getString("JMacshaReaderPanel.setBuzzerButton.text"));
		setBuzzerButton.setEnabled(false);
		setBuzzerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setBuzzerButtonActionPerformed(e);
			}
		});
		add(setBuzzerButton, CC.xy(9, 7));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel label1;
	private JTextField readerAddressTextField;
	private JButton connectButton;
	private JButton startReadingButton;
	private JButton removeReaderButton;
	private JLabel label2;
	private JComboBox checkPointComboBox;
	private JButton applyCheckpointButton;
	private JButton newFileButton;
	private JLabel acPowerRadioButton;
	private JLabel batteryLabel;
	private JLabel label5;
	private JLabel tagsReadLabel;
	private JButton openBackupsButton;
	private JButton setTimeButton;
	private JButton setBuzzerButton;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public TagReadListener getTagReadListener() {
		return tagReadListener;
	}

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;
		reader.setTagReadListener(this);
	}

	@Override
	public void handleCommandResponse(MacshaCommand command) {
		logger.info("Handling command " + command.getClass().getCanonicalName());
		if (command instanceof SetTimeCommand) {
			JOptionPane.showMessageDialog(this, "Se fijó la hora del reader");
		}
		if (command instanceof ReadBatteryCommand) {
			ReadBatteryCommand batteryCommand = (ReadBatteryCommand) command;
			batteryLabel.setText("Voltaje : " + batteryCommand.getVoltage() + "V Carga : "
					+ batteryCommand.getCharge().intValue() + " %");
			batteryLabel.setEnabled(true);
			acPowerRadioButton.setEnabled(true);

			if (batteryCommand.getCharge() > 50) {
				batteryLabel.setForeground(Color.BLACK);
			} else if (batteryCommand.getCharge() > 30) {
				batteryLabel.setForeground(Color.ORANGE);
			} else {
				batteryLabel.setForeground(Color.RED);
			}
			if (batteryCommand.isHasPower()) {
				acPowerRadioButton.setText("AC Conectado");
				acPowerRadioButton.setForeground(Color.GREEN);
			} else {
				acPowerRadioButton.setText("AC No Conectado");
				acPowerRadioButton.setForeground(Color.RED);
			}
		}

	}

	@Override
	public void notifyCommException(IOException e) {
		JOptionPane.showMessageDialog(this, "Se ha perdido la conexión al reader. " + e.getMessage(),
				"Error de conexión", JOptionPane.ERROR_MESSAGE);
		shutdownReader();
	}

	private void shutdownReader() {
		try {
			reader.stop();
			connectButton.setText("Conectar");
			connectButton.setBackground(Color.RED);
			batteryLabel.setEnabled(false);
			acPowerRadioButton.setEnabled(false);
			started = false;
			startReadingButton.setText("Iniciar Lectura");
			startReadingButton.setEnabled(false);
			setTimeButton.setEnabled(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "No se pudo detener el servicio. " + e.getMessage(),
					"Error de conexión", JOptionPane.ERROR_MESSAGE);
		}

	}

	@Override
	public void notifyTagReads(List<RawChipRead> chipReadList) {
		tagsRead = tagsRead + chipReadList.size();
		tagsReadLabel.setText(tagsRead.toString());
		tagReadListener.notifyTagReads(chipReadList);
	}
}
