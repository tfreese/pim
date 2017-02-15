// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisimplementierung eines JavaFX-AddressbookService.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFXAddressbookService implements FXAddressbookService
{
    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link AbstractFXAddressbookService}
     */
    public AbstractFXAddressbookService()
    {
        super();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
