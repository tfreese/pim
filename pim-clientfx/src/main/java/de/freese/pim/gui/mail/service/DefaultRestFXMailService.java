/**
 * Created: 14.02.2017
 */

package de.freese.pim.gui.mail.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.freese.pim.common.PIMException;
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
    public void disconnectAccounts(final long... accountIDs) throws PIMException
    {
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#getMailAccounts()
     */
    @Override
    public List<FXMailAccount> getMailAccounts() throws PIMException
    {
        FXMailAccount[] accounts = this.restTemplate.getForObject("/mail/accounts", FXMailAccount[].class);

        return Arrays.asList(accounts);
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
        return Collections.emptyList();
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadMails(long, long, java.lang.String)
     */
    @Override
    public List<FXMail> loadMails(final long accountID, final long folderID, final String folderFullName) throws PIMException
    {
        return Collections.emptyList();
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

    /**
     * @return {@link RestTemplate}
     */
    protected RestTemplate getRestTemplate()
    {
        return this.restTemplate;
    }

    /**
     * @see de.freese.pim.gui.mail.service.AbstractFXMailService#loadMailContentAsJSON(long, java.lang.String, long,
     *      de.freese.pim.common.utils.io.IOMonitor)
     */
    @Override
    protected String loadMailContentAsJSON(final long accountID, final String folderFullName, final long mailUID, final IOMonitor monitor)
            throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }
}
