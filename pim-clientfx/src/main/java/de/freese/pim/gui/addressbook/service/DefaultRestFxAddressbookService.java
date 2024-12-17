// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.Resource;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.freese.pim.gui.addressbook.model.FxKontakt;

/**
 * Addressbook-Service für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 */
@Service("clientAddressBookService")
@Profile({"ClientREST", "ClientEmbeddedServer"})
public class DefaultRestFxAddressbookService extends AbstractFxAddressbookService {
    private RestTemplate restTemplate;

    @Override
    public int deleteKontakt(final long id) {
        final Integer affectedRows = getRestTemplate().postForObject("/addressBook/contact/delete/{contactID}", id, Integer.class);

        return Optional.ofNullable(affectedRows).orElse(0);
    }

    @Override
    public List<FxKontakt> getKontaktDetails(final long... ids) {
        final FxKontakt[] details = getRestTemplate().postForObject("/addressBook/details", ids, FxKontakt[].class);

        if (details == null) {
            throw new IllegalArgumentException("details");
        }

        return Arrays.asList(details);
    }

    @Override
    public void insertKontakt(final FxKontakt kontakt) {
        final Long primaryKey = getRestTemplate().postForObject("/addressBook/contact/insert", kontakt, Long.class);

        if (primaryKey == null) {
            throw new IllegalArgumentException("primaryKey");
        }

        kontakt.setID(primaryKey);
    }

    @Resource
    public void setRestTemplateBuilder(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public int updateKontakt(final long id, final String nachname, final String vorname) {
        final Map<String, String> variables = new HashMap<>();
        variables.put("surname", nachname);
        variables.put("forename", vorname);

        final Integer affectedRows = getRestTemplate().postForObject("/addressBook/contact/update/{contactID}", id, Integer.class, variables);

        return Optional.ofNullable(affectedRows).orElse(0);
    }

    protected RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
}
