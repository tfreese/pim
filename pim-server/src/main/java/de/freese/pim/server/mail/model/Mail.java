// Created: 09.01.2017
package de.freese.pim.server.mail.model;

import java.util.Date;

import de.freese.pim.common.model.mail.InternetAddress;

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
    private InternetAddress[] bcc = null;

    /**
    *
    */
    private InternetAddress[] cc = null;

    /**
     *
     */
    private String folderFullName = null;

    /**
    *
    */
    private long folderID = 0L;

    /**
     *
     */
    private InternetAddress from = null;

    /**
     * Ist immer größer als 0.
     */
    private int msgNum = 0;

    /**
    *
    */
    private Date receivedDate = null;

    /**
    *
    */
    private boolean seen = false;

    /**
    *
    */
    private Date sendDate = null;

    /**
     *
     */
    private int size = 0;

    /**
    *
    */
    private String subject = null;

    /**
    *
    */
    private InternetAddress[] to = null;

    /**
     *
     */
    private long uid = 0L;

    /**
     * Erzeugt eine neue Instanz von {@link Mail}
     */
    public Mail()
    {
        super();
    }

    /**
     * Liefert den Empfänger, blind Copy.
     *
     * @return {@link InternetAddress}
     */
    public InternetAddress[] getBcc()
    {
        return this.bcc;
    }

    /**
     * Liefert den Empfänger, Copy.
     *
     * @return {@link InternetAddress}
     */
    public InternetAddress[] getCc()
    {
        return this.cc;
    }

    /**
     * Kompletter Name des Folders.
     *
     * @return String
     */
    public String getFolderFullName()
    {
        return this.folderFullName;
    }

    /**
     * @return long
     */
    public long getFolderID()
    {
        return this.folderID;
    }

    /**
     * Liefert den Absender.
     *
     * @return {@link InternetAddress}
     */
    public InternetAddress getFrom()
    {
        return this.from;
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
     * Liefert das Empfangs-Datum.
     *
     * @return {@link Date}
     */
    public Date getReceivedDate()
    {
        return this.receivedDate;
    }

    /**
     * Liefert das Sende-Datum.
     *
     * @return {@link Date}
     */
    public Date getSendDate()
    {
        return this.sendDate;
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
        return this.subject;
    }

    /**
     * Liefert den Empfänger.
     *
     * @return {@link InternetAddress}
     */
    public InternetAddress[] getTo()
    {
        return this.to;
    }

    /**
     * Liefert UID.<br>
     * Bildet mit dem Folder den PrimaryKey.
     *
     * @return long
     */
    public long getUID()
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
        return this.seen;
    }

    /**
     * Setzt den Empfänger, Blind Copy.
     *
     * @param bcc {@link InternetAddress}
     */
    public void setBcc(final InternetAddress[] bcc)
    {
        this.bcc = bcc;
    }

    /**
     * Setzt den Empfänger, Copy.
     *
     * @param cc {@link InternetAddress}
     */
    public void setCc(final InternetAddress[] cc)
    {
        this.cc = cc;
    }

    /**
     * Kompletter Name des Folders.
     *
     * @param folderFullName String
     */
    public void setFolderFullName(final String folderFullName)
    {
        this.folderFullName = folderFullName;
    }

    /**
     * @param folderID long
     */
    public void setFolderID(final long folderID)
    {
        this.folderID = folderID;
    }

    /**
     * Setzt den Absender.
     *
     * @param from {@link InternetAddress}
     */
    public void setFrom(final InternetAddress from)
    {
        this.from = from;
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
        this.receivedDate = date;
    }

    /**
     * Setzt true, wenn die Mail bereits gelesen wurde.
     *
     * @param seen boolean
     */
    public void setSeen(final boolean seen)
    {
        this.seen = seen;
    }

    /**
     * Setzt das Sende-Datum.
     *
     * @param date {@link Date}
     */
    public void setSendDate(final Date date)
    {
        this.sendDate = date;
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
        this.subject = subject;
    }

    /**
     * Setzt den Empfänger.
     *
     * @param to {@link InternetAddress}
     */
    public void setTo(final InternetAddress[] to)
    {
        this.to = to;
    }

    /**
     * Setzt die UID.<br>
     * Bildet mit dem Folder den PrimaryKey.
     *
     * @param uid long
     */
    public void setUID(final long uid)
    {
        this.uid = uid;
    }
}
