/*
 * Created by JFormDesigner on Sat Feb 22 14:35:58 CST 2020
 */

package com.tiempometa.pandora.ipicoreader;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JIpicoReaderFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5737894657705021013L;

	public JIpicoReaderFrame() {
		initComponents();
		initListeners();
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
		panel2 = new JTagReadPanel();

		//======== this ========
		setIconImage(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/ipicoreader/tiempometa_icon_large_alpha.png")).getImage());
		setTitle(bundle.getString("JIpicoReaderFrame.this.title"));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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

				//---- addReaderMenuItem ----
				addReaderMenuItem.setText(bundle.getString("JIpicoReaderFrame.addReaderMenuItem.text"));
				addReaderMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						addReaderMenuItemActionPerformed(e);
					}
				});
				menu2.add(addReaderMenuItem);

				//---- importBackupMenuItem ----
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
		contentPane.add(panel2, BorderLayout.EAST);
		pack();
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
	private JTagReadPanel panel2;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
