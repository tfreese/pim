// Created: 09.01.2017
package de.freese.pim.core.model.mail;

import java.util.function.Predicate;

/**
 * Entity für einen Mail-Folder.
 *
 * @author Thomas Freese
 */
public class MailFolder {
    // @NotNull
    private boolean abonniert = true;

    // @NotNull
    private long accountID;

    // @NotNull
    // @Size(min = 0, max = 100)
    private String fullName;

    // @NotNull
    private long id;

    // @NotNull
    // @Size(min = 0, max = 100)
    private String name;

    // @NotNull
    private boolean sendFolder;

    public long getAccountID() {
        return this.accountID;
    }

    /**
     * Liefert den vollen Hierarchie-Namen, z.B. PARENT_NAME/FOLDER_NAME.
     */
    public String getFullName() {
        return this.fullName;
    }

    public long getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAbonniert() {
        return this.abonniert;
    }

    public boolean isSendFolder() {
        return this.sendFolder;
    }

    public void setAbonniert(final boolean abo) {
        this.abonniert = abo;
    }

    public void setAccountID(final long accountID) {
        this.accountID = accountID;
    }

    /**
     * Setzt den vollen Hierarchie-Namen, z.B. PARENT_NAME/FOLDER_NAME.
     */
    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public void setID(final long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;

        Predicate<String> predicate = "send"::equals;
        predicate = predicate.or("sent"::equals);
        predicate = predicate.or(n -> n.startsWith("gesendet"));

        this.sendFolder = predicate.test(name.toLowerCase());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MailFolder [fullName=").append(getFullName());
        builder.append("]");

        return builder.toString();
    }
}
