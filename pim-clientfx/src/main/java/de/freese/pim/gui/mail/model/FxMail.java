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
public class FxMail
{
    private final ObjectProperty<InternetAddress[]> bccProperty = new SimpleObjectProperty<>(this, "to", null);

    private final ObjectProperty<InternetAddress[]> ccProperty = new SimpleObjectProperty<>(this, "to", null);

    private final StringProperty folderFullNameProperty = new SimpleStringProperty(this, "folderFullName", null);

    private final LongProperty folderIDProperty = new SimpleLongProperty(this, "folderID", 0L);

    private final ObjectProperty<InternetAddress> fromProperty = new SimpleObjectProperty<>(this, "from", null);
    /**
     * Ist immer größer als 0.
     */
    private final IntegerProperty msgNumProperty = new SimpleIntegerProperty(this, "msgNum", 0);

    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final ObjectProperty<Date> receivedDateProperty = new SimpleObjectProperty<>(this, "receivedDate", null);

    private final BooleanProperty seenProperty = new SimpleBooleanProperty(this, "seen", false);

    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final ObjectProperty<Date> sendDateProperty = new SimpleObjectProperty<>(this, "sendDate", null);

    private final IntegerProperty sizeProperty = new SimpleIntegerProperty(this, "size", 0);

    private final StringProperty subjectProperty = new SimpleStringProperty(this, "subject", null);

    private final ObjectProperty<InternetAddress[]> toProperty = new SimpleObjectProperty<>(this, "to", null);

    private final LongProperty uidProperty = new SimpleLongProperty(this, "uid", 0L);

    public ObjectProperty<InternetAddress[]> bccProperty()
    {
        return this.bccProperty;
    }

    public ObjectProperty<InternetAddress[]> ccProperty()
    {
        return this.ccProperty;
    }

    public StringProperty folderFullNameProperty()
    {
        return this.folderFullNameProperty;
    }

    public LongProperty folderIDProperty()
    {
        return this.folderIDProperty;
    }

    public ObjectProperty<InternetAddress> fromProperty()
    {
        return this.fromProperty;
    }

    public InternetAddress[] getBcc()
    {
        return bccProperty().get();
    }

    public InternetAddress[] getCc()
    {
        return ccProperty().get();
    }

    public String getFolderFullName()
    {
        return folderFullNameProperty().get();
    }

    public long getFolderID()
    {
        return folderIDProperty().get();
    }

    public InternetAddress getFrom()
    {
        return fromProperty().get();
    }

    /**
     * Ist immer größer als 0.
     */
    public int getMsgNum()
    {
        return msgNumProperty().get();
    }

    public Date getReceivedDate()
    {
        return receivedDateProperty().get();
    }

    public Date getSendDate()
    {
        return this.sendDateProperty.get();
    }

    public int getSize()
    {
        return sizeProperty().get();
    }

    public String getSubject()
    {
        return subjectProperty().get();
    }

    public InternetAddress[] getTo()
    {
        return toProperty().get();
    }

    /**
     * Bildet mit dem Folder den PrimaryKey.
     */
    public long getUID()
    {
        return uidProperty().get();
    }

    public boolean isSeen()
    {
        return seenProperty().get();
    }

    public IntegerProperty msgNumProperty()
    {
        return this.msgNumProperty;
    }

    public ObjectProperty<Date> receivedDateProperty()
    {
        return this.receivedDateProperty;
    }

    public BooleanProperty seenProperty()
    {
        return this.seenProperty;
    }

    public ObjectProperty<Date> sendDateProperty()
    {
        return this.sendDateProperty;
    }

    public void setBcc(final InternetAddress[] bcc)
    {
        bccProperty().set(bcc);
    }

    public void setCc(final InternetAddress[] cc)
    {
        ccProperty().set(cc);
    }

    public void setFolderFullName(final String folderFullName)
    {
        folderFullNameProperty().set(folderFullName);
    }

    public void setFolderID(final long folderID)
    {
        folderIDProperty().set(folderID);
    }

    public void setFrom(final InternetAddress from)
    {
        fromProperty().set(from);
    }

    /**
     * Ist immer größer als 0.
     */
    public void setMsgNum(final int msgNum)
    {
        msgNumProperty().set(msgNum);
    }

    public void setReceivedDate(final Date date)
    {
        receivedDateProperty().set(date);
    }

    public void setSeen(final boolean seen)
    {
        seenProperty().set(seen);
    }

    public void setSendDate(final Date date)
    {
        sendDateProperty().set(date);
    }

    public void setSize(final int size)
    {
        sizeProperty().set(size);
    }

    public void setSubject(final String subject)
    {
        subjectProperty().set(subject);
    }

    public void setTo(final InternetAddress[] to)
    {
        toProperty().set(to);
    }

    /**
     * Bildet mit dem Folder den PrimaryKey.
     */
    public void setUID(final long uid)
    {
        uidProperty().set(uid);
    }

    public IntegerProperty sizeProperty()
    {
        return this.sizeProperty;
    }

    public StringProperty subjectProperty()
    {
        return this.subjectProperty;
    }

    public ObjectProperty<InternetAddress[]> toProperty()
    {
        return this.toProperty;
    }

    public LongProperty uidProperty()
    {
        return this.uidProperty;
    }
}
