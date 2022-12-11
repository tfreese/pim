// Created: 14.02.2017
package de.freese.pim.gui.mail.service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import jakarta.annotation.Resource;

import de.freese.pim.core.PIMException;
import de.freese.pim.core.mail.DefaultMailContent;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.gui.mail.model.FxMail;
import de.freese.pim.gui.mail.model.FxMailAccount;
import de.freese.pim.gui.mail.model.FxMailFolder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * REST-MailService für JavaFX.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("deprecation")
@Service("clientMailService")
@Profile(
        {
                "ClientREST", "ClientEmbeddedServer"
        })
public class DefaultRestFxMailService extends AbstractFxMailService
{
    private RestTemplate restTemplate;

    /**
     * @see FxMailService#connectAccount(FxMailAccount)
     */
    @Override
    public void connectAccount(final FxMailAccount account)
    {
        getRestTemplate().postForObject("/mail/connect", account, Void.class);
    }

    /**
     * @see FxMailService#deleteAccount(long)
     */
    @Override
    public int deleteAccount(final long accountID)
    {
        return getRestTemplate().postForObject("/mail/account/delete/{id}", accountID, Integer.class);
    }

    /**
     * @see FxMailService#disconnectAccounts(long[])
     */
    @Override
    public void disconnectAccounts(final long... accountIDs)
    {
        getRestTemplate().postForObject("/mail/account/disconnect", accountIDs, Void.class);
    }

    /**
     * @see FxMailService#getMailAccounts()
     */
    @Override
    public List<FxMailAccount> getMailAccounts()
    {
        FxMailAccount[] accounts = getRestTemplate().getForObject("/mail/accounts", FxMailAccount[].class);

        return Arrays.asList(accounts);
    }

    /**
     * @see FxMailService#insertAccount(FxMailAccount)
     */
    @Override
    public void insertAccount(final FxMailAccount account)
    {
        long primaryKey = getRestTemplate().postForObject("/mail/account/insert", account, Long.class);

        account.setID(primaryKey);
    }

    /**
     * @see FxMailService#insertOrUpdateFolder(long, java.util.List)
     */
    @Override
    public int insertOrUpdateFolder(final long accountID, final List<FxMailFolder> folders)
    {
        int affectedRows = 0;

        // ID != 0 -> update
        List<FxMailFolder> toUpdate = folders.stream().filter(mf -> mf.getID() > 0).toList();

        if (!toUpdate.isEmpty())
        {
            int[] result = getRestTemplate().postForObject("/mail/folder/update/{accountID}", toUpdate, int[].class, accountID);
            affectedRows += IntStream.of(result).sum();
        }

        // ID = 0 -> insert
        List<FxMailFolder> toInsert = folders.stream().filter(mf -> mf.getID() == 0).toList();

        if (!toInsert.isEmpty())
        {
            long[] primaryKeys = getRestTemplate().postForObject("/mail/folder/insert/{accountID}", toInsert, long[].class, accountID);
            affectedRows += primaryKeys.length;

            for (int i = 0; i < primaryKeys.length; i++)
            {
                toInsert.get(i).setAccountID(accountID);
                toInsert.get(i).setID(primaryKeys[i]);
            }
        }

        return affectedRows;
    }

    /**
     * @see FxMailService#loadFolder(long)
     */
    @Override
    public List<FxMailFolder> loadFolder(final long accountID)
    {
        FxMailFolder[] folders = getRestTemplate().getForObject("/mail/folder/{accountID}", FxMailFolder[].class, accountID);

        return Arrays.asList(folders);
    }

    /**
     * @see FxMailService#loadMails(FxMailAccount, FxMailFolder)
     */
    @Override
    public List<FxMail> loadMails(final FxMailAccount account, final FxMailFolder folder)
    {
        getLogger().info("Load Mails: account={}, folder={}", account.getMail(), folder.getFullName());

        try
        {
            String folderName = urlEncode(urlEncode(folder.getFullName()));
            FxMail[] mails = null;
            boolean async = false;

            if (!async)
            {
                String restURL = "/mail/mails/{accountID}/{folderID}/{folderFullName}";
                mails = getRestTemplate().getForObject(restURL, FxMail[].class, account.getID(), folder.getID(), folderName);
            }
            else
            {
                // String restURL = "/mail/mailsAsyncDeferredResult/{accountID}/{folderID}/{folderFullName}";
                String restURL = "/mail/mailsAsyncCallable/{accountID}/{folderID}/{folderFullName}";
                // ListenableFuture<ResponseEntity<String>> responseJSON =
                // getAsyncRestTemplate().getForEntity(restURL, String.class, account.getID(), folder.getID(), folderName);
                // String jsonContent = responseJSON.get().getBody();
                // mails = getJsonMapper().readValue(jsonContent, FxMail[].class);

                ResponseEntity<FxMail[]> response =
                        getRestTemplate().getForEntity(restURL, FxMail[].class, account.getID(), folder.getID(), folderName);
                // mails = mails.get(10, TimeUnit.SECONDS).getBody();
                mails = response.getBody();
            }

            getLogger().info("Load Mails finished: account={}, folder={}", account.getMail(), folder.getFullName());

            return Arrays.asList(mails);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    @Resource
    public void setRestTemplateBuilder(final RestTemplateBuilder restTemplateBuilder)
    {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * @see FxMailService#test(FxMailAccount)
     */
    @Override
    public List<FxMailFolder> test(final FxMailAccount account)
    {
        FxMailFolder[] folders = getRestTemplate().postForObject("/mail/test", account, FxMailFolder[].class);

        return Arrays.asList(folders);
    }

    /**
     * @see FxMailService#updateAccount(FxMailAccount)
     */
    @Override
    public int updateAccount(final FxMailAccount account)
    {
        return getRestTemplate().postForObject("/mail/account/update", account, int.class);
    }

    protected RestTemplate getRestTemplate()
    {
        return this.restTemplate;
    }

    /**
     * @see AbstractFxMailService#loadMailContent(java.nio.file.Path, FxMailAccount,
     * FxMail, de.freese.pim.core.utils.io.IOMonitor)
     */
    @Override
    protected MailContent loadMailContent(final Path mailPath, final FxMailAccount account, final FxMail mail, final IOMonitor monitor) throws Exception
    {
        ResponseEntity<String> jsonContent = getRestTemplate().getForEntity("/mail/content/{accountID}/{folderFullName}/{mailUID}", String.class,
                account.getID(), urlEncode(urlEncode(mail.getFolderFullName())), mail.getUID());

        saveMailContent(mailPath, jsonContent.getBody());

        return getJsonMapper().readValue(jsonContent.getBody(), DefaultMailContent.class);
    }
}
