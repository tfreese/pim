package de.freese.pim.core.function;

/**
 * Interface eines {@link Runnable} mit einer Exception.<br>
 *
 * @param <E> Konkreter Exception-Typ
 *
 * @author Thomas Freese
 * @since 16.02.2017
 */
@FunctionalInterface
public interface ExceptionalRunnable<E extends Exception> {
    void run() throws E;
}
