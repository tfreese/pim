// Created: 09.01.2017
package de.freese.pim.server.mail.model;

import java.util.function.Predicate;

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
    // @NotNull
    private boolean abonniert = true;

    /**
    *
    */
    // @NotNull
    private long accountID = 0;

    /**
    *
    */
    // @NotNull
    // @Size(min = 0, max = 100)
    private String fullName = null;

    /**
    *
    */
    // @NotNull
    private long id = 0;

    /**
     *
     */
    // @NotNull
    private boolean isSendFolder = false;

    /**
    *
    */
    // @NotNull
    // @Size(min = 0, max = 100)
    private String name = null;

    /**
     * Erzeugt eine neue Instanz von {@link MailFolder}
     */
    public MailFolder()
    {
        super();
    }

    /**
     * @return long
     */
    public long getAccountID()
    {
        return this.accountID;
    }

    /**
     * Liefert den vollen Hierarchie-Namen, zB PARENT_NAME/FOLDER_NAME.
     *
     * @return String
     */
    public String getFullName()
    {
        return this.fullName;
    }

    /**
     * @return long
     */
    public long getID()
    {
        return this.id;
    }

    /**
     * Liefert den Namen des Folders.
     *
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Liefert das Flag um den Folder zu abonnieren/beobachten.
     *
     * @return boolean
     */
    public boolean isAbonniert()
    {
        return this.abonniert;
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
     * Setzt das Flag um den Folder zu abonnieren/beobachten.
     *
     * @param abo boolean
     */
    public void setAbonniert(final boolean abo)
    {
        this.abonniert = abo;
    }

    /**
     * @param accountID long
     */
    public void setAccountID(final long accountID)
    {
        this.accountID = accountID;
    }

    /**
     * Setzt den vollen Hierarchie-Namen, zB PARENT_NAME/FOLDER_NAME.
     *
     * @param fullName String
     */
    public void setFullName(final String fullName)
    {
        this.fullName = fullName;
    }

    /**
     * @param id long
     */
    public void setID(final long id)
    {
        this.id = id;
    }

    /**
     * Setzt den Namen des Folders.
     *
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;

        Predicate<String> predicate = n -> "send".equals(n);
        predicate = predicate.or(n -> "sent".equals(n));
        predicate = predicate.or(n -> n.startsWith("gesendet"));

        this.isSendFolder = predicate.test(name.toLowerCase());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MailFolder [fullName=").append(getFullName());
        builder.append("]");

        return builder.toString();
    }
}
