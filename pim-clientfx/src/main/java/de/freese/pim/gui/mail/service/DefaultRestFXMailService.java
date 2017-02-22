/**
 * Created: 14.02.2017
 */

package de.freese.pim.gui.mail.service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
 * MailService für JavaFX, wenn es keinen Server gibt.
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
    public void connectAccount(final FXMailAccount account) throws PIMException
    {
        getRestTemplate().postForObject("/mail/connect", account, Void.class);
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#deleteAccount(long)
     */
    @Override
    public int deleteAccount(final long accountID) throws PIMException
    {
        return 0;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#disconnectAccounts(long[])
     */
    @Override
    public void disconnectAccounts(final long...accountIDs) throws PIMException
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
    public List<FXMailAccount> getMailAccounts() throws PIMException
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
    public void insertAccount(final FXMailAccount account) throws PIMException
    {
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#insertOrUpdateFolder(long, java.util.List)
     */
    @Override
    public int insertOrUpdateFolder(final long accountID, final List<FXMailFolder> folders) throws PIMException
    {
        return 0;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadFolder(long)
     */
    @Override
    public List<FXMailFolder> loadFolder(final long accountID) throws PIMException
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
        // MailContent mailContent =
        // this.restTemplate.getForObject("/mail/content/{accountID}/{folderFullName}/{mailUID}", MailContent.class, accountID, folderFullName, mailUID);
        ResponseEntity<String> jsonContent =
                getRestTemplate().getForEntity("/mail/content/{accountID}/{folderFullName}/{mailUID}", String.class, accountID, folderFullName, mailUID);

        saveMailContent(mailPath, jsonContent.getBody());

        MailContent mailContent = getJsonMapper().readValue(jsonContent.getBody(), DefaultMailContent.class);

        return mailContent;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadMails(long, long, java.lang.String)
     */
    @Override
    public List<FXMail> loadMails(final long accountID, final long folderID, final String folderFullName) throws PIMException
    {
        // FXMail[] mails =
        // getRestTemplate().getForObject("/mail/mails/{accountID}/{folderID}/{folderFullName}", FXMail[].class, accountID, folderID, folderFullName);
        ListenableFuture<ResponseEntity<FXMail[]>> mails = getAsyncRestTemplate().getForEntity("/mail/mailsAsync/{accountID}/{folderID}/{folderFullName}",
                FXMail[].class, accountID, folderID, folderFullName);

        try
        {
            FXMail[] array = mails.get().getBody();
            // String value = mails.get().getBody();

            return Arrays.asList(array);
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
    public List<FXMailFolder> test(final FXMailAccount account) throws PIMException
    {
        return Collections.emptyList();
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#updateAccount(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public int updateAccount(final FXMailAccount account) throws PIMException
    {
        return 0;
    }
}
