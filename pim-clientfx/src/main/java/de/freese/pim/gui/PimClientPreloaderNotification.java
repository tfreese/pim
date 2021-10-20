// Created: 01.12.2016
package de.freese.pim.gui;

import javafx.application.Preloader.PreloaderNotification;

/**
 * @author Thomas Freese
 */
public class PimClientPreloaderNotification implements PreloaderNotification
{
    /**
     *
     */
    private final double progress;
    /**
     *
     */
    private final String status;

    /**
     * Erzeugt eine neue Instanz von {@link PimClientPreloaderNotification}
     *
     * @param progress double
     * @param status String
     */
    public PimClientPreloaderNotification(final double progress, final String status)
    {
        super();

        this.progress = progress;
        this.status = status;
    }

    /**
     * Erzeugt eine neue Instanz von {@link PimClientPreloaderNotification}
     *
     * @param status String
     */
    public PimClientPreloaderNotification(final String status)
    {
        super();

        this.progress = -1;
        this.status = status;
    }

    /**
     * @return double
     */
    public double getProgress()
    {
        return this.progress;
    }

    /**
     * @return String
     */
    public String getStatus()
    {
        return this.status;
    }
}
