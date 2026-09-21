package de.freese.pim.gui.addressbook.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.freese.pim.gui.addressbook.model.FxKontakt;

/**
 * Addressbook-Service für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 * @since 15.02.2017
 */
@Service("clientAddressBookService")
@Profile({"ClientREST", "ClientEmbeddedServer"})
public class DefaultRestFxAddressbookService extends AbstractFxAddressbookService {
    private final RestClient restClient;

    public DefaultRestFxAddressbookService(final RestClient.Builder restClientBuilder) {
        super();

        this.restClient = restClientBuilder.build();
    }

    @Override
    public int deleteKontakt(final long id) {
        final Integer affectedRows = getRestClient()
                .post()
                .uri("/addressBook/contact/delete/{contactID}", id)
                .retrieve()
                .body(Integer.class);

        return Optional.ofNullable(affectedRows).orElse(0);
    }

    @Override
    public List<FxKontakt> getKontaktDetails(final long... ids) {
        final FxKontakt[] details = getRestClient()
                .post()
                .uri("/addressBook/details")
                .body(ids)
                .retrieve()
                .body(FxKontakt[].class);

        if (details == null) {
            throw new IllegalArgumentException("details");
        }

        return Arrays.asList(details);
    }

    @Override
    public void insertKontakt(final FxKontakt kontakt) {
        final Long primaryKey = getRestClient()
                .post()
                .uri("/addressBook/contact/insert")
                .body(kontakt)
                .retrieve()
                .body(Long.class);

        if (primaryKey == null) {
            throw new IllegalArgumentException("primaryKey");
        }

        kontakt.setID(primaryKey);
    }

    @Override
    public int updateKontakt(final long id, final String nachname, final String vorname) {
        final Map<String, String> variables = new HashMap<>();
        variables.put("surname", nachname);
        variables.put("forename", vorname);

        final Integer affectedRows = getRestClient()
                .post()
                .uri("/addressBook/contact/update/{contactID}", id)
                .body(variables)
                .retrieve()
                .body(Integer.class);

        return Optional.ofNullable(affectedRows).orElse(0);
    }

    protected RestClient getRestClient() {
        return restClient;
    }
}
