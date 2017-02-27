// Created: 09.01.2017
package de.freese.pim.gui.mail.model;

import java.util.Comparator;
import java.util.function.Predicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 * FX-Bean für einen Mail-Folder.
 *
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FXMailFolder
{
    /**
     *
     */
    private final BooleanProperty abonniertProperty = new SimpleBooleanProperty(this, "abonniert", true);

    /**
    *
    */
    private LongProperty accountIDProperty = new SimpleLongProperty(this, "accountID", 0L);

    /**
     *
     */
    @JsonIgnore
    private final ObservableList<FXMailFolder> childs = FXCollections.observableArrayList();

    /**
    *
    */
    private final StringProperty fullNameProperty = new SimpleStringProperty(this, "fullName", null);

    /**
    *
    */
    private LongProperty idProperty = new SimpleLongProperty(this, "id", 0L);

    /**
     *
     */
    private BooleanProperty isSendFolderProperty = new SimpleBooleanProperty(this, "isSendFolder", false);

    /**
      *
      */
    @JsonIgnore
    private final ObservableList<FXMail> mails = FXCollections.observableArrayList();

    /**
     *
     */
    @JsonIgnore
    private final SortedList<FXMail> mailsSorted = new SortedList<>(this.mails);

    /**
    *
    */
    private final StringProperty nameProperty = new SimpleStringProperty(this, "name", null);

    /**
    *
    */
    @JsonIgnore
    private FXMailFolder parent = null;

    /**
    *
    */
    @JsonIgnore
    private IntegerBinding unreadMailsCount = null;

    /**
    *
    */
    @JsonIgnore
    private IntegerBinding unreadMailsCountTotal = null;

    /**
     * Erzeugt eine neue Instanz von {@link FXMailFolder}
     */
    public FXMailFolder()
    {
        super();

        this.unreadMailsCount = new SumUnreadMailsBinding(this.mails);
        IntegerBinding unreadMailsCountChildFolder = new SumUnreadMailsInChildFolderBinding(this.childs);

        this.unreadMailsCountTotal = (IntegerBinding) this.unreadMailsCount.add(unreadMailsCountChildFolder);
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
     * @return {@link LongProperty}
     */
    public LongProperty accountIDProperty()
    {
        return this.accountIDProperty;
    }

    /**
     * Hinzufügen eines Child-Folders.
     *
     * @param child {@link FXMailFolder}
     */
    public void addChild(final FXMailFolder child)
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
     * @return long
     */
    public long getAccountID()
    {
        return accountIDProperty().get();
    }

    /**
     * @return {@link ObservableList}<MailFolder>
     */
    ObservableList<FXMailFolder> getChilds()
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
        return fullNameProperty().get();
    }

    /**
     * @return long
     */
    public long getID()
    {
        return idProperty().get();
    }

    /**
     * @return {@link ObservableList}
     */
    public ObservableList<FXMail> getMails()
    {
        return this.mails;
    }

    /**
     * Wird nur für die Tabelle verwendet.
     *
     * @return {@link SortedList}
     */
    public SortedList<FXMail> getMailsSorted()
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
     * Liefert den Parent.
     *
     * @return {@link FXMailFolder}
     */
    FXMailFolder getParent()
    {
        return this.parent;
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
     * @return {@link LongProperty}
     */
    public LongProperty idProperty()
    {
        return this.idProperty;
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
        return isSendFolderProperty().get();
    }

    /**
     * @return {@link BooleanProperty}
     */
    public BooleanProperty isSendFolderProperty()
    {
        return this.isSendFolderProperty;
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
     * @param child {@link FXMailFolder}
     */
    public void removeChild(final FXMailFolder child)
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
     * @param accountID long
     */
    public void setAccountID(final long accountID)
    {
        accountIDProperty().set(accountID);
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
        idProperty().set(id);
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

        isSendFolderProperty().set(predicate.test(name.toLowerCase()));

        if (isSendFolder())
        {
            this.mailsSorted.setComparator(Comparator.comparing(FXMail::getSendDate).reversed());
        }
        else
        {
            this.mailsSorted.setComparator(Comparator.comparing(FXMail::getReceivedDate).reversed());
        }
    }

    /**
     * Setzt den Parent.
     *
     * @param parent {@link FXMailFolder}
     */
    void setParent(final FXMailFolder parent)
    {
        this.parent = parent;
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
     * @return {@link ObservableIntegerValue}
     */
    ObservableIntegerValue unreadMailsCountTotalBinding()
    {
        return this.unreadMailsCountTotal;
    }
}
