/*
 * Created by JFormDesigner on Mon Apr 08 10:16:45 CDT 2019
 */

package com.tiempometa.pandora.checatuchip.preview;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JFieldPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4354140855546464076L;
	private String title;
	private String value;

	public JFieldPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		titleLabel = new JLabel();
		valueLabel = new JLabel();

		//======== this ========
		setBackground(Color.white);
		setLayout(new FormLayout(
			"5dlu, $lcgap, 200dlu, $lcgap, 157dlu:grow, $lcgap, 5dlu",
			"$rgap, default, $rgap"));

		//---- titleLabel ----
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
		add(titleLabel, CC.xy(3, 2));

		//---- valueLabel ----
		valueLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
		add(valueLabel, CC.xy(5, 2));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel titleLabel;
	private JLabel valueLabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		titleLabel.setText(this.title);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		valueLabel.setText(this.value);
	}

	public JFieldPanel(String title) {
		super();
		initComponents();
		this.title = title;
		titleLabel.setText(this.title);
	}
}
