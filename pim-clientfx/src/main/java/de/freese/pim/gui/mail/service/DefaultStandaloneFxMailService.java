// Created: 14.02.2017
package de.freese.pim.gui.mail.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jakarta.annotation.Resource;

import com.fasterxml.jackson.databind.JavaType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import de.freese.pim.core.PIMException;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;
import de.freese.pim.core.service.MailService;
import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.gui.mail.model.FxMail;
import de.freese.pim.gui.mail.model.FxMailAccount;
import de.freese.pim.gui.mail.model.FxMailFolder;

/**
 * Standalone-MailService für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 */
@Service("clientMailService")
@Profile("ClientStandalone")
public class DefaultStandaloneFxMailService extends AbstractFxMailService {
    private MailService mailService;

    @Override
    public void connectAccount(final FxMailAccount account) {
        try {
            MailAccount pojo = toPojoMailAccount(account);

            getMailService().connectAccount(pojo);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public int deleteAccount(final long accountID) {
        try {
            return getMailService().deleteAccount(accountID);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public void disconnectAccounts(final long... accountIDs) {
        try {
            getMailService().disconnectAccounts(accountIDs);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public List<FxMailAccount> getMailAccounts() {
        try {
            List<MailAccount> accounts = getMailService().getMailAccounts();

            return toFXMailAccounts(accounts);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public void insertAccount(final FxMailAccount account) {
        try {
            MailAccount pojo = toPojoMailAccount(account);

            long id = getMailService().insertAccount(pojo);
            account.setID(id);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public int insertOrUpdateFolder(final long accountID, final List<FxMailFolder> folders) {
        try {
            int affectedRows = 0;

            // ID != 0 -> update
            List<MailFolder> toUpdate = toPojoMailFolders(folders.stream().filter(mf -> mf.getID() > 0).toList());

            if (!toUpdate.isEmpty()) {
                int[] result = getMailService().updateFolder(accountID, toUpdate);
                affectedRows += IntStream.of(result).sum();
            }

            // ID = 0 -> insert
            List<MailFolder> toInsert = toPojoMailFolders(folders.stream().filter(mf -> mf.getID() == 0).toList());

            if (!toInsert.isEmpty()) {
                long[] primaryKeys = getMailService().insertFolder(accountID, toInsert);
                affectedRows += primaryKeys.length;

                for (int i = 0; i < primaryKeys.length; i++) {
                    toInsert.get(i).setAccountID(accountID);
                    toInsert.get(i).setID(primaryKeys[i]);
                }
            }

            return affectedRows;
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public List<FxMailFolder> loadFolder(final long accountID) {
        try {
            List<MailFolder> folders = getMailService().loadFolder(accountID);

            List<FxMailFolder> fxBeans = toFXMailFolders(folders);

            buildHierarchie(fxBeans);

            return fxBeans;
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public List<FxMail> loadMails(final FxMailAccount account, final FxMailFolder folder) {
        getLogger().info("Load Mails: account={}, folder={}", account.getMail(), folder.getFullName());

        try {
            List<Mail> mails = getMailService().loadMails(account.getID(), folder.getID(), folder.getFullName());

            List<FxMail> fxBeans = toFXMails(mails);

            getLogger().info("Load Mails finished: account={}, folder={}", account.getMail(), folder.getFullName());

            return fxBeans;
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Resource
    public void setMailService(final MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public List<FxMailFolder> test(final FxMailAccount account) {
        try {
            // MailAccount pojo = toPOJO(account);
            //
            // List<FxMailFolder> fxBeans = getMailService().test(pojo).stream().map(this::toFXBean).collect(Collectors.toList());
            MailAccount pojo = toPojoMailAccount(account);

            return toFXMailFolders(getMailService().test(pojo));
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    // @Override
    // public Future<List<FxMail>> loadMails2(final long accountID, final long folderID, final String folderFullName) throws Exception
    // {
    // Future<List<Mail>> pojoFuture = getMailService().loadMails2(accountID, folderID, folderFullName);
    //
    // List<FxMail> fxBeans = toFXMails(pojoFuture.get().stream().collect(Collectors.toList()));
    //
    // return new AsyncResult<>(fxBeans);
    // }

    @Override
    public int updateAccount(final FxMailAccount account) {
        try {
            MailAccount pojo = toPojoMailAccount(account);

            return getMailService().updateAccount(pojo);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    protected MailService getMailService() {
        return this.mailService;
    }

    @Override
    protected MailContent loadMailContent(final Path mailPath, final FxMailAccount account, final FxMail mail, final IOMonitor monitor) throws Exception {
        MailContent mailContent = getMailService().loadMailContent(account.getID(), mail.getFolderFullName(), mail.getUID(), monitor);

        saveMailContent(mailPath, mailContent);

        return mailContent;
    }

    private List<FxMailAccount> toFXMailAccounts(final List<MailAccount> accounts) throws Exception {
        // FxMailAccount ma = new FxMailAccount();
        // ma.setID(pojo.getID());
        // ma.setImapHost(pojo.getImapHost());
        // ma.setImapLegitimation(pojo.isImapLegitimation());
        // ma.setImapPort(pojo.getImapPort());
        // ma.setMail(pojo.getMail());
        // ma.setPassword(pojo.getPassword());
        // ma.setSmtpHost(pojo.getSmtpHost());
        // ma.setSmtpLegitimation(pojo.isSmtpLegitimation());
        // ma.setSmtpPort(pojo.getSmtpPort());

        JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FxMailAccount.class);

        byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(accounts);

        return getJsonMapper().readValue(jsonBytes, type);
    }

    private List<FxMailFolder> toFXMailFolders(final List<MailFolder> folders) throws Exception {
        // FxMailFolder mf = new FxMailFolder();
        // mf.setAbonniert(folder.isAbonniert());
        // mf.setAccountID(folder.getAccountID());
        // mf.setFullName(folder.getFullName());
        // mf.setID(folder.getID());
        // mf.setName(folder.getName());

        JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FxMailFolder.class);

        byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(folders);

        return getJsonMapper().readValue(jsonBytes, type);
    }

    private List<FxMail> toFXMails(final List<Mail> mails) throws Exception {
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

        // List<FxMail> fxBeans = pojos.stream().map(this::toFXBean).collect(Collectors.toList());

        JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FxMail.class);

        // byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(mails);
        String json = getJsonMapper().writer().writeValueAsString(mails);

        return getJsonMapper().readValue(json, type);
    }

    private MailAccount toPojoMailAccount(final FxMailAccount account) throws Exception {
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

    private List<MailFolder> toPojoMailFolders(final List<FxMailFolder> folders) throws Exception {
        JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, MailFolder.class);

        byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(folders);

        return getJsonMapper().readValue(jsonBytes, type);
    }
}
