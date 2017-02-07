// Created: 23.01.2017
package de.freese.pim.core.mail.api;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.model.SumUnreadMailsInChildFolderBinding;
import de.freese.pim.core.mail.service.IMailService;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Basis-Implementierung der {@link IMailAPI}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMailAPI implements IMailAPI
{
    /**
    *
    */
    private final FilteredList<MailFolder> abonnierteFolder;

    /**
     *
     */
    private final MailAccount account;

    /**
    *
    */
    private final Path basePath;

    /**
    *
    */
    private ExecutorService executor = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private IMailService mailService = null;

    /**
    *
    */
    private final FilteredList<MailFolder> rootFolder;

    /**
    *
    */
    private ObservableIntegerValue unreadMailsCount = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMailAPI}
     *
     * @param account {@link MailAccount}
     * @param basePath {@link Path}
     */
    public AbstractMailAPI(final MailAccount account, final Path basePath)
    {
        super();

        Objects.requireNonNull(account, "account required");
        Objects.requireNonNull(basePath, "basePath required");

        this.account = account;
        this.basePath = basePath;

        this.abonnierteFolder = new FilteredList<>(this.account.getFolder(), MailFolder::isAbonniert);
        this.rootFolder = new FilteredList<>(this.abonnierteFolder, MailFolder::isParent);

        // Zähler mit der Folder-List verbinden.
        this.unreadMailsCount = new SumUnreadMailsInChildFolderBinding(this.rootFolder);
    }

    /**
     * @see de.freese.pim.core.mail.api.IMailAPI#getAccount()
     */
    @Override
    public MailAccount getAccount()
    {
        return this.account;
    }

    /**
     * @see de.freese.pim.core.mail.api.IMailAPI#getBasePath()
     */
    @Override
    public Path getBasePath()
    {
        return this.basePath;
    }

    /**
     * @see de.freese.pim.core.mail.api.IMailAPI#getFolder()
     */
    @Override
    public ObservableList<MailFolder> getFolder()
    {
        return getAccount().getFolder();
    }

    /**
     * @see de.freese.pim.core.mail.api.IMailAPI#getFolderSubscribed()
     */
    @Override
    public FilteredList<MailFolder> getFolderSubscribed()
    {
        return this.abonnierteFolder;
    }

    /**
     * @see de.freese.pim.core.mail.api.IMailAPI#getUnreadMailsCount()
     */
    @Override
    public int getUnreadMailsCount()
    {
        return this.unreadMailsCount.intValue();
    }

    /**
     * @see de.freese.pim.core.mail.api.IMailAPI#setExecutorService(java.util.concurrent.ExecutorService)
     */
    @Override
    public void setExecutorService(final ExecutorService executor)
    {
        this.executor = executor;
    }

    /**
     * @see de.freese.pim.core.mail.api.IMailAPI#setMailService(de.freese.pim.core.mail.service.IMailService)
     */
    @Override
    public void setMailService(final IMailService mailService)
    {
        this.mailService = mailService;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("JavaMailAPI [").append(getAccount()).append("]");

        return builder.toString();
    }

    /**
     * Optionaler {@link ExecutorService} für die Mail-API.
     *
     * @return {@link ExecutorService}
     */
    protected ExecutorService getExecutor()
    {
        return this.executor;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert den {@link IMailService}.
     *
     * @return {@link IMailService}
     */
    protected IMailService getMailService()
    {
        return this.mailService;
    }
}
