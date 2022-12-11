// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import de.freese.pim.gui.addressbook.model.FxKontakt;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Addressbook-Service für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 */
@Service("clientAddressBookService")
@Profile(
        {
                "ClientREST", "ClientEmbeddedServer"
        })
public class DefaultRestFxAddressbookService extends AbstractFxAddressbookService
{
    private RestTemplate restTemplate;

    /**
     * @see FxAddressbookService#deleteKontakt(long)
     */
    @Override
    public int deleteKontakt(final long id)
    {
        return getRestTemplate().postForObject("/addressBook/contact/delete/{contactID}", id, Integer.class);
    }

    /**
     * @see FxAddressbookService#getKontaktDetails(long[])
     */
    @Override
    public List<FxKontakt> getKontaktDetails(final long... ids)
    {
        FxKontakt[] details = getRestTemplate().postForObject("/addressBook/details", ids, FxKontakt[].class);

        return Arrays.asList(details);
    }

    /**
     * @see FxAddressbookService#insertKontakt(FxKontakt)
     */
    @Override
    public void insertKontakt(final FxKontakt kontakt)
    {
        long primaryKey = getRestTemplate().postForObject("/addressBook/contact/insert", kontakt, Long.class);

        kontakt.setID(primaryKey);
    }

    @Resource
    public void setRestTemplateBuilder(final RestTemplateBuilder restTemplateBuilder)
    {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * @see FxAddressbookService#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    public int updateKontakt(final long id, final String nachname, final String vorname)
    {
        Map<String, String> variables = new HashMap<>();
        variables.put("surname", nachname);
        variables.put("forename", vorname);

        return getRestTemplate().postForObject("/addressBook/contact/update/{contactID}", id, Integer.class, variables);
    }

    protected RestTemplate getRestTemplate()
    {
        return this.restTemplate;
    }
}
