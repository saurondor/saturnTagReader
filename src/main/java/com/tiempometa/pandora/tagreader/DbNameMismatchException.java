package com.tiempometa.pandora.tagreader;

/** @deprecated Use {@link com.tiempometa.timing.local.DbNameMismatchException}. */
@Deprecated
public class DbNameMismatchException
        extends com.tiempometa.timing.local.DbNameMismatchException {

    public DbNameMismatchException(String localBaseDbName, String pandoraDbName) {
        super(localBaseDbName, pandoraDbName);
    }
}
