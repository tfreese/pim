// Created: 07.02.2017
package de.freese.pim.common.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Basis-Implementierung eines Remote-Service.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRemoteService extends AbstractService
{
    /**
     * Erzeugt eine neue Instanz von {@link AbstractRemoteService}
     */
    protected AbstractRemoteService()
    {
        super();
    }

    /**
     * @param value String
     * @return String
     */
    protected String urlDecode(final String value)
    {
        if (value == null)
        {
            return null;
        }

        try
        {
            String encoded = URLDecoder.decode(value.trim(), "UTF-8");

            return encoded;
        }
        catch (UnsupportedEncodingException ueex)
        {
            throw new RuntimeException(ueex);
        }
    }

    /**
     * @param value String
     * @return String
     */
    protected String urlEncode(final String value)
    {
        if (value == null)
        {
            return null;
        }

        try
        {
            String encoded = URLEncoder.encode(value.trim(), "UTF-8");

            return encoded;
        }
        catch (UnsupportedEncodingException ueex)
        {
            throw new RuntimeException(ueex);
        }
    }
}
