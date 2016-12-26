/**
 * Created: 26.12.2016
 */

package de.freese.pim.core;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPimTest
{
    /**
     * {@link Path} für TMP-Dateien.
     */
    public static final Path TMP_TEST_PATH = Paths.get(System.getProperty("user.dir"), "test");

    /**
     * Erstellt ein neues {@link AbstractPimTest} Object.
     */
    public AbstractPimTest()
    {
        super();
    }
}
