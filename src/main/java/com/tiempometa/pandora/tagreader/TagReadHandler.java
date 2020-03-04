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
public interface TagReadHandler {

	void handleRouteReads(List<RawChipRead> chipReadList);

	void handleChecaTuChipReads(List<RawChipRead> chipReadList);

	void handleChecaTuResultadoReads(List<RawChipRead> chipReadList);

}
