// Created: 30.05.2016
package de.freese.pim.core.model.addressbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity für einen Kontakt.
 *
 * @author Thomas Freese
 */
public class Kontakt implements Comparable<Kontakt> {
    private final List<KontaktAttribut> attribute = new ArrayList<>();

    private long id;
    private String nachname;
    private String vorname;

    public Kontakt() {
        super();
    }

    public Kontakt(final long id, final String nachname, final String vorname) {
        super();

        this.id = id;
        this.nachname = nachname;
        this.vorname = vorname;

    }

    public void addAttribut(final String attribut, final String wert) {
        final KontaktAttribut ka = new KontaktAttribut();
        ka.setKontaktID(getID());
        ka.setAttribut(attribut);
        ka.setWert(wert);

        addAttribut(ka);
    }

    // public void addPropertyChangeListener(final PropertyChangeListener listener) {
    // pcs.addPropertyChangeListener(listener);
    // }

    @Override
    public int compareTo(final Kontakt k) {
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

        if (!(obj instanceof Kontakt other)) {
            return false;
        }

        return Objects.equals(attribute, other.attribute) && id == other.id && Objects.equals(nachname, other.nachname) && Objects.equals(vorname,
                other.vorname);
    }

    public List<KontaktAttribut> getAttribute() {
        return attribute;
    }

    public long getID() {
        return id;
    }

    public String getNachname() {
        return nachname;
    }

    public String getVorname() {
        return vorname;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute, id, nachname, vorname);
    }

    public void setID(final long id) {
        // Object old = getID();
        this.id = id;
        // pcs.firePropertyChange("id", old, getID());
    }

    // public void removePropertyChangeListener(final PropertyChangeListener listener) {
    // pcs.removePropertyChangeListener(listener);
    // }

    public void setNachname(final String nachname) {
        // Object old = getNachname();
        this.nachname = nachname;
        // pcs.firePropertyChange("nachname", old, getNachname());
    }

    public void setVorname(final String vorname) {
        // Object old = getVorname();
        this.vorname = vorname;
        // pcs.firePropertyChange("vorname", old, getVorname());
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Kontakt [attribute=").append(getAttribute());
        builder.append(", id=").append(getID());
        builder.append(", nachname=").append(getNachname());
        builder.append(", vorname=").append(getVorname());
        builder.append("]");

        return builder.toString();
    }

    private void addAttribut(final KontaktAttribut attribut) {
        if (getAttribute().contains(attribut)) {
            throw new RuntimeException("Attribut bereits vorhanden");
        }

        getAttribute().add(attribut);
    }
}
