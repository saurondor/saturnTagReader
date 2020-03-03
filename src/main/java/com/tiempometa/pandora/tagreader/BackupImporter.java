/**
 * 
 */
package com.tiempometa.pandora.tagreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.tiempometa.webservice.model.RawChipRead;

/**
 * @author gtasi
 *
 */
public interface BackupImporter {

	void load(String fileName) throws IOException;

	List<RawChipRead> getChipReads();

	void load(File dataFile) throws IOException;

}
