/**
 * Created: 26.12.2016
 */
package de.freese.pim.core.mail.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Entity für einen Mail-Account.
 *
 * @author Thomas Freese
 */
// @SuppressWarnings("restriction")
// @JsonRootName("mailAccount")
public class MailAccount
{
    /**
    *
    */
    private final FilteredList<MailFolder> abonnierteFolder;

    /**
    *
    */
    private final ObservableList<MailFolder> folder = FXCollections.observableArrayList();

    /**
     *
     */
    private long id = 0;

    /**
     *
     */
    private final StringProperty imapHostProperty = new SimpleStringProperty(this, "imapHost", null);

    /**
     *
     */
    private final BooleanProperty imapLegitimationProperty = new SimpleBooleanProperty(this, "imapLegitimation", true);

    /**
     *
     */
    private final ObjectProperty<MailPort> imapPortProperty = new SimpleObjectProperty<>(this, "imapPort", MailPort.IMAPS);

    /**
     *
     */
    private final StringProperty mailProperty = new SimpleStringProperty(this, "mail", null);

    /**
     *
     */
    private final StringProperty passwordProperty = new SimpleStringProperty(this, "password", null);

    /**
    *
    */
    private final FilteredList<MailFolder> rootFolder;

    /**
     *
     */
    private final StringProperty smtpHostProperty = new SimpleStringProperty(this, "smtpHost", null);

    /**
     *
     */
    private final BooleanProperty smtpLegitimationProperty = new SimpleBooleanProperty(this, "smtpLegitimation", true);

    /**
     *
     */
    private final ObjectProperty<MailPort> smtpPortProperty = new SimpleObjectProperty<>(this, "smtpPort", MailPort.SMTPS);

    /**
    *
    */
    private ObservableIntegerValue unreadMailsCount = null;

    /**
     * Erstellt ein neues {@link MailAccount} Object.
     */
    public MailAccount()
    {
        super();

        this.abonnierteFolder = new FilteredList<>(getFolder(), MailFolder::isAbonniert);
        this.rootFolder = new FilteredList<>(this.abonnierteFolder, MailFolder::isParent);

        // Zähler mit der Folder-List verbinden.
        this.unreadMailsCount = new SumUnreadMailsInChildFolderBinding(this.rootFolder);
    }

    /**
     * Erstellt ein neues {@link MailAccount} Object mit den Attributen der Quelle.
     *
     * @param src {@link MailAccount}
     */
    public MailAccount(final MailAccount src)
    {
        this();

        copyFrom(src);
    }

    /**
     * Kopiert die Attribute von der Quelle.
     *
     * @param src {@link MailAccount}
     */
    public void copyFrom(final MailAccount src)
    {
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

    /**
     * @return {@link ObservableList}
     */
    public ObservableList<MailFolder> getFolder()
    {
        return this.folder;
    }

    /**
     * Liefert alle abonnierte Folder des Accounts.
     *
     * @return {@link FilteredList}
     */
    public FilteredList<MailFolder> getFolderSubscribed()
    {
        return this.abonnierteFolder;
    }

    /**
     * @return long
     */
    public long getID()
    {
        return this.id;
    }

    /**
     * @return String
     */
    public String getImapHost()
    {
        return imapHostProperty().get();
    }

    /**
     * @return {@link MailPort}
     */
    public MailPort getImapPort()
    {
        return imapPortProperty().get();
    }

    /**
     * @return String
     */
    public String getMail()
    {
        return mailProperty().get();
    }

    /**
     * @return String
     */
    public String getPassword()
    {
        return passwordProperty().get();
    }

    /**
     * @return String
     */
    public String getSmtpHost()
    {
        return smtpHostProperty().get();
    }

    /**
     * @return {@link MailPort}
     */
    public MailPort getSmtpPort()
    {
        return smtpPortProperty().get();
    }

    /**
     * Liefert die Anzahl ungelesener Mails des Accounts.
     *
     * @return int
     */
    public int getUnreadMailsCount()
    {
        return this.unreadMailsCount.intValue();
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty imapHostProperty()
    {
        return this.imapHostProperty;
    }

    /**
     * @return {@link BooleanProperty}
     */
    public BooleanProperty imapLegitimationProperty()
    {
        return this.imapLegitimationProperty;
    }

    /**
     * @return {@link ObjectProperty}
     */
    public ObjectProperty<MailPort> imapPortProperty()
    {
        return this.imapPortProperty;
    }

    /**
     * @return boolean
     */
    public boolean isImapLegitimation()
    {
        return imapLegitimationProperty().get();
    }

    /**
     * @return boolean
     */
    public boolean isSmtpLegitimation()
    {
        return smtpLegitimationProperty().get();
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty mailProperty()
    {
        return this.mailProperty;
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty passwordProperty()
    {
        return this.passwordProperty;
    }

    /**
     * @param id long
     */
    public void setID(final long id)
    {
        this.id = id;
    }

    /**
     * @param host String
     */
    public void setImapHost(final String host)
    {
        imapHostProperty().set(host);
    }

    /**
     * @param legitimation boolean
     */
    public void setImapLegitimation(final boolean legitimation)
    {
        imapLegitimationProperty().set(legitimation);
    }

    /**
     * @param port {@link MailPort}
     */
    public void setImapPort(final MailPort port)
    {
        imapPortProperty().set(port);
    }

    /**
     * @param mail String
     */
    public void setMail(final String mail)
    {
        mailProperty().set(mail);
    }

    /**
     * @param password String
     */
    public void setPassword(final String password)
    {
        passwordProperty().set(password);
    }

    /**
     * @param host String
     */
    public void setSmtpHost(final String host)
    {
        smtpHostProperty().set(host);
    }

    /**
     * @param legitimation boolean
     */
    public void setSmtpLegitimation(final boolean legitimation)
    {
        smtpLegitimationProperty().set(legitimation);
    }

    /**
     * @param port {@link MailPort}
     */
    public void setSmtpPort(final MailPort port)
    {
        smtpPortProperty().set(port);
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty smtpHostProperty()
    {
        return this.smtpHostProperty;
    }

    /**
     * @return {@link BooleanProperty}
     */
    public BooleanProperty smtpLegitimationProperty()
    {
        return this.smtpLegitimationProperty;
    }

    /**
     * @return {@link ObjectProperty}
     */
    public ObjectProperty<MailPort> smtpPortProperty()
    {
        return this.smtpPortProperty;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MailAccount [id=").append(this.id);
        builder.append(", mail=").append(getMail());
        builder.append("]");

        return builder.toString();
    }
}
