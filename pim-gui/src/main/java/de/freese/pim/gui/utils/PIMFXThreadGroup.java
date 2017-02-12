package de.freese.pim.gui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ThreadGroup für Handling von Runtime-Exceptions.
 *
 * @author Thomas Freese
 */
public class PIMFXThreadGroup extends ThreadGroup
{
    /**
    *
    */
    public static final Logger LOGGER = LoggerFactory.getLogger(PIMFXThreadGroup.class);

    /**
     * Erzeugt eine neue Instanz von {@link PIMFXThreadGroup}
     */
    public PIMFXThreadGroup()
    {
        super("PIM-FX-ThreadGroup");
    }

    /**
     * @see java.lang.ThreadGroup#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    @Override
    public void uncaughtException(final Thread t, final Throwable e)
    {
        // super.uncaughtException(t, e);

        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(t, e);
    }
}
