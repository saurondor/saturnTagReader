/*
 * Created by JFormDesigner on Tue Feb 25 08:10:28 CST 2020
 */

package com.tiempometa.pandora.foxberry.usb;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.ipicoreader.CommandResponseHandler;
import com.tiempometa.pandora.ipicoreader.JIpicoReaderPanel;
import com.tiempometa.pandora.ipicoreader.commands.IpicoCommand;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.JReaderListPanel;
import com.tiempometa.pandora.tagreader.JReaderPanel;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.timing.model.dao.RouteDao;
import com.tiempometa.webservice.model.CookedChipRead;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JFoxberryUsbReaderPanel extends JReaderPanel implements CommandResponseHandler, TagReadListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1089096348083032269L;
	private static final Logger logger = Logger.getLogger(JFoxberryUsbReaderPanel.class);

	public static final int MODE_CHECA_TU_CHIP = 0;
	public static final int MODE_CHECA_TU_RESULTADO = 1;
	public static final int MODE_ROUTE_POINT = 2;

	private JReaderListPanel listPanel;
	private FoxberryUSBClient reader = new FoxberryUSBClient();
	private String checkPoint1 = null;
	private String terminal = null;
	private TagReadListener tagReadListener;
	private int mode = MODE_CHECA_TU_CHIP;
	private Integer tagCount = 0;

	public JFoxberryUsbReaderPanel(JReaderListPanel listPanel) {
		initComponents();
		this.listPanel = listPanel;
		reader.setCommandResponseHandler(this);
		reader.registerTagReadListener(this);
		loadCheckPoints();
		try {
			loadSerialPorts();
		} catch (UnsatisfiedLinkError e) {
			int response = JOptionPane.showConfirmDialog(this,
					"El driver de puertos seriales no está instalado.\n¿Deseas instalar el driver ahora?",
					"Error de configuración", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (response == JOptionPane.YES_OPTION) {
				installDllFiles();
			}
		}
	}

	private void installDllFiles() {
		String user_home = System.getProperty("user.dir");
		String dllFileName = user_home + "\\rxtxSerial.dll";
		String driverFileName = user_home + "\\Ipico_USB_cdc.inf";
		logger.info("Installing driver and dll @ " + driverFileName + " - " + dllFileName);
		File dllFile = new File(dllFileName);
		if (!dllFile.exists()) {
			File driverFile = new File(driverFileName);
			FileOutputStream foStream;
			try {
				foStream = new FileOutputStream(dllFile);
				InputStream dllFileStream = null;
				if (System.getProperty("os.arch").equals("x86")) {
					dllFileStream = this.getClass().getResourceAsStream("/rxtx/32bit/rxtxSerial.dll");
				} else {
					this.getClass().getResourceAsStream("/rxtx/64bit/rxtxSerial.dll");
				}
				if (dllFileStream == null) {
					logger.error("Unable to find resource file ");
				} else {
					byte[] b = new byte[1024];
					int rAmount = 0;
					while (dllFileStream.available() > 0) {
						rAmount = dllFileStream.read(b);
						foStream.write(b, 0, rAmount);
						logger.debug("Writing new file: " + rAmount + " bytes");
					}
					dllFileStream.close();
					foStream.flush();
					foStream.close();
					JOptionPane.showMessageDialog(null, "El archivo se ha instalado con éxito.", "Instalación dll",
							JOptionPane.INFORMATION_MESSAGE);
				}
				foStream = new FileOutputStream(driverFile);
				dllFileStream = this.getClass().getResourceAsStream("/Ipico_USB_cdc.inf");
				if (dllFileStream == null) {
					logger.error("Unable to find resource file ");
				} else {
					byte[] b = new byte[1024];
					int rAmount = 0;
					while (dllFileStream.available() > 0) {
						rAmount = dllFileStream.read(b);
						foStream.write(b, 0, rAmount);
						logger.debug("Writing new file: " + rAmount + " bytes");
					}
					dllFileStream.close();
					foStream.flush();
					foStream.close();
					JOptionPane.showMessageDialog(null,
							"El archivo se ha instalado con éxito.\n Si requiere instalar el driver del lector USB puede usar el archivo Ipico_USB_cdc.inf\n que se ha ubicado en el directorio donde está este programa.",
							"Instalación driver", JOptionPane.INFORMATION_MESSAGE);
				}
				JOptionPane.showMessageDialog(this,
						"Se ha completado la instalación. Se debe reiniciar la aplicación ahora.", "Instalación",
						JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void loadSerialPorts() {
		List<String> serialPorts = reader.getSerialPorts();
		String[] checkPointArray = new String[serialPorts.size()];
		serialPortComboBox.setModel(new DefaultComboBoxModel<String>(serialPorts.toArray(checkPointArray)));
	}

	private void loadCheckPoints() {
		RouteDao rDao = (RouteDao) Context.getCtx().getBean("routeDao");
		List<String> checkPoints = rDao.getCheckPointNames();
		logger.debug("Available checkpoints ");
		for (String string : checkPoints) {
			logger.debug(string);
		}
		String[] checkPointArray = new String[checkPoints.size()];
		checkPointComboBox1.setModel(new DefaultComboBoxModel<String>(checkPoints.toArray(checkPointArray)));
	}

	private void connectButtonActionPerformed(ActionEvent e) {
		if (reader.isConnected()) {
			reader.disconnect();
			setDisconnected();
		} else {
			if (serialPortComboBox.getSelectedIndex() < 0) {
				JOptionPane.showMessageDialog(this, "Se debe seleccionar un puerto serial", "Puerto serial",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					reader.connect((String) serialPortComboBox.getSelectedItem());
					setConnected();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this,
							"No se pudo conectar " + e1.getMessage() + " - " + e1.getClass().getCanonicalName(),
							"Error de conexión", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private void setConnected() {
		connectButton.setText("Desconectar");
		connectButton.setBackground(Color.GREEN);
		removeReaderButton.setEnabled(false);
	}

	private void setDisconnected() {
		connectButton.setText("Conectar");
		connectButton.setBackground(Color.RED);
		removeReaderButton.setEnabled(true);
	}

	private void removeReaderButtonActionPerformed(ActionEvent e) {
		listPanel.removeReader(this);
	}

	private void checkPointComboBox1ItemStateChanged(ItemEvent e) {
		applyCheckpointButton.setBackground(Color.RED);
	}

	private void applyCheckpointButtonActionPerformed(ActionEvent e) {
		checkPoint1 = (String) checkPointComboBox1.getSelectedItem();
		terminal = terminalTextField.getText();
//		reader.setCheckPointOne(checkPoint1);
//		reader.setTerminal(terminalTextField.getText());
//		reader.setCheckPoint(checkPoint1);
		applyCheckpointButton.setBackground(Color.GREEN);
	}

	private void modeComboBoxItemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			switch (modeComboBox.getSelectedIndex()) {
			case 0:
				mode = MODE_CHECA_TU_CHIP;
				disableRoutePoint();
				break;
			case 1:
				mode = MODE_CHECA_TU_RESULTADO;
				disableRoutePoint();
				break;
			case 2:
				mode = MODE_ROUTE_POINT;
				enableRoutePoint();

				break;
			default:
				break;
			}

		}
	}

	private void enableRoutePoint() {
		pointLabel.setEnabled(true);
		checkPointComboBox1.setEnabled(true);
		applyCheckpointButton.setEnabled(true);
	}

	private void disableRoutePoint() {
		pointLabel.setEnabled(false);
		checkPointComboBox1.setEnabled(false);
		applyCheckpointButton.setEnabled(false);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.ipicoreader.ipicoreader");
		label1 = new JLabel();
		serialPortComboBox = new JComboBox();
		connectButton = new JButton();
		removeReaderButton = new JButton();
		label3 = new JLabel();
		modeComboBox = new JComboBox<>();
		label4 = new JLabel();
		terminalTextField = new JTextField();
		pointLabel = new JLabel();
		checkPointComboBox1 = new JComboBox();
		applyCheckpointButton = new JButton();
		label5 = new JLabel();
		tagsReadLabel = new JLabel();

		//======== this ========
		setMaximumSize(new Dimension(550, 120));
		setMinimumSize(new Dimension(550, 120));
		setPreferredSize(new Dimension(550, 120));
		setBorder(new TitledBorder(bundle.getString("JIpicoUsbReaderPanel.this.border")));
		setLayout(new FormLayout(
			"5dlu, $lcgap, default, $lcgap, 94dlu, $lcgap, 79dlu, $lcgap, 41dlu, $lcgap, 10dlu, $lcgap, 41dlu, $lcgap, 22dlu",
			"5dlu, 3*($lgap, default)"));

		//---- label1 ----
		label1.setText(bundle.getString("JIpicoUsbReaderPanel.label1.text"));
		add(label1, CC.xy(3, 3));
		add(serialPortComboBox, CC.xy(5, 3));

		//---- connectButton ----
		connectButton.setText(bundle.getString("JIpicoUsbReaderPanel.connectButton.text"));
		connectButton.setBackground(Color.red);
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectButtonActionPerformed(e);
			}
		});
		add(connectButton, CC.xy(7, 3));

		//---- removeReaderButton ----
		removeReaderButton.setIcon(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/tagreader/x-remove.png")));
		removeReaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeReaderButtonActionPerformed(e);
			}
		});
		add(removeReaderButton, CC.xy(15, 3));

		//---- label3 ----
		label3.setText(bundle.getString("JIpicoUsbReaderPanel.label3.text"));
		add(label3, CC.xy(3, 5));

		//---- modeComboBox ----
		modeComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
			"Checa Tu Chip",
			"Checa tu Resultado",
			"Punto en ruta"
		}));
		modeComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				modeComboBoxItemStateChanged(e);
			}
		});
		add(modeComboBox, CC.xy(5, 5));

		//---- label4 ----
		label4.setText(bundle.getString("JIpicoUsbReaderPanel.label4.text"));
		add(label4, CC.xywh(9, 5, 3, 1));
		add(terminalTextField, CC.xywh(13, 5, 3, 1));

		//---- pointLabel ----
		pointLabel.setText(bundle.getString("JIpicoUsbReaderPanel.pointLabel.text"));
		pointLabel.setEnabled(false);
		add(pointLabel, CC.xy(3, 7));

		//---- checkPointComboBox1 ----
		checkPointComboBox1.setBackground(Color.red);
		checkPointComboBox1.setEnabled(false);
		checkPointComboBox1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkPointComboBox1ItemStateChanged(e);
			}
		});
		add(checkPointComboBox1, CC.xy(5, 7));

		//---- applyCheckpointButton ----
		applyCheckpointButton.setText(bundle.getString("JIpicoUsbReaderPanel.applyCheckpointButton.text"));
		applyCheckpointButton.setBackground(Color.red);
		applyCheckpointButton.setEnabled(false);
		applyCheckpointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyCheckpointButtonActionPerformed(e);
			}
		});
		add(applyCheckpointButton, CC.xy(7, 7));

		//---- label5 ----
		label5.setText(bundle.getString("JIpicoUsbReaderPanel.label5.text"));
		add(label5, CC.xy(13, 7));

		//---- tagsReadLabel ----
		tagsReadLabel.setText(bundle.getString("JIpicoUsbReaderPanel.tagsReadLabel.text"));
		add(tagsReadLabel, CC.xy(15, 7));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel label1;
	private JComboBox serialPortComboBox;
	private JButton connectButton;
	private JButton removeReaderButton;
	private JLabel label3;
	private JComboBox<String> modeComboBox;
	private JLabel label4;
	private JTextField terminalTextField;
	private JLabel pointLabel;
	private JComboBox checkPointComboBox1;
	private JButton applyCheckpointButton;
	private JLabel label5;
	private JLabel tagsReadLabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
	@Override
	public void notifyTagReads(List<RawChipRead> chipReadList) {
		logger.info("Handle raw chip reads");
		for (RawChipRead rawChipRead : chipReadList) {
			rawChipRead.setCheckPoint(checkPoint1);
			rawChipRead.setLoadName(terminal);
			switch (mode) {
			case JFoxberryUsbReaderPanel.MODE_CHECA_TU_CHIP:
				rawChipRead.setReadType(CookedChipRead.TYPE_CHECATUCHIP);
				break;
			case JFoxberryUsbReaderPanel.MODE_CHECA_TU_RESULTADO:
				rawChipRead.setReadType(CookedChipRead.TYPE_CHECATURESULTADO);
				break;
			case JFoxberryUsbReaderPanel.MODE_ROUTE_POINT:
				rawChipRead.setReadType(CookedChipRead.TYPE_TAG);
				break;
			default:
				rawChipRead.setReadType(CookedChipRead.TYPE_CHECATUCHIP);
				break;
			}
			logger.debug(rawChipRead);
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

	public String getCheckPoint1() {
		return checkPoint1;
	}

	public TagReadListener getTagReadListener() {
		return tagReadListener;
	}

	public void setListPanel(JReaderListPanel listPanel) {
		this.listPanel = listPanel;
	}

	public void setCheckPoint1(String checkPoint1) {
		this.checkPoint1 = checkPoint1;
	}

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;
	}

	public void setTerminal(String terminal) {
		terminalTextField.setText(terminal);
		this.terminal = terminal;
	}
}
