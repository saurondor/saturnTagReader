/**
 * 
 */
package com.tiempometa.pandora.tagreader;

import java.util.List;

import com.tiempometa.webservice.model.RawChipRead;


/**
 * @author gtasi
 *
 */
public interface TagReadListener {

	void notifyTagReads(List<RawChipRead> chipReadList);
}
