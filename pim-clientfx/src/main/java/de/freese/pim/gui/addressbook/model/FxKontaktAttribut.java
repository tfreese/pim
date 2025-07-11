// Created: 30.05.2016
package de.freese.pim.gui.addressbook.model;

import java.util.Objects;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * FX-Bean für einen Kontakt.
 *
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FxKontaktAttribut implements Comparable<FxKontaktAttribut> {
    private final StringProperty attributProperty = new SimpleStringProperty(this, "attribut", null);
    private final LongProperty kontaktIDProperty = new SimpleLongProperty(this, "kontaktID", 0);
    private final StringProperty wertProperty = new SimpleStringProperty(this, "wert", null);

    public StringProperty attributProperty() {
        return attributProperty;
    }

    @Override
    public int compareTo(final FxKontaktAttribut ka) {
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
        if (!(o instanceof final FxKontaktAttribut that)) {
            return false;
        }

        return Objects.equals(attributProperty, that.attributProperty) && Objects.equals(kontaktIDProperty, that.kontaktIDProperty)
                && Objects.equals(wertProperty, that.wertProperty);
    }

    public String getAttribut() {
        return attributProperty().get();
    }

    public long getKontaktID() {
        return kontaktIDProperty().get();
    }

    public String getWert() {
        return wertProperty().get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributProperty, kontaktIDProperty, wertProperty);
    }

    public LongProperty kontaktIDProperty() {
        return kontaktIDProperty;
    }

    public void setAttribut(final String attribut) {
        attributProperty().set(attribut);
    }

    public void setKontaktID(final long kontaktID) {
        kontaktIDProperty().set(kontaktID);
    }

    public void setWert(final String wert) {
        wertProperty().set(wert);
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

    public StringProperty wertProperty() {
        return wertProperty;
    }
}
