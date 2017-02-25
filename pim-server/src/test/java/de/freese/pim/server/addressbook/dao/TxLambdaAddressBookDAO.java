// Created: 07.07.2016
package de.freese.pim.server.addressbook.dao;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import de.freese.pim.common.function.ExceptionalSupplier;
import de.freese.pim.common.utils.Utils;
import de.freese.pim.server.addressbook.model.Kontakt;
import de.freese.pim.server.jdbc.transaction.ConnectionHolder;

/**
 * DAO-Implementierung für das Addressbuch mit Connection- und Transaction-Steuerung über Lambdas.
 *
 * @author Thomas Freese
 */
public class TxLambdaAddressBookDAO extends AbstractAddressBookDAO
{
    /**
     * Erzeugt eine neue Instanz von {@link TxLambdaAddressBookDAO}
     */
    public TxLambdaAddressBookDAO()
    {
        super();
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractAddressBookDAO#backup(java.nio.file.Path)
     */
    @Override
    public boolean backup(final Path directory)
    {
        return execute(getDataSource()::getConnection, () -> super.backup(directory), false);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractAddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    public int deleteAttribut(final long kontakt_id, final String attribut)
    {
        return execute(getDataSource()::getConnection, () -> super.deleteAttribut(kontakt_id, attribut), true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractAddressBookDAO#deleteKontakt(long)
     */
    @Override
    public int deleteKontakt(final long id)
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
     */
    private <R> R execute(final ExceptionalSupplier<Connection, SQLException> connectionSupplier, final ExceptionalSupplier<R, Exception> resultSupplier,
                          final boolean transactional)
    {
        R result = null;

        try
        {
            try (Connection connection = connectionSupplier.get())
            {
                ConnectionHolder.set(connection);

                if (transactional)
                {
                    ConnectionHolder.beginTX();
                }

                result = resultSupplier.get();

                if (transactional)
                {
                    ConnectionHolder.commitTX();
                }
            }

            return result;
        }
        catch (Exception ex) // catch (Error | RuntimeException rex)
        {
            if (transactional)
            {
                Utils.executeSafely(() -> ConnectionHolder.rollbackTX());
            }

            throw new RuntimeException(Utils.getCause(ex));
        }
        finally
        {
            Utils.executeSafely(() -> ConnectionHolder.close());
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractAddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    public List<Kontakt> getKontaktDetails(final long...ids)
    {
        return execute(getDataSource()::getConnection, () -> super.getKontaktDetails(ids), false);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractAddressBookDAO#getKontakte()
     */
    @Override
    public List<Kontakt> getKontakte()
    {
        return execute(getDataSource()::getConnection, () -> super.getKontakte(), false);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractAddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    public int insertAttribut(final long kontakt_id, final String attribut, final String wert)
    {
        return execute(getDataSource()::getConnection, () -> super.insertAttribut(kontakt_id, attribut, wert), true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractAddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    public long insertKontakt(final String nachname, final String vorname)
    {
        return execute(getDataSource()::getConnection, () -> super.insertKontakt(nachname, vorname), true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractAddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    public List<Kontakt> searchKontakte(final String name)
    {
        return execute(getDataSource()::getConnection, () -> super.searchKontakte(name), false);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractAddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    public int updateAttribut(final long kontakt_id, final String attribut, final String wert)
    {
        return execute(getDataSource()::getConnection, () -> super.updateAttribut(kontakt_id, attribut, wert), true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractAddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    public int updateKontakt(final long id, final String nachname, final String vorname)
    {
        return execute(getDataSource()::getConnection, () -> super.updateKontakt(id, nachname, vorname), true);
    }
}
