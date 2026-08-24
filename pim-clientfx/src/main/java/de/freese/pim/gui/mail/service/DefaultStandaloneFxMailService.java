// Created: 14.02.2017
package de.freese.pim.gui.mail.service;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import jakarta.annotation.Resource;

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
            final MailAccount pojo = account.toPojo();

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
            final List<MailAccount> accounts = getMailService().getMailAccounts();

            return accounts.stream().map(FxMailAccount::from).toList();
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public void insertAccount(final FxMailAccount account) {
        try {
            final MailAccount pojo = account.toPojo();

            final long id = getMailService().insertAccount(pojo);
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
            final List<MailFolder> toUpdate = folders.stream().filter(mf -> mf.getID() > 0).map(FxMailFolder::toPojo).toList();

            if (!toUpdate.isEmpty()) {
                final int[] result = getMailService().updateFolder(accountID, toUpdate);
                affectedRows += IntStream.of(result).sum();
            }

            // ID = 0 -> insert
            final List<MailFolder> toInsert = folders.stream().filter(mf -> mf.getID() == 0).map(FxMailFolder::toPojo).toList();

            if (!toInsert.isEmpty()) {
                final long[] primaryKeys = getMailService().insertFolder(accountID, toInsert);
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
            final List<MailFolder> folders = getMailService().loadFolder(accountID);

            final List<FxMailFolder> fxBeans = folders.stream().map(FxMailFolder::from).toList();

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
            final List<Mail> mails = getMailService().loadMails(account.getID(), folder.getID(), folder.getFullName());

            final List<FxMail> fxBeans = mails.stream().map(FxMail::from).toList();

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
            final MailAccount pojo = account.toPojo();

            return getMailService().test(pojo).stream().map(FxMailFolder::from).toList();
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    // @Override
    // public Future<List<FxMail>> loadMails2(final long accountID, final long folderID, final String folderFullName) throws Exception {
    // Future<List<Mail>> pojoFuture = getMailService().loadMails2(accountID, folderID, folderFullName);
    //
    // List<FxMail> fxBeans = pojoFuture.get().stream().map(FxMailFolder::from).toList();
    //
    // return new AsyncResult<>(fxBeans);
    // }

    @Override
    public int updateAccount(final FxMailAccount account) {
        try {
            final MailAccount pojo = account.toPojo();

            return getMailService().updateAccount(pojo);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    protected MailService getMailService() {
        return mailService;
    }

    @Override
    protected MailContent loadMailContent(final Path mailPath, final FxMailAccount account, final FxMail mail, final IOMonitor monitor) {
        final MailContent mailContent = getMailService().loadMailContent(account.getID(), mail.getFolderFullName(), mail.getUID(), monitor);

        saveMailContent(mailPath, mailContent);

        return mailContent;
    }

    // private List<FxMailAccount> toFXMailAccounts(final List<MailAccount> accounts) throws Exception {
    //     final JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FxMailAccount.class);
    //
    //     final byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(accounts);
    //
    //     return getJsonMapper().readValue(jsonBytes, type);
    // }

    // private List<FxMailFolder> toFXMailFolders(final List<MailFolder> folders) throws Exception {
    //     // FxMailFolder mf = new FxMailFolder();
    //     // mf.setAbonniert(folder.isAbonniert());
    //     // mf.setAccountID(folder.getAccountID());
    //     // mf.setFullName(folder.getFullName());
    //     // mf.setID(folder.getID());
    //     // mf.setName(folder.getName());
    //
    //     final JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FxMailFolder.class);
    //
    //     final byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(folders);
    //
    //     return getJsonMapper().readValue(jsonBytes, type);
    // }

    // private List<FxMail> toFXMails(final List<Mail> mails) throws Exception {
    //     // List<FxMail> fxBeans = pojos.stream().map(this::toFXBean).collect(Collectors.toList());
    //
    //     final JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FxMail.class);
    //
    //     // byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(mails);
    //     final String json = getJsonMapper().writer().writeValueAsString(mails);
    //
    //     return getJsonMapper().readValue(json, type);
    // }

    // private MailAccount toPojoMailAccount(final FxMailAccount account) throws Exception {
    //     final byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(account);
    //
    //     return getJsonMapper().readValue(jsonBytes, MailAccount.class);
    // }

    // private List<MailFolder> toPojoMailFolders(final List<FxMailFolder> folders) throws Exception {
    //     final JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, MailFolder.class);
    //
    //     final byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(folders);
    //
    //     return getJsonMapper().readValue(jsonBytes, type);
    // }
}
