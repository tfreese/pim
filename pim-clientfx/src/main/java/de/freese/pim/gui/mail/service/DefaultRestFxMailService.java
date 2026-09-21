package de.freese.pim.gui.mail.service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.freese.pim.core.PIMException;
import de.freese.pim.core.mail.DefaultMailContent;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.gui.mail.model.FxMail;
import de.freese.pim.gui.mail.model.FxMailAccount;
import de.freese.pim.gui.mail.model.FxMailFolder;

/**
 * REST-MailService für JavaFX.
 *
 * @author Thomas Freese
 * @since 14.02.2017
 */
@Service("clientMailService")
@Profile({"ClientREST", "ClientEmbeddedServer"})
public class DefaultRestFxMailService extends AbstractFxMailService {
    private final RestClient restClient;

    public DefaultRestFxMailService(final RestClient.Builder restClientBuilder) {
        super();

        this.restClient = restClientBuilder.build();
    }

    @Override
    public void connectAccount(final FxMailAccount account) {
        getRestClient()
                .post()
                .uri("/mail/connect")
                .body(account)
                .retrieve()
                .body(Void.class);
    }

    @Override
    public int deleteAccount(final long accountID) {
        final Integer affectedRows = getRestClient()
                .post()
                .uri("/mail/account/delete/{id}", accountID)
                .retrieve()
                .body(Integer.class);

        return Optional.ofNullable(affectedRows).orElse(0);
    }

    @Override
    public void disconnectAccounts(final long... accountIDs) {
        getRestClient()
                .post()
                .uri("/mail/account/disconnect")
                .body(accountIDs)
                .retrieve()
                .body(Void.class);
    }

    @Override
    public List<FxMailAccount> getMailAccounts() {
        final FxMailAccount[] accounts = getRestClient()
                .get()
                .uri("/mail/accounts")
                .retrieve()
                .body(FxMailAccount[].class);

        if (accounts == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(accounts);
    }

    @Override
    public void insertAccount(final FxMailAccount account) {
        final Long primaryKey = getRestClient()
                .post()
                .uri("/mail/account/insert")
                .body(account)
                .retrieve()
                .body(Long.class);

        if (primaryKey == null) {
            throw new IllegalArgumentException("primaryKey");
        }

        account.setID(primaryKey);
    }

    @Override
    public int insertOrUpdateFolder(final long accountID, final List<FxMailFolder> folders) {
        int affectedRows = 0;

        // ID != 0 -> update
        final List<FxMailFolder> toUpdate = folders.stream().filter(mf -> mf.getID() > 0).toList();

        if (!toUpdate.isEmpty()) {
            final int[] result = getRestClient()
                    .post()
                    .uri("/mail/folder/update/{accountID}", accountID)
                    .body(toUpdate)
                    .retrieve()
                    .body(int[].class);

            if (result == null) {
                throw new IllegalArgumentException("result");
            }

            affectedRows += IntStream.of(result).sum();
        }

        // ID = 0 -> insert
        final List<FxMailFolder> toInsert = folders.stream().filter(mf -> mf.getID() == 0).toList();

        if (!toInsert.isEmpty()) {
            final long[] primaryKeys = getRestClient()
                    .post()
                    .uri("/mail/folder/insert/{accountID}", accountID)
                    .body(toInsert)
                    .retrieve()
                    .body(long[].class);

            if (primaryKeys == null) {
                throw new IllegalArgumentException("primaryKeys");
            }

            affectedRows += primaryKeys.length;

            for (int i = 0; i < primaryKeys.length; i++) {
                toInsert.get(i).setAccountID(accountID);
                toInsert.get(i).setID(primaryKeys[i]);
            }
        }

        return affectedRows;
    }

    @Override
    public List<FxMailFolder> loadFolder(final long accountID) {
        final FxMailFolder[] folders = getRestClient()
                .get()
                .uri("/mail/folder/{accountID}", accountID)
                .retrieve()
                .body(FxMailFolder[].class);

        if (folders == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(folders);
    }

    @Override
    public List<FxMail> loadMails(final FxMailAccount account, final FxMailFolder folder) {
        getLogger().info("Load Mails: account={}, folder={}", account.getMail(), folder.getFullName());

        try {
            final String folderName = urlEncode(urlEncode(folder.getFullName()));
            FxMail[] mails = null;
            final boolean async = false;

            if (!async) {
                final String restURL = "/mail/mails/{accountID}/{folderID}/{folderFullName}";
                mails = getRestClient()
                        .get()
                        .uri(restURL, account.getID(), folder.getID(), folderName)
                        .retrieve()
                        .body(FxMail[].class);
            }
            else {
                final String restURL = "/mail/mailsAsyncCallable/{accountID}/{folderID}/{folderFullName}";

                mails = getRestClient()
                        .get()
                        .uri(restURL, account.getID(), folder.getID(), folderName)
                        .retrieve()
                        .body(FxMail[].class);
            }

            getLogger().info("Load Mails finished: account={}, folder={}", account.getMail(), folder.getFullName());

            if (mails == null) {
                return Collections.emptyList();
            }

            return Arrays.asList(mails);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public List<FxMailFolder> test(final FxMailAccount account) {
        final FxMailFolder[] folders = getRestClient()
                .post()
                .uri("/mail/test")
                .body(account)
                .retrieve()
                .body(FxMailFolder[].class);

        if (folders == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(folders);
    }

    @Override
    public int updateAccount(final FxMailAccount account) {
        final Integer affectedRows = getRestClient()
                .post()
                .uri("/mail/account/update")
                .body(account)
                .retrieve()
                .body(Integer.class);

        return Optional.ofNullable(affectedRows).orElse(0);
    }

    protected RestClient getRestClient() {
        return restClient;
    }

    @Override
    protected MailContent loadMailContent(final Path mailPath, final FxMailAccount account, final FxMail mail, final IOMonitor monitor) throws Exception {
        final String jsonContent = getRestClient()
                .get()
                .uri("/mail/content/{accountID}/{folderFullName}/{mailUID}", account.getID(),
                        urlEncode(urlEncode(mail.getFolderFullName())), mail.getUID())
                .retrieve()
                .body(String.class);

        saveMailContent(mailPath, jsonContent);

        return getJsonMapper().readValue(jsonContent, DefaultMailContent.class);
    }
}
