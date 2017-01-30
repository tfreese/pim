// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.service.IMailAPI;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Initialisieerung der MailAPI pro {@link MailAccount}.
 *
 * @author Thomas Freese
 */
public class InitMailAPITask extends Task<Void>
{
    /**
    *
    */
    public static final Logger LOGGER = LoggerFactory.getLogger(InitMailAPITask.class);

    /**
     *
     */
    private final IMailAPI mailAPI;

    /**
     *
     */
    private final TreeItem<Object> parent;

    /**
     * Erzeugt eine neue Instanz von {@link InitMailAPITask}
     *
     * @param treeView {@link TreeView}
     * @param parent {@link TreeItem}
     * @param mailAPI {@link IMailAPI}
     */
    public InitMailAPITask(final TreeView<Object> treeView, final TreeItem<Object> parent, final IMailAPI mailAPI)
    {
        super();

        Objects.requireNonNull(treeView, "treeView required");
        Objects.requireNonNull(parent, "parent required");
        Objects.requireNonNull(mailAPI, "mailAPI required");

        this.parent = parent;
        this.mailAPI = mailAPI;

        setOnSucceeded(event -> treeView.refresh());
        setOnFailed(event -> {
            Throwable th = getException();

            LOGGER.error(null, th);

            new ErrorDialog().forThrowable(th).showAndWait();
        });
    }

    /**
     * @see javafx.concurrent.Task#call()
     */
    @Override
    protected Void call() throws Exception
    {
        LOGGER.info("Init MailAccount {}", this.mailAPI.getAccount().getMail());

        this.mailAPI.connect();

        PIMApplication.registerCloseable(() -> {
            PIMApplication.LOGGER.info("Close " + this.mailAPI.getAccount().getMail());
            this.mailAPI.disconnect();
        });

        // Tree aufbauen.
        this.mailAPI.getFolderSubscribed().addListener(new TreeFolderListChangeListener(this.parent));

        this.mailAPI.loadFolder(mf -> Platform.runLater(() -> {
            this.mailAPI.getFolder().add(mf);
            mf.bindUnreadMailsChildFolder();
        }));

        LOGGER.info("Initialisation of {} finished", this.mailAPI.getAccount().getMail());

        return null;
    }
}
