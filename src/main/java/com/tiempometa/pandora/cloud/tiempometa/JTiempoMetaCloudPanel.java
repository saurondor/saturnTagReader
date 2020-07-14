/*
 * Created by JFormDesigner on Sat Feb 22 14:40:34 CST 2020
 */

package com.tiempometa.pandora.cloud.tiempometa;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.pandora.ipicoreader.CommandResponseHandler;
import com.tiempometa.pandora.ipicoreader.JIpicoReaderPanel;
import com.tiempometa.pandora.ipicoreader.commands.GetTimeCommand;
import com.tiempometa.pandora.ipicoreader.commands.IpicoCommand;
import com.tiempometa.pandora.ipicoreader.commands.SetTimeCommand;
import com.tiempometa.pandora.tagreader.Context;
import com.tiempometa.pandora.tagreader.JReaderListPanel;
import com.tiempometa.pandora.tagreader.JReaderPanel;
import com.tiempometa.pandora.tagreader.TagReadListener;
import com.tiempometa.timing.model.dao.RouteDao;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JTiempoMetaCloudPanel extends JReaderPanel implements CommandResponseHandler, TagReadListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1234969021040337654L;
	private static final Logger logger = Logger.getLogger(JTiempoMetaCloudPanel.class);
	private JReaderListPanel listPanel;
	private TiempoMetaCloudClient reader = new TiempoMetaCloudClient();
	private TagReadListener tagReadListener;
	private Integer tagCount;

	public JTiempoMetaCloudPanel(JReaderListPanel listPanel) {
		initComponents();
		this.listPanel = listPanel;
		reader.registerTagReadListener(this);
		loadCheckPoints();
	}

	/**
	 * 
	 */
	private void loadCheckPoints() {
		List<String> checkPoints = Context.getResultsWebservice().getCheckPointNames();
		logger.debug("Available checkpoints ");
		for (String string : checkPoints) {
			logger.debug(string);
		}
		String[] checkPointArray = new String[checkPoints.size()];
	}

//	private void checkPointComboBoxItemStateChanged(ItemEvent e) {
//		// TODO add your code here
//	}

	private void connectButtonActionPerformed(ActionEvent e) {
	}

	private void removeReaderButtonActionPerformed(ActionEvent e) {
		listPanel.removeReader(this);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.pandora.foxberry.foxberry");
		label1 = new JLabel();
		readerAddressTextField = new JTextField();
		connectButton = new JButton();
		removeReaderButton = new JButton();
		label2 = new JLabel();
		label4 = new JLabel();
		label3 = new JLabel();
		label6 = new JLabel();
		label5 = new JLabel();
		tagsReadLabel = new JLabel();

		//======== this ========
		setBorder(new TitledBorder(bundle.getString("JIpicoReaderPanel.this.border")));
		setInheritsPopupMenu(true);
		setMaximumSize(new Dimension(550, 120));
		setMinimumSize(new Dimension(550, 120));
		setPreferredSize(new Dimension(550, 120));
		setLayout(new FormLayout(
			"5dlu, $lcgap, default, $lcgap, 57dlu, $lcgap, 15dlu, $lcgap, 63dlu, $lcgap, 51dlu, $lcgap, 15dlu, $lcgap, 57dlu, $lcgap, 22dlu",
			"5dlu, 3*($lgap, default)"));

		//---- label1 ----
		label1.setText(bundle.getString("JIpicoReaderPanel.label1.text"));
		add(label1, CC.xy(3, 3));
		add(readerAddressTextField, CC.xywh(5, 3, 8, 1));

		//---- connectButton ----
		connectButton.setText(bundle.getString("JIpicoReaderPanel.connectButton.text"));
		connectButton.setBackground(Color.red);
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectButtonActionPerformed(e);
			}
		});
		add(connectButton, CC.xy(15, 3));

		//---- removeReaderButton ----
		removeReaderButton.setIcon(new ImageIcon(getClass().getResource("/com/tiempometa/pandora/tagreader/x-remove.png")));
		removeReaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeReaderButtonActionPerformed(e);
			}
		});
		add(removeReaderButton, CC.xy(17, 3));

		//---- label2 ----
		label2.setText(bundle.getString("JIpicoReaderPanel.label2.text"));
		add(label2, CC.xy(3, 5));

		//---- label4 ----
		label4.setText(bundle.getString("JIpicoReaderPanel.label4.text"));
		add(label4, CC.xy(5, 5));

		//---- label3 ----
		label3.setText(bundle.getString("JIpicoReaderPanel.label3.text"));
		add(label3, CC.xy(9, 5));

		//---- label6 ----
		label6.setText(bundle.getString("JIpicoReaderPanel.label6.text"));
		add(label6, CC.xy(11, 5));

		//---- label5 ----
		label5.setText(bundle.getString("JIpicoReaderPanel.label5.text"));
		add(label5, CC.xy(3, 7));

		//---- tagsReadLabel ----
		tagsReadLabel.setText(bundle.getString("JIpicoReaderPanel.tagsReadLabel.text"));
		add(tagsReadLabel, CC.xy(5, 7));
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel label1;
	private JTextField readerAddressTextField;
	private JButton connectButton;
	private JButton removeReaderButton;
	private JLabel label2;
	private JLabel label4;
	private JLabel label3;
	private JLabel label6;
	private JLabel label5;
	private JLabel tagsReadLabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setTagReadListener(TagReadListener tagReadListener) {
		this.tagReadListener = tagReadListener;

	}

	@Override
	public void notifyTagReads(List<RawChipRead> chipReadList) {
		// TODO Auto-generated method stub
		logger.debug("Notified tag reads " + chipReadList.size());
		for (RawChipRead rawChipRead : chipReadList) {
			logger.debug("TAG READ " + rawChipRead.getRfidString());
		}
		tagReadListener.notifyTagReads(chipReadList);
		tagCount = tagCount + chipReadList.size();
		tagsReadLabel.setText(tagCount.toString());
	}

	@Override
	public void handleCommandResponse(IpicoCommand command) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyCommException(IOException e) {
		// TODO Auto-generated method stub

	}

	public JReaderListPanel getListPanel() {
		return listPanel;
	}

	public void setListPanel(JReaderListPanel listPanel) {
		this.listPanel = listPanel;
	}

	public TagReadListener getTagReadListener() {
		return tagReadListener;
	}
}
