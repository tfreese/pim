/**
 * Created: 26.06.2016
 */
package de.freese.pim.core.addressbook.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import de.freese.pim.core.addressbook.model.Kontakt;
import de.freese.pim.core.persistence.ConnectionHolder;

/**
 * DAO-Implementierung für das Addressbuch mit Connection- und Transaction-Steuerung über einen Proxy.<br>
 *
 * @author Thomas Freese
 */
public class TxProxyAddressBookDAO implements IAddressBookDAO
{
    /**
     * {@link InvocationHandler} für die Transaktionen.
     *
     * @author Thomas Freese
     */
    private static class TransactionInvocationHandler implements InvocationHandler
    {
        /**
         *
         */
        private final IAddressBookDAO dao;

        /**
         *
         */
        private final DataSource dataSource;

        /**
         * Erstellt ein neues {@link TransactionInvocationHandler} Object.
         *
         * @param dao {@link IAddressBookDAO}
         * @param dataSource {@link DataSource}
         */
        public TransactionInvocationHandler(final IAddressBookDAO dao, final DataSource dataSource)
        {
            super();

            this.dao = dao;
            this.dataSource = dataSource;
        }

        /**
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
         */
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
        {
            switch (method.getName())
            {
                case "equals":
                    return (proxy == args[0]);
                case "hashCode":
                    return System.identityHashCode(proxy);
                default:
                    break;
            }

            Object result = null;
            String methodName = method.getName();
            boolean transactional = methodName.startsWith("insert") || methodName.startsWith("update") || methodName.startsWith("delete");
            // boolean transactional = method.isAnnotationPresent(Transactional.class);

            try
            {
                @SuppressWarnings("resource")
                final Connection connection = this.dataSource.getConnection();
                ConnectionHolder.set(connection);

                if (transactional)
                {
                    ConnectionHolder.beginTX();
                }

                // System.out.println(methodName + "; Transactional = " + transactional);

                result = method.invoke(this.dao, args);

                if (transactional)
                {
                    ConnectionHolder.commitTX();
                }
            }
            catch (InvocationTargetException ex)
            {
                if (transactional)
                {
                    ConnectionHolder.rollbackTX();
                }

                throw ex.getTargetException();
            }
            finally
            {
                ConnectionHolder.closeAndRemove();
            }

            return result;
        }
    }

    /**
     *
     */
    private final IAddressBookDAO dao;

    /**
     * Erstellt ein neues {@link TxProxyAddressBookDAO} Object.
     *
     * @param dataSource {@link DataSource}
     */
    public TxProxyAddressBookDAO(final DataSource dataSource)
    {
        super();

        DefaultAddressBookDAO dao = new DefaultAddressBookDAO(dataSource);

        this.dao = (IAddressBookDAO) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]
        {
                IAddressBookDAO.class
        }, new TransactionInvocationHandler(dao, dataSource));
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#backup(java.nio.file.Path)
     */
    @Override
    public boolean backup(final Path directory) throws Exception
    {
        return this.dao.backup(directory);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    public boolean deleteAttribut(final long kontaktID, final String attribut) throws Exception
    {
        return this.dao.deleteAttribut(kontaktID, attribut);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#deleteKontakt(long)
     */
    @Override
    public boolean deleteKontakt(final long id) throws Exception
    {
        return this.dao.deleteKontakt(id);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    public List<Kontakt> getKontaktDetails(final long... ids) throws Exception
    {
        return this.dao.getKontaktDetails(ids);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#getKontakte()
     */
    @Override
    public List<Kontakt> getKontakte() throws Exception
    {
        return this.dao.getKontakte();
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    public boolean insertAttribut(final long kontaktID, final String attribut, final String wert) throws Exception
    {
        return this.dao.insertAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    public long insertKontakt(final String nachname, final String vorname) throws Exception
    {
        return this.dao.insertKontakt(nachname, vorname);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    public List<Kontakt> searchKontakte(final String name) throws Exception
    {
        return this.dao.searchKontakte(name);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    public boolean updateAttribut(final long kontaktID, final String attribut, final String wert) throws Exception
    {
        return this.dao.updateAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    public boolean updateKontakt(final long id, final String nachname, final String vorname) throws Exception
    {
        return this.dao.updateKontakt(id, nachname, vorname);
    }
}
