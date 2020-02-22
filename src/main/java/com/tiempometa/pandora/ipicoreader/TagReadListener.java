/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import java.util.List;

import com.tiempometa.timing.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public interface TagReadListener {

	void notifyTagReads(List<RawChipRead> chipReadList);
	
	

}
