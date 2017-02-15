/**
 * Created: 14.02.2017
 */

package de.freese.pim.gui.mail.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.scheduling.annotation.AsyncResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailAccount;
import de.freese.pim.gui.mail.model.FXMailFolder;
import de.freese.pim.server.mail.model.Mail;
import de.freese.pim.server.mail.model.MailAccount;
import de.freese.pim.server.mail.model.MailFolder;
import de.freese.pim.server.mail.service.MailService;

/**
 * MailService für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 */
// @Service
public class DefaultEmbeddedFXMailService extends AbstractFXMailService
{
    /**
     *
     */
    private MailService mailService = null;

    /**
     * Erstellt ein neues {@link DefaultEmbeddedFXMailService} Object.
     */
    public DefaultEmbeddedFXMailService()
    {
        super();
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#connectAccount(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public void connectAccount(final FXMailAccount account) throws Exception
    {
        MailAccount pojo = toPOJO(account);

        getMailService().connectAccount(pojo);
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#deleteAccount(long)
     */
    @Override
    public int deleteAccount(final long accountID) throws Exception
    {
        return getMailService().deleteAccount(accountID);
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#disconnectAccounts()
     */
    @Override
    public void disconnectAccounts() throws Exception
    {
        getMailService().disconnectAccounts();
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#getMailAccounts()
     */
    @Override
    public List<FXMailAccount> getMailAccounts() throws Exception
    {
        List<MailAccount> pojos = getMailService().getMailAccounts();

        // List<FXMailAccount> fxBeans = pojos.stream().map(this::toFXBean).collect(Collectors.toList());

        ObjectMapper jsonMapper = new ObjectMapper();
        // jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        // jsonMapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
        // jsonMapper.setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY);
        // jsonMapper.setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);

        JavaType type = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, FXMailAccount.class);

        // String jsonString = jsonMapper.writer().writeValueAsString(pojos);
        // List<FXMailAccount> fxBeans = jsonMapper.readValue(jsonString, type);

        byte[] jsonBytes = jsonMapper.writer().writeValueAsBytes(pojos);
        List<FXMailAccount> fxBeans = jsonMapper.readValue(jsonBytes, type);

        return fxBeans;
    }

    /**
     * @return {@link MailService}
     */
    protected MailService getMailService()
    {
        return this.mailService;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#insertAccount(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public void insertAccount(final FXMailAccount account) throws Exception
    {
        MailAccount pojo = toPOJO(account);

        long id = getMailService().insertAccount(pojo);
        account.setID(id);
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#insertOrUpdateFolder(long, java.util.List)
     */
    @Override
    public int insertOrUpdateFolder(final long accountID, final List<FXMailFolder> folders) throws Exception
    {
        int affectedRows = 0;

        // ID = 0 -> insert
        List<MailFolder> toInsert = folders.stream().filter(mf -> mf.getID() == 0).map(this::toPOJO).collect(Collectors.toList());

        long[] primaryKeys = getMailService().insertFolder(accountID, toInsert);
        affectedRows += primaryKeys.length;

        for (int i = 0; i < primaryKeys.length; i++)
        {
            toInsert.get(i).setID(primaryKeys[i]);
        }

        // ID != 0 -> update
        List<MailFolder> toUpdate = folders.stream().filter(mf -> mf.getID() > 0).map(this::toPOJO).collect(Collectors.toList());
        affectedRows += IntStream.of(getMailService().updateFolder(accountID, toUpdate)).sum();

        return affectedRows;
    }

    /**
     * @see de.freese.pim.gui.mail.service.AbstractFXMailService#loadContent(long, java.lang.String, long, int, java.util.function.BiConsumer)
     */
    @Override
    protected byte[] loadContent(final long accountID, final String folderFullName, final long mailUID, final int size,
                                 final BiConsumer<Long, Long> loadMonitor)
        throws Exception
    {
        byte[] rawData = getMailService().loadContent(accountID, folderFullName, mailUID, size, loadMonitor);

        return rawData;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadFolder(long)
     */
    @Override
    public List<FXMailFolder> loadFolder(final long accountID) throws Exception
    {
        List<MailFolder> pojos = getMailService().loadFolder(accountID);

        List<FXMailFolder> fxBeans = pojos.stream().map(this::toFXBean).collect(Collectors.toList());

        buildHierarchie(fxBeans);

        return fxBeans;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadMails(long, long, java.lang.String)
     */
    @Override
    public List<FXMail> loadMails(final long accountID, final long folderID, final String folderFullName) throws Exception
    {
        List<Mail> pojos = getMailService().loadMails(accountID, folderID, folderFullName);

        List<FXMail> fxBeans = pojos.stream().map(this::toFXBean).collect(Collectors.toList());

        return fxBeans;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadMails2(long, long, java.lang.String)
     */
    @Override
    public Future<List<FXMail>> loadMails2(final long accountID, final long folderID, final String folderFullName) throws Exception
    {
        Future<List<Mail>> pojoFuture = getMailService().loadMails2(accountID, folderID, folderFullName);

        List<FXMail> fxBeans = pojoFuture.get().stream().map(this::toFXBean).collect(Collectors.toList());

        return new AsyncResult<>(fxBeans);
    }

    /**
     * @param mailService {@link MailService}
     */
    public void setMailService(final MailService mailService)
    {
        this.mailService = mailService;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#test(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public List<FXMailFolder> test(final FXMailAccount account) throws Exception
    {
        MailAccount pojo = toPOJO(account);

        List<FXMailFolder> folder = getMailService().test(pojo).stream().map(this::toFXBean).collect(Collectors.toList());

        return folder;
    }

    /**
     * Konvertiert das POJO in die FX-Bean.
     *
     * @param mail {@link Mail}
     * @return {@link FXMail}
     */
    private FXMail toFXBean(final Mail mail)
    {
        FXMail m = new FXMail();
        m.setBcc(mail.getBcc());
        m.setCc(mail.getCc());
        m.setFolderFullName(mail.getFolderFullName());
        m.setFolderID(mail.getFolderID());
        m.setFrom(mail.getFrom());
        m.setMsgNum(mail.getMsgNum());
        m.setReceivedDate(mail.getReceivedDate());
        m.setSeen(mail.isSeen());
        m.setSendDate(mail.getSendDate());
        m.setSize(mail.getSize());
        m.setSubject(mail.getSubject());
        m.setTo(mail.getTo());
        m.setUID(mail.getUID());

        return m;
    }

    /**
     * Konvertiert das POJO in die FX-Bean.
     *
     * @param account {@link MailAccount}
     * @return {@link FXMailAccount}
     */
    private FXMailAccount toFXBean(final MailAccount account)
    {
        FXMailAccount ma = new FXMailAccount();
        ma.setID(account.getID());
        ma.setImapHost(account.getImapHost());
        ma.setImapLegitimation(account.isImapLegitimation());
        ma.setImapPort(account.getImapPort());
        ma.setMail(account.getMail());
        ma.setPassword(account.getPassword());
        ma.setSmtpHost(account.getSmtpHost());
        ma.setSmtpLegitimation(account.isSmtpLegitimation());
        ma.setSmtpPort(account.getSmtpPort());

        return ma;
    }

    /**
     * Konvertiert das POJO in die FX-Bean.
     *
     * @param folder {@link MailFolder}
     * @return {@link FXMailFolder}
     */
    private FXMailFolder toFXBean(final MailFolder folder)
    {
        FXMailFolder mf = new FXMailFolder();
        mf.setAbonniert(folder.isAbonniert());
        mf.setAccountID(folder.getAccountID());
        mf.setFullName(folder.getFullName());
        mf.setID(folder.getID());
        mf.setName(folder.getName());

        return mf;
    }

    /**
     * Konvertiert die FX-Bean in das POJO.
     *
     * @param mail {@link FXMail}
     * @return {@link Mail}
     */
    private Mail toPOJO(final FXMail mail)
    {
        Mail m = new Mail();
        m.setBcc(mail.getBcc());
        m.setCc(mail.getCc());
        m.setFolderFullName(mail.getFolderFullName());
        m.setFolderID(mail.getFolderID());
        m.setFrom(mail.getFrom());
        m.setMsgNum(mail.getMsgNum());
        m.setReceivedDate(mail.getReceivedDate());
        m.setSeen(mail.isSeen());
        m.setSendDate(mail.getSendDate());
        m.setSize(mail.getSize());
        m.setSubject(mail.getSubject());
        m.setTo(mail.getTo());
        m.setUID(mail.getUID());

        return m;
    }

    /**
     * Konvertiert die FX-Bean in das POJO.
     *
     * @param account {@link FXMailAccount}
     * @return {@link MailAccount}
     */
    private MailAccount toPOJO(final FXMailAccount account)
    {
        MailAccount ma = new MailAccount();
        ma.setID(account.getID());
        ma.setImapHost(account.getImapHost());
        ma.setImapLegitimation(account.isImapLegitimation());
        ma.setImapPort(account.getImapPort());
        ma.setMail(account.getMail());
        ma.setPassword(account.getPassword());
        ma.setSmtpHost(account.getSmtpHost());
        ma.setSmtpLegitimation(account.isSmtpLegitimation());
        ma.setSmtpPort(account.getSmtpPort());

        return ma;
    }

    /**
     * Konvertiert die FX-Bean in das POJO.
     *
     * @param folder {@link FXMailFolder}
     * @return {@link MailFolder}
     */
    private MailFolder toPOJO(final FXMailFolder folder)
    {
        MailFolder mf = new MailFolder();
        mf.setAbonniert(folder.isAbonniert());
        mf.setAccountID(folder.getAccountID());
        mf.setFullName(folder.getFullName());
        mf.setID(folder.getID());
        mf.setName(folder.getName());

        return mf;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#updateAccount(de.freese.pim.gui.mail.model.FXMailAccount)
     */
    @Override
    public int updateAccount(final FXMailAccount account) throws Exception
    {
        MailAccount pojo = toPOJO(account);

        return getMailService().updateAccount(pojo);
    }
}
