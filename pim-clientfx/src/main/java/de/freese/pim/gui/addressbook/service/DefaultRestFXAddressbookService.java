// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.freese.pim.common.PIMException;
import de.freese.pim.gui.addressbook.model.FXKontakt;

/**
 * AddressbookService für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 */
@Service("clientAddressBookService")
@Profile("ClientREST")
public class DefaultRestFXAddressbookService extends AbstractFXAddressbookService
{
    /**
    *
    */
    private RestTemplate restTemplate = null;

    /**
     * Erzeugt eine neue Instanz von {@link DefaultRestFXAddressbookService}
     */
    public DefaultRestFXAddressbookService()
    {
        super();
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#deleteKontakt(long)
     */
    @Override
    public int deleteKontakt(final long id) throws PIMException
    {
        return 0;
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#getKontaktDetails(long[])
     */
    @Override
    public List<FXKontakt> getKontaktDetails(final long... ids) throws PIMException
    {
        return Collections.emptyList();
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#insertKontakt(de.freese.pim.gui.addressbook.model.FXKontakt)
     */
    @Override
    public void insertKontakt(final FXKontakt kontakt) throws PIMException
    {

    }

    /**
     * @param restTemplateBuilder {@link RestTemplateBuilder}
     */
    @Resource
    public void setRestTemplateBuilder(final RestTemplateBuilder restTemplateBuilder)
    {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    public int updateKontakt(final long id, final String nachname, final String vorname) throws PIMException
    {
        return 0;
    }

    /**
     * @return {@link RestTemplate}
     */
    protected RestTemplate getRestTemplate()
    {
        return this.restTemplate;
    }
}
