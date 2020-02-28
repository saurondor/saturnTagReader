/*
 * Created by JFormDesigner on Sat Feb 22 14:35:58 CST 2020
 */

package com.tiempometa.pandora.tagreader;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.checatuchip.preview.JPreviewConfig;
import com.tiempometa.pandora.checatuchip.preview.JPreviewFrame;
import com.tiempometa.pandora.ipicoreader.JImportBackupsFrame;
import com.tiempometa.pandora.ipicoreader.TagReadListener;
import com.tiempometa.timing.model.RawChipRead;
import com.tiempometa.timing.model.dao.RawChipReadDao;
import com.tiempometa.webservice.RegistrationWebservice;
import com.tiempometa.webservice.ResultsWebservice;
import com.tiempometa.webservice.model.Bib;
import com.tiempometa.webservice.model.ParticipantRegistration;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JReaderFrame extends JFrame implements JPandoraApplication, TagReadListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5737894657705021013L;
	private static final Logger logger = Logger.getLogger(JReaderFrame.class);
	static JSplashScreen splash = new JSplashScreen();
	private String eventTitle;
	JPreviewFrame previewFrame = new JPreviewFrame();
	String serverAddress = null;
	private RegistrationWebservice registrationWebservice;
	private ResultsWebservice resultsWebservice;
	ZoneId zoneId = null;

	/**
	 * 
	 */
	private void initWebserviceClient() {
		try {
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setServiceClass(RegistrationWebservice.class);
			String wsAddress = "http://" + serverAddress + ":9000/registrationClient";
			logger.info("Connecting webservice to " + wsAddress);
			factory.setAddress(wsAddress);
			registrationWebservice = (RegistrationWebservice) factory.create();
			logger.info("Registration client created");
			registrationWebservice.findByTag("TAG");
			String zoneIdString = registrationWebservice.getZoneId();
			try {
				zoneId = ZoneId.of(zoneIdString);
			} catch (DateTimeException e) {
				JOptionPane.showMessageDialog(this, "No se pudo establecer la zona horaria." + e.getMessage(),
						"Error de configuración", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			logger.info("Set timezone to " + zoneIdString);

			factory = new JaxWsProxyFactoryBean();
			factory.setServiceClass(ResultsWebservice.class);
			wsAddress = "http://" + serverAddress + ":9000/resultsClient";
			logger.info("Connecting webservice to " + wsAddress);
			factory.setAddress(wsAddress);
			resultsWebservice = (ResultsWebservice) factory.create();
			logger.info("Results client created");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"No se pudo conectar a la base de datos.\nVerifica que el Saturno esté funcionando y disponible en la dirección :"
							+ serverAddress + "\nError:" + e.getMessage(),
					"Error de conexión: ", JOptionPane.ERROR_MESSAGE);
		}
	}

	public JReaderFrame() {
		splash.setVisible(true);
		boolean connected = false;
		do {
			try {
				showProgress("Cargando configuración", 20);
				Context.setApplication(this);
				Context.loadSettings();
				serverAddress = Context.loadServerAddress();
				showProgress("Probando conexión al servidor @" + serverAddress, 40);
				initWebserviceClient();
				registrationWebservice.getZoneId();
//				Context.openEvent();
				connected = true;
//				Context.setApplicationTitle(Context.registrationHelper.getEvent().getTitle());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "No se pudo cargar la configuración " + e.getMessage(),
						"Error de configuración", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				dispose();
			} catch (Exception e) {
				logger.info("Exception opening database and event " + e.getClass().getCanonicalName());
				if (e instanceof org.springframework.dao.InvalidDataAccessResourceUsageException) {
					handleInvalidDatabaseSchema();
				} else {
//					connected = handleMissingDatabase(connected, e);
				}
//				JOptionPane.showMessageDialog(this, "Inicializar el evento. " + e.getMessage(),
//						"Error de configuración", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (ExceptionInInitializerError e) {
				JOptionPane.showMessageDialog(this,
						"No se pudo inicializar la aplicación. ¿Está corriendo la base de datos?",
						"Error iniciando aplicación", JOptionPane.ERROR_MESSAGE);
			} catch (NoClassDefFoundError e) {
				JOptionPane.showMessageDialog(this,
						"No se pudo inicializar la aplicación. Error inicializando application context. Error de sistema es: "
								+ e.getMessage(),
						"Error iniciando aplicación", JOptionPane.ERROR_MESSAGE);
				this.dispose();
				System.exit(1);
			}

		} while (!connected);
		showProgress("Iniciando componentes", 65);
		initComponents();
		showProgress("Iniciando servicios", 85);
		initListeners();
		splash.setVisible(false);
		readerListPanel.addIpicoEliteReader();
	}

	private void handleInvalidDatabaseSchema() {
		// TODO Auto-generated method stub

	}

//	private void openEvent(boolean b) {
//		JOpenEventDialog openEventDialog = new JOpenEventDialog(this);
//		logger.debug("Opening new event");
//		openEventDialog.setVisible(true);
//		logger.debug("Event opened");
//	}

//	private boolean handleMissingDatabase(boolean connected, Exception e) {
//		// if invalid database format
//		int response = JOptionPane.showConfirmDialog(this,
//				"La base de datos seleccionada no es compatible con esta versión. \n¿Deseas cambiar de base de datos?",
//				"Error conectando a la base", JOptionPane.YES_NO_CANCEL_OPTION);
//		switch (response) {
//		case JOptionPane.CANCEL_OPTION:
//			dispose();
//			System.exit(0);
//			break;
//		case JOptionPane.YES_OPTION:
//			openEvent(true);
//			return true;
//		case JOptionPane.NO_OPTION:
//			return false;
//		default:
//			return false;
//		}
//		return false;
//	}

	/**
	 * @param status
	 * @param progress
	 */
	private void showProgress(String status, Integer progress) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				splash.setProgres(status, progress);

			}
		});
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initListeners() {
		/**
		 * Handle application closing
		 */
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				closeApplication(we.getWindow());
			}
		});
		readerListPanel.setTagReadListener(this);

	}

	private void closeApplication(Window window) {
		if (Context.isApplicationCloseable()) {
			int response = JOptionPane.showConfirmDialog(null, "¿Seguro que desea cerrar la aplicación?",
					"Confirmar Cierre", JOptionPane.WARNING_MESSAGE);
			if (response == JOptionPane.OK_OPTION) {
				window.dispose();
				System.exit(0);
			}
		} else {
			JOptionPane.showMessageDialog(null, "No se puede cerrar la aplicación en este momento.", "Cierre",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		JReaderFrame timer = new JReaderFrame();
		timer.setVisible(true);
	}

	private void openEventMenuItemActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void closeMenuItemActionPerformed(ActionEvent e) {
		closeApplication(this);
	}

	private void importBackupMenuItemActionPerformed(ActionEvent e) {
		JImportBackupsFrame importFrame = new JImportBackupsFrame();
		importFrame.setVisible(true);
	}

	private void checaTuChipConfigurationMenuItemActionPerformed(ActionEvent e) {
		JPreviewConfig configPanel = new JPreviewConfig(this);
		configPanel.setVisible(true);
	}

	private void showPreviewMenuItemActionPerformed(ActionEvent e) {
		if (previewFrame.isVisible()) {
			previewFrame.setVisible(false);
		} else {
			previewFrame.setVisible(true);
		}
	}

	private void addUsbReaderMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addIpicoUsbReader();
	}

	private void addEliteReaderMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addIpicoEliteReader();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.ipicoreader.ipicoreader");
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		openEventMenuItem = new JMenuItem();
		closeMenuItem = new JMenuItem();
		menu2 = new JMenu();
		menu4 = new JMenu();
		addEliteReaderMenuItem = new JMenuItem();
		addUsbReaderMenuItem = new JMenuItem();
		importBackupMenuItem = new JMenuItem();
		menu5 = new JMenu();
		menuItem1 = new JMenuItem();
		menuItem5 = new JMenuItem();
		menu6 = new JMenu();
		menuItem2 = new JMenuItem();
		menuItem6 = new JMenuItem();
		menu7 = new JMenu();
		menuItem3 = new JMenuItem();
		menuItem4 = new JMenuItem();
		menuItem7 = new JMenuItem();
		menu8 = new JMenu();
		menuItem8 = new JMenuItem();
		menuItem9 = new JMenuItem();
		menu3 = new JMenu();
		checaTuChipConfigurationMenuItem = new JMenuItem();
		showPreviewMenuItem = new JMenuItem();
		readerListPanel = new JReaderListPanel();
		tagReadPanel = new JTagReadPanel();

		// ======== this ========
		setIconImage(new ImageIcon(
				getClass().getResource("/com/tiempometa/pandora/tagreader/tiempometa_icon_large_alpha.png"))
						.getImage());
		setTitle(bundle.getString("JIpicoReaderFrame.this.title"));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== menuBar1 ========
		{

			// ======== menu1 ========
			{
				menu1.setText(bundle.getString("JIpicoReaderFrame.menu1.text"));

				// ---- openEventMenuItem ----
				openEventMenuItem.setText(bundle.getString("JIpicoReaderFrame.openEventMenuItem.text"));
				openEventMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						openEventMenuItemActionPerformed(e);
					}
				});
				menu1.add(openEventMenuItem);

				// ---- closeMenuItem ----
				closeMenuItem.setText(bundle.getString("JIpicoReaderFrame.closeMenuItem.text"));
				closeMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						closeMenuItemActionPerformed(e);
					}
				});
				menu1.add(closeMenuItem);
			}
			menuBar1.add(menu1);

			// ======== menu2 ========
			{
				menu2.setText(bundle.getString("JIpicoReaderFrame.menu2.text"));

				// ======== menu4 ========
				{
					menu4.setText(bundle.getString("JIpicoReaderFrame.menu4.text"));

					// ---- addEliteReaderMenuItem ----
					addEliteReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addEliteReaderMenuItem.text"));
					addEliteReaderMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addEliteReaderMenuItemActionPerformed(e);
						}
					});
					menu4.add(addEliteReaderMenuItem);

					// ---- addUsbReaderMenuItem ----
					addUsbReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addUsbReaderMenuItem.text"));
					addUsbReaderMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addUsbReaderMenuItemActionPerformed(e);
						}
					});
					menu4.add(addUsbReaderMenuItem);

					// ---- importBackupMenuItem ----
					importBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importBackupMenuItem.text"));
					importBackupMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							importBackupMenuItemActionPerformed(e);
						}
					});
					menu4.add(importBackupMenuItem);
				}
				menu2.add(menu4);

				// ======== menu5 ========
				{
					menu5.setText(bundle.getString("JIpicoReaderFrame.menu5.text"));

					// ---- menuItem1 ----
					menuItem1.setText(bundle.getString("JIpicoReaderFrame.menuItem1.text"));
					menu5.add(menuItem1);

					// ---- menuItem5 ----
					menuItem5.setText(bundle.getString("JIpicoReaderFrame.menuItem5.text"));
					menu5.add(menuItem5);
				}
				menu2.add(menu5);

				// ======== menu6 ========
				{
					menu6.setText(bundle.getString("JIpicoReaderFrame.menu6.text"));

					// ---- menuItem2 ----
					menuItem2.setText(bundle.getString("JIpicoReaderFrame.menuItem2.text"));
					menu6.add(menuItem2);

					// ---- menuItem6 ----
					menuItem6.setText(bundle.getString("JIpicoReaderFrame.menuItem6.text"));
					menu6.add(menuItem6);
				}
				menu2.add(menu6);

				// ======== menu7 ========
				{
					menu7.setText(bundle.getString("JIpicoReaderFrame.menu7.text"));

					// ---- menuItem3 ----
					menuItem3.setText(bundle.getString("JIpicoReaderFrame.menuItem3.text"));
					menu7.add(menuItem3);

					// ---- menuItem4 ----
					menuItem4.setText(bundle.getString("JIpicoReaderFrame.menuItem4.text"));
					menu7.add(menuItem4);

					// ---- menuItem7 ----
					menuItem7.setText(bundle.getString("JIpicoReaderFrame.menuItem7.text"));
					menu7.add(menuItem7);
				}
				menu2.add(menu7);

				// ======== menu8 ========
				{
					menu8.setText(bundle.getString("JIpicoReaderFrame.menu8.text"));

					// ---- menuItem8 ----
					menuItem8.setText(bundle.getString("JIpicoReaderFrame.menuItem8.text"));
					menu8.add(menuItem8);

					// ---- menuItem9 ----
					menuItem9.setText(bundle.getString("JIpicoReaderFrame.menuItem9.text"));
					menu8.add(menuItem9);
				}
				menu2.add(menu8);
			}
			menuBar1.add(menu2);

			// ======== menu3 ========
			{
				menu3.setText(bundle.getString("JIpicoReaderFrame.menu3.text"));

				// ---- checaTuChipConfigurationMenuItem ----
				checaTuChipConfigurationMenuItem
						.setText(bundle.getString("JIpicoReaderFrame.checaTuChipConfigurationMenuItem.text"));
				checaTuChipConfigurationMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						checaTuChipConfigurationMenuItemActionPerformed(e);
					}
				});
				menu3.add(checaTuChipConfigurationMenuItem);

				// ---- showPreviewMenuItem ----
				showPreviewMenuItem.setText(bundle.getString("JIpicoReaderFrame.showPreviewMenuItem.text"));
				showPreviewMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showPreviewMenuItemActionPerformed(e);
					}
				});
				menu3.add(showPreviewMenuItem);
			}
			menuBar1.add(menu3);
		}
		setJMenuBar(menuBar1);
		contentPane.add(readerListPanel, BorderLayout.CENTER);
		contentPane.add(tagReadPanel, BorderLayout.EAST);
		setSize(805, 505);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JMenuBar menuBar1;
	private JMenu menu1;
	private JMenuItem openEventMenuItem;
	private JMenuItem closeMenuItem;
	private JMenu menu2;
	private JMenu menu4;
	private JMenuItem addEliteReaderMenuItem;
	private JMenuItem addUsbReaderMenuItem;
	private JMenuItem importBackupMenuItem;
	private JMenu menu5;
	private JMenuItem menuItem1;
	private JMenuItem menuItem5;
	private JMenu menu6;
	private JMenuItem menuItem2;
	private JMenuItem menuItem6;
	private JMenu menu7;
	private JMenuItem menuItem3;
	private JMenuItem menuItem4;
	private JMenuItem menuItem7;
	private JMenu menu8;
	private JMenuItem menuItem8;
	private JMenuItem menuItem9;
	private JMenu menu3;
	private JMenuItem checaTuChipConfigurationMenuItem;
	private JMenuItem showPreviewMenuItem;
	private JReaderListPanel readerListPanel;
	private JTagReadPanel tagReadPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	@Override
	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;

	}

	@Override
	public String getEventTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshTitle() {
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.ipicoreader.ipicoreader");
		setTitle(bundle.getString("JIpicoReaderFrame.this.title"));

	}

	@Override
	public void notifyTagReads(List<RawChipRead> readings) {
		logger.debug("GOT TAG READS...");
		List<com.tiempometa.webservice.model.RawChipRead> wsReadings = new ArrayList<com.tiempometa.webservice.model.RawChipRead>();
		for (RawChipRead tagRead : readings) {
			logger.debug(tagRead);
			tagReadPanel.add(tagRead);
			com.tiempometa.webservice.model.RawChipRead wsRead = tagReadToWsRead(tagRead);
			wsReadings.add(wsRead);
		}
		resultsWebservice.saveRawChipReads(wsReadings);
//		RawChipReadDao chipDao = (RawChipReadDao) Context.getCtx().getBean("rawChipReadDao");
//		chipDao.batchSave(readings);
		for (RawChipRead tagRead : readings) {
			logger.debug("Query participants by tag " + tagRead);
			List<ParticipantRegistration> registrationList = registrationWebservice.findByTag(tagRead.getRfidString());
			if (registrationList==null) {
				
			} else {
				logger.debug("Registration list size " + registrationList);
				showParticipantInfo(registrationList);				
			}
//			logger.debug("Query bibs by tag " + tagRead);
//			List<Bib> bibs = registrationWebservice.getBibByRfidString(tagRead.getRfidString());
//			logger.debug("Bib list size " + bibs);
		}
	}

	private void showParticipantInfo(List<ParticipantRegistration> registrationList) {
		for (ParticipantRegistration participantRegistration : registrationList) {
			previewFrame.setRegistration(participantRegistration);
		}

	}

	/**
	 * @param tagRead
	 * @return
	 */
	private com.tiempometa.webservice.model.RawChipRead tagReadToWsRead(RawChipRead tagRead) {
		com.tiempometa.webservice.model.RawChipRead wsRead = new com.tiempometa.webservice.model.RawChipRead();
		wsRead.setCheckPoint(tagRead.getCheckPoint());
		wsRead.setChipNumber(tagRead.getChipNumber());
		wsRead.setCooked(tagRead.getCooked());
		wsRead.setEventId(tagRead.getEventId());
		wsRead.setFiltered(tagRead.getFiltered());
		wsRead.setId(tagRead.getId());
		wsRead.setLoadName(tagRead.getLoadName());
		wsRead.setPhase(tagRead.getPhase());
//			wsRead.setReadTime(tagRead.getReadTime());
		wsRead.setReadType(tagRead.getReadType());
		wsRead.setRfidString(tagRead.getRfidString());
		wsRead.setTime(tagRead.getTime());
		wsRead.setTimeMillis(tagRead.getTimeMillis());
		return wsRead;
	}
}
