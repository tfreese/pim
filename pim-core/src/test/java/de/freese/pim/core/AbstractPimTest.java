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
    *
    */
    public static final String MAIL_IMAP_HOST = "imap.1und1.de";

    /**
    *
    */
    public static final int MAIL_IMAP_PORT = 993;

    /**
    *
    */
    public static final String MAIL_SMPT_HOST = "smtp.1und1.de";

    /**
    *
    */
    public static final int MAIL_SMPT_PORT = 587;

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
