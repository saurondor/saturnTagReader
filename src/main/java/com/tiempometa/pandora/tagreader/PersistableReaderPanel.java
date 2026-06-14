package com.tiempometa.pandora.tagreader;

import com.tiempometa.pandora.tagreader.config.ReaderPanelConfig;

public interface PersistableReaderPanel {
    ReaderPanelConfig getConfig();
    void applyConfig(ReaderPanelConfig config);
}
