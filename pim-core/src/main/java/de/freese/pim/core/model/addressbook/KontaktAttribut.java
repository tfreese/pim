// Created: 30.05.2016
package de.freese.pim.core.model.addressbook;

import java.util.Objects;

/**
 * Entity für einen Kontakt.
 *
 * @author Thomas Freese
 */
public class KontaktAttribut implements Comparable<KontaktAttribut> {
    private String attribut;

    private long kontaktID;

    private String wert;

    @Override
    public int compareTo(final KontaktAttribut ka) {
        if (ka == null) {
            return 0;
        }

        int comp = getAttribut().compareTo(ka.getAttribut());

        if (comp == 0) {
            comp = getWert().compareTo(ka.getWert());
        }

        return comp;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final KontaktAttribut that)) {
            return false;
        }

        return kontaktID == that.kontaktID && Objects.equals(attribut, that.attribut) && Objects.equals(wert, that.wert);
    }

    public String getAttribut() {
        return attribut;
    }

    public long getKontaktID() {
        return kontaktID;
    }

    public String getWert() {
        return wert;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribut, kontaktID, wert);
    }

    public void setAttribut(final String attribut) {
        this.attribut = attribut;
    }

    public void setKontaktID(final long kontaktID) {
        this.kontaktID = kontaktID;
    }

    public void setWert(final String wert) {
        this.wert = wert;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("KontaktAttribut [kontaktID=").append(getKontaktID());
        builder.append(", attribut=").append(getAttribut());
        builder.append(", wert=").append(getWert());
        builder.append("]");

        return builder.toString();
    }
}
