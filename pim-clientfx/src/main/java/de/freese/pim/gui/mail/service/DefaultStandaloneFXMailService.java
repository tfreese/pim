// Created: 14.02.2017
package de.freese.pim.gui.mail.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jakarta.annotation.Resource;

import com.fasterxml.jackson.databind.JavaType;
import de.freese.pim.core.PIMException;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;
import de.freese.pim.core.service.MailService;
import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailAccount;
import de.freese.pim.gui.mail.model.FXMailFolder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Standalone-MailService für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 */
@Service("clientMailService")
@Profile("ClientStandalone")
public class DefaultStandaloneFXMailService extends AbstractFXMailService
{
    private MailService mailService;

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#connectAccount(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public void connectAccount(final FXMailAccount account)
    {
        try
        {
            MailAccount pojo = toPojoMailAccount(account);

            getMailService().connectAccount(pojo);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#deleteAccount(long)
     */
    @Override
    public int deleteAccount(final long accountID)
    {
        try
        {
            return getMailService().deleteAccount(accountID);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#disconnectAccounts(long[])
     */
    @Override
    public void disconnectAccounts(final long... accountIDs)
    {
        try
        {
            getMailService().disconnectAccounts(accountIDs);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#getMailAccounts()
     */
    @Override
    public List<FXMailAccount> getMailAccounts()
    {
        try
        {
            List<MailAccount> accounts = getMailService().getMailAccounts();

            return toFXMailAccounts(accounts);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#insertAccount(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public void insertAccount(final FXMailAccount account)
    {
        try
        {
            MailAccount pojo = toPojoMailAccount(account);

            long id = getMailService().insertAccount(pojo);
            account.setID(id);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#insertOrUpdateFolder(long, java.util.List)
     */
    @Override
    public int insertOrUpdateFolder(final long accountID, final List<FXMailFolder> folders)
    {
        try
        {
            int affectedRows = 0;

            // ID != 0 -> update
            List<MailFolder> toUpdate = toPojoMailFolders(folders.stream().filter(mf -> mf.getID() > 0).toList());

            if (!toUpdate.isEmpty())
            {
                int[] result = getMailService().updateFolder(accountID, toUpdate);
                affectedRows += IntStream.of(result).sum();
            }

            // ID = 0 -> insert
            List<MailFolder> toInsert = toPojoMailFolders(folders.stream().filter(mf -> mf.getID() == 0).toList());

            if (!toInsert.isEmpty())
            {
                long[] primaryKeys = getMailService().insertFolder(accountID, toInsert);
                affectedRows += primaryKeys.length;

                for (int i = 0; i < primaryKeys.length; i++)
                {
                    toInsert.get(i).setAccountID(accountID);
                    toInsert.get(i).setID(primaryKeys[i]);
                }
            }

            return affectedRows;
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadFolder(long)
     */
    @Override
    public List<FXMailFolder> loadFolder(final long accountID)
    {
        try
        {
            List<MailFolder> folders = getMailService().loadFolder(accountID);

            List<FXMailFolder> fxBeans = toFXMailFolders(folders);

            buildHierarchie(fxBeans);

            return fxBeans;
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadMails(de.freese.pim.gui.mail.model.FXMailAccount, de.freese.pim.gui.mail.model.FXMailFolder)
     */
    @Override
    public List<FXMail> loadMails(final FXMailAccount account, final FXMailFolder folder)
    {
        getLogger().info("Load Mails: account={}, folder={}", account.getMail(), folder.getFullName());

        try
        {
            List<Mail> mails = getMailService().loadMails(account.getID(), folder.getID(), folder.getFullName());

            List<FXMail> fxBeans = toFXMails(mails);

            getLogger().info("Load Mails finished: account={}, folder={}", account.getMail(), folder.getFullName());

            return fxBeans;
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    @Resource
    public void setMailService(final MailService mailService)
    {
        this.mailService = mailService;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#test(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public List<FXMailFolder> test(final FXMailAccount account)
    {
        try
        {
            // MailAccount pojo = toPOJO(account);
            //
            // List<FXMailFolder> fxBeans = getMailService().test(pojo).stream().map(this::toFXBean).collect(Collectors.toList());
            MailAccount pojo = toPojoMailAccount(account);

            return toFXMailFolders(getMailService().test(pojo));
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    // /**
    // * @see de.freese.pim.gui.mail.service.FXMailService#loadMails2(long, long, java.lang.String)
    // */
    // @Override
    // public Future<List<FXMail>> loadMails2(final long accountID, final long folderID, final String folderFullName) throws Exception
    // {
    // Future<List<Mail>> pojoFuture = getMailService().loadMails2(accountID, folderID, folderFullName);
    //
    // List<FXMail> fxBeans = toFXMails(pojoFuture.get().stream().collect(Collectors.toList()));
    //
    // return new AsyncResult<>(fxBeans);
    // }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#updateAccount(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public int updateAccount(final FXMailAccount account)
    {
        try
        {
            MailAccount pojo = toPojoMailAccount(account);

            return getMailService().updateAccount(pojo);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    protected MailService getMailService()
    {
        return this.mailService;
    }

    /**
     * @see de.freese.pim.gui.mail.service.AbstractFXMailService#loadMailContent(java.nio.file.Path, de.freese.pim.gui.mail.model.FXMailAccount,
     * de.freese.pim.gui.mail.model.FXMail, de.freese.pim.core.utils.io.IOMonitor)
     */
    @Override
    protected MailContent loadMailContent(final Path mailPath, final FXMailAccount account, final FXMail mail, final IOMonitor monitor) throws Exception
    {
        MailContent mailContent = getMailService().loadMailContent(account.getID(), mail.getFolderFullName(), mail.getUID(), monitor);

        saveMailContent(mailPath, mailContent);

        return mailContent;
    }

    private List<FXMailAccount> toFXMailAccounts(final List<MailAccount> accounts) throws Exception
    {
        // FXMailAccount ma = new FXMailAccount();
        // ma.setID(pojo.getID());
        // ma.setImapHost(pojo.getImapHost());
        // ma.setImapLegitimation(pojo.isImapLegitimation());
        // ma.setImapPort(pojo.getImapPort());
        // ma.setMail(pojo.getMail());
        // ma.setPassword(pojo.getPassword());
        // ma.setSmtpHost(pojo.getSmtpHost());
        // ma.setSmtpLegitimation(pojo.isSmtpLegitimation());
        // ma.setSmtpPort(pojo.getSmtpPort());

        JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FXMailAccount.class);

        byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(accounts);

        return getJsonMapper().readValue(jsonBytes, type);
    }

    private List<FXMailFolder> toFXMailFolders(final List<MailFolder> folders) throws Exception
    {
        // FXMailFolder mf = new FXMailFolder();
        // mf.setAbonniert(folder.isAbonniert());
        // mf.setAccountID(folder.getAccountID());
        // mf.setFullName(folder.getFullName());
        // mf.setID(folder.getID());
        // mf.setName(folder.getName());

        JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FXMailFolder.class);

        byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(folders);

        return getJsonMapper().readValue(jsonBytes, type);
    }

    private List<FXMail> toFXMails(final List<Mail> mails) throws Exception
    {
        // Mail m = new Mail();
        // m.setBcc(mail.getBcc());
        // m.setCc(mail.getCc());
        // m.setFolderFullName(mail.getFolderFullName());
        // m.setFolderID(mail.getFolderID());
        // m.setFrom(mail.getFrom());
        // m.setMsgNum(mail.getMsgNum());
        // m.setReceivedDate(mail.getReceivedDate());
        // m.setSeen(mail.isSeen());
        // m.setSendDate(mail.getSendDate());
        // m.setSize(mail.getSize());
        // m.setSubject(mail.getSubject());
        // m.setTo(mail.getTo());
        // m.setUID(mail.getUID());

        // List<FXMail> fxBeans = pojos.stream().map(this::toFXBean).collect(Collectors.toList());

        JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FXMail.class);

        // byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(mails);
        String json = getJsonMapper().writer().writeValueAsString(mails);

        return getJsonMapper().readValue(json, type);
    }

    private MailAccount toPojoMailAccount(final FXMailAccount account) throws Exception
    {
        // MailAccount ma = new MailAccount();
        // ma.setID(account.getID());
        // ma.setImapHost(account.getImapHost());
        // ma.setImapLegitimation(account.isImapLegitimation());
        // ma.setImapPort(account.getImapPort());
        // ma.setMail(account.getMail());
        // ma.setPassword(account.getPassword());
        // ma.setSmtpHost(account.getSmtpHost());
        // ma.setSmtpLegitimation(account.isSmtpLegitimation());
        // ma.setSmtpPort(account.getSmtpPort());

        byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(account);

        return getJsonMapper().readValue(jsonBytes, MailAccount.class);
    }

    private List<MailFolder> toPojoMailFolders(final List<FXMailFolder> folders) throws Exception
    {
        JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, MailFolder.class);

        byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(folders);

        return getJsonMapper().readValue(jsonBytes, type);
    }
}
