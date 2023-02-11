// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import java.util.List;

import de.freese.pim.gui.addressbook.model.FxKontakt;

/**
 * @author Thomas Freese
 */
public interface FxAddressbookService {
    /**
     * @return int; affectedRows
     */
    int deleteKontakt(long id);

    /**
     * Liefert den oder die Kontakte mit den KontaktAttributen.
     */
    List<FxKontakt> getKontaktDetails(long... ids);

    /**
     * Die ID wird dabei in die Entity gesetzt.
     */
    void insertKontakt(FxKontakt kontakt);

    /**
     * @return int; affectedRows
     */
    int updateKontakt(long id, String nachname, String vorname);
}
