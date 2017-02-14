// Created: 30.05.2016
package de.freese.pim.server.addressbook.dao;

import java.nio.file.Path;
import java.util.List;

import de.freese.pim.server.addressbook.model.Kontakt;
import de.freese.pim.server.addressbook.model.KontaktAttribut;

/**
 * DAO-Interface für das Addressbuch.
 *
 * @author Thomas Freese
 */
public interface AddressBookDAO
{
    /**
     * Erstellt ein Backup der DB im Verzeichnis.<br>
     * Funktioniert nicht im Memory Mode.
     *
     * @param directory {@link Path}
     * @return boolean; true = erfolgreich
     * @throws Exception Falls was schief geht.
     */
    public boolean backup(Path directory) throws Exception;

    /**
     * Löscht ein {@link KontaktAttribut} von einem {@link Kontakt}.
     *
     * @param kontaktID long
     * @param attribut String
     * @return boolean; true = erfolgreich, false = Datensatz nicht gefunden
     * @throws Exception Falls was schief geht.
     */
    public boolean deleteAttribut(long kontaktID, String attribut) throws Exception;

    /**
     * Löscht einen {@link Kontakt}.
     *
     * @param id long
     * @return boolean; true = erfolgreich, false = Datensatz nicht gefunden
     * @throws Exception Falls was schief geht.
     */
    public boolean deleteKontakt(long id) throws Exception;

    /**
     * Liefert den oder die Kontakte mit den KontaktAttributen.
     *
     * @param ids long[]
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Kontakt> getKontaktDetails(long... ids) throws Exception;

    /**
     * Liefert alle Kontakte sortiert nach Vorname und Nachname ohne die KontaktAttribute.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Kontakt> getKontakte() throws Exception;

    /**
     * Hinzufügen eines {@link KontaktAttribut}s zu einem {@link Kontakt}.
     *
     * @param kontaktID long
     * @param attribut String
     * @param wert String
     * @return boolean; true = erfolgreich, false = Datensatz nicht eingefügt
     * @throws Exception Falls was schief geht.
     */
    public boolean insertAttribut(long kontaktID, String attribut, String wert) throws Exception;

    /**
     * Fügen einen neuem {@link Kontakt} hinzu und liefert dessen ID.
     *
     * @param nachname String
     * @param vorname String
     * @return long
     * @throws Exception Falls was schief geht.
     */
    public long insertKontakt(String nachname, String vorname) throws Exception;

    /**
     * Liefert alle Kontakte sortiert nach Vorname und Nachname mit den KontaktAttributen.
     *
     * @param name String
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Kontakt> searchKontakte(String name) throws Exception;

    /**
     * Aktualisiert ein {@link KontaktAttribut}.
     *
     * @param kontaktID long
     * @param attribut String
     * @param wert String
     * @return boolean; true = erfolgreich, false = Datensatz nicht gefunden
     * @throws Exception Falls was schief geht.
     */
    public boolean updateAttribut(long kontaktID, String attribut, String wert) throws Exception;

    /**
     * Aktualisiert einen {@link Kontakt}.
     *
     * @param id long
     * @param nachname String
     * @param vorname String
     * @return boolean; true = erfolgreich, false = Datensatz nicht gefunden
     * @throws Exception Falls was schief geht.
     */
    public boolean updateKontakt(long id, String nachname, String vorname) throws Exception;
}
