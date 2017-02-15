// Created: 30.05.2016
package de.freese.pim.gui.addressbook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * FX-Bean für einen Kontakt.
 *
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FXKontakt implements Comparable<FXKontakt>
{
    /**
     *
     */
    private final ObservableList<FXKontaktAttribut> attribute = FXCollections.observableArrayList();

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
    private final StringExpression toStringExpression;

    /**
     *
     */
    private final StringProperty vornameProperty = new SimpleStringProperty(this, "vorname", null);

    /**
     * Erzeugt eine neue Instanz von {@link FXKontakt}
     */
    public FXKontakt()
    {
        this(null, null);
    }

    /**
     * Erzeugt eine neue Instanz von {@link FXKontakt}
     *
     * @param nachname String
     * @param vorname String
     */
    public FXKontakt(final String nachname, final String vorname)
    {
        super();

        this.nachnameProperty.set(nachname);
        this.vornameProperty.set(vorname);

        this.toStringExpression = Bindings.format("Kontakt [nachname=%s, vorname=%s]", nachnameProperty(), vornameProperty());
    }

    /**
     * @param attribut String
     * @param wert String
     */
    public void addAttribut(final String attribut, final String wert)
    {
        FXKontaktAttribut ka = new FXKontaktAttribut();
        ka.setKontaktID(getID());
        ka.setAttribut(attribut);
        ka.setWert(wert);

        addAttribut(ka);
    }

    // /**
    // * @param listener {@link PropertyChangeListener}
    // */
    // public void addPropertyChangeListener(final PropertyChangeListener listener)
    // {
    // this.pcs.addPropertyChangeListener(listener);
    // }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final FXKontakt k)
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

        if (!(obj instanceof FXKontakt))
        {
            return false;
        }

        FXKontakt other = (FXKontakt) obj;

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
     * @return {@link ObservableList}
     */
    public ObservableList<FXKontaktAttribut> getAttribute()
    {
        return this.attribute;
    }

    /**
     * @return long
     */
    public long getID()
    {
        return idProperty().get();
    }

    /**
     * @return String
     */
    public String getNachname()
    {
        return nachnameProperty().get();
    }

    /**
     * @return String
     */
    public String getVorname()
    {
        return vornameProperty().get();
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

    /**
     * @return {@link StringProperty}
     */
    public StringProperty nachnameProperty()
    {
        return this.nachnameProperty;
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
        idProperty().set(id);
        // this.pcs.firePropertyChange("id", old, getID());
    }

    /**
     * @param nachname String
     */
    public void setNachname(final String nachname)
    {
        // Object old = getNachname();
        nachnameProperty().set(nachname);
        // this.pcs.firePropertyChange("nachname", old, getNachname());
    }

    /**
     * @param vorname String
     */
    public void setVorname(final String vorname)
    {
        // Object old = getVorname();
        vornameProperty().set(vorname);
        // this.pcs.firePropertyChange("vorname", old, getVorname());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // StringBuilder builder = new StringBuilder();
        // builder.append("Kontakt [attribute=").append(getAttribute());
        // builder.append(", id=").append(getID());
        // builder.append(", nachname=").append(getNachname());
        // builder.append(", vorname=").append(getVorname());
        // builder.append("]");
        //
        // return builder.toString();
        return this.toStringExpression.get();
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty vornameProperty()
    {
        return this.vornameProperty;
    }

    /**
     * @param attribut {@link FXKontaktAttribut}
     */
    private void addAttribut(final FXKontaktAttribut attribut)
    {
        if (getAttribute().contains(attribut))
        {
            throw new RuntimeException("Attribut bereits vorhanden");
        }

        getAttribute().add(attribut);
    }
}
