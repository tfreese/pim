// Created: 07.02.2017
package de.freese.pim.core.service;

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
     * @param value String
     *
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
            return URLDecoder.decode(value.trim(), "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param value String
     *
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
            return URLEncoder.encode(value.trim(), "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}