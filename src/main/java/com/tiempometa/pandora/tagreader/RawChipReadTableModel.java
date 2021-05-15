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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.tiempometa.Utils;
import com.tiempometa.timing.model.CookedChipRead;
import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public class RawChipReadTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8579865802486643525L;
	private static final String[] HEADERS = { "Tag", "Hora/Tiempo", "Distancia", "Punto" };
	private List<RawChipRead> chipReads = new ArrayList<RawChipRead>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return chipReads.size();
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
		RawChipRead chipRead = chipReads.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return chipRead.getRfidString();
		case 1:
			if (chipRead.getReadType() == CookedChipRead.TYPE_MANUAL_TESTIMONIAL
					|| chipRead.getReadType() == CookedChipRead.TYPE_TESTIMONIAL) {
				if (chipRead.getRunTime() == null) {
					return "ND";
				}
				return Utils.millisToTime(chipRead.getRunTime() * 1000, 0);
			} else {
				if (chipRead.getTime() == null) {
					return "ND";
				}
				return chipRead.getTime().toString();
			}
		case 2:
			return chipRead.getDistance();
		case 3:
			return chipRead.getCheckPoint();
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int column) {
		return HEADERS[column];
	}

	public List<RawChipRead> getChipReads() {
		return chipReads;
	}

	public void setChipReads(List<RawChipRead> chipReads) {
		this.chipReads = chipReads;
	}

}
