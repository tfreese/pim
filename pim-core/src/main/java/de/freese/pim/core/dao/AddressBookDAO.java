// Created: 30.05.2016
package de.freese.pim.core.dao;

import java.nio.file.Path;
import java.util.List;

import de.freese.pim.core.model.addressbook.Kontakt;
import de.freese.pim.core.model.addressbook.KontaktAttribut;

/**
 * @author Thomas Freese
 */
public interface AddressBookDAO
{
    /**
     * Erstellt ein Backup der DB im Verzeichnis.<br>
     * Funktioniert nicht im HSQLDB-Memory Mode.
     *
     * @param directory {@link Path}
     *
     * @return boolean; true = erfolgreich
     */
    boolean backup(Path directory);

    /**
     * Löscht ein {@link KontaktAttribut} von einem {@link Kontakt}.
     *
     * @param kontaktID long
     * @param attribut String
     *
     * @return int; affectedRows
     */
    int deleteAttribut(long kontaktID, String attribut);

    /**
     * Löscht einen {@link Kontakt}.
     *
     * @param id long
     *
     * @return int; affectedRows
     */
    int deleteKontakt(long id);

    /**
     * Liefert den oder die Kontakte mit den KontaktAttributen.
     *
     * @param ids long[]
     *
     * @return {@link List}
     */
    List<Kontakt> getKontaktDetails(long... ids);

    /**
     * Liefert alle Kontakte sortiert nach Vorname und Nachname ohne die KontaktAttribute.
     *
     * @return {@link List}
     */
    List<Kontakt> getKontakte();

    /**
     * Hinzufügen eines {@link KontaktAttribut}s zu einem {@link Kontakt}.
     *
     * @param kontaktID long
     * @param attribut String
     * @param wert String
     *
     * @return int; affectedRows
     */
    int insertAttribut(long kontaktID, String attribut, String wert);

    /**
     * Fügen einen neuem {@link Kontakt} hinzu und liefert dessen ID.
     *
     * @param nachname String
     * @param vorname String
     *
     * @return long; PrimaryKey
     */
    long insertKontakt(String nachname, String vorname);

    /**
     * Liefert alle Kontakte sortiert nach Vorname und Nachname mit den KontaktAttributen.
     *
     * @param name String
     *
     * @return {@link List}
     */
    List<Kontakt> searchKontakte(String name);

    /**
     * Aktualisiert ein {@link KontaktAttribut}.
     *
     * @param kontaktID long
     * @param attribut String
     * @param wert String
     *
     * @return int; affectedRows
     */
    int updateAttribut(long kontaktID, String attribut, String wert);

    /**
     * Aktualisiert einen {@link Kontakt}.
     *
     * @param id long
     * @param nachname String
     * @param vorname String
     *
     * @return int; affectedRows
     */
    int updateKontakt(long id, String nachname, String vorname);
}
