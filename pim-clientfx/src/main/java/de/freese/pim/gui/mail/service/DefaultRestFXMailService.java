/**
 * Created: 14.02.2017
 */

package de.freese.pim.gui.mail.service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import de.freese.pim.common.PIMException;
import de.freese.pim.common.model.mail.DefaultMailContent;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.common.utils.io.IOMonitor;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailAccount;
import de.freese.pim.gui.mail.model.FXMailFolder;

/**
 * REST-MailService für JavaFX.
 *
 * @author Thomas Freese
 */
@Service("clientMailService")
@Profile("ClientREST")
public class DefaultRestFXMailService extends AbstractFXMailService
{
    /**
    *
    */
    private AsyncRestTemplate asyncRestTemplate = null;

    /**
     *
     */
    private RestTemplate restTemplate = null;

    /**
     * Erstellt ein neues {@link DefaultRestFXMailService} Object.
     */
    public DefaultRestFXMailService()
    {
        super();
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#connectAccount(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public void connectAccount(final FXMailAccount account)
    {
        getRestTemplate().postForObject("/mail/connect", account, Void.class);
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#deleteAccount(long)
     */
    @Override
    public int deleteAccount(final long accountID)
    {
        int affectedRows = getRestTemplate().postForObject("/mail/account/delete/{id}", accountID, Integer.class);

        return affectedRows;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#disconnectAccounts(long[])
     */
    @Override
    public void disconnectAccounts(final long...accountIDs)
    {
        getRestTemplate().postForObject("/mail/account/disconnect", accountIDs, Void.class);
    }

    /**
     * @return {@link AsyncRestTemplate}
     */
    public AsyncRestTemplate getAsyncRestTemplate()
    {
        return this.asyncRestTemplate;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#getMailAccounts()
     */
    @Override
    public List<FXMailAccount> getMailAccounts()
    {
        FXMailAccount[] accounts = getRestTemplate().getForObject("/mail/accounts", FXMailAccount[].class);

        return Arrays.asList(accounts);
    }

    /**
     * @return {@link RestTemplate}
     */
    protected RestTemplate getRestTemplate()
    {
        return this.restTemplate;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#insertAccount(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public void insertAccount(final FXMailAccount account)
    {
        long primaryKey = getRestTemplate().postForObject("/mail/account/insert", account, Long.class);

        account.setID(primaryKey);
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#insertOrUpdateFolder(long, java.util.List)
     */
    @Override
    public int insertOrUpdateFolder(final long accountID, final List<FXMailFolder> folders)
    {
        int affectedRows = 0;

        // ID = 0 -> insert
        List<FXMailFolder> toInsert = folders.stream().filter(mf -> mf.getID() == 0).collect(Collectors.toList());

        long[] primaryKeys = getRestTemplate().postForObject("/mail/folder/insert/{accountID}", toInsert, long[].class, accountID);
        affectedRows += primaryKeys.length;

        for (int i = 0; i < primaryKeys.length; i++)
        {
            toInsert.get(i).setID(primaryKeys[i]);
        }

        // ID != 0 -> update
        List<FXMailFolder> toUpdate = folders.stream().filter(mf -> mf.getID() > 0).collect(Collectors.toList());
        int[] result = getRestTemplate().postForObject("/mail/folder/update/{accountID}", toUpdate, int[].class, accountID);
        affectedRows += IntStream.of(result).sum();

        return affectedRows;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadFolder(long)
     */
    @Override
    public List<FXMailFolder> loadFolder(final long accountID)
    {
        FXMailFolder[] folders = getRestTemplate().getForObject("/mail/folder/{accountID}", FXMailFolder[].class, accountID);

        return Arrays.asList(folders);
    }

    /**
     * @see de.freese.pim.gui.mail.service.AbstractFXMailService#loadMailContent(java.nio.file.Path, long, java.lang.String, long,
     *      de.freese.pim.common.utils.io.IOMonitor)
     */
    @Override
    protected MailContent loadMailContent(final Path mailPath, final long accountID, final String folderFullName, final long mailUID, final IOMonitor monitor)
        throws Exception
    {
        ResponseEntity<String> jsonContent = getRestTemplate().getForEntity("/mail/content/{accountID}/{folderFullName}/{mailUID}", String.class, accountID,
                urlEncode(urlEncode(folderFullName)), mailUID);

        saveMailContent(mailPath, jsonContent.getBody());

        MailContent mailContent = getJsonMapper().readValue(jsonContent.getBody(), DefaultMailContent.class);

        return mailContent;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadMails(long, long, java.lang.String)
     */
    @Override
    public List<FXMail> loadMails(final long accountID, final long folderID, final String folderFullName)
    {
        try
        {
            // FXMail[] mails =
            // getRestTemplate().getForObject("/mail/mails/{accountID}/{folderID}/{folderFullName}", FXMail[].class, accountID, folderID,
            // urlEncode(urlEncode(folderFullName)));
            ListenableFuture<ResponseEntity<FXMail[]>> mails = getAsyncRestTemplate().getForEntity("/mail/mailsAsync/{accountID}/{folderID}/{folderFullName}",
                    FXMail[].class, accountID, folderID, urlEncode(urlEncode(folderFullName)));

            // ListenableFuture<ResponseEntity<String>> mails = getAsyncRestTemplate().getForEntity("/mail/mailsAsync/{accountID}/{folderID}/{folderFullName}",
            // String.class, accountID, folderID, urlEncode(urlEncode(folderFullName)));

            // FXMail[] array = mails.get(10, TimeUnit.SECONDS).getBody();
            FXMail[] array = mails.get().getBody();

            return Arrays.asList(array);

            // String jsonContent = mails.get().getBody();
            // return Collections.emptyList();
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @param restTemplateBuilder {@link RestTemplateBuilder}
     */
    @Resource
    public void setRestTemplateBuilder(final RestTemplateBuilder restTemplateBuilder)
    {
        this.restTemplate = restTemplateBuilder.build();
        this.asyncRestTemplate = new AsyncRestTemplate((AsyncListenableTaskExecutor) getTaskScheduler());
        // this.asyncRestTemplate = new AsyncRestTemplate(new SimpleClientHttpRequestFactory(), this.restTemplate);
        this.asyncRestTemplate.setErrorHandler(this.restTemplate.getErrorHandler());
        this.asyncRestTemplate.setUriTemplateHandler(this.restTemplate.getUriTemplateHandler());
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#test(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public List<FXMailFolder> test(final FXMailAccount account)
    {
        FXMailFolder[] folders = getRestTemplate().postForObject("/mail/test", account, FXMailFolder[].class);

        return Arrays.asList(folders);
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#updateAccount(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public int updateAccount(final FXMailAccount account)
    {
        int affectedRows = getRestTemplate().postForObject("/mail/account/update", account, int.class);

        return affectedRows;
    }
}
