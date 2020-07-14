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
import com.tiempometa.pandora.foxberry.FoxberryBackupImporter;
import com.tiempometa.pandora.ipicoreader.IpicoBackupImporter;
import com.tiempometa.pandora.macsha.MacshaBackupImporter;
import com.tiempometa.pandora.macsha.MacshaCloudBackupImporter;
import com.tiempometa.pandora.macsha.MacshaOcelotBackupImporter;
import com.tiempometa.pandora.rfidtiming.UltraBackupImporter;
import com.tiempometa.pandora.timinsense.TimingsenseBackupImporter;
import com.tiempometa.webservice.RegistrationWebservice;
import com.tiempometa.webservice.ResultsWebservice;
import com.tiempometa.webservice.model.Bib;
import com.tiempometa.webservice.model.ParticipantRegistration;
import com.tiempometa.webservice.model.RawChipRead;

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

	/**
	 * 
	 */
	private void initWebserviceClient() {
		boolean retry = true;
		do {
			try {
				Context.initWebserviceClients();
				retry = false;
			} catch (DateTimeException e) {
				JOptionPane.showMessageDialog(this, "No se pudo establecer la zona horaria." + e.getMessage(),
						"Error de configuración", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				int response = JOptionPane.showConfirmDialog(this, "¿Deseas reintentar?",
						"Error solicitando configuración", JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.NO_OPTION) {
					retry = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						"No se pudo conectar a la base de datos.\nVerifica que el Saturno esté funcionando y disponible en la dirección :"
								+ Context.getServerAddress() + "\nError:" + e.getMessage(),
						"Error de conexión: ", JOptionPane.ERROR_MESSAGE);
				int response = JOptionPane.showConfirmDialog(this, "¿Deseas cambiar la dirección IP?",
						"Error conectando a Saturno", JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.YES_OPTION) {
					String ipAddress = JOptionPane.showInputDialog("Dirección IP", Context.getServerAddress());
					if (ipAddress == null) {

					} else {
						try {
							Context.setServerAddress(ipAddress);
							logger.info("Updated server address to " + Context.getServerAddress());
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(this,
									"No se pudo guardar la configuración. " + e1.getMessage(), "Error de configuración",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					response = JOptionPane.showConfirmDialog(this, "¿Deseas reintentar conectar con Saturno?",
							"Error conectando a Saturno", JOptionPane.YES_NO_OPTION);
					if (response == JOptionPane.NO_OPTION) {
//						retry = false;
						System.exit(ERROR);
					}
				}
			}
		} while (retry);
	}

	public JReaderFrame() {
		splash.setVisible(true);
		boolean connected = false;
		do {
			try {
				showProgress("Cargando configuración", 20);
				Context.setApplication(this);
				Context.loadSettings();
				Context.previewHelper.getSettings().loadSettings();
//				Context.loadServerAddress();
				showProgress("Probando conexión al servidor @" + Context.getServerAddress(), 40);
				initWebserviceClient();
				Context.getZoneId();
				connected = true;
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
				e.printStackTrace();
			} catch (ExceptionInInitializerError e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						"No se pudo inicializar la aplicación. ¿Está corriendo la base de datos?",
						"Error iniciando aplicación", JOptionPane.ERROR_MESSAGE);
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
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
		String response = JOptionPane.showInputDialog("Dirección IP del servidor remoto o 127.0.0.1 para local", Context.getServerAddress());
		if (response == null) {

		} else {
			try {
				Context.setServerAddress(response);
				logger.info("Updated server address to " + Context.getServerAddress());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "No se pudo guardar la configuración. " + e1.getMessage(),
						"Error de configuración", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void closeMenuItemActionPerformed(ActionEvent e) {
		closeApplication(this);
	}

	private void checaTuChipConfigurationMenuItemActionPerformed(ActionEvent e) {
		JPreviewConfig configPanel = new JPreviewConfig(this);
		configPanel.setVisible(true);
	}

	private void showPreviewMenuItemActionPerformed(ActionEvent e) {
		if (previewFrame.isVisible()) {
			previewFrame.setVisible(false);
		} else {
			previewFrame.prepareView();
			previewFrame.loadImages();
			previewFrame.setVisible(true);
		}
	}

	private void addUsbReaderMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addIpicoUsbReader();
	}

	private void addEliteReaderMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addIpicoEliteReader();
	}

	private void addOne4AllReaderMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addOne4AllReader();
	}

	private void importIpicoBackupMenuItemActionPerformed(ActionEvent e) {
		JImportBackupsFrame importFrame = new JImportBackupsFrame(new IpicoBackupImporter());
		importFrame.setVisible(true);
	}

	private void addUltraReaderMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addUltraReader();
	}

	private void addFoxberryReaderMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addFoxberryReader();
	}

	private void addFoxberryUSBReaderMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addFoxberryUSBReader();
	}

	private void importMacshaBackupMenuItemActionPerformed(ActionEvent e) {
		JImportBackupsFrame importFrame = new JImportBackupsFrame(new MacshaBackupImporter());
		importFrame.setVisible(true);
	}

	private void importTimingSenseBackupMenuItemActionPerformed(ActionEvent e) {
		JImportBackupsFrame importFrame = new JImportBackupsFrame(new TimingsenseBackupImporter());
		importFrame.setVisible(true);
	}

	private void importFoxberryBackupMenuItemActionPerformed(ActionEvent e) {
		JImportBackupsFrame importFrame = new JImportBackupsFrame(new FoxberryBackupImporter());
		importFrame.setVisible(true);
	}

	private void importUltraBackupMenuItemActionPerformed(ActionEvent e) {
		JImportBackupsFrame importFrame = new JImportBackupsFrame(new UltraBackupImporter());
		importFrame.setVisible(true);
	}

	private void addTSCollectorMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addTSCollector();
	}

	private void importMacshaCloudBackupMenuItemActionPerformed(ActionEvent e) {
		JImportBackupsFrame importFrame = new JImportBackupsFrame(new MacshaCloudBackupImporter());
		importFrame.setVisible(true);
	}

	private void addOcelotReaderMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addOcelot();
	}

	private void importMacshaOcelotBackupMenuItemActionPerformed(ActionEvent e) {
		JImportBackupsFrame importFrame = new JImportBackupsFrame(new MacshaOcelotBackupImporter());
		importFrame.setVisible(true);
	}

	private void tiempoMetaCloudMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addTiempoMetaCloud();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.tagreader.tagreader");
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		openEventMenuItem = new JMenuItem();
		closeMenuItem = new JMenuItem();
		menu2 = new JMenu();
		menu4 = new JMenu();
		addEliteReaderMenuItem = new JMenuItem();
		addUsbReaderMenuItem = new JMenuItem();
		importIpicoBackupMenuItem = new JMenuItem();
		menu5 = new JMenu();
		addOne4AllReaderMenuItem = new JMenuItem();
		addOcelotReaderMenuItem = new JMenuItem();
		importMacshaBackupMenuItem = new JMenuItem();
		importMacshaOcelotBackupMenuItem = new JMenuItem();
		importMacshaCloudBackupMenuItem = new JMenuItem();
		menuTimingSense = new JMenu();
		addTSCollectorMenuItem = new JMenuItem();
		importTimingSenseBackupMenuItem = new JMenuItem();
		menu7 = new JMenu();
		addFoxberryReaderMenuItem = new JMenuItem();
		addFoxberryUSBReaderMenuItem = new JMenuItem();
		importFoxberryBackupMenuItem = new JMenuItem();
		tiempoMetaCloudMenuItem = new JMenuItem();
		menu8 = new JMenu();
		addUltraReaderMenuItem = new JMenuItem();
		importUltraBackupMenuItem = new JMenuItem();
		menu3 = new JMenu();
		checaTuChipConfigurationMenuItem = new JMenuItem();
		showPreviewMenuItem = new JMenuItem();
		readerListPanel = new JReaderListPanel();
		tagReadPanel = new JTagReadPanel();

		//======== this ========
		setIconImage(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/tagreader/tiempometa_icon_large_alpha.png")).getImage());
		setTitle(bundle.getString("JIpicoReaderFrame.this.title"));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== menuBar1 ========
		{

			//======== menu1 ========
			{
				menu1.setText(bundle.getString("JIpicoReaderFrame.menu1.text"));

				//---- openEventMenuItem ----
				openEventMenuItem.setText(bundle.getString("JIpicoReaderFrame.openEventMenuItem.text"));
				openEventMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						openEventMenuItemActionPerformed(e);
					}
				});
				menu1.add(openEventMenuItem);

				//---- closeMenuItem ----
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

			//======== menu2 ========
			{
				menu2.setText(bundle.getString("JIpicoReaderFrame.menu2.text"));

				//======== menu4 ========
				{
					menu4.setText(bundle.getString("JIpicoReaderFrame.menu4.text"));

					//---- addEliteReaderMenuItem ----
					addEliteReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addEliteReaderMenuItem.text"));
					addEliteReaderMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addEliteReaderMenuItemActionPerformed(e);
						}
					});
					menu4.add(addEliteReaderMenuItem);

					//---- addUsbReaderMenuItem ----
					addUsbReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addUsbReaderMenuItem.text"));
					addUsbReaderMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addUsbReaderMenuItemActionPerformed(e);
						}
					});
					menu4.add(addUsbReaderMenuItem);

					//---- importIpicoBackupMenuItem ----
					importIpicoBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importIpicoBackupMenuItem.text"));
					importIpicoBackupMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							importIpicoBackupMenuItemActionPerformed(e);
						}
					});
					menu4.add(importIpicoBackupMenuItem);
				}
				menu2.add(menu4);

				//======== menu5 ========
				{
					menu5.setText(bundle.getString("JIpicoReaderFrame.menu5.text"));

					//---- addOne4AllReaderMenuItem ----
					addOne4AllReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addOne4AllReaderMenuItem.text"));
					addOne4AllReaderMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addOne4AllReaderMenuItemActionPerformed(e);
						}
					});
					menu5.add(addOne4AllReaderMenuItem);

					//---- addOcelotReaderMenuItem ----
					addOcelotReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addOcelotReaderMenuItem.text"));
					addOcelotReaderMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addOcelotReaderMenuItemActionPerformed(e);
						}
					});
					menu5.add(addOcelotReaderMenuItem);

					//---- importMacshaBackupMenuItem ----
					importMacshaBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importMacshaBackupMenuItem.text"));
					importMacshaBackupMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							importMacshaBackupMenuItemActionPerformed(e);
						}
					});
					menu5.add(importMacshaBackupMenuItem);

					//---- importMacshaOcelotBackupMenuItem ----
					importMacshaOcelotBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importMacshaOcelotBackupMenuItem.text"));
					importMacshaOcelotBackupMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							importMacshaOcelotBackupMenuItemActionPerformed(e);
						}
					});
					menu5.add(importMacshaOcelotBackupMenuItem);

					//---- importMacshaCloudBackupMenuItem ----
					importMacshaCloudBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importMacshaCloudBackupMenuItem.text"));
					importMacshaCloudBackupMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							importMacshaCloudBackupMenuItemActionPerformed(e);
						}
					});
					menu5.add(importMacshaCloudBackupMenuItem);
				}
				menu2.add(menu5);

				//======== menuTimingSense ========
				{
					menuTimingSense.setText(bundle.getString("JIpicoReaderFrame.menuTimingSense.text"));

					//---- addTSCollectorMenuItem ----
					addTSCollectorMenuItem.setText(bundle.getString("JIpicoReaderFrame.addTSCollectorMenuItem.text"));
					addTSCollectorMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addTSCollectorMenuItemActionPerformed(e);
						}
					});
					menuTimingSense.add(addTSCollectorMenuItem);

					//---- importTimingSenseBackupMenuItem ----
					importTimingSenseBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importTimingSenseBackupMenuItem.text"));
					importTimingSenseBackupMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							importTimingSenseBackupMenuItemActionPerformed(e);
						}
					});
					menuTimingSense.add(importTimingSenseBackupMenuItem);
				}
				menu2.add(menuTimingSense);

				//======== menu7 ========
				{
					menu7.setText(bundle.getString("JIpicoReaderFrame.menu7.text"));

					//---- addFoxberryReaderMenuItem ----
					addFoxberryReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addFoxberryReaderMenuItem.text"));
					addFoxberryReaderMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addFoxberryReaderMenuItemActionPerformed(e);
						}
					});
					menu7.add(addFoxberryReaderMenuItem);

					//---- addFoxberryUSBReaderMenuItem ----
					addFoxberryUSBReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addFoxberryUSBReaderMenuItem.text"));
					addFoxberryUSBReaderMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addFoxberryUSBReaderMenuItemActionPerformed(e);
						}
					});
					menu7.add(addFoxberryUSBReaderMenuItem);

					//---- importFoxberryBackupMenuItem ----
					importFoxberryBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importFoxberryBackupMenuItem.text"));
					importFoxberryBackupMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							importFoxberryBackupMenuItemActionPerformed(e);
						}
					});
					menu7.add(importFoxberryBackupMenuItem);

					//---- tiempoMetaCloudMenuItem ----
					tiempoMetaCloudMenuItem.setText(bundle.getString("JIpicoReaderFrame.tiempoMetaCloudMenuItem.text"));
					tiempoMetaCloudMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							tiempoMetaCloudMenuItemActionPerformed(e);
						}
					});
					menu7.add(tiempoMetaCloudMenuItem);
				}
				menu2.add(menu7);

				//======== menu8 ========
				{
					menu8.setText(bundle.getString("JIpicoReaderFrame.menu8.text"));

					//---- addUltraReaderMenuItem ----
					addUltraReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addUltraReaderMenuItem.text"));
					addUltraReaderMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addUltraReaderMenuItemActionPerformed(e);
						}
					});
					menu8.add(addUltraReaderMenuItem);

					//---- importUltraBackupMenuItem ----
					importUltraBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importUltraBackupMenuItem.text"));
					importUltraBackupMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							importUltraBackupMenuItemActionPerformed(e);
						}
					});
					menu8.add(importUltraBackupMenuItem);
				}
				menu2.add(menu8);
			}
			menuBar1.add(menu2);

			//======== menu3 ========
			{
				menu3.setText(bundle.getString("JIpicoReaderFrame.menu3.text"));

				//---- checaTuChipConfigurationMenuItem ----
				checaTuChipConfigurationMenuItem.setText(bundle.getString("JIpicoReaderFrame.checaTuChipConfigurationMenuItem.text"));
				checaTuChipConfigurationMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						checaTuChipConfigurationMenuItemActionPerformed(e);
					}
				});
				menu3.add(checaTuChipConfigurationMenuItem);

				//---- showPreviewMenuItem ----
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
		setSize(1215, 505);
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
	private JMenuItem importIpicoBackupMenuItem;
	private JMenu menu5;
	private JMenuItem addOne4AllReaderMenuItem;
	private JMenuItem addOcelotReaderMenuItem;
	private JMenuItem importMacshaBackupMenuItem;
	private JMenuItem importMacshaOcelotBackupMenuItem;
	private JMenuItem importMacshaCloudBackupMenuItem;
	private JMenu menuTimingSense;
	private JMenuItem addTSCollectorMenuItem;
	private JMenuItem importTimingSenseBackupMenuItem;
	private JMenu menu7;
	private JMenuItem addFoxberryReaderMenuItem;
	private JMenuItem addFoxberryUSBReaderMenuItem;
	private JMenuItem importFoxberryBackupMenuItem;
	private JMenuItem tiempoMetaCloudMenuItem;
	private JMenu menu8;
	private JMenuItem addUltraReaderMenuItem;
	private JMenuItem importUltraBackupMenuItem;
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
			wsReadings.add(tagRead);
		}
		Context.getResultsWebservice().saveRawChipReads(wsReadings);
		for (RawChipRead tagRead : readings) {
			logger.debug("Query participants by tag " + tagRead);
			List<ParticipantRegistration> registrationList = Context.getRegistrationWebservice()
					.findByTag(tagRead.getRfidString());
			if (registrationList == null) {
				tagReadPanel.add(TagReadLog.fromRawRead(tagRead));
			} else {
				logger.debug("Registration list size " + registrationList);
				for (ParticipantRegistration registration : registrationList) {
					tagReadPanel.add(TagReadLog.fromRawRead(tagRead, registration));
				}
				showParticipantInfo(registrationList);
			}
		}
	}

	private void showParticipantInfo(List<ParticipantRegistration> registrationList) {
		for (ParticipantRegistration participantRegistration : registrationList) {
			previewFrame.setRegistration(participantRegistration);
		}

	}

}
