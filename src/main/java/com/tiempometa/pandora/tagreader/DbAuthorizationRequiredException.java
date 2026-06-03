package com.tiempometa.pandora.tagreader;

/**
 * Thrown when a local H2 database has no base_db_name set and the operator
 * must explicitly authorise pairing it with the saturnPandora database.
 */
public class DbAuthorizationRequiredException extends Exception {

    private final String pandoraDbName;

    public DbAuthorizationRequiredException(String pandoraDbName) {
        super("Authorization required to pair with database: " + pandoraDbName);
        this.pandoraDbName = pandoraDbName;
    }

    public String getPandoraDbName() {
        return pandoraDbName;
    }
}
