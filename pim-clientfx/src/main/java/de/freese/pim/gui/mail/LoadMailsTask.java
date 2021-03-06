// Created: 25.01.2017
package de.freese.pim.gui.mail;

import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailAccount;
import de.freese.pim.gui.mail.model.FXMailFolder;
import de.freese.pim.gui.mail.service.FXMailService;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Laden der Mails pro MailFolder.
 *
 * @author Thomas Freese
 */
public class LoadMailsTask extends Task<Void> implements Callable<Void>
{
    /**
     *
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(LoadMailsTask.class);

    /**
     *
     */
    private final FXMailAccount account;

    /**
     *
     */
    private final List<FXMailFolder> folders;

    /**
     *
     */
    private final FXMailService mailService;

    /**
     * Erzeugt eine neue Instanz von {@link LoadMailsTask}
     *
     * @param treeView    {@link TreeView}
     * @param folders     {@link List}
     * @param mailService {@link FXMailService}
     * @param account     {@link FXMailAccount}
     */
    public LoadMailsTask(final TreeView<Object> treeView, final List<FXMailFolder> folders, final FXMailService mailService, final FXMailAccount account)
    {
        super();

        Objects.requireNonNull(treeView, "treeView required");

        this.folders = Objects.requireNonNull(folders, "mailFolder required");
        this.mailService = Objects.requireNonNull(mailService, "mailService required");
        this.account = Objects.requireNonNull(account, "account required");

        setOnSucceeded(event -> treeView.refresh());
        setOnFailed(event ->
        {
            Throwable th = getException();

            LOGGER.error(null, th);

            new ErrorDialog().forThrowable(th).showAndWait();
        });
    }

    /**
     * @see javafx.concurrent.Task#call()
     * @see Callable
     */
    @Override
    public Void call() throws Exception
    {
        for (FXMailFolder mf : this.folders)
        {
            // Thread.sleep(1000);

            List<FXMail> mails = this.mailService.loadMails(this.account, mf);

            Runnable task = () ->
            {

                if (mails != null)
                {
                    mf.getMails().addAll(mails);
                }
            };

            Platform.runLater(task);
        }

        return null;
    }
}
