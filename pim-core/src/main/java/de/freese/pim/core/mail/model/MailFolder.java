// Created: 09.01.2017
package de.freese.pim.core.mail.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import de.freese.pim.core.mail.service.IMailService;
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
    private final List<MailFolder> childs = new ArrayList<>();

    // /**
    // *
    // */
    // private String fullName = null;

    /**
      *
      */
    private final ObservableList<Mail> mails = FXCollections.observableArrayList();

    /**
    *
    */
    private final IMailService mailService;

    /**
     *
     */
    private final SortedList<Mail> mailsSorted = new SortedList<>(this.mails);

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
     * @param mailService {@link IMailService}
     * @param name String
     */
    public MailFolder(final IMailService mailService, final String name)
    {
        this(mailService, name, null);
    }

    /**
     * Erzeugt eine neue Instanz von {@link MailFolder}
     *
     * @param mailService {@link MailAccount}
     * @param name String
     * @param parent {@link MailFolder}
     */
    public MailFolder(final IMailService mailService, final String name, final MailFolder parent)
    {
        super();

        Objects.requireNonNull(mailService, "mailService required");
        Objects.requireNonNull(name, "name required");

        this.mailService = mailService;
        setName(name);
        this.parent = parent;
    }

    /**
     * @return {@link List}<MailFolder>
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> getChilds() throws Exception
    {
        return this.childs;
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
     * Liefert den {@link IMailService}.
     *
     * @return {@link IMailService}
     */
    public IMailService getMailService()
    {
        return this.mailService;
    }

    /**
     * @return {@link SortedList}
     */
    public SortedList<Mail> getMailsSorted()
    {
        return this.mailsSorted;
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
     * @return {@link MailFolder}
     */
    private MailFolder getParent()
    {
        return this.parent;
    }

    /**
     * Liefert den lokalen Temp-{@link Path} des Folders.
     *
     * @return {@link Path}
     */
    public Path getPath()
    {
        Path basePath = Optional.ofNullable(getParent()).map(p -> p.getPath()).orElse(getMailService().getBasePath());
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
            this.mailsSorted.setComparator(Comparator.comparing(Mail::getSendDate).reversed());
        }
        else
        {
            this.mailsSorted.setComparator(Comparator.comparing(Mail::getReceivedDate).reversed());
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MailFolder [name=");
        builder.append(getName());
        builder.append("]");

        return builder.toString();
    }
}
