/**
 * 
 */
package com.tiempometa.pandora.tagreader;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.tiempometa.timing.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class TagReadTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4901920600211371008L;
	private static final String[] HEADERS = { "tag", "hora", "punto" };
	private List<RawChipRead> tagReads = new ArrayList<RawChipRead>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return tagReads.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return HEADERS.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		RawChipRead tagRead = tagReads.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return tagRead.getRfidString();
		case 1:
			return DateTimeFormatter.ISO_LOCAL_TIME.format(tagRead.getTime());
		case 2:
			return tagRead.getCheckPoint();
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int column) {
		return HEADERS[column];
	}

	public List<RawChipRead> getTagReads() {
		return tagReads;
	}

	public void setTagReads(List<RawChipRead> tagReads) {
		this.tagReads = tagReads;
	}

}
