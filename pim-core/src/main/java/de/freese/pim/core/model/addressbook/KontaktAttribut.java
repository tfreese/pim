// Created: 30.05.2016
package de.freese.pim.core.model.addressbook;

/**
 * Entity für einen Kontakt.
 *
 * @author Thomas Freese
 */
public class KontaktAttribut implements Comparable<KontaktAttribut>
{
    private String attribut;

    private long kontaktID;

    private String wert;

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final KontaktAttribut ka)
    {
        if (ka == null)
        {
            return 0;
        }

        int comp = getAttribut().compareTo(ka.getAttribut());

        if (comp == 0)
        {
            comp = getWert().compareTo(ka.getWert());
        }

        return comp;
    }

    public String getAttribut()
    {
        return this.attribut;
    }

    public long getKontaktID()
    {
        return this.kontaktID;
    }

    public String getWert()
    {
        return this.wert;
    }

    public void setAttribut(final String attribut)
    {
        this.attribut = attribut;
    }

    public void setKontaktID(final long kontaktID)
    {
        this.kontaktID = kontaktID;
    }

    public void setWert(final String wert)
    {
        this.wert = wert;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("KontaktAttribut [kontaktID=").append(getKontaktID());
        builder.append(", attribut=").append(getAttribut());
        builder.append(", wert=").append(getWert());
        builder.append("]");

        return builder.toString();
    }
}
