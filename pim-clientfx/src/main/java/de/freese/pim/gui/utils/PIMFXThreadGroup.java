package de.freese.pim.gui.utils;

/**
 * ThreadGroup für Handling von Runtime-Exceptions.
 *
 * @author Thomas Freese
 */
public class PIMFXThreadGroup extends ThreadGroup
{
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
