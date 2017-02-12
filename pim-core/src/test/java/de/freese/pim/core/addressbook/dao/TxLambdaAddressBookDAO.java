// Created: 07.07.2016
package de.freese.pim.core.addressbook.dao;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;
import de.freese.pim.common.jdbc.tx.ConnectionHolder;
import de.freese.pim.common.utils.Utils;
import de.freese.pim.core.addressbook.model.Kontakt;

/**
 * DAO-Implementierung für das Addressbuch mit Connection- und Transaction-Steuerung über Lambdas.
 *
 * @author Thomas Freese
 */
public class TxLambdaAddressBookDAO extends AbstractAddressBookDAO
{
    /**
     * Interface eines {@link Supplier} mit einer Exception.<br>
     *
     * @author Thomas Freese
     * @param <R> Konkreter Ergebnistyp
     * @param <E> Konkreter @param <R> Konkreter Ergebnistyptyp
     */
    private interface ExceptionalSupplier<R, E extends Exception>
    {
        /**
         * @return Object
         * @throws Exception Falls was schief geht.
         */
        public R get() throws E;
    }

    /**
     * Erzeugt eine neue Instanz von {@link TxLambdaAddressBookDAO}
     */
    public TxLambdaAddressBookDAO()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#backup(java.nio.file.Path)
     */
    @Override
    public boolean backup(final Path directory) throws Exception
    {
        return execute(getDataSource()::getConnection, () -> super.backup(directory), false);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    public boolean deleteAttribut(final long kontakt_id, final String attribut) throws Exception
    {
        return execute(getDataSource()::getConnection, () -> super.deleteAttribut(kontakt_id, attribut), true);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#deleteKontakt(long)
     */
    @Override
    public boolean deleteKontakt(final long id) throws Exception
    {
        return execute(getDataSource()::getConnection, () -> super.deleteKontakt(id), true);

        // Using a method reference
        // Function<Long, Boolean> f = AbstractAddressBookDAO.super::deleteKontakt;
        // return execute(this::tryGetConnection, () -> f.apply(id), true);
    }

    /**
     * @param <R> Konkreter Return-Wert
     * @param connectionSupplier {@link ExceptionalSupplier}
     * @param resultSupplier {@link ExceptionalSupplier}
     * @param transactional boolean
     * @return Object
     * @throws Exception Falls was schief geht.
     */
    private <R> R execute(final ExceptionalSupplier<Connection, SQLException> connectionSupplier, final ExceptionalSupplier<R, Exception> resultSupplier,
                          final boolean transactional)
        throws Exception
    {
        R result = null;

        try (Connection connection = connectionSupplier.get())
        {
            ConnectionHolder.set(connection);

            if (transactional)
            {
                ConnectionHolder.beginTX();
            }

            try
            {
                result = resultSupplier.get();

                if (transactional)
                {
                    ConnectionHolder.commitTX();
                }
            }
            catch (Exception ex) // catch (Error | RuntimeException rex)
            {
                if (transactional)
                {
                    ConnectionHolder.rollbackTX();
                }

                throw Utils.getCause(ex);
            }
            finally
            {
                ConnectionHolder.close();
            }
        }

        return result;
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    public List<Kontakt> getKontaktDetails(final long...ids) throws Exception
    {
        return execute(getDataSource()::getConnection, () -> super.getKontaktDetails(ids), false);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#getKontakte()
     */
    @Override
    public List<Kontakt> getKontakte() throws Exception
    {
        return execute(getDataSource()::getConnection, () -> super.getKontakte(), false);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    public boolean insertAttribut(final long kontakt_id, final String attribut, final String wert) throws Exception
    {
        return execute(getDataSource()::getConnection, () -> super.insertAttribut(kontakt_id, attribut, wert), true);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    public long insertKontakt(final String nachname, final String vorname) throws Exception
    {
        return execute(getDataSource()::getConnection, () -> super.insertKontakt(nachname, vorname), true);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    public List<Kontakt> searchKontakte(final String name) throws Exception
    {
        return execute(getDataSource()::getConnection, () -> super.searchKontakte(name), false);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    public boolean updateAttribut(final long kontakt_id, final String attribut, final String wert) throws Exception
    {
        return execute(getDataSource()::getConnection, () -> super.updateAttribut(kontakt_id, attribut, wert), true);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    public boolean updateKontakt(final long id, final String nachname, final String vorname) throws Exception
    {
        return execute(getDataSource()::getConnection, () -> super.updateKontakt(id, nachname, vorname), true);
    }
}
