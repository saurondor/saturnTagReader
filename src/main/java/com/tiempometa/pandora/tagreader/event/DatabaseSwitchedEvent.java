package com.tiempometa.pandora.tagreader.event;

public class DatabaseSwitchedEvent extends DatabaseChangeEvent {

    private final String newDbName;

    public DatabaseSwitchedEvent(String newDbName) {
        this.newDbName = newDbName;
    }

    public String getNewDbName() {
        return newDbName;
    }
}
