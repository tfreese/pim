// Created: 09.01.2017
package de.freese.pim.gui.mail.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.freese.pim.core.mail.InternetAddress;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * FX-Bean für eine Mail.
 *
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FXMail
{
    /**
    *
    */
    private final ObjectProperty<InternetAddress[]> bccProperty = new SimpleObjectProperty<>(this, "to", null);
    /**
    *
    */
    private final ObjectProperty<InternetAddress[]> ccProperty = new SimpleObjectProperty<>(this, "to", null);
    /**
     *
     */
    private StringProperty folderFullNameProperty = new SimpleStringProperty(this, "folderFullName", null);
    /**
    *
    */
    private LongProperty folderIDProperty = new SimpleLongProperty(this, "folderID", 0L);
    /**
     *
     */
    private final ObjectProperty<InternetAddress> fromProperty = new SimpleObjectProperty<>(this, "from", null);
    /**
     * Ist immer größer als 0.
     */
    private IntegerProperty msgNumProperty = new SimpleIntegerProperty(this, "msgNum", 0);
    /**
    *
    */
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final ObjectProperty<Date> receivedDateProperty = new SimpleObjectProperty<>(this, "receivedDate", null);
    /**
    *
    */
    private final BooleanProperty seenProperty = new SimpleBooleanProperty(this, "seen", false);
    /**
    *
    */
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final ObjectProperty<Date> sendDateProperty = new SimpleObjectProperty<>(this, "sendDate", null);
    /**
     *
     */
    private IntegerProperty sizeProperty = new SimpleIntegerProperty(this, "size", 0);
    /**
    *
    */
    private final StringProperty subjectProperty = new SimpleStringProperty(this, "subject", null);
    /**
    *
    */
    private final ObjectProperty<InternetAddress[]> toProperty = new SimpleObjectProperty<>(this, "to", null);
    /**
     *
     */
    private LongProperty uidProperty = new SimpleLongProperty(this, "uid", 0L);

    /**
     * @return {@link ObjectProperty}
     */
    public ObjectProperty<InternetAddress[]> bccProperty()
    {
        return this.bccProperty;
    }

    /**
     * @return {@link ObjectProperty}
     */
    public ObjectProperty<InternetAddress[]> ccProperty()
    {
        return this.ccProperty;
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty folderFullNameProperty()
    {
        return this.folderFullNameProperty;
    }

    /**
     * @return {@link LongProperty}
     */
    public LongProperty folderIDProperty()
    {
        return this.folderIDProperty;
    }

    /**
     * @return {@link ObjectProperty}
     */
    public ObjectProperty<InternetAddress> fromProperty()
    {
        return this.fromProperty;
    }

    /**
     * Liefert den Empfänger, blind Copy.
     *
     * @return {@link InternetAddress}
     */
    public InternetAddress[] getBcc()
    {
        return bccProperty().get();
    }

    /**
     * Liefert den Empfänger, Copy.
     *
     * @return {@link InternetAddress}
     */
    public InternetAddress[] getCc()
    {
        return ccProperty().get();
    }

    /**
     * Kompletter Name des Folders.
     *
     * @return String
     */
    public String getFolderFullName()
    {
        return folderFullNameProperty().get();
    }

    /**
     * @return long
     */
    public long getFolderID()
    {
        return folderIDProperty().get();
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
        return msgNumProperty().get();
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
        return sizeProperty().get();
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
    public InternetAddress[] getTo()
    {
        return toProperty().get();
    }

    /**
     * Liefert UID.<br>
     * Bildet mit dem Folder den PrimaryKey.
     *
     * @return long
     */
    public long getUID()
    {
        return uidProperty().get();
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
     * @return {@link IntegerProperty}
     */
    public IntegerProperty msgNumProperty()
    {
        return this.msgNumProperty;
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
     * Setzt den Empfänger, Blind Copy.
     *
     * @param bcc {@link InternetAddress}
     */
    public void setBcc(final InternetAddress[] bcc)
    {
        bccProperty().set(bcc);
    }

    /**
     * Setzt den Empfänger, Copy.
     *
     * @param cc {@link InternetAddress}
     */
    public void setCc(final InternetAddress[] cc)
    {
        ccProperty().set(cc);
    }

    /**
     * Kompletter Name des Folders.
     *
     * @param folderFullName String
     */
    public void setFolderFullName(final String folderFullName)
    {
        folderFullNameProperty().set(folderFullName);
    }

    /**
     * @param folderID long
     */
    public void setFolderID(final long folderID)
    {
        folderIDProperty().set(folderID);
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
        msgNumProperty().set(msgNum);
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
        sizeProperty().set(size);
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
    public void setTo(final InternetAddress[] to)
    {
        toProperty().set(to);
    }

    /**
     * Setzt die UID.<br>
     * Bildet mit dem Folder den PrimaryKey.
     *
     * @param uid long
     */
    public void setUID(final long uid)
    {
        uidProperty().set(uid);
    }

    /**
     * @return {@link IntegerProperty}
     */
    public IntegerProperty sizeProperty()
    {
        return this.sizeProperty;
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
    public ObjectProperty<InternetAddress[]> toProperty()
    {
        return this.toProperty;
    }

    /**
     * @return {@link LongProperty}
     */
    public LongProperty uidProperty()
    {
        return this.uidProperty;
    }
}
