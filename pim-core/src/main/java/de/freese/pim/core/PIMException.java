// Created: 21.02.2017
package de.freese.pim.core;

import java.io.Serial;

/**
 * {@link RuntimeException} für die PIM-Anwendung.
 *
 * @author Thomas Freese
 */
public class PIMException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5365328556318748263L;

    public PIMException() {
        super();
    }

    public PIMException(final String message) {
        super(message);
    }

    public PIMException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PIMException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PIMException(final Throwable cause) {
        super(cause);
    }
}
