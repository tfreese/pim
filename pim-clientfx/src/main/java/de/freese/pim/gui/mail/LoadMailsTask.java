// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import de.freese.pim.gui.mail.model.FxMail;
import de.freese.pim.gui.mail.model.FxMailAccount;
import de.freese.pim.gui.mail.model.FxMailFolder;
import de.freese.pim.gui.mail.service.FxMailService;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Laden der Mails pro MailFolder.
 *
 * @author Thomas Freese
 */
public class LoadMailsTask extends Task<Void> implements Callable<Void>
{
    public static final Logger LOGGER = LoggerFactory.getLogger(LoadMailsTask.class);

    private final FxMailAccount account;

    private final List<FxMailFolder> folders;

    private final FxMailService mailService;

    public LoadMailsTask(final TreeView<Object> treeView, final List<FxMailFolder> folders, final FxMailService mailService, final FxMailAccount account)
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

            LOGGER.error(th.getMessage(), th);

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
        for (FxMailFolder mf : this.folders)
        {
            List<FxMail> mails = this.mailService.loadMails(this.account, mf);

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
