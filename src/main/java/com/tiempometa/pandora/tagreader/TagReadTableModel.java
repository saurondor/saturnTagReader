/**
 * 
 */
package com.tiempometa.pandora.tagreader;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class TagReadTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4901920600211371008L;
	private static final String[] HEADERS = { "tag", "hora", "punto", "número", "nombre", "categoría" };
	private List<TagReadLog> tagReads = new ArrayList<TagReadLog>();

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
		TagReadLog tagRead = tagReads.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return tagRead.getTag();
		case 1:
			return DateTimeFormatter.ISO_LOCAL_TIME.format(tagRead.getTime());
		case 2:
			return tagRead.getCheckPoint();
		case 3:
			return tagRead.getBib();
		case 4:
			return tagRead.getName();
		case 5:
			return tagRead.getCategory();
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int column) {
		return HEADERS[column];
	}

	public List<TagReadLog> getTagReads() {
		return tagReads;
	}

	public void setTagReads(List<TagReadLog> tagReads) {
		this.tagReads = tagReads;
	}

}
