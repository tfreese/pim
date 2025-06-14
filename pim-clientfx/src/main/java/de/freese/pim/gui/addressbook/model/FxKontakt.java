// Created: 30.05.2016
package de.freese.pim.gui.addressbook.model;

import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * FX-Bean für einen Kontakt.
 *
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FxKontakt implements Comparable<FxKontakt> {
    @JsonIgnore
    private final ObservableList<FxKontaktAttribut> attribute = FXCollections.observableArrayList();
    private final LongProperty idProperty = new SimpleLongProperty(this, "id", 0);
    private final StringProperty nachnameProperty = new SimpleStringProperty(this, "nachname", null);
    // private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final StringExpression toStringExpression;
    private final StringProperty vornameProperty = new SimpleStringProperty(this, "vorname", null);

    public FxKontakt() {
        this(null, null);
    }

    public FxKontakt(final String nachname, final String vorname) {
        super();

        nachnameProperty.set(nachname);
        vornameProperty.set(vorname);

        toStringExpression = Bindings.format("Kontakt [nachname=%s, vorname=%s]", nachnameProperty(), vornameProperty());
    }

    public void addAttribut(final String attribut, final String wert) {
        final FxKontaktAttribut ka = new FxKontaktAttribut();
        ka.setKontaktID(getID());
        ka.setAttribut(attribut);
        ka.setWert(wert);

        addAttribut(ka);
    }

    // public void addPropertyChangeListener(final PropertyChangeListener listener)
    // {
    // pcs.addPropertyChangeListener(listener);
    // }

    @Override
    public int compareTo(final FxKontakt k) {
        if (k == null) {
            return 0;
        }

        int comp = getNachname().compareTo(k.getNachname());

        if (comp == 0) {
            comp = getVorname().compareTo(k.getVorname());
        }

        return comp;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FxKontakt other)) {
            return false;
        }

        return Objects.equals(getAttribute(), other.getAttribute()) && Objects.equals(getID(), other.getID()) && Objects.equals(getNachname(),
                other.getNachname()) && Objects.equals(getVorname(), other.getVorname());
    }

    public ObservableList<FxKontaktAttribut> getAttribute() {
        return attribute;
    }

    public long getID() {
        return idProperty().get();
    }

    public String getNachname() {
        return nachnameProperty().get();
    }

    public String getVorname() {
        return vornameProperty().get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAttribute(), getID(), getNachname(), getVorname());
    }

    public LongProperty idProperty() {
        return idProperty;
    }

    public StringProperty nachnameProperty() {
        return nachnameProperty;
    }

    // public void removePropertyChangeListener(final PropertyChangeListener listener) {
    // pcs.removePropertyChangeListener(listener);
    // }

    public void setID(final long id) {
        // Object old = getID();
        idProperty().set(id);
        // pcs.firePropertyChange("id", old, getID());
    }

    public void setNachname(final String nachname) {
        // Object old = getNachname();
        nachnameProperty().set(nachname);
        // pcs.firePropertyChange("nachname", old, getNachname());
    }

    public void setVorname(final String vorname) {
        // Object old = getVorname();
        vornameProperty().set(vorname);
        // pcs.firePropertyChange("vorname", old, getVorname());
    }

    @Override
    public String toString() {
        // final StringBuilder builder = new StringBuilder();
        // builder.append("Kontakt [attribute=").append(getAttribute());
        // builder.append(", id=").append(getID());
        // builder.append(", nachname=").append(getNachname());
        // builder.append(", vorname=").append(getVorname());
        // builder.append("]");
        //
        // return builder.toString();
        return toStringExpression.get();
    }

    public StringProperty vornameProperty() {
        return vornameProperty;
    }

    private void addAttribut(final FxKontaktAttribut attribut) {
        if (getAttribute().contains(attribut)) {
            throw new RuntimeException("Attribut bereits vorhanden");
        }

        getAttribute().add(attribut);
    }
}
