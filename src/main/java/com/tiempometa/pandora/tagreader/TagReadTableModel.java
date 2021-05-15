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
package com.tiempometa.pandora.tagreader;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author Gerardo Esteban Tasistro Giubetic
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
			if (tagRead.getTime() == null) {
				return "ND";
			} else {
				return DateTimeFormatter.ISO_LOCAL_TIME.format(tagRead.getTime());
			}
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
