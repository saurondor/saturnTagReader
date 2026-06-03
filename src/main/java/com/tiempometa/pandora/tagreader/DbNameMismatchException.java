package com.tiempometa.pandora.tagreader;

/**
 * Thrown when the local H2 base_db_name does not match the database name
 * reported by saturnPandora, preventing an accidental cross-event sync.
 */
public class DbNameMismatchException extends Exception {

    private final String localBaseDbName;
    private final String pandoraDbName;

    public DbNameMismatchException(String localBaseDbName, String pandoraDbName) {
        super("Local base DB '" + localBaseDbName + "' does not match Pandora DB '" + pandoraDbName + "'");
        this.localBaseDbName = localBaseDbName;
        this.pandoraDbName = pandoraDbName;
    }

    public String getLocalBaseDbName() { return localBaseDbName; }
    public String getPandoraDbName()   { return pandoraDbName; }
}
