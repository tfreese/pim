// Created: 07.02.2017
package de.freese.pim.core.service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Basis-Implementierung eines Remote-Service.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRemoteService extends AbstractService {
    protected String urlDecode(final String value) {
        if (value == null) {
            return null;
        }

        return URLDecoder.decode(value.strip(), StandardCharsets.UTF_8);
    }

    protected String urlEncode(final String value) {
        if (value == null) {
            return null;
        }

        return URLEncoder.encode(value.strip(), StandardCharsets.UTF_8);
    }
}
