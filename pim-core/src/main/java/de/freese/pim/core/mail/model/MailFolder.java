// Created: 09.01.2017
package de.freese.pim.core.mail.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import de.freese.pim.core.mail.service.IMailAPI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private final BooleanProperty abonniertProperty = new SimpleBooleanProperty(this, "abonniert", true);

    /**
     *
     */
    private final List<MailFolder> childs = new ArrayList<>();

    /**
    *
    */
    private final StringProperty fullNameProperty = new SimpleStringProperty(this, "fullName", null);

    /**
    *
    */
    private long id = 0;

    /**
     *
     */
    private boolean isSendFolder = false;

    /**
     *
     */
    private IMailAPI mailAPI = null;

    /**
      *
      */
    private final ObservableList<Mail> mails = FXCollections.observableArrayList();

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
    private MailFolder parent = null;

    /**
     *
     */
    private int unreadMailsCount = 0;

    /**
     * Erzeugt eine neue Instanz von {@link MailFolder}
     */
    public MailFolder()
    {
        super();
    }

    /**
     * @return {@link BooleanProperty}
     */
    public BooleanProperty abonniertProperty()
    {
        return this.abonniertProperty;
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty fullNameProperty()
    {
        return this.fullNameProperty;
    }

    /**
     * Liefert den vollen Hierarchie-Namen, zB PARENT_NAME/FOLDER_NAME.
     *
     * @return String
     */
    public String getFullName()
    {
        return fullNameProperty().get();
    }

    /**
     * @return long
     */
    public long getID()
    {
        return this.id;
    }

    /**
     * @return {@link IMailAPI}
     */
    public IMailAPI getMailAPI()
    {
        return this.mailAPI;
    }

    /**
     * @return {@link ObservableList}
     */
    public ObservableList<Mail> getMails()
    {
        return this.mails;
    }

    /**
     * Wird nur für die Tabelle verwendet.
     *
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
     * Liefert den lokalen Temp-{@link Path} des Folders.
     *
     * @return {@link Path}
     */
    public Path getPath()
    {
        // Path basePath = Optional.ofNullable(getParent()).map(p -> p.getPath()).orElse(getMailAPI().getBasePath());
        Path basePath = getMailAPI().getBasePath();
        Path path = basePath.resolve(getName());
        // Path path = basePath.resolve(getFullName().replaceAll("/", "__"));

        return path;
    }

    /**
     * Liefert die Anzahl ungelesener Mails, inklusive der Child-Folder.
     *
     * @return int
     */
    public int getUnreadMailsCount()
    {
        return this.unreadMailsCount;
        // int sum = getChilds().stream().mapToInt(MailFolder::getUnreadMailsCount).sum();
        // sum += this.unreadMailsCount;
        //
        // return sum;
    }

    /**
     * Liefert das Flag um den Folder zu abonnieren/beobachten.
     *
     * @return boolean
     */
    public boolean isAbonniert()
    {
        return abonniertProperty().get();
    }

    /**
     * Liefert true, wenn dieser Folder die gesendeten Mails enthält.
     *
     * @return boolean
     */
    public boolean isSendFolder()
    {
        return this.isSendFolder;
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty nameProperty()
    {
        return this.nameProperty;
    }

    /**
     * Setzt das Flag um den Folder zu abonnieren/beobachten.
     *
     * @param abo boolean
     */
    public void setAbonniert(final boolean abo)
    {
        abonniertProperty().set(abo);
    }

    /**
     * Setzt den vollen Hierarchie-Namen, zB PARENT_NAME/FOLDER_NAME.
     *
     * @param fullName String
     */
    public void setFullName(final String fullName)
    {
        fullNameProperty().set(fullName);
    }

    /**
     * @param id long
     */
    public void setID(final long id)
    {
        this.id = id;
    }

    /**
     * @param mailAPI {@link IMailAPI}
     */
    public void setMailAPI(final IMailAPI mailAPI)
    {
        this.mailAPI = mailAPI;
    }

    /**
     * Setzt den Namen des Folders.
     *
     * @param name String
     */
    public void setName(final String name)
    {
        // Objects.requireNonNull(name, "name required");

        nameProperty().set(name);

        Predicate<String> predicate = n -> "send".equals(n);
        predicate = predicate.or(n -> "sent".equals(n));
        predicate = predicate.or(n -> n.startsWith("gesendet"));

        this.isSendFolder = predicate.test(name.toLowerCase());

        if (this.isSendFolder)
        {
            this.mailsSorted.setComparator(Comparator.comparing(Mail::getSendDate).reversed());
        }
        else
        {
            this.mailsSorted.setComparator(Comparator.comparing(Mail::getReceivedDate).reversed());
        }
    }

    /**
     * Setzt den Parent.
     *
     * @param parent {@link MailFolder}
     */
    public void setParent(final MailFolder parent)
    {
        this.parent = parent;
        this.parent.getChilds().add(this);
    }

    /**
     * Setzt die Anzahl ungelesener Mails.
     *
     * @param unreadMailsCount int
     */
    public void setUnreadMailsCount(final int unreadMailsCount)
    {
        this.unreadMailsCount = unreadMailsCount;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MailFolder [fullName=");
        builder.append(getFullName());
        builder.append("]");

        return builder.toString();
    }

    /**
     * Aktualisiert den Zähler der ungelesenen Mails, inklusive der Child-Folder.
     */
    public void updateUnreadMailsCount()
    {
        if (!getMails().isEmpty())
        {
            this.unreadMailsCount = getMails().parallelStream().mapToInt(m -> m.isSeen() ? 0 : 1).sum();
        }

        if (!getChilds().isEmpty())
        {
            this.unreadMailsCount += getChilds().stream().mapToInt(MailFolder::getUnreadMailsCount).sum();
        }
    }

    /**
     * Liefert den Parent.
     *
     * @return {@link MailFolder}
     */
    private MailFolder getParent()
    {
        return this.parent;
    }

    /**
     * @return {@link List}<MailFolder>
     */
    List<MailFolder> getChilds()
    {
        return this.childs;
    }
}
