/*
 * Created by JFormDesigner on Sun Apr 07 10:54:35 CDT 2019
 */

package com.tiempometa.pandora.checatuchip.preview;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JRightPreviewPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7933786981529108962L;

	public JRightPreviewPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		//======== this ========
		setPreferredSize(new Dimension(200, 700));
		setLayout(new FormLayout(
			"default, $lcgap, default",
			"2*(default, $lgap), default"));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	public void loadImage(String fieldRightBanner) {
//		imageLabel.setIcon(new ImageIcon(imagePath));
		
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
