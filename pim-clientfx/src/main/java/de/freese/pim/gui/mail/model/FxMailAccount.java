// Created: 26.12.2016
package de.freese.pim.gui.mail.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.freese.pim.core.mail.MailPort;

/**
 * FX-Bean für einen Mail-Account.
 *
 * @author Thomas Freese
 */
// @SuppressWarnings("restriction")
// @JsonRootName("mailAccount")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FxMailAccount {
    @JsonIgnore
    private final FilteredList<FxMailFolder> abonnierteFolder;
    @JsonIgnore
    private final ObservableList<FxMailFolder> folder = FXCollections.observableArrayList();
    private final LongProperty idProperty = new SimpleLongProperty(this, "id", 0L);
    private final StringProperty imapHostProperty = new SimpleStringProperty(this, "imapHost", null);
    private final BooleanProperty imapLegitimationProperty = new SimpleBooleanProperty(this, "imapLegitimation", true);
    private final ObjectProperty<MailPort> imapPortProperty = new SimpleObjectProperty<>(this, "imapPort", MailPort.IMAPS);
    private final StringProperty mailProperty = new SimpleStringProperty(this, "mail", null);
    private final StringProperty passwordProperty = new SimpleStringProperty(this, "password", null);
    @JsonIgnore
    private final FilteredList<FxMailFolder> rootFolder;
    private final StringProperty smtpHostProperty = new SimpleStringProperty(this, "smtpHost", null);
    private final BooleanProperty smtpLegitimationProperty = new SimpleBooleanProperty(this, "smtpLegitimation", true);
    private final ObjectProperty<MailPort> smtpPortProperty = new SimpleObjectProperty<>(this, "smtpPort", MailPort.SMTPS);
    @JsonIgnore
    private final ObservableIntegerValue unreadMailsCount;

    public FxMailAccount() {
        super();

        abonnierteFolder = new FilteredList<>(getFolder(), FxMailFolder::isAbonniert);
        rootFolder = new FilteredList<>(abonnierteFolder, FxMailFolder::isParent);

        // Zähler mit der Folder-List verbinden.
        unreadMailsCount = new SumUnreadMailsInChildFolderBinding(rootFolder);
    }

    public FxMailAccount(final FxMailAccount src) {
        this();

        copyFrom(src);
    }

    public void copyFrom(final FxMailAccount src) {
        setID(src.getID());
        setImapHost(src.getImapHost());
        setImapLegitimation(src.isImapLegitimation());
        setImapPort(src.getImapPort());
        setMail(src.getMail());
        setPassword(src.getPassword());
        setSmtpHost(src.getSmtpHost());
        setSmtpLegitimation(src.isSmtpLegitimation());
        setSmtpPort(src.getSmtpPort());

        getFolder().clear();
        getFolder().addAll(src.getFolder());
    }

    public ObservableList<FxMailFolder> getFolder() {
        return folder;
    }

    @JsonIgnore
    public FilteredList<FxMailFolder> getFolderSubscribed() {
        return abonnierteFolder;
    }

    public long getID() {
        return idProperty().get();
    }

    public String getImapHost() {
        return imapHostProperty().get();
    }

    public MailPort getImapPort() {
        return imapPortProperty().get();
    }

    public String getMail() {
        return mailProperty().get();
    }

    public String getPassword() {
        return passwordProperty().get();
    }

    public String getSmtpHost() {
        return smtpHostProperty().get();
    }

    public MailPort getSmtpPort() {
        return smtpPortProperty().get();
    }

    public int getUnreadMailsCount() {
        return unreadMailsCount.intValue();
    }

    public LongProperty idProperty() {
        return idProperty;
    }

    public StringProperty imapHostProperty() {
        return imapHostProperty;
    }

    public BooleanProperty imapLegitimationProperty() {
        return imapLegitimationProperty;
    }

    public ObjectProperty<MailPort> imapPortProperty() {
        return imapPortProperty;
    }

    public boolean isImapLegitimation() {
        return imapLegitimationProperty().get();
    }

    public boolean isSmtpLegitimation() {
        return smtpLegitimationProperty().get();
    }

    public StringProperty mailProperty() {
        return mailProperty;
    }

    public StringProperty passwordProperty() {
        return passwordProperty;
    }

    public void setID(final long id) {
        idProperty().set(id);
    }

    public void setImapHost(final String host) {
        imapHostProperty().set(host);
    }

    public void setImapLegitimation(final boolean legitimation) {
        imapLegitimationProperty().set(legitimation);
    }

    public void setImapPort(final MailPort port) {
        imapPortProperty().set(port);
    }

    public void setMail(final String mail) {
        mailProperty().set(mail);
    }

    public void setPassword(final String password) {
        passwordProperty().set(password);
    }

    public void setSmtpHost(final String host) {
        smtpHostProperty().set(host);
    }

    public void setSmtpLegitimation(final boolean legitimation) {
        smtpLegitimationProperty().set(legitimation);
    }

    public void setSmtpPort(final MailPort port) {
        smtpPortProperty().set(port);
    }

    public StringProperty smtpHostProperty() {
        return smtpHostProperty;
    }

    public BooleanProperty smtpLegitimationProperty() {
        return smtpLegitimationProperty;
    }

    public ObjectProperty<MailPort> smtpPortProperty() {
        return smtpPortProperty;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("MailAccount [id=").append(getID());
        builder.append(", mail=").append(getMail());
        builder.append("]");

        return builder.toString();
    }
}
