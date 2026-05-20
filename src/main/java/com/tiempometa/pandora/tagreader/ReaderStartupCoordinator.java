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
import java.time.DateTimeException;
import java.util.function.BiConsumer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the webservice connection retry loop that runs before the main window
 * is fully initialized. Extracted from the JReaderFrame constructor to keep
 * startup sequencing separate from UI construction.
 *
 * @author Gerardo Esteban Tasistro Giubetic
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
     * Blocks until settings are loaded and a successful webservice connection is
     * established. On unrecoverable errors the application is terminated via
     * System.exit.
     */
    public void connect() {
        boolean connected = false;
        do {
            try {
                progress("Cargando configuración", 20);
                Context.setApplication(application);
                Context.loadSettings();
                Context.previewHelper.getSettings().loadSettings();
                progress("Probando conexión al servidor @" + Context.getServerAddress(), 40);
                initWebserviceClient();
                Context.getZoneId();
                connected = true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(owner,
                        "No se pudo cargar la configuración " + e.getMessage(),
                        "Error de configuración", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                owner.dispose();
            } catch (Exception e) {
                logger.info("Exception during startup: " + e.getClass().getCanonicalName());
                if (e instanceof org.springframework.dao.InvalidDataAccessResourceUsageException) {
                    handleInvalidDatabaseSchema();
                }
                e.printStackTrace();
            } catch (ExceptionInInitializerError e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(owner,
                        "No se pudo inicializar la aplicación. ¿Está corriendo la base de datos?",
                        "Error iniciando aplicación", JOptionPane.ERROR_MESSAGE);
            } catch (NoClassDefFoundError e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(owner,
                        "No se pudo inicializar la aplicación. Error inicializando application context."
                                + " Error de sistema es: " + e.getMessage(),
                        "Error iniciando aplicación", JOptionPane.ERROR_MESSAGE);
                owner.dispose();
                System.exit(1);
            }
        } while (!connected);
    }

    private void initWebserviceClient() {
        boolean retry = true;
        do {
            try {
                Context.initWebserviceClients();
                retry = false;
            } catch (DateTimeException e) {
                JOptionPane.showMessageDialog(owner,
                        "No se pudo establecer la zona horaria. " + e.getMessage(),
                        "Error de configuración", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                int response = JOptionPane.showConfirmDialog(owner, "¿Deseas reintentar?",
                        "Error solicitando configuración", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.NO_OPTION) {
                    retry = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(owner,
                        "No se pudo conectar a la base de datos.\nVerifica que el Saturno está"
                                + " funcionando y disponible en la dirección: "
                                + Context.getServerAddress() + "\nError: " + e.getMessage(),
                        "Error de conexión", JOptionPane.ERROR_MESSAGE);
                int response = JOptionPane.showConfirmDialog(owner,
                        "¿Deseas cambiar la dirección IP?",
                        "Error conectando a Saturno", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    String ipAddress = JOptionPane.showInputDialog(
                            "Dirección IP", Context.getServerAddress());
                    if (ipAddress != null) {
                        try {
                            Context.setServerAddress(ipAddress);
                            logger.info("Updated server address to " + Context.getServerAddress());
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(owner,
                                    "No se pudo guardar la configuración. " + e1.getMessage(),
                                    "Error de configuración", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    response = JOptionPane.showConfirmDialog(owner,
                            "¿Deseas reintentar conectar con Saturno?",
                            "Error conectando a Saturno", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.NO_OPTION) {
                        System.exit(1);
                    }
                }
            }
        } while (retry);
    }

    private void handleInvalidDatabaseSchema() {
        // TODO: handle incompatible database schema at startup
    }

    private void progress(String message, int percent) {
        progressReporter.accept(message, percent);
    }
}
