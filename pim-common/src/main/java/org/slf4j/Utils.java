// Created: 06.04.2017
package org.slf4j;

import java.util.function.Supplier;

/**
 * Utils für den Lamda-Logger.
 *
 * @author Thomas Freese
 */
public final class Utils
{
    /**
     * @param supplier {@link Supplier}
     *
     * @return String
     */
    public static String getNullSafe(final Supplier<String> supplier)
    {
        return supplier == null ? null : supplier.get();
    }

    /**
     * Erstellt ein neues {@link Utils} Object.
     */
    private Utils()
    {
        super();
    }
}
