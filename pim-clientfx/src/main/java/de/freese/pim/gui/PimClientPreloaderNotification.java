// Created: 01.12.2016
package de.freese.pim.gui;

import javafx.application.Preloader.PreloaderNotification;

/**
 * @author Thomas Freese
 */
public class PimClientPreloaderNotification implements PreloaderNotification {
    private final double progress;

    private final String status;

    public PimClientPreloaderNotification(final double progress, final String status) {
        super();

        this.progress = progress;
        this.status = status;
    }

    public PimClientPreloaderNotification(final String status) {
        super();

        this.progress = -1;
        this.status = status;
    }

    public double getProgress() {
        return progress;
    }

    public String getStatus() {
        return status;
    }
}
