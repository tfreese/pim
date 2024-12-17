// Created: 14.02.2017
package de.freese.pim.gui.mail.service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import jakarta.annotation.Resource;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
 */
@SuppressWarnings("deprecation")
@Service("clientMailService")
@Profile({"ClientREST", "ClientEmbeddedServer"})
public class DefaultRestFxMailService extends AbstractFxMailService {
    private RestTemplate restTemplate;

    @Override
    public void connectAccount(final FxMailAccount account) {
        getRestTemplate().postForObject("/mail/connect", account, Void.class);
    }

    @Override
    public int deleteAccount(final long accountID) {
        final Integer affectedRows = getRestTemplate().postForObject("/mail/account/delete/{id}", accountID, Integer.class);

        return Optional.ofNullable(affectedRows).orElse(0);
    }

    @Override
    public void disconnectAccounts(final long... accountIDs) {
        getRestTemplate().postForObject("/mail/account/disconnect", accountIDs, Void.class);
    }

    @Override
    public List<FxMailAccount> getMailAccounts() {
        final FxMailAccount[] accounts = getRestTemplate().getForObject("/mail/accounts", FxMailAccount[].class);

        if (accounts == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(accounts);
    }

    @Override
    public void insertAccount(final FxMailAccount account) {
        final Long primaryKey = getRestTemplate().postForObject("/mail/account/insert", account, Long.class);

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
            final int[] result = getRestTemplate().postForObject("/mail/folder/update/{accountID}", toUpdate, int[].class, accountID);

            if (result == null) {
                throw new IllegalArgumentException("result");
            }

            affectedRows += IntStream.of(result).sum();
        }

        // ID = 0 -> insert
        final List<FxMailFolder> toInsert = folders.stream().filter(mf -> mf.getID() == 0).toList();

        if (!toInsert.isEmpty()) {
            final long[] primaryKeys = getRestTemplate().postForObject("/mail/folder/insert/{accountID}", toInsert, long[].class, accountID);

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
        final FxMailFolder[] folders = getRestTemplate().getForObject("/mail/folder/{accountID}", FxMailFolder[].class, accountID);

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
                mails = getRestTemplate().getForObject(restURL, FxMail[].class, account.getID(), folder.getID(), folderName);
            }
            else {
                // String restURL = "/mail/mailsAsyncDeferredResult/{accountID}/{folderID}/{folderFullName}";
                final String restURL = "/mail/mailsAsyncCallable/{accountID}/{folderID}/{folderFullName}";
                // ListenableFuture<ResponseEntity<String>> responseJSON =
                // getAsyncRestTemplate().getForEntity(restURL, String.class, account.getID(), folder.getID(), folderName);
                // String jsonContent = responseJSON.get().getBody();
                // mails = getJsonMapper().readValue(jsonContent, FxMail[].class);

                final ResponseEntity<FxMail[]> response = getRestTemplate().getForEntity(restURL, FxMail[].class, account.getID(), folder.getID(), folderName);
                // mails = mails.get(10, TimeUnit.SECONDS).getBody();
                mails = response.getBody();
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

    @Resource
    public void setRestTemplateBuilder(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public List<FxMailFolder> test(final FxMailAccount account) {
        final FxMailFolder[] folders = getRestTemplate().postForObject("/mail/test", account, FxMailFolder[].class);

        if (folders == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(folders);
    }

    @Override
    public int updateAccount(final FxMailAccount account) {
        final Integer affectedRows = getRestTemplate().postForObject("/mail/account/update", account, int.class);

        return Optional.ofNullable(affectedRows).orElse(0);
    }

    protected RestTemplate getRestTemplate() {
        return this.restTemplate;
    }

    @Override
    protected MailContent loadMailContent(final Path mailPath, final FxMailAccount account, final FxMail mail, final IOMonitor monitor) throws Exception {
        final ResponseEntity<String> jsonContent = getRestTemplate().getForEntity("/mail/content/{accountID}/{folderFullName}/{mailUID}", String.class, account.getID(),
                urlEncode(urlEncode(mail.getFolderFullName())), mail.getUID());

        saveMailContent(mailPath, jsonContent.getBody());

        return getJsonMapper().readValue(jsonContent.getBody(), DefaultMailContent.class);
    }
}
