package com.tiempometa.pandora.tagreader.config;

public class IpicoUsbReaderPanelConfig extends ReaderPanelConfig {

    public static final String TYPE = "ipico_usb";

    public String serialPort;
    public String terminal;
    public String checkpoint1;
    public int mode;

    public IpicoUsbReaderPanelConfig() {
        this.type = TYPE;
    }
}
