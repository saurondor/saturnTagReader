/*
 * Copyright (c) 2019 Gerardo Esteban Tasistro Giubetic
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.tiempometa.pandora.checatuchip.preview;

import java.awt.*;
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
