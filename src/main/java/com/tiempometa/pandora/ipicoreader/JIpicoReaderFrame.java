/*
 * Created by JFormDesigner on Sat Feb 22 14:35:58 CST 2020
 */

package com.tiempometa.pandora.ipicoreader;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.*;
import com.tiempometa.timing.model.RawChipRead;
import com.tiempometa.timing.model.dao.RawChipReadDao;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JIpicoReaderFrame extends JFrame implements JPandoraApplication, TagReadListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5737894657705021013L;
	private static final Logger logger = Logger.getLogger(JIpicoReaderFrame.class);
	static JSplashScreen splash = new JSplashScreen();
	private String eventTitle;

	public JIpicoReaderFrame() {
		splash.setVisible(true);
		boolean connected = false;
		do {
			try {
				showProgress("Cargando configuración", 20);
				Context.setApplication(this);
				Context.loadSettings();
				showProgress("Abriendo evento", 40);
				Context.openEvent();
				connected = true;
				Context.setApplicationTitle(Context.registrationHelper.getEvent().getTitle());
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
					connected = handleMissingDatabase(connected, e);
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
		readerListPanel.addReader();
	}

	private void handleInvalidDatabaseSchema() {
		// TODO Auto-generated method stub

	}

	private void openEvent(boolean b) {
		JOpenEventDialog openEventDialog = new JOpenEventDialog(this);
		logger.debug("Opening new event");
		openEventDialog.setVisible(true);
		logger.debug("Event opened");
	}

	private boolean handleMissingDatabase(boolean connected, Exception e) {
		// if invalid database format
		int response = JOptionPane.showConfirmDialog(this,
				"La base de datos seleccionada no es compatible con esta versión. \n¿Deseas cambiar de base de datos?",
				"Error conectando a la base", JOptionPane.YES_NO_CANCEL_OPTION);
		switch (response) {
		case JOptionPane.CANCEL_OPTION:
			dispose();
			System.exit(0);
			break;
		case JOptionPane.YES_OPTION:
			openEvent(true);
			return true;
		case JOptionPane.NO_OPTION:
			return false;
		default:
			return false;
		}
		return false;
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
		JIpicoReaderFrame timer = new JIpicoReaderFrame();
		timer.setVisible(true);
	}

	private void openEventMenuItemActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void closeMenuItemActionPerformed(ActionEvent e) {
		closeApplication(this);
	}

	private void addReaderMenuItemActionPerformed(ActionEvent e) {
		readerListPanel.addReader();
	}

	private void importBackupMenuItemActionPerformed(ActionEvent e) {
		JImportBackupsFrame importFrame = new JImportBackupsFrame();
		importFrame.setVisible(true);
	}

	private void checaTuChipConfigurationMenuItemActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void showPreviewMenuItemActionPerformed(ActionEvent e) {
		// TODO add your code here
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
		addReaderMenuItem = new JMenuItem();
		importBackupMenuItem = new JMenuItem();
		menu3 = new JMenu();
		checaTuChipConfigurationMenuItem = new JMenuItem();
		showPreviewMenuItem = new JMenuItem();
		readerListPanel = new JReaderListPanel();
		tagReadPanel = new JTagReadPanel();

		// ======== this ========
		setIconImage(new ImageIcon(
				getClass().getResource("/com/tiempometa/pandora/ipicoreader/tiempometa_icon_large_alpha.png"))
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

				// ---- addReaderMenuItem ----
				addReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addReaderMenuItem.text"));
				addReaderMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						addReaderMenuItemActionPerformed(e);
					}
				});
				menu2.add(addReaderMenuItem);

				// ---- importBackupMenuItem ----
				importBackupMenuItem.setText(bundle.getString("JIpicoReaderFrame.importBackupMenuItem.text"));
				importBackupMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						importBackupMenuItemActionPerformed(e);
					}
				});
				menu2.add(importBackupMenuItem);
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
	private JMenuItem addReaderMenuItem;
	private JMenuItem importBackupMenuItem;
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
		for (RawChipRead tagRead : readings) {
			logger.debug(tagRead);
			tagReadPanel.add(tagRead);
		}
		RawChipReadDao chipDao = (RawChipReadDao) Context.getCtx().getBean("rawChipReadDao");
		chipDao.batchSave(readings);

	}
}
