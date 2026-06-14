package com.tiempometa.pandora.tagreader;

/** @deprecated Use {@link com.tiempometa.timing.local.DbAuthorizationRequiredException}. */
@Deprecated
public class DbAuthorizationRequiredException
        extends com.tiempometa.timing.local.DbAuthorizationRequiredException {

    public DbAuthorizationRequiredException(String pandoraDbName) {
        super(pandoraDbName);
    }
}
