/**
 * Created: 14.01.2017
 */

package de.freese.pim.core.mail.dao;

import javax.sql.DataSource;

import de.freese.pim.core.persistence.JdbcTemplate;

/**
 * Default-Implementierung für das Mail DAO.<br>
 *
 * @author Thomas Freese
 */
public class DefaultMailDAO extends AbstractMailDAO
{
    /**
     * Erstellt ein neues {@link DefaultMailDAO} Object.
     */
    public DefaultMailDAO()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link DefaultMailDAO}
     *
     * @param dataSource {@link DataSource}
     */
    public DefaultMailDAO(final DataSource dataSource)
    {
        super();

        setJdbcTemplate(new JdbcTemplate().setDataSource(dataSource));
    }
}
