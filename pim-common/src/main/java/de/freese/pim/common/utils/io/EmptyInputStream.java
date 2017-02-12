/**
 * Created: 06.02.2017
 */

package de.freese.pim.common.utils.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Leerer {@link InputStream}.
 * 
 * @author Thomas Freese
 */
public class EmptyInputStream extends InputStream
{
    /**
     * Erstellt ein neues {@link EmptyInputStream} Object.
     */
    public EmptyInputStream()
    {
        super();
    }

    /**
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException
    {
        return 0;
    }

    /**
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException
    {
        return 0;
    }
}
