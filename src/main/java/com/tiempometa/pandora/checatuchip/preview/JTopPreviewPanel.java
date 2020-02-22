/*
 * Created by JFormDesigner on Sun Apr 07 10:54:03 CDT 2019
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
public class JTopPreviewPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7193447727787783126L;

	public JTopPreviewPanel() {
		initComponents();
	}

	public void loadImage(String imagePath) {
		imageLabel.setIcon(new ImageIcon(imagePath));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		imageLabel = new JLabel();

		//======== this ========
		setPreferredSize(new Dimension(200, 250));
		setLayout(new FormLayout(
			"default:grow, left:default:grow, default:grow",
			"fill:default"));

		//---- imageLabel ----
		imageLabel.setIcon(null);
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(imageLabel, CC.xy(2, 1));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel imageLabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
