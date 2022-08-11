// Created: 21.02.2017
package de.freese.pim.core;

import java.io.Serial;

/**
 * {@link RuntimeException} für die PIM-Anwendung.
 *
 * @author Thomas Freese
 */
public class PIMException extends RuntimeException
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 5365328556318748263L;

    /**
     * Erzeugt eine neue Instanz von {@link PIMException}
     */
    public PIMException()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link PIMException}
     *
     * @param message String
     */
    public PIMException(final String message)
    {
        super(message);
    }

    /**
     * Erzeugt eine neue Instanz von {@link PIMException}
     *
     * @param message String
     * @param cause {@link Throwable}
     */
    public PIMException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Erzeugt eine neue Instanz von {@link PIMException}
     *
     * @param message String
     * @param cause {@link Throwable}
     * @param enableSuppression boolean
     * @param writableStackTrace boolean
     */
    public PIMException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Erzeugt eine neue Instanz von {@link PIMException}
     *
     * @param cause {@link Throwable}
     */
    public PIMException(final Throwable cause)
    {
        super(cause);
    }
}
