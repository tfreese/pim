// Created: 11.01.2017
package de.freese.pim.core.service;

import java.nio.file.Path;
import java.util.List;

import de.freese.pim.core.model.addressbook.Kontakt;
import de.freese.pim.core.model.addressbook.KontaktAttribut;

/**
 * Interface für den Service des AddressBooks<br>
 *
 * @author Thomas Freese
 */
public interface AddressBookService {
    /**
     * Erstellt ein Backup der DB im Verzeichnis.<br>
     * Funktioniert nicht im HSQLDB-Memory Mode.
     *
     * @return boolean; true = erfolgreich
     */
    boolean backup(Path directory);

    /**
     * @return int; affectedRows
     */
    int deleteAttribut(long kontaktID, String attribut);

    /**
     * @return int; affectedRows
     */
    int deleteKontakt(long id);

    /**
     * Liefert den oder die Kontakte mit den KontaktAttributen.
     */
    List<Kontakt> getKontaktDetails(long... ids);

    /**
     * Liefert alle Kontakte sortiert nach Vorname und Nachname ohne die KontaktAttribute.
     */
    List<Kontakt> getKontakte();

    /**
     * Hinzufügen eines {@link KontaktAttribut}s zu einem {@link Kontakt}.
     *
     * @return int; affectedRows
     */
    int insertAttribut(long kontaktID, String attribut, String wert);

    /**
     * @return long; PrimaryKey
     */
    long insertKontakt(String nachname, String vorname);

    /**
     * Liefert alle Kontakte sortiert nach Vorname und Nachname mit den KontaktAttributen.
     */
    List<Kontakt> searchKontakte(String name);

    /**
     * @return int; affectedRows
     */
    int updateAttribut(long kontaktID, String attribut, String wert);

    /**
     * @return int; affectedRows
     */
    int updateKontakt(long id, String nachname, String vorname);
}
