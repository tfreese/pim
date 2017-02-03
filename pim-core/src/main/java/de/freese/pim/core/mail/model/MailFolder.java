// Created: 09.01.2017
package de.freese.pim.core.mail.model;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Predicate;

import de.freese.pim.core.mail.service.IMailAPI;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableIntegerValue;
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
    private final ObservableList<MailFolder> childs = FXCollections.observableArrayList();

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
    private IntegerBinding unreadMailsCount = null;

    /**
    *
    */
    private ObservableIntegerValue unreadMailsCountTotal = null;

    /**
     * Erzeugt eine neue Instanz von {@link MailFolder}
     */
    public MailFolder()
    {
        super();

        this.unreadMailsCount = new SumUnreadMailsBinding(this.mails);
        IntegerBinding unreadMailsCountChildFolder = new SumUnreadMailsInChildFolderBinding(this.childs);

        this.unreadMailsCountTotal = (ObservableIntegerValue) this.unreadMailsCount.add(unreadMailsCountChildFolder);
        // this.unreadMailsCountTotal = (ObservableIntegerValue) Bindings.add(this.unreadMailsCount, unreadMailsCountChildFolder);
        // ((IntegerBinding) this.unreadMailsCountTotal).invalidate();

        // this.mails.addListener((final ListChangeListener.Change<? extends Mail> change) -> this.unreadMailsCountTmp = 0);
    }

    /**
     * @return {@link BooleanProperty}
     */
    public BooleanProperty abonniertProperty()
    {
        return this.abonniertProperty;
    }

    /**
     * Hinzufügen eines Child-Folders.
     *
     * @param child {@link MailFolder}
     */
    public void addChild(final MailFolder child)
    {
        getChilds().add(child);
        child.setParent(this);
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
     * Liefert die Anzahl ungelesener Mails des Folders.
     *
     * @return int
     */
    public int getUnreadMailsCount()
    {
        return this.unreadMailsCount.get();
    }

    /**
     * Liefert die Anzahl ungelesener Mails des Folders, inklusive der Child-Folder.
     *
     * @return int
     */
    public int getUnreadMailsCountTotal()
    {
        return unreadMailsCountTotalBinding().intValue();
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
     * Liefert das Flag um den Folder zu abonnieren/beobachten.
     *
     * @return boolean
     */
    public boolean isParent()
    {
        return getParent() == null;
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
     * Entfernen eines Child-Folders.
     *
     * @param child {@link MailFolder}
     */
    public void removeChild(final MailFolder child)
    {
        getChilds().remove(child);
        child.setParent(null);
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
     * @return {@link ObservableList}<MailFolder>
     */
    ObservableList<MailFolder> getChilds()
    {
        return this.childs;
    }

    /**
     * Liefert den Parent.
     *
     * @return {@link MailFolder}
     */
    MailFolder getParent()
    {
        return this.parent;
    }

    /**
     * Setzt den Parent.
     *
     * @param parent {@link MailFolder}
     */
    void setParent(final MailFolder parent)
    {
        this.parent = parent;
    }

    /**
     * @return {@link ObservableIntegerValue}
     */
    ObservableIntegerValue unreadMailsCountTotalBinding()
    {
        return this.unreadMailsCountTotal;
    }
}
