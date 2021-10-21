// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.freese.pim.gui.addressbook.model.FXKontakt;

/**
 * AddressbookService für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 */
@Service("clientAddressBookService")
@Profile(
{
        "ClientREST", "ClientEmbeddedServer"
})
public class DefaultRestFXAddressbookService extends AbstractFXAddressbookService
{
    /**
    *
    */
    private RestTemplate restTemplate;

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#deleteKontakt(long)
     */
    @Override
    public int deleteKontakt(final long id)
    {
        return getRestTemplate().postForObject("/addressBook/contact/delete/{contactID}", id, Integer.class);
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#getKontaktDetails(long[])
     */
    @Override
    public List<FXKontakt> getKontaktDetails(final long...ids)
    {
        FXKontakt[] details = getRestTemplate().postForObject("/addressBook/details", ids, FXKontakt[].class);

        return Arrays.asList(details);
    }

    /**
     * @return {@link RestTemplate}
     */
    protected RestTemplate getRestTemplate()
    {
        return this.restTemplate;
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#insertKontakt(de.freese.pim.gui.addressbook.model.FXKontakt)
     */
    @Override
    public void insertKontakt(final FXKontakt kontakt)
    {
        long primaryKey = getRestTemplate().postForObject("/addressBook/contact/insert", kontakt, Long.class);

        kontakt.setID(primaryKey);
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
    public int updateKontakt(final long id, final String nachname, final String vorname)
    {
        Map<String, String> variables = new HashMap<>();
        variables.put("surname", nachname);
        variables.put("forename", vorname);

        return getRestTemplate().postForObject("/addressBook/contact/update/{contactID}", id, Integer.class, variables);
    }
}
