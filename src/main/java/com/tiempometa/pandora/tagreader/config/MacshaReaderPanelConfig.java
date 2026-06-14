package com.tiempometa.pandora.tagreader.config;

public class MacshaReaderPanelConfig extends ReaderPanelConfig {

    public static final String TYPE = "macsha";

    public String address;
    public String checkpoint;
    public int mode;

    public MacshaReaderPanelConfig() {
        this.type = TYPE;
    }
}
