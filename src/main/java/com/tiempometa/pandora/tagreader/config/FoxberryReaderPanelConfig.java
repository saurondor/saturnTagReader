package com.tiempometa.pandora.tagreader.config;

public class FoxberryReaderPanelConfig extends ReaderPanelConfig {

    public static final String TYPE = "foxberry_tcp";

    public String address;
    public String terminal;
    public String checkpoint1;
    public String checkpoint2;

    public FoxberryReaderPanelConfig() {
        this.type = TYPE;
    }
}
