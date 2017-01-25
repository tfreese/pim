// Created: 09.01.2017
package de.freese.pim.core.mail.model;

import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

import javax.mail.internet.InternetAddress;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Entity für eine Mail.
 *
 * @author Thomas Freese
 */
public class Mail
{
    /**
     *
     */
    private final MailFolder folder;

    /**
     *
     */
    private final ObjectProperty<InternetAddress> fromProperty = new SimpleObjectProperty<>(this, "from", null);

    /**
     * Ist immer größer als 0.
     */
    private int msgNum = 0;

    /**
    *
    */
    private final ObjectProperty<Date> receivedDateProperty = new SimpleObjectProperty<>(this, "receivedDate", null);

    /**
    *
    */
    private final BooleanProperty seenProperty = new SimpleBooleanProperty(this, "seen", false);

    /**
    *
    */
    private final ObjectProperty<Date> sendDateProperty = new SimpleObjectProperty<>(this, "sendDate", null);

    /**
     *
     */
    private int size = -1;

    /**
    *
    */
    private final StringProperty subjectProperty = new SimpleStringProperty(this, "subject", null);

    /**
    *
    */
    private final ObjectProperty<InternetAddress> toProperty = new SimpleObjectProperty<>(this, "to", null);

    /**
     *
     */
    private String uid = null;

    /**
     * Erzeugt eine neue Instanz von {@link Mail}
     *
     * @param folder {@link MailFolder}
     */
    public Mail(final MailFolder folder)
    {
        super();

        Objects.requireNonNull(folder, "folder required");

        this.folder = folder;
    }

    /**
     * @return {@link ObjectProperty}
     */
    public ObjectProperty<InternetAddress> fromProperty()
    {
        return this.fromProperty;
    }

    /**
     * @return {@link MailFolder}
     */
    public MailFolder getFolder()
    {
        return this.folder;
    }

    /**
     * Liefert den Absender.
     *
     * @return {@link InternetAddress}
     */
    public InternetAddress getFrom()
    {
        return fromProperty().get();
    }

    /**
     * Ist immer größer als 0.
     *
     * @return int
     */
    public int getMsgNum()
    {
        return this.msgNum;
    }

    /**
     * Liefert den lokalen Temp-{@link Path} der Mail.
     *
     * @return {@link Path}
     */
    public Path getPath()
    {
        Path basePath = getFolder().getPath();
        Path path = basePath.resolve(getUID()).resolve(getUID() + ".eml");

        return path;
    }

    /**
     * Liefert das Empfangs-Datum.
     *
     * @return {@link Date}
     */
    public Date getReceivedDate()
    {
        return receivedDateProperty().get();
    }

    /**
     * Liefert das Sende-Datum.
     *
     * @return {@link Date}
     */
    public Date getSendDate()
    {
        return this.sendDateProperty.get();
    }

    /**
     * Liefert die Größe der Mail.
     * 
     * @return int
     */
    public int getSize()
    {
        return this.size;
    }

    /**
     * Liefert das Subject.
     *
     * @return String
     */
    public String getSubject()
    {
        return subjectProperty().get();
    }

    /**
     * Liefert den Empfänger.
     *
     * @return {@link InternetAddress}
     */
    public InternetAddress getTo()
    {
        return toProperty().get();
    }

    /**
     * Liefert die Message-ID oder bei IMAP die UID.
     *
     * @return String
     */
    public String getUID()
    {
        return this.uid;
    }

    /**
     * Liefert true, wenn die Mail bereits gelesen wurde.
     *
     * @return boolean
     */
    public boolean isSeen()
    {
        return seenProperty().get();
    }

    /**
     * @return {@link ObjectProperty}
     */
    public ObjectProperty<Date> receivedDateProperty()
    {
        return this.receivedDateProperty;
    }

    /**
     * @return {@link BooleanProperty}
     */
    public BooleanProperty seenProperty()
    {
        return this.seenProperty;
    }

    /**
     * @return {@link ObjectProperty}
     */
    public ObjectProperty<Date> sendDateProperty()
    {
        return this.sendDateProperty;
    }

    /**
     * Setzt den Absender.
     *
     * @param from {@link InternetAddress}
     */
    public void setFrom(final InternetAddress from)
    {
        fromProperty().set(from);
    }

    /**
     * Ist immer größer als 0.
     *
     * @param msgNum int
     */
    public void setMsgNum(final int msgNum)
    {
        this.msgNum = msgNum;
    }

    /**
     * Setzt das Empfangs-Datum.
     *
     * @param date {@link Date}
     */
    public void setReceivedDate(final Date date)
    {
        receivedDateProperty().set(date);
    }

    /**
     * Setzt true, wenn die Mail bereits gelesen wurde.
     *
     * @param seen boolean
     */
    public void setSeen(final boolean seen)
    {
        seenProperty().set(seen);
    }

    /**
     * Setzt das Sende-Datum.
     *
     * @param date {@link Date}
     */
    public void setSendDate(final Date date)
    {
        sendDateProperty().set(date);
    }

    /**
     * Setzt die Größe der Mail.
     *
     * @param size int
     */
    public void setSize(final int size)
    {
        this.size = size;
    }

    /**
     * Setzt das Subject.
     *
     * @param subject String
     */
    public void setSubject(final String subject)
    {
        subjectProperty().set(subject);
    }

    /**
     * Setzt den Empfänger.
     *
     * @param to {@link InternetAddress}
     */
    public void setTo(final InternetAddress to)
    {
        toProperty().set(to);
    }

    /**
     * Setzt die Message-ID oder bei IMAP die UID.
     *
     * @param uid String
     */
    public void setUID(final String uid)
    {
        this.uid = uid;
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty subjectProperty()
    {
        return this.subjectProperty;
    }

    /**
     * @return {@link ObjectProperty}
     */
    public ObjectProperty<InternetAddress> toProperty()
    {
        return this.toProperty;
    }
}
