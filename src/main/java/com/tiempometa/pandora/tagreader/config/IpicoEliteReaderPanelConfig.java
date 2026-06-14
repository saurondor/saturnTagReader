package com.tiempometa.pandora.tagreader.config;

public class IpicoEliteReaderPanelConfig extends ReaderPanelConfig {

    public static final String TYPE = "ipico_elite";

    public String address;
    public String terminal;
    public String checkpoint1;

    public IpicoEliteReaderPanelConfig() {
        this.type = TYPE;
    }
}
