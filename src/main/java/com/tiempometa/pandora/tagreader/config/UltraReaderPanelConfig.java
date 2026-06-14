package com.tiempometa.pandora.tagreader.config;

public class UltraReaderPanelConfig extends ReaderPanelConfig {

    public static final String TYPE = "ultra";

    public String address;
    public String terminal;
    public String checkpoint1;
    public String checkpoint2;

    public UltraReaderPanelConfig() {
        this.type = TYPE;
    }
}
