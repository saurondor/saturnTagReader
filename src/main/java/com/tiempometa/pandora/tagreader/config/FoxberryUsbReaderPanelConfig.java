package com.tiempometa.pandora.tagreader.config;

public class FoxberryUsbReaderPanelConfig extends ReaderPanelConfig {

    public static final String TYPE = "foxberry_usb";

    public String serialPort;
    public String terminal;
    public String checkpoint;
    public int mode;

    public FoxberryUsbReaderPanelConfig() {
        this.type = TYPE;
    }
}
