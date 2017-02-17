// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import java.util.List;

import de.freese.pim.gui.addressbook.model.FXKontakt;

/**
 * Interface für einen JavaFX-AddressbookService.
 *
 * @author Thomas Freese
 */
public interface FXAddressbookService
{
    /**
     * Löscht einen Kontakt.
     *
     * @param id long
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int deleteKontakt(long id) throws Exception;

    /**
     * Liefert den oder die Kontakte mit den KontaktAttributen.
     *
     * @param ids long[]
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<FXKontakt> getKontaktDetails(long... ids) throws Exception;

    /**
     * Anlegen eines neuen Kontakts.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param kontakt FXKontakt
     * @throws Exception Falls was schief geht.
     */
    public void insertKontakt(FXKontakt kontakt) throws Exception;

    /**
     * Aktualisiert einen Kontakt.
     *
     * @param id long
     * @param nachname String
     * @param vorname String
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int updateKontakt(long id, String nachname, String vorname) throws Exception;
}