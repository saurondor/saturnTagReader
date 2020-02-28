/*
 * Created by JFormDesigner on Sat Feb 22 15:10:04 CST 2020
 */

package com.tiempometa.pandora.tagreader;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JSplashScreen extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -946938216793125361L;

	public JSplashScreen() {
		initComponents();
	}

	public void setProgres(String status, Integer progress) {
		progressBar.setValue(progress);
		statusLabel.setText(status);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.ipicoreader.ipicoreader");
		panel1 = new JPanel();
		button1 = new JButton();
		label1 = new JLabel();
		label4 = new JLabel();
		label3 = new JLabel();
		label2 = new JLabel();
		progressBar = new JProgressBar();
		statusLabel = new JLabel();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setUndecorated(true);
		setIconImage(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/tagreader/tiempometa_icon_large_alpha.png")).getImage());
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		//======== panel1 ========
		{
			panel1.setFont(new Font("Tahoma", Font.BOLD, 14));
			panel1.setLayout(new FormLayout(
				"20dlu, $lcgap, 179dlu, $lcgap, 159dlu",
				"20dlu, 2*($lgap, default), $lgap, 26dlu, $lgap, default, $lgap, 86dlu, 2*($lgap, default)"));

			//---- button1 ----
			button1.setIcon(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/tagreader/tiempometa_icon_large_alpha.png")));
			button1.setFocusable(false);
			panel1.add(button1, CC.xywh(3, 3, 1, 9));

			//---- label1 ----
			label1.setText(bundle.getString("JSplashScreen.label1.text"));
			label1.setFont(new Font("Tahoma", Font.BOLD, 14));
			label1.setHorizontalAlignment(SwingConstants.CENTER);
			panel1.add(label1, CC.xy(5, 3));

			//---- label4 ----
			label4.setText(bundle.getString("JSplashScreen.label4.text"));
			label4.setHorizontalAlignment(SwingConstants.CENTER);
			panel1.add(label4, CC.xy(5, 5));

			//---- label3 ----
			label3.setText(bundle.getString("JSplashScreen.label3.text"));
			label3.setHorizontalAlignment(SwingConstants.CENTER);
			label3.setFont(new Font("Tahoma", Font.PLAIN, 12));
			panel1.add(label3, CC.xy(5, 7));

			//---- label2 ----
			label2.setText(bundle.getString("JSplashScreen.label2.text"));
			label2.setHorizontalAlignment(SwingConstants.CENTER);
			panel1.add(label2, CC.xy(5, 9));
			panel1.add(progressBar, CC.xywh(3, 13, 3, 1));

			//---- statusLabel ----
			statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
			panel1.add(statusLabel, CC.xywh(3, 15, 3, 1));
		}
		contentPane.add(panel1);
		setSize(580, 355);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JPanel panel1;
	private JButton button1;
	private JLabel label1;
	private JLabel label4;
	private JLabel label3;
	private JLabel label2;
	private JProgressBar progressBar;
	private JLabel statusLabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
