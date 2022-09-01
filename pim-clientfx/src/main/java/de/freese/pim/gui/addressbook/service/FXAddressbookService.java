// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import java.util.List;

import de.freese.pim.core.PIMException;
import de.freese.pim.gui.addressbook.model.FXKontakt;

/**
 * @author Thomas Freese
 */
public interface FXAddressbookService
{
    /**
     * Löscht einen Kontakt.
     *
     * @param id long
     *
     * @return int; affectedRows
     *
     * @throws PIMException Falls was schiefgeht.
     */
    int deleteKontakt(long id);

    /**
     * Liefert den oder die Kontakte mit den KontaktAttributen.
     *
     * @param ids long[]
     *
     * @return {@link List}
     *
     * @throws PIMException Falls was schiefgeht.
     */
    List<FXKontakt> getKontaktDetails(long... ids);

    /**
     * Anlegen eines neuen Kontakts.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param kontakt FXKontakt
     *
     * @throws PIMException Falls was schiefgeht.
     */
    void insertKontakt(FXKontakt kontakt);

    /**
     * Aktualisiert einen Kontakt.
     *
     * @param id long
     * @param nachname String
     * @param vorname String
     *
     * @return int; affectedRows
     *
     * @throws PIMException Falls was schiefgeht.
     */
    int updateKontakt(long id, String nachname, String vorname);
}
