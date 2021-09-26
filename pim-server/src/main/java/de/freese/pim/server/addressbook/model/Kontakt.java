// Created: 30.05.2016
package de.freese.pim.server.addressbook.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity für einen Kontakt.
 *
 * @author Thomas Freese
 */
public class Kontakt implements Comparable<Kontakt>
{
    /**
     *
     */
    private final List<KontaktAttribut> attribute = new ArrayList<>();
    /**
     *
     */
    private long id;
    /**
     *
     */
    private String nachname;
    // /**
    // *
    // */
    // private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    /**
     *
     */
    private String vorname;

    /**
     * Erzeugt eine neue Instanz von {@link Kontakt}
     */
    public Kontakt()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link Kontakt}
     *
     * @param id long
     * @param nachname String
     * @param vorname String
     */
    public Kontakt(final long id, final String nachname, final String vorname)
    {
        super();

        this.id = id;
        this.nachname = nachname;
        this.vorname = vorname;

    }

    /**
     * @param attribut {@link KontaktAttribut}
     */
    private void addAttribut(final KontaktAttribut attribut)
    {
        if (getAttribute().contains(attribut))
        {
            throw new RuntimeException("Attribut bereits vorhanden");
        }

        getAttribute().add(attribut);
    }

    // /**
    // * @param listener {@link PropertyChangeListener}
    // */
    // public void addPropertyChangeListener(final PropertyChangeListener listener)
    // {
    // this.pcs.addPropertyChangeListener(listener);
    // }

    /**
     * @param attribut String
     * @param wert String
     */
    public void addAttribut(final String attribut, final String wert)
    {
        KontaktAttribut ka = new KontaktAttribut();
        ka.setKontaktID(getID());
        ka.setAttribut(attribut);
        ka.setWert(wert);

        addAttribut(ka);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Kontakt k)
    {
        if (k == null)
        {
            return 0;
        }

        int comp = getNachname().compareTo(k.getNachname());

        if (comp == 0)
        {
            comp = getVorname().compareTo(k.getVorname());
        }

        return comp;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Kontakt other))
        {
            return false;
        }

        return Objects.equals(this.attribute, other.attribute) && (this.id == other.id) && Objects.equals(this.nachname, other.nachname)
                && Objects.equals(this.vorname, other.vorname);
    }

    /**
     * Liefert eine unmodifiable Liste.
     *
     * @return {@link List}
     */
    public List<KontaktAttribut> getAttribute()
    {
        return this.attribute;
    }

    /**
     * @return long
     */
    public long getID()
    {
        return this.id;
    }

    /**
     * @return String
     */
    public String getNachname()
    {
        return this.nachname;
    }

    /**
     * @return String
     */
    public String getVorname()
    {
        return this.vorname;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.attribute, this.id, this.nachname, this.vorname);
    }

    // /**
    // * @param listener {@link PropertyChangeListener}
    // */
    // public void removePropertyChangeListener(final PropertyChangeListener listener)
    // {
    // this.pcs.removePropertyChangeListener(listener);
    // }

    /**
     * @param id long
     */
    public void setID(final long id)
    {
        // Object old = getID();
        this.id = id;
        // this.pcs.firePropertyChange("id", old, getID());
    }

    /**
     * @param nachname String
     */
    public void setNachname(final String nachname)
    {
        // Object old = getNachname();
        this.nachname = nachname;
        // this.pcs.firePropertyChange("nachname", old, getNachname());
    }

    /**
     * @param vorname String
     */
    public void setVorname(final String vorname)
    {
        // Object old = getVorname();
        this.vorname = vorname;
        // this.pcs.firePropertyChange("vorname", old, getVorname());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Kontakt [attribute=").append(getAttribute());
        builder.append(", id=").append(getID());
        builder.append(", nachname=").append(getNachname());
        builder.append(", vorname=").append(getVorname());
        builder.append("]");

        return builder.toString();
    }
}
