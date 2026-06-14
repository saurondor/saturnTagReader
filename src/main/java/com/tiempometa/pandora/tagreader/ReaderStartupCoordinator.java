/*
 * Copyright (c) 2019 Gerardo Esteban Tasistro Giubetic
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.tiempometa.pandora.tagreader;

import java.io.IOException;
import java.util.function.BiConsumer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.timing.local.LocalDataContext;

/**
 * Manages startup sequencing before the main window is fully initialised.
 * Extracted from the JReaderFrame constructor to keep startup separate from
 * UI construction.
 *
 * <p>Startup no longer requires a saturnPandora connection. Settings and the
 * local H2 database are always available. The webservice connection is
 * optional and triggered by the operator via the Evento menu.
 */
public class ReaderStartupCoordinator {

    private static final Logger logger = LogManager.getLogger(ReaderStartupCoordinator.class);

    private final JFrame owner;
    private final JPandoraApplication application;
    private final BiConsumer<String, Integer> progressReporter;

    public ReaderStartupCoordinator(JFrame owner, JPandoraApplication application,
            BiConsumer<String, Integer> progressReporter) {
        this.owner = owner;
        this.application = application;
        this.progressReporter = progressReporter;
    }

    /**
     * Loads settings and initialises the local H2 database. Always succeeds
     * locally — no network required. On unrecoverable error (e.g. settings
     * file corrupt) the application is terminated via System.exit.
     */
    public void connect() {
        try {
            progress("Cargando configuración", 20);
            Context.setApplication(application);
            Context.loadSettings();
            Context.previewHelper.getSettings().loadSettings();
            progress("Iniciando base de datos local", 40);
            int h2Port = Integer.parseInt(
                    Context.loadSetting(PandoraSettings.LOCAL_H2_PORT,
                            String.valueOf(PandoraSettings.LOCAL_H2_PORT_DEFAULT)));
            String savedDbName = Context.loadSetting(PandoraSettings.LOCAL_DB_NAME, null);
            String h2Path = (savedDbName != null && !savedDbName.isEmpty())
                    ? System.getProperty("user.home") + "/.tiempometa/databases/" + savedDbName
                    : PandoraSettings.LOCAL_H2_PATH_DEFAULT;
            LocalDataContext.init(h2Port, h2Path);
            logger.info("Local H2 context started at {}.mv.db on port {}", h2Path, h2Port);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(owner,
                    "No se pudo cargar la configuración: " + e.getMessage(),
                    "Error de configuración", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            owner.dispose();
            System.exit(1);
        }
    }

    private void progress(String message, int percent) {
        progressReporter.accept(message, percent);
    }
}
