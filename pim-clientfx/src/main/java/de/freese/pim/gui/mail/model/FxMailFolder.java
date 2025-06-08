// Created: 09.01.2017
package de.freese.pim.gui.mail.model;

import java.util.Comparator;
import java.util.function.Predicate;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * FX-Bean für einen Mail-Folder.
 *
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FxMailFolder {
    private final BooleanProperty abonniertProperty = new SimpleBooleanProperty(this, "abonniert", true);
    private final LongProperty accountIDProperty = new SimpleLongProperty(this, "accountID", 0L);
    @JsonIgnore
    private final ObservableList<FxMailFolder> children = FXCollections.observableArrayList();
    private final StringProperty fullNameProperty = new SimpleStringProperty(this, "fullName", null);
    private final LongProperty idProperty = new SimpleLongProperty(this, "id", 0L);
    private final BooleanProperty isSendFolderProperty = new SimpleBooleanProperty(this, "isSendFolder", false);
    @JsonIgnore
    private final ObservableList<FxMail> mails = FXCollections.observableArrayList();
    @JsonIgnore
    private final SortedList<FxMail> mailsSorted = new SortedList<>(mails);
    private final StringProperty nameProperty = new SimpleStringProperty(this, "name", null);
    @JsonIgnore
    private final IntegerBinding unreadMailsCount;
    @JsonIgnore
    private final IntegerBinding unreadMailsCountTotal;
    @JsonIgnore
    private FxMailFolder parent;

    public FxMailFolder() {
        super();

        unreadMailsCount = new SumUnreadMailsBinding(mails);
        final IntegerBinding unreadMailsCountChildFolder = new SumUnreadMailsInChildFolderBinding(children);

        unreadMailsCountTotal = (IntegerBinding) unreadMailsCount.add(unreadMailsCountChildFolder);
        // unreadMailsCountTotal = (ObservableIntegerValue) Bindings.add(unreadMailsCount, unreadMailsCountChildFolder);
        // ((IntegerBinding) unreadMailsCountTotal).invalidate();

        // mails.addListener((final ListChangeListener.Change<? extends Mail> change) -> unreadMailsCountTmp = 0);
    }

    public BooleanProperty abonniertProperty() {
        return abonniertProperty;
    }

    public LongProperty accountIDProperty() {
        return accountIDProperty;
    }

    public void addChild(final FxMailFolder child) {
        getChildren().add(child);
        child.setParent(this);
    }

    public StringProperty fullNameProperty() {
        return fullNameProperty;
    }

    public long getAccountID() {
        return accountIDProperty().get();
    }

    public String getFullName() {
        return fullNameProperty().get();
    }

    public long getID() {
        return idProperty().get();
    }

    public ObservableList<FxMail> getMails() {
        return mails;
    }

    public SortedList<FxMail> getMailsSorted() {
        return mailsSorted;
    }

    public String getName() {
        return nameProperty().get();
    }

    public int getUnreadMailsCount() {
        return unreadMailsCount.get();
    }

    public int getUnreadMailsCountTotal() {
        return unreadMailsCountTotalBinding().intValue();
    }

    public LongProperty idProperty() {
        return idProperty;
    }

    public boolean isAbonniert() {
        return abonniertProperty().get();
    }

    public boolean isParent() {
        return getParent() == null;
    }

    public boolean isSendFolder() {
        return isSendFolderProperty().get();
    }

    public BooleanProperty isSendFolderProperty() {
        return isSendFolderProperty;
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public void removeChild(final FxMailFolder child) {
        getChildren().remove(child);
        child.setParent(null);
    }

    public void setAbonniert(final boolean abo) {
        abonniertProperty().set(abo);
    }

    public void setAccountID(final long accountID) {
        accountIDProperty().set(accountID);
    }

    public void setFullName(final String fullName) {
        fullNameProperty().set(fullName);
    }

    public void setID(final long id) {
        idProperty().set(id);
    }

    public void setName(final String name) {
        nameProperty().set(name);

        Predicate<String> predicate = "send"::equals;
        predicate = predicate.or("sent"::equals);
        predicate = predicate.or(n -> n.startsWith("gesendet"));

        isSendFolderProperty().set(predicate.test(name.toLowerCase()));

        if (isSendFolder()) {
            mailsSorted.setComparator(Comparator.comparing(FxMail::getSendDate).reversed());
        }
        else {
            mailsSorted.setComparator(Comparator.comparing(FxMail::getReceivedDate).reversed());
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("MailFolder [fullName=");
        builder.append(getFullName());
        builder.append("]");

        return builder.toString();
    }

    ObservableList<FxMailFolder> getChildren() {
        return children;
    }

    FxMailFolder getParent() {
        return parent;
    }

    void setParent(final FxMailFolder parent) {
        this.parent = parent;
    }

    ObservableIntegerValue unreadMailsCountTotalBinding() {
        return unreadMailsCountTotal;
    }
}
