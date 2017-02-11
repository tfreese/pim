/**
 * Created: 26.12.2016
 */

package de.freese.pim.core;

import java.net.InetAddress;
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
    public static final Path HOME_DEFAULT = Paths.get(System.getProperty("user.home"), ".pim");

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

    /**
     * Liefert true, wenn ich auf der Arbeit bin.
     *
     * @return boolean
     */
    protected boolean isWork()
    {
        String domain = System.getenv("userdomain");

        if ((domain != null) && domain.equals("DEVWAG00"))
        {
            return true;
        }

        try
        {
            InetAddress address = InetAddress.getLocalHost();
            String canonicalHostName = address.getCanonicalHostName();

            if ((canonicalHostName != null) && canonicalHostName.endsWith("wob.vw.vwg"))
            {
                return true;
            }
        }
        catch (Exception ex)
        {
            // Ignore
        }

        return false;
    }
}
