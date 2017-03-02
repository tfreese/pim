/**
 * Created: 14.01.2017
 */

package de.freese.pim.server.mail.dao;

import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

/**
 * Default-Implementierung für das Mail DAO.<br>
 *
 * @author Thomas Freese
 */
@Repository("mailDAO")
public class DefaultMailDAO extends AbstractMailDAO
{
    /**
     * Erstellt ein neues {@link DefaultMailDAO} Object.
     */
    public DefaultMailDAO()
    {
        super();
    }

    // /**
    // * Ersetzt ggf. ein vorhandenes {@link JdbcTemplate}.
    // *
    // * @param dataSource {@link DataSource}
    // * @param maxConnections int
    // */
    // @Resource
    // public void setDataSource(final DataSource dataSource, @Value("${spring.datasource.tomcat.maxActive:1}") final int maxConnections)
    // {
    // Objects.requireNonNull(dataSource, "dataSource required");
    //
    // JdbcTemplate jdbcTemplate = new JdbcTemplate();
    // jdbcTemplate.setDataSource(dataSource);
    // jdbcTemplate.setConnectionSemaphore(new Semaphore(maxConnections));
    //
    // setJdbcTemplate(jdbcTemplate);
    // }

    /**
     * @see de.freese.pim.server.dao.AbstractDAO#setDataSource(javax.sql.DataSource)
     */
    @Override
    @Resource
    public void setDataSource(final DataSource dataSource)
    {
        super.setDataSource(dataSource);
    }
}
