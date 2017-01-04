// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Date;

/**
 * Interface für eine Mail
 *
 * @author Thomas Freese
 */
public interface IMail
{
    /**
     * Liefert das Empfangs-Datum.
     * 
     * @return {@link Date}
     */
    public Date getReceivedDate();
}
