// Created: 09.01.2017
package de.freese.pim.core.model.mail;

import java.util.Date;

import de.freese.pim.core.mail.InternetAddress;

/**
 * Entity für eine Mail.
 *
 * @author Thomas Freese
 */
public class Mail {
    private InternetAddress[] bcc;
    private InternetAddress[] cc;
    // @NotNull
    private String folderFullName;
    // @NotNull
    private long folderID;
    private InternetAddress from;
    /**
     * Ist immer größer als 0.
     */
    // @NotNull
    private int msgNum;
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receivedDate;
    // @NotNull
    private boolean seen;
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendDate;
    // @NotNull
    private int size;
    // @Size(max = 400)
    private String subject;
    private InternetAddress[] to;
    // @NotNull
    private long uid;

    public InternetAddress[] getBcc() {
        return this.bcc;
    }

    public InternetAddress[] getCc() {
        return this.cc;
    }

    public String getFolderFullName() {
        return this.folderFullName;
    }

    public long getFolderID() {
        return this.folderID;
    }

    public InternetAddress getFrom() {
        return this.from;
    }

    public int getMsgNum() {
        return this.msgNum;
    }

    public Date getReceivedDate() {
        return this.receivedDate;
    }

    public Date getSendDate() {
        return this.sendDate;
    }

    public int getSize() {
        return this.size;
    }

    public String getSubject() {
        return this.subject;
    }

    public InternetAddress[] getTo() {
        return this.to;
    }

    /**
     * Liefert UID.<br>
     * Bildet mit dem Folder den PrimaryKey.
     *
     * @return long
     */
    public long getUID() {
        return this.uid;
    }

    public boolean isSeen() {
        return this.seen;
    }

    public void setBcc(final InternetAddress[] bcc) {
        this.bcc = bcc;
    }

    public void setCc(final InternetAddress[] cc) {
        this.cc = cc;
    }

    public void setFolderFullName(final String folderFullName) {
        this.folderFullName = folderFullName;
    }

    public void setFolderID(final long folderID) {
        this.folderID = folderID;
    }

    public void setFrom(final InternetAddress from) {
        this.from = from;
    }

    /**
     * Ist immer größer als 0.
     */
    public void setMsgNum(final int msgNum) {
        this.msgNum = msgNum;
    }

    public void setReceivedDate(final Date date) {
        this.receivedDate = date;
    }

    public void setSeen(final boolean seen) {
        this.seen = seen;
    }

    public void setSendDate(final Date date) {
        this.sendDate = date;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public void setTo(final InternetAddress[] to) {
        this.to = to;
    }

    /**
     * Setzt die UID.<br>
     * Bildet mit dem Folder den PrimaryKey.
     */
    public void setUID(final long uid) {
        this.uid = uid;
    }
}
