/*
 * Created by JFormDesigner on Sun Nov 17 07:54:32 CST 2019
 */

package com.tiempometa.pandora.macsha;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.macsha.commands.MacshaCommand;
//import com.tiempometa.pandora.macsha.commands.one4all.PushTagsCommand;
//import com.tiempometa.pandora.macsha.commands.one4all.ReadBatteryCommand;
//import com.tiempometa.pandora.macsha.commands.one4all.SetBuzzerCommand;
//import com.tiempometa.pandora.macsha.commands.one4all.SetTimeCommand;
import com.tiempometa.pandora.macsha.commands.ocelot.StartCommand;
import com.tiempometa.pandora.macsha.commands.ocelot.StopCommand;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.JReaderListPanel;
import com.tiempometa.pandora.tagreader.JReaderPanel;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.webservice.model.CookedChipRead;
import com.tiempometa.webservice.model.RawChipRead;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JMacshaOcelotPanel extends JReaderPanel implements CommandResponseHandler, TagReadListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2774027271874159969L;
	private static final Logger logger = Logger.getLogger(JMacshaOcelotPanel.class);

	private JReaderListPanel listPanel;
	private TagReadListener tagReadListener;
	Ocelot reader = new Ocelot();
	Thread workerThread = null;
	private String checkPoint = null;
	private boolean started = false;
	private Integer tagsRead = 0;
	boolean playback = false;

	public JMacshaOcelotPanel(JReaderListPanel listPanel) {
		super();
		this.listPanel = listPanel;
		initComponents();
		reader.setCommandResponseHandler(this);
		loadCheckPoints();
		startReader();
	}

	private void startReader() {
		workerThread = new Thread(reader);
		workerThread.start();
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
		checkPointComboBox.setModel(new DefaultComboBoxModel<String>(checkPoints.toArray(checkPointArray)));
	}

	public JMacshaOcelotPanel() {
		initComponents();
		reader.setCommandResponseHandler(this);
		loadCheckPoints();
		startReader();
	}

	private void connectButtonActionPerformed(ActionEvent e) {
		doConnectButton();
	}

	private void doConnectButton() {
		if (reader.isConnected()) {
			try {
				disconnectReader();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "No se pudo desconectar. " + e1.getMessage(), "Error de conexión",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			try {
				if (checkPoint == null) {
					JOptionPane.showMessageDialog(this, "Se debe seleccionar un punto antes de conectar",
							"Error de configuración", JOptionPane.WARNING_MESSAGE);
				} else {
					connectReader();
				}
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "No se pudo conectar. " + e1.getMessage(), "Error de conexión",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void connectReader() throws UnknownHostException, IOException {
		if (ipHasPort()) {
			connectByIpPort();
		} else {
			connectByIp();
		}
		synchronized (workerThread) {
			playback = false;
		}
		connectButton.setText("Desconectar");
		connectButton.setBackground(Color.GREEN);
		startReadingButton.setEnabled(true);
	}

	/**
	 * @throws IOException
	 */
	private void disconnectReader() throws IOException {
		reader.disconnect();
		connectButton.setText("Conectar");
		connectButton.setBackground(Color.RED);
		startReadingButton.setEnabled(false);
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
			logger.info("Send start command");
			reader.sendCommand(new StartCommand());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			started = true;
			startReadingButton.setText("Detener Lectura");
		}
	}

	private void removeReaderButtonActionPerformed(ActionEvent e) {
		listPanel.removeReader(this);
	}

	private void applyCheckpointButtonActionPerformed(ActionEvent e) {
		checkPoint = (String) checkPointComboBox.getSelectedItem();
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
		modeComboBox = new JComboBox<>();
		label5 = new JLabel();
		tagsReadLabel = new JLabel();

		// ======== this ========
		setBorder(new TitledBorder("Macsha Ocelot"));
		setMaximumSize(new Dimension(550, 120));
		setMinimumSize(new Dimension(550, 120));
		setPreferredSize(new Dimension(550, 120));
		setLayout(new FormLayout("11dlu, $lcgap, 60dlu, $lcgap, 55dlu, 2*($lcgap, 69dlu), $lcgap, 54dlu, $lcgap, 18dlu",
				"3*(default, $lgap), 16dlu"));

		// ---- label1 ----
		label1.setText(bundle.getString("JMacshaReaderPanel.label1.text"));
		add(label1, CC.xy(3, 1));
		add(readerAddressTextField, CC.xy(5, 1));

		// ---- connectButton ----
		connectButton.setText(bundle.getString("JMacshaReaderPanel.connectButton.text"));
		connectButton.setBackground(Color.red);
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectButtonActionPerformed(e);
			}
		});
		add(connectButton, CC.xy(7, 1));

		// ---- startReadingButton ----
		startReadingButton.setText(bundle.getString("JMacshaReaderPanel.startReadingButton.text"));
		startReadingButton.setEnabled(false);
		startReadingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startReadingButtonActionPerformed(e);
			}
		});
		add(startReadingButton, CC.xywh(9, 1, 3, 1));

		// ---- removeReaderButton ----
		removeReaderButton
				.setIcon(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/tagreader/x-remove.png")));
		removeReaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeReaderButtonActionPerformed(e);
			}
		});
		add(removeReaderButton, CC.xy(13, 1));

		// ---- label2 ----
		label2.setText(bundle.getString("JMacshaReaderPanel.label2.text"));
		add(label2, CC.xy(3, 3));

		// ---- checkPointComboBox ----
		checkPointComboBox.setBackground(Color.red);
		checkPointComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkPointComboBoxItemStateChanged(e);
			}
		});
		add(checkPointComboBox, CC.xy(5, 3));

		// ---- applyCheckpointButton ----
		applyCheckpointButton.setText(bundle.getString("JMacshaReaderPanel.applyCheckpointButton.text"));
		applyCheckpointButton.setBackground(Color.red);
		applyCheckpointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyCheckpointButtonActionPerformed(e);
			}
		});
		add(applyCheckpointButton, CC.xy(7, 3));

		// ---- modeComboBox ----
		modeComboBox.setModel(new DefaultComboBoxModel<>(
				new String[] { "Modo ruta", "Modo checa tu chip", "Modo checa tu resultado" }));
		add(modeComboBox, CC.xywh(9, 3, 3, 1));

		// ---- label5 ----
		label5.setText(bundle.getString("JMacshaReaderPanel.label5.text"));
		add(label5, CC.xy(9, 5));

		// ---- tagsReadLabel ----
		tagsReadLabel.setText(bundle.getString("JMacshaReaderPanel.tagsReadLabel.text"));
		add(tagsReadLabel, CC.xy(11, 5));
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
	private JComboBox<String> modeComboBox;
	private JLabel label5;
	private JLabel tagsReadLabel;
	private boolean retrying = false;
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
			started = false;
			startReadingButton.setText("Iniciar Lectura");
			startReadingButton.setEnabled(false);
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
		for (RawChipRead rawChipRead : chipReadList) {
			rawChipRead.setCheckPoint(checkPoint);
			switch (modeComboBox.getSelectedIndex()) {
			case 0:
				rawChipRead.setReadType(CookedChipRead.TYPE_TAG);
				break;
			case 1:
				rawChipRead.setReadType(CookedChipRead.TYPE_CHECATUCHIP);
				break;
			case 2:
				rawChipRead.setReadType(CookedChipRead.TYPE_CHECATURESULTADO);
				break;
			default:
				rawChipRead.setReadType(CookedChipRead.TYPE_TAG);
				break;
			}
		}
		tagReadListener.notifyTagReads(chipReadList);
	}

	@Override
	public void notifyTimeout() {
		logger.error("NOTIFIED TIMEOUT OCELOT");
		if (retrying) {
			logger.warn("Still waiting to reconnect...");
		} else {
			synchronized (this) {
				retrying = true;
			}
			try {
				disconnectReader();
			} catch (IOException e1) {
				logger.error("Unable to disconnect from reader on timeout. " + e1.getMessage());
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
//			for (int i = 0; i < 5; i++) {
			logger.info("Trying to reconnect...");
			try {
				connectReader();
			} catch (IOException e1) {
				logger.error("Unable to connect to reader on timeout. " + e1.getMessage());
				playback = true;
				startWarningPlayback();
			}
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//				}
//			}
			synchronized (this) {
				retrying = false;
			}
		}

	}

	/**
	 * 
	 */
	private void startWarningPlayback() {
		(new Thread(new Runnable() {

			@Override
			public void run() {
				boolean run = playback;
				while (run) {
					synchronized (workerThread) {
						run = playback;
					}
					try {
						playWarning();
						logger.info("Done with warning playback");
					} catch (JavaLayerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		})).start();
	}

	private void playWarning() throws JavaLayerException {
		InputStream mp3Stream = this.getClass().getResourceAsStream("/keepalive.mp3");
		Player player = new Player(mp3Stream);
		player.play();
	}
}
