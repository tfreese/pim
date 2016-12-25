// Created: 30.05.2016
package de.freese.pim.core.addressbook.model;

import java.io.Serializable;
import java.util.List;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Entity für einen Kontakt.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class Kontakt implements Serializable, Comparable<Kontakt>
{
    /**
     *
     */
    private static final long serialVersionUID = -4373195495051336639L;

    /**
     *
     */
    private final ObjectProperty<ObservableList<KontaktAttribut>> attributeProperty =
            new SimpleObjectProperty<>(this, "attribute", FXCollections.observableArrayList());

    /**
     *
     */
    private final LongProperty idProperty = new SimpleLongProperty(this, "id", 0);

    /**
     *
     */
    private final StringProperty nachnameProperty = new SimpleStringProperty(this, "nachname", null);

    // /**
    // *
    // */
    // private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     *
     */
    private final StringProperty vornameProperty = new SimpleStringProperty(this, "vorname", null);

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

        this.idProperty.set(id);
        this.nachnameProperty.set(nachname);
        this.vornameProperty.set(vorname);

        // _toString.bind(format("{ %s, %s }", oneProperty(), twoProperty()));
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

        this.attributeProperty.get().add(attribut);
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
     * @return {@link ObjectProperty}
     */
    public ObjectProperty<ObservableList<KontaktAttribut>> attributeProperty()
    {
        return this.attributeProperty;
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

        int comp = 0;

        if (comp == 0)
        {
            comp = getNachname().compareTo(k.getNachname());
        }

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

        if (obj == null)
        {
            return false;
        }

        if (!(obj instanceof Kontakt))
        {
            return false;
        }

        Kontakt other = (Kontakt) obj;

        if (getID() != getID())
        {
            return false;
        }

        if (getNachname() == null)
        {
            if (other.getNachname() != null)
            {
                return false;
            }
        }
        else if (!getNachname().equals(other.getNachname()))
        {
            return false;
        }

        if (getVorname() == null)
        {
            if (getVorname() != null)
            {
                return false;
            }
        }
        else if (!getVorname().equals(getVorname()))
        {
            return false;
        }

        return true;
    }

    /**
     * Liefert eine unmodifiable Liste.
     *
     * @return {@link List}
     */
    public List<KontaktAttribut> getAttribute()
    {
        return this.attributeProperty.get();
    }

    /**
     * @return long
     */
    public long getID()
    {
        return this.idProperty.get();
    }

    /**
     * @return String
     */
    public String getNachname()
    {
        return this.nachnameProperty.get();
    }

    /**
     * @return String
     */
    public String getVorname()
    {
        return this.vornameProperty.get();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = (prime * result) + ((getAttribute() == null) ? 0 : getAttribute().hashCode());
        result = (prime * result) + (int) (getID() ^ (getID() >>> 32));
        result = (prime * result) + ((getNachname() == null) ? 0 : getNachname().hashCode());
        result = (prime * result) + ((getVorname() == null) ? 0 : getVorname().hashCode());

        return result;
    }

    /**
     * @return {@link LongProperty}
     */
    public LongProperty idProperty()
    {
        return this.idProperty;
    }

    // /**
    // * @param listener {@link PropertyChangeListener}
    // */
    // public void removePropertyChangeListener(final PropertyChangeListener listener)
    // {
    // this.pcs.removePropertyChangeListener(listener);
    // }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty nachnameProperty()
    {
        return this.nachnameProperty;
    }

    /**
     * @param id long
     */
    public void setID(final long id)
    {
        // Object old = getID();
        this.idProperty.set(id);
        // this.pcs.firePropertyChange("id", old, getID());
    }

    /**
     * @param nachname String
     */
    public void setNachname(final String nachname)
    {
        // Object old = getNachname();
        this.nachnameProperty.set(nachname);
        // this.pcs.firePropertyChange("nachname", old, getNachname());
    }

    /**
     * @param vorname String
     */
    public void setVorname(final String vorname)
    {
        // Object old = getVorname();
        this.vornameProperty.set(vorname);
        // this.pcs.firePropertyChange("vorname", old, getVorname());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Kontakt [attribute=");
        builder.append(getAttribute());
        builder.append(", id=");
        builder.append(getID());
        builder.append(", nachname=");
        builder.append(getNachname());
        builder.append(", vorname=");
        builder.append(getVorname());
        builder.append("]");

        return builder.toString();
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty vornameProperty()
    {
        return this.vornameProperty;
    }
}
