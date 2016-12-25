// Created: 30.05.2016
package de.freese.pim.core.addressbook.model;

import java.io.Serializable;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Entity für einen Kontakt.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class KontaktAttribut implements Serializable, Comparable<KontaktAttribut>
{
    /**
     *
     */
    private static final long serialVersionUID = 1030551267798759976L;

    /**
    *
    */
    private final StringProperty attributProperty = new SimpleStringProperty(this, "attribut", null);

    /**
    *
    */
    private final LongProperty kontaktIDProperty = new SimpleLongProperty(this, "kontaktID", 0);

    /**
    *
    */
    private final StringProperty wertProperty = new SimpleStringProperty(this, "wert", null);

    /**
     * Erzeugt eine neue Instanz von {@link KontaktAttribut}
     */
    KontaktAttribut()
    {
        super();
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty attributProperty()
    {
        return this.attributProperty;
    }

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

        int comp = 0;

        if (comp == 0)
        {
            comp = getAttribut().compareTo(ka.getAttribut());
        }

        if (comp == 0)
        {
            comp = getWert().compareTo(ka.getWert());
        }

        return comp;
    }

    /**
     * @return String
     */
    public String getAttribut()
    {
        return this.attributProperty.get();
    }

    /**
     * @return long
     */
    public long getKontaktID()
    {
        return this.kontaktIDProperty.get();
    }

    /**
     * @return String
     */
    public String getWert()
    {
        return this.wertProperty.get();
    }

    /**
     * @return {@link LongProperty}
     */
    public LongProperty kontaktIDProperty()
    {
        return this.kontaktIDProperty;
    }

    /**
     * @param attribut String
     */
    void setAttribut(final String attribut)
    {
        this.attributProperty.set(attribut);
    }

    /**
     * @param kontaktID long
     */
    void setKontaktID(final long kontaktID)
    {
        this.kontaktIDProperty.set(kontaktID);
    }

    /**
     * @param wert String
     */
    void setWert(final String wert)
    {
        this.wertProperty.set(wert);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("KontaktAttribut [kontaktID=");
        builder.append(getKontaktID());
        builder.append(", attribut=");
        builder.append(getAttribut());
        builder.append(", wert=");
        builder.append(getWert());
        builder.append("]");

        return builder.toString();
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty wertProperty()
    {
        return this.wertProperty;
    }
}
