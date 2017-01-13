// Created: 09.01.2017
package de.freese.pim.core.mail.model_new;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 * Entity für einen Mail-Folder.
 *
 * @author Thomas Freese
 */
public class MailFolder
{
    /**
     *
     */
    private final MailAccount account;

    // /**
    // *
    // */
    // private String fullName = null;

    /**
     *
     */
    private final SortedList<Mail> mails = new SortedList<>(FXCollections.observableArrayList());

    /**
    *
    */
    private final StringProperty nameProperty = new SimpleStringProperty(this, "name", null);

    /**
    *
    */
    private final MailFolder parent;

    /**
     * Erzeugt eine neue Instanz von {@link MailFolder}
     *
     * @param account {@link MailAccount}
     * @param name String
     */
    public MailFolder(final MailAccount account, final String name)
    {
        this(account, name, null);
    }

    /**
     * Erzeugt eine neue Instanz von {@link MailFolder}
     *
     * @param account {@link MailAccount}
     * @param name String
     * @param parent {@link MailFolder}
     */
    public MailFolder(final MailAccount account, final String name, final MailFolder parent)
    {
        super();

        Objects.requireNonNull(account, "account required");
        Objects.requireNonNull(name, "name required");

        this.account = account;
        setName(name);
        this.parent = parent;
    }

    /**
     * Liefert den vollen Hierarchie-Namen, zB PARENT_NAME/FOLDER_NAME.
     *
     * @return String
     */
    public String getFullName()
    {
        String fullName = Optional.ofNullable(getParent()).map(p -> p.getName() + "/" + getName()).orElse(getName());

        return fullName;
    }

    /**
     * @return {@link ObservableList}
     */
    public ObservableList<Mail> getMails()
    {
        return this.mails;
    }

    /**
     * Liefert den Namen des Folders.
     *
     * @return String
     */
    public String getName()
    {
        return nameProperty().get();
    }

    /**
     * Liefert den lokalen Temp-{@link Path} des Folders.
     *
     * @return {@link Path}
     */
    public Path getPath()
    {
        Path basePath = Optional.ofNullable(getParent()).map(p -> p.getPath()).orElse(getAccount().getPath());
        Path path = basePath.resolve(getName());
        // Path basePath = getAccount().getPath();
        // Path path = basePath.resolve(getFullName().replaceAll("/", "__"));

        return path;
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty nameProperty()
    {
        return this.nameProperty;
    }

    /**
     * Liefert den {@link MailAccount}.
     *
     * @return {@link MailAccount}
     */
    private MailAccount getAccount()
    {
        return this.account;
    }

    // /**
    // * Setzt den vollen Hierarchie-Namen, zB PARENT_NAME/FOLDER_NAME.
    // *
    // * @param fullName String
    // */
    // public void setFullName(final String fullName)
    // {
    // this.fullName = fullName;
    // }

    /**
     * @return {@link MailFolder}
     */
    private MailFolder getParent()
    {
        return this.parent;
    }

    /**
     * Setzt den Namen des Folders.
     *
     * @param name String
     */
    private void setName(final String name)
    {
        // Objects.requireNonNull(name, "name required");

        nameProperty().set(name);

        if ("SEND".equals(name.toUpperCase()) || "SENT".equals(name.toUpperCase()) || name.toUpperCase().startsWith("GESENDETE"))
        {
            this.mails.setComparator(Comparator.comparing(Mail::getSendDate));
        }
        else
        {
            this.mails.setComparator(Comparator.comparing(Mail::getReceivedDate));
        }
    }
}
