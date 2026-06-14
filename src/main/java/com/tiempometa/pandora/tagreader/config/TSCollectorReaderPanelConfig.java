package com.tiempometa.pandora.tagreader.config;

public class TSCollectorReaderPanelConfig extends ReaderPanelConfig {

    public static final String TYPE = "ts_collector";

    public String address;
    public String port;

    public TSCollectorReaderPanelConfig() {
        this.type = TYPE;
    }
}
