/*
 * Created by JFormDesigner on Sat Feb 22 14:35:58 CST 2020
 */

package com.tiempometa.pandora.tagreader;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.pandora.checatuchip.preview.JPreviewConfig;
import com.tiempometa.pandora.checatuchip.preview.JPreviewFrame;
import com.tiempometa.pandora.cloud.tiempometa.JImportTestimonialsFrame;
import com.tiempometa.pandora.cloud.tiempometa.JImportVirtualTagReadsFrame;
import com.tiempometa.pandora.cloud.tiempometa.TestimonialBackupImporter;
import com.tiempometa.pandora.cloud.tiempometa.VirtualTagBackupImporter;
import com.tiempometa.pandora.foxberry.FoxberryBackupImporter;
import com.tiempometa.pandora.foxberry.JImportBackupsFrame;
import com.tiempometa.pandora.ipicoreader.IpicoBackupImporter;
import com.tiempometa.pandora.macsha.MacshaBackupImporter;
import com.tiempometa.pandora.macsha.MacshaCloudBackupImporter;
import com.tiempometa.pandora.macsha.MacshaOcelotBackupImporter;
import com.tiempometa.pandora.rfidtiming.UltraBackupImporter;
import com.tiempometa.pandora.timingsense.TimingsenseBackupImporter;
import com.tiempometa.pandora.webservice.api.ParticipantDetailDto;
import com.tiempometa.timing.local.LocalDataContext;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JReaderFrame extends JFrame implements JPandoraApplication, TagReadListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5737894657705021013L;
	private static final Logger logger = LogManager.getLogger(JReaderFrame.class);
	static JSplashScreen splash = new JSplashScreen();
	private String eventTitle;
	JPreviewFrame previewFrame = new JPreviewFrame();


	public JReaderFrame() {
		splash.setVisible(true);
		new ReaderStartupCoordinator(this, this, this::showProgress).connect();
		showProgress("Iniciando componentes", 65);
		initComponents();
		refreshTitle();
		showProgress("Iniciando servicios", 85);
		initListeners();
		splash.setVisible(false);
		if (readerListPanel.restoreReaderConfigs() == 0) {
			readerListPanel.addIpicoEliteReader();
		}
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
			if (response != JOptionPane.OK_OPTION) return;

			int unsynced = LocalDataContext.getUnsyncedReadCount();
			if (unsynced > 0) {
				if (Context.isWebserviceConnected()) {
					int syncResponse = JOptionPane.showConfirmDialog(null,
							"Hay " + unsynced + " lectura(s) sin sincronizar con Saturno.\n"
									+ "¿Deseas sincronizar antes de cerrar?",
							"Lecturas pendientes", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE);
					if (syncResponse == JOptionPane.CANCEL_OPTION) return;
					if (syncResponse == JOptionPane.YES_OPTION) {
						pushUnsyncedReads();
					}
				} else {
					logger.info("closeApplication: {} unsynced read(s), restConnected=false — exporting CSV", unsynced);
					java.io.File csv = LocalDataContext.exportUnsyncedReadsToCsv();
					if (csv != null) {
						String[] opts = {"OK", "Abrir ubicación"};
						int choice = JOptionPane.showOptionDialog(null,
								"No hay conexión con Saturno.\n"
										+ unsynced + " lectura(s) exportadas a:\n" + csv.getAbsolutePath(),
								"Lecturas exportadas", JOptionPane.DEFAULT_OPTION,
								JOptionPane.INFORMATION_MESSAGE, null, opts, opts[0]);
						if (choice == 1 && Desktop.isDesktopSupported()) {
							try {
								Desktop.getDesktop().open(csv.getParentFile());
							} catch (IOException ex) {
								logger.warn("Could not open folder: {}", ex.getMessage());
							}
						}
					} else {
						JOptionPane.showMessageDialog(null,
								"No hay conexión con Saturno y no se pudo exportar el CSV.\n"
										+ "Revisa el archivo de base de datos antes de cerrar.",
								"Advertencia", JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			readerListPanel.saveReaderConfigs();
			readerListPanel.disconnectAll();
			LocalDataContext.close();
			window.dispose();
			System.exit(0);
		} else {
			JOptionPane.showMessageDialog(null, "No se puede cerrar la aplicación en este momento.", "Cierre",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void pushUnsyncedReads() {
		List<com.tiempometa.timing.model.RawChipRead> unsynced =
				LocalDataContext.getRawChipReadDao().getUncookedReads();
		if (unsynced.isEmpty()) return;
		boolean ok = Context.pushRawReads(unsynced);
		if (ok) {
			LocalDataContext.markReadsAsSynced(unsynced);
			logger.info("Pushed {} unsynced reads to Saturno on close", unsynced.size());
		} else {
			java.io.File csv = LocalDataContext.exportUnsyncedReadsToCsv();
			String msg = "No se pudieron sincronizar las lecturas con Saturno.";
			if (csv != null) msg += "\nExportadas a: " + csv.getAbsolutePath();
			JOptionPane.showMessageDialog(null, msg, "Error de sincronización",
					JOptionPane.ERROR_MESSAGE);
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
		String ipAddress = JOptionPane.showInputDialog(
				this,
				"Dirección IP de Saturno (o 127.0.0.1 para local)",
				Context.getServerAddress());
		if (ipAddress == null) return;
		try {
			Context.setServerAddress(ipAddress);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this,
					"No se pudo guardar la configuración: " + e1.getMessage(),
					"Error de configuración", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			Context.initWebserviceClients();
			logger.info("Connected to Saturno db '{}' at {}",
					LocalDataContext.getBaseDbName(), Context.getServerAddress());
			startSnapshotDownload("Conectado a Saturno en " + Context.getServerAddress());
		} catch (DbAuthorizationRequiredException ex) {
			// First time connecting this local DB — ask operator to authorise pairing
			int auth = JOptionPane.showConfirmDialog(this,
					"Esta base de datos local no está conectada a ningún evento.\n"
							+ "¿Deseas conectarla a la base de Saturno '"
							+ ex.getPandoraDbName() + "'?",
					"Autorizar conexión", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (auth == JOptionPane.YES_OPTION) {
				try {
					Context.saveSetting(PandoraSettings.LOCAL_DB_NAME, ex.getPandoraDbName());
					Context.flushSettings();
					Context.reinitLocalH2(ex.getPandoraDbName());
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(this,
							"Error al inicializar la base de datos local: " + ioe.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					Context.initWebserviceClients();
					startSnapshotDownload(
							"Conectado y autorizado a base '" + ex.getPandoraDbName() + "'.");
				} catch (Exception retryEx) {
					logger.error("Connect failed after authorisation", retryEx);
					JOptionPane.showMessageDialog(this,
							"Error al conectar tras autorización: " + retryEx.getMessage(),
							"Error de conexión", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (DbNameMismatchException ex) {
			logger.warn("DB name mismatch: local='{}' pandora='{}'",
					ex.getLocalBaseDbName(), ex.getPandoraDbName());
			int repair = JOptionPane.showConfirmDialog(this,
					"Esta base de datos local está conectada a '" + ex.getLocalBaseDbName()
							+ "',\npero Saturno tiene abierta la base '"
							+ ex.getPandoraDbName() + "'.\n\n"
							+ "¿Deseas cambiar la vinculación a '" + ex.getPandoraDbName() + "'?",
					"Evento incorrecto", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (repair == JOptionPane.YES_OPTION) {
				try {
					Context.saveSetting(PandoraSettings.LOCAL_DB_NAME, ex.getPandoraDbName());
					Context.flushSettings();
					Context.reinitLocalH2(ex.getPandoraDbName());
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(this,
							"Error al cambiar la base de datos local: " + ioe.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					Context.initWebserviceClients();
					startSnapshotDownload("Conectado a base '" + ex.getPandoraDbName() + "'.");
				} catch (Exception retryEx) {
					logger.error("Connect failed after re-pairing", retryEx);
					JOptionPane.showMessageDialog(this,
							"Error al conectar tras cambio de evento: " + retryEx.getMessage(),
							"Error de conexión", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception ex) {
			logger.warn("Could not connect to Saturno: {}", ex.getMessage());
			refreshTitle();
			JOptionPane.showMessageDialog(this,
					"No se pudo conectar a Saturno en " + Context.getServerAddress()
							+ "\n" + ex.getMessage(),
					"Error de conexión", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void startSnapshotDownload(String successMessage) {
		JDialog progress = buildProgressDialog("Descargando datos del evento...");
		new javax.swing.SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				Context.downloadEventSnapshot();
				flushUnsyncedReadsOnConnect();
				return null;
			}

			@Override
			protected void done() {
				progress.dispose();
				refreshTitle();
				JOptionPane.showMessageDialog(JReaderFrame.this,
						successMessage, "Conexión exitosa", JOptionPane.INFORMATION_MESSAGE);
			}
		}.execute();
		progress.setVisible(true); // blocks EDT until SwingWorker calls progress.dispose()
	}

	private void flushUnsyncedReadsOnConnect() {
		int count = LocalDataContext.getUnsyncedReadCount();
		if (count == 0) return;
		logger.info("Flushing {} unsynced read(s) after connect", count);
		List<com.tiempometa.timing.model.RawChipRead> reads =
				LocalDataContext.getRawChipReadDao().getUncookedReads();
		boolean ok = Context.pushRawReads(reads);
		if (ok) {
			LocalDataContext.markReadsAsSynced(reads);
			logger.info("Flushed {} read(s) to Saturno", reads.size());
		} else {
			logger.warn("Could not flush {} read(s) to Saturno — will retry on close", reads.size());
		}
	}

	private JDialog buildProgressDialog(String message) {
		JDialog dlg = new JDialog(this, "Saturno", true);
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		JPanel panel = new JPanel(new java.awt.BorderLayout(10, 10));
		panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 30, 20, 30));
		panel.add(new javax.swing.JLabel(message), java.awt.BorderLayout.NORTH);
		javax.swing.JProgressBar bar = new javax.swing.JProgressBar();
		bar.setIndeterminate(true);
		panel.add(bar, java.awt.BorderLayout.CENTER);
		dlg.add(panel);
		dlg.pack();
		dlg.setLocationRelativeTo(this);
		return dlg;
	}

	private void closeMenuItemActionPerformed(ActionEvent e) {
		closeApplication(this);
	}

	/** Wire this to a "Cambiar nombre de BD" menu item in JFormDesigner. */
	private void changeLocalDbNameMenuItemActionPerformed(ActionEvent e) {
		String current = Context.loadSetting(PandoraSettings.LOCAL_DB_NAME, "");
		String input = JOptionPane.showInputDialog(this,
				"Nombre de este punto de lectura o evento:",
				current);
		if (input == null || input.isBlank()) return;
		Context.saveSetting(PandoraSettings.LOCAL_DB_NAME, input.trim());
		try {
			Context.flushSettings();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this,
					"No se pudo guardar el nombre: " + ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
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

	private void importVirtualTagReadsMenuItemActionPerformed(ActionEvent e) {
		JImportVirtualTagReadsFrame importFrame = new JImportVirtualTagReadsFrame(new VirtualTagBackupImporter());
		importFrame.setVisible(true);
	}

	private void importTestimonialMenuItemActionPerformed(ActionEvent e) {
		JImportTestimonialsFrame importFrame = new JImportTestimonialsFrame(new TestimonialBackupImporter());
		importFrame.setVisible(true);
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
        importVirtualTagReadsMenuItem = new JMenuItem();
        importTestimonialMenuItem = new JMenuItem();
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
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText(bundle.getString("JIpicoReaderFrame.menu1.text"));

                //---- openEventMenuItem ----
                openEventMenuItem.setText(bundle.getString("JIpicoReaderFrame.openEventMenuItem.text"));
                openEventMenuItem.addActionListener(e -> openEventMenuItemActionPerformed(e));
                menu1.add(openEventMenuItem);

                //---- closeMenuItem ----
                closeMenuItem.setText(bundle.getString("JIpicoReaderFrame.closeMenuItem.text"));
                closeMenuItem.addActionListener(e -> closeMenuItemActionPerformed(e));
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
                    addEliteReaderMenuItem.addActionListener(e -> addEliteReaderMenuItemActionPerformed(e));
                    menu4.add(addEliteReaderMenuItem);

                    //---- addUsbReaderMenuItem ----
                    addUsbReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addUsbReaderMenuItem.text"));
                    addUsbReaderMenuItem.addActionListener(e -> addUsbReaderMenuItemActionPerformed(e));
                    menu4.add(addUsbReaderMenuItem);

                    //---- importIpicoBackupMenuItem ----
                    importIpicoBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importIpicoBackupMenuItem.text"));
                    importIpicoBackupMenuItem.addActionListener(e -> importIpicoBackupMenuItemActionPerformed(e));
                    menu4.add(importIpicoBackupMenuItem);
                }
                menu2.add(menu4);

                //======== menu5 ========
                {
                    menu5.setText(bundle.getString("JIpicoReaderFrame.menu5.text"));

                    //---- addOne4AllReaderMenuItem ----
                    addOne4AllReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addOne4AllReaderMenuItem.text"));
                    addOne4AllReaderMenuItem.addActionListener(e -> addOne4AllReaderMenuItemActionPerformed(e));
                    menu5.add(addOne4AllReaderMenuItem);

                    //---- addOcelotReaderMenuItem ----
                    addOcelotReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addOcelotReaderMenuItem.text"));
                    addOcelotReaderMenuItem.addActionListener(e -> addOcelotReaderMenuItemActionPerformed(e));
                    menu5.add(addOcelotReaderMenuItem);

                    //---- importMacshaBackupMenuItem ----
                    importMacshaBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importMacshaBackupMenuItem.text"));
                    importMacshaBackupMenuItem.addActionListener(e -> importMacshaBackupMenuItemActionPerformed(e));
                    menu5.add(importMacshaBackupMenuItem);

                    //---- importMacshaOcelotBackupMenuItem ----
                    importMacshaOcelotBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importMacshaOcelotBackupMenuItem.text"));
                    importMacshaOcelotBackupMenuItem.addActionListener(e -> importMacshaOcelotBackupMenuItemActionPerformed(e));
                    menu5.add(importMacshaOcelotBackupMenuItem);

                    //---- importMacshaCloudBackupMenuItem ----
                    importMacshaCloudBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importMacshaCloudBackupMenuItem.text"));
                    importMacshaCloudBackupMenuItem.addActionListener(e -> importMacshaCloudBackupMenuItemActionPerformed(e));
                    menu5.add(importMacshaCloudBackupMenuItem);
                }
                menu2.add(menu5);

                //======== menuTimingSense ========
                {
                    menuTimingSense.setText(bundle.getString("JIpicoReaderFrame.menuTimingSense.text"));

                    //---- addTSCollectorMenuItem ----
                    addTSCollectorMenuItem.setText(bundle.getString("JIpicoReaderFrame.addTSCollectorMenuItem.text"));
                    addTSCollectorMenuItem.addActionListener(e -> addTSCollectorMenuItemActionPerformed(e));
                    menuTimingSense.add(addTSCollectorMenuItem);

                    //---- importTimingSenseBackupMenuItem ----
                    importTimingSenseBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importTimingSenseBackupMenuItem.text"));
                    importTimingSenseBackupMenuItem.addActionListener(e -> importTimingSenseBackupMenuItemActionPerformed(e));
                    menuTimingSense.add(importTimingSenseBackupMenuItem);
                }
                menu2.add(menuTimingSense);

                //======== menu7 ========
                {
                    menu7.setText(bundle.getString("JIpicoReaderFrame.menu7.text"));

                    //---- addFoxberryReaderMenuItem ----
                    addFoxberryReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addFoxberryReaderMenuItem.text"));
                    addFoxberryReaderMenuItem.addActionListener(e -> addFoxberryReaderMenuItemActionPerformed(e));
                    menu7.add(addFoxberryReaderMenuItem);

                    //---- addFoxberryUSBReaderMenuItem ----
                    addFoxberryUSBReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addFoxberryUSBReaderMenuItem.text"));
                    addFoxberryUSBReaderMenuItem.addActionListener(e -> addFoxberryUSBReaderMenuItemActionPerformed(e));
                    menu7.add(addFoxberryUSBReaderMenuItem);

                    //---- importFoxberryBackupMenuItem ----
                    importFoxberryBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importFoxberryBackupMenuItem.text"));
                    importFoxberryBackupMenuItem.addActionListener(e -> importFoxberryBackupMenuItemActionPerformed(e));
                    menu7.add(importFoxberryBackupMenuItem);

                    //---- tiempoMetaCloudMenuItem ----
                    tiempoMetaCloudMenuItem.setText(bundle.getString("JIpicoReaderFrame.tiempoMetaCloudMenuItem.text"));
                    tiempoMetaCloudMenuItem.addActionListener(e -> tiempoMetaCloudMenuItemActionPerformed(e));
                    menu7.add(tiempoMetaCloudMenuItem);
                    menu7.addSeparator();

                    //---- importVirtualTagReadsMenuItem ----
                    importVirtualTagReadsMenuItem.setText(bundle.getString("JIpicoReaderFrame.importVirtualTagReadsMenuItem.text"));
                    importVirtualTagReadsMenuItem.addActionListener(e -> importVirtualTagReadsMenuItemActionPerformed(e));
                    menu7.add(importVirtualTagReadsMenuItem);

                    //---- importTestimonialMenuItem ----
                    importTestimonialMenuItem.setText(bundle.getString("JIpicoReaderFrame.importTestimonialMenuItem.text"));
                    importTestimonialMenuItem.addActionListener(e -> importTestimonialMenuItemActionPerformed(e));
                    menu7.add(importTestimonialMenuItem);
                }
                menu2.add(menu7);

                //======== menu8 ========
                {
                    menu8.setText(bundle.getString("JIpicoReaderFrame.menu8.text"));

                    //---- addUltraReaderMenuItem ----
                    addUltraReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addUltraReaderMenuItem.text"));
                    addUltraReaderMenuItem.addActionListener(e -> addUltraReaderMenuItemActionPerformed(e));
                    menu8.add(addUltraReaderMenuItem);

                    //---- importUltraBackupMenuItem ----
                    importUltraBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importUltraBackupMenuItem.text"));
                    importUltraBackupMenuItem.addActionListener(e -> importUltraBackupMenuItemActionPerformed(e));
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
                checaTuChipConfigurationMenuItem.addActionListener(e -> checaTuChipConfigurationMenuItemActionPerformed(e));
                menu3.add(checaTuChipConfigurationMenuItem);

                //---- showPreviewMenuItem ----
                showPreviewMenuItem.setText(bundle.getString("JIpicoReaderFrame.showPreviewMenuItem.text"));
                showPreviewMenuItem.addActionListener(e -> showPreviewMenuItemActionPerformed(e));
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
    private JMenuItem importVirtualTagReadsMenuItem;
    private JMenuItem importTestimonialMenuItem;
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
		return eventTitle;
	}

	@Override
	public void refreshTitle() {
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.tagreader.tagreader");
		String base = bundle.getString("JIpicoReaderFrame.this.title");
		String dbName = LocalDataContext.getBaseDbName();
		if (dbName != null) {
			String status = Context.isWebserviceConnected() ? "[conectado]" : "[** sin conexión **]";
			setTitle(base + " — " + dbName + " " + status);
		} else {
			setTitle(base);
		}
	}

	@Override
	public void notifyTagReads(List<RawChipRead> readings) {
		logger.info("notifyTagReads: {} read(s), restConnected={}", readings.size(), Context.isWebserviceConnected());

		// Always save to local H2 first
		List<com.tiempometa.timing.model.RawChipRead> localReads = new ArrayList<>();
		for (RawChipRead tagRead : readings) {
			logger.debug(tagRead);
			localReads.add(toLocalRead(tagRead));
		}
		LocalDataContext.getRawChipReadDao().batchSave(localReads);

		// Push to saturnPandora if webservice is connected; mark synced only on success
		if (Context.isWebserviceConnected()) {
			try {
				boolean pushed = Context.pushRawReads(localReads);
				if (pushed) {
					LocalDataContext.markReadsAsSynced(localReads);
					logger.info("Pushed {} read(s) to Saturno", localReads.size());
				} else {
					logger.warn("pushRawReads returned false for {} read(s) — will retry on close", localReads.size());
				}
			} catch (Exception e) {
				logger.warn("Failed to push reads to Saturno: {}", e.getMessage());
			}
		}

		// Participant lookup and display (rfid first, bib fallback for manual reads)
		for (RawChipRead tagRead : readings) {
			List<ParticipantDetailDto> participants =
					Context.findParticipantByRfid(tagRead.getRfidString());
			if (participants == null || participants.isEmpty()) {
				participants = Context.findParticipantByBib(tagRead.getRfidString());
			}
			if (participants == null || participants.isEmpty()) {
				logger.info("No participant found for rfid '{}'", tagRead.getRfidString());
				tagReadPanel.add(TagReadLog.fromRawRead(tagRead));
			} else {
				logger.info("Participant lookup found {} result(s) for rfid '{}'", participants.size(), tagRead.getRfidString());
				for (ParticipantDetailDto dto : participants) {
					tagReadPanel.add(TagReadLog.fromRawRead(tagRead, dto));
				}
				showParticipantInfo(participants);
			}
		}
	}

	private com.tiempometa.timing.model.RawChipRead toLocalRead(RawChipRead ws) {
		com.tiempometa.timing.model.RawChipRead local = new com.tiempometa.timing.model.RawChipRead();
		local.setRfidString(ws.getRfidString());
		local.setTime(ws.getTime());
		local.setTimeMillis(ws.getTimeMillis());
		local.setCheckPoint(ws.getCheckPoint());
		local.setLoadName(ws.getLoadName());
		local.setReadType(ws.getReadType());
		local.setDevice(ws.getDevice());
		local.setMobileApp(ws.getMobileApp());
		local.setChipNumber(ws.getChipNumber());
		local.setFiltered(ws.getFiltered());
		local.setDistance(ws.getDistance());
		local.setCalories(ws.getCalories());
		local.setSteps(ws.getSteps());
		local.setRunTime(ws.getRunTime());
		local.setCooked(ws.getCooked());
		return local;
	}

	private void showParticipantInfo(List<ParticipantDetailDto> participants) {
		for (ParticipantDetailDto dto : participants) {
			previewFrame.setRegistration(dto);
		}
	}

}
