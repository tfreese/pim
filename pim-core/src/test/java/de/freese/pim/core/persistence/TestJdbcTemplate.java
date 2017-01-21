// Created: 12.01.2017
package de.freese.pim.core.persistence;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import de.freese.pim.core.addressbook.TestAddressbookConfig;

/**
 * TestCase des eigenen {@link JdbcTemplate}.
 *
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJdbcTemplate
{
    /**
     * @author Thomas Freese
     */
    private static class Entity
    {
        /**
         *
         */
        private Map<String, Object> map = new HashMap<>();

        /**
         * Erzeugt eine neue Instanz von {@link Entity}
         *
         * @param userID String
         * @param id String
         * @param nachname String
         * @param vorname String
         */
        public Entity(final String userID, final String id, final String nachname, final String vorname)
        {
            super();

            this.map.put("USER_ID", userID);
            this.map.put("ID", id);
            this.map.put("NACHNAME", nachname);
            this.map.put("VORNAME", vorname);
        }

        /**
         * @return String
         */
        public String getID()
        {
            return (String) this.map.get("ID");
        }

        /**
         * @return String
         */
        public String getNachname()
        {
            return (String) this.map.get("NACHNAME");
        }

        /**
         * @return String
         */
        public String getUserID()
        {
            return (String) this.map.get("USER_ID");
        }

        /**
         * @return String
         */
        public String getVorname()
        {
            return (String) this.map.get("VORNAME");
        }
    }

    /**
     *
     */
    private static JdbcTemplate jdbcTemplate = null;

    /**
     *
     */
    @AfterClass
    public static void afterClass()
    {
        ((SingleConnectionDataSource) jdbcTemplate.getDataSource()).destroy();
    }

    /**
     *
     */
    @BeforeClass
    public static void beforeClass()
    {
        jdbcTemplate = new JdbcTemplate().setDataSource(new TestAddressbookConfig().dataSource());
    }

    /**
     * Erzeugt eine neue Instanz von {@link TestJdbcTemplate}
     */
    public TestJdbcTemplate()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test(expected = SQLException.class)
    public void test010InsertReadOnly() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO KONTAKT (user_id, id, nachname, vorname)");
        sql.append(" VALUES");
        sql.append(" ('TEST', next value for kontakt_seq, 'Freese', 'Thomas')");

        jdbcTemplate.update(sql.toString());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test011Insert() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO KONTAKT (user_id, id, nachname, vorname)");
        sql.append(" VALUES");
        sql.append(" ('TEST', next value for kontakt_seq, 'Freese', 'Thomas')");

        ConnectionHolder.set(jdbcTemplate.getDataSource().getConnection());
        ConnectionHolder.beginTX();

        try
        {
            int affectedRows = jdbcTemplate.update(sql.toString());

            ConnectionHolder.commitTX();

            Assert.assertEquals(1, affectedRows);
        }
        catch (Exception ex)
        {
            ConnectionHolder.rollbackTX();

            throw ex;
        }
        finally
        {
            ConnectionHolder.close();
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020QueryAsListReadOnly() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select user_id, id, nachname, vorname from KONTAKT");

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString());

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());

        // Keys
        Set<String> keys = results.get(0).keySet();
        Iterator<String> keyIterator = keys.iterator();

        Assert.assertEquals("USER_ID", keyIterator.next());
        Assert.assertEquals("ID", keyIterator.next());
        Assert.assertEquals("NACHNAME", keyIterator.next());
        Assert.assertEquals("VORNAME", keyIterator.next());

        // Values
        Map<String, Object> map = results.get(0);

        Assert.assertEquals("TEST", map.get("USER_ID"));
        Assert.assertEquals("1", map.get("ID").toString());
        Assert.assertEquals("Freese", map.get("NACHNAME"));
        Assert.assertEquals("Thomas", map.get("VORNAME"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test021QueryAsList() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select user_id, id, nachname, vorname from KONTAKT");

        ConnectionHolder.set(jdbcTemplate.getDataSource().getConnection());
        ConnectionHolder.beginTX();

        try
        {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString());

            ConnectionHolder.commitTX();

            Assert.assertNotNull(results);
            Assert.assertEquals(1, results.size());

            // Keys
            Set<String> keys = results.get(0).keySet();
            Iterator<String> keyIterator = keys.iterator();

            Assert.assertEquals("USER_ID", keyIterator.next());
            Assert.assertEquals("ID", keyIterator.next());
            Assert.assertEquals("NACHNAME", keyIterator.next());
            Assert.assertEquals("VORNAME", keyIterator.next());

            // Values
            Map<String, Object> map = results.get(0);

            Assert.assertEquals("TEST", map.get("USER_ID"));
            Assert.assertEquals("1", map.get("ID").toString());
            Assert.assertEquals("Freese", map.get("NACHNAME"));
            Assert.assertEquals("Thomas", map.get("VORNAME"));
        }
        catch (Exception ex)
        {
            ConnectionHolder.rollbackTX();

            throw ex;
        }
        finally
        {
            ConnectionHolder.close();
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test022QueryReadOnlyWithRowMapper() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select user_id, id, nachname, vorname from KONTAKT");

        List<Entity> results =
                jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new Entity(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());

        Entity entity = results.get(0);
        Assert.assertEquals("TEST", entity.getUserID());
        Assert.assertEquals("1", entity.getID());
        Assert.assertEquals("Freese", entity.getNachname());
        Assert.assertEquals("Thomas", entity.getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030InsertPrepared() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO KONTAKT (user_id, id, nachname, vorname)");
        sql.append(" VALUES");
        sql.append(" (?, ?, ?, ?)");

        ConnectionHolder.set(jdbcTemplate.getDataSource().getConnection());
        ConnectionHolder.beginTX();

        try
        {
            long newID = jdbcTemplate.query("call next value for kontakt_seq", rs -> {
                rs.next();

                return rs.getLong(1);
            });

            Assert.assertEquals(2L, newID);

            int affectedRows = jdbcTemplate.update(sql.toString(), ps -> {
                ps.setString(1, "TEST");
                ps.setLong(2, newID);
                ps.setString(3, "Freesee");
                ps.setString(4, "Thomass");
            });

            ConnectionHolder.commitTX();

            Assert.assertEquals(1, affectedRows);
        }
        catch (Exception ex)
        {
            ConnectionHolder.rollbackTX();

            throw ex;
        }
        finally
        {
            ConnectionHolder.close();
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test040QueryReadOnlyPrepared() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select user_id, id, nachname, vorname from KONTAKT");
        sql.append(" where lower(nachname) like ? or lower(vorname) like ?");
        sql.append(" order by id asc");

        List<Entity> results = jdbcTemplate.query(sql.toString(), ps -> {
            ps.setString(1, "%ree%");
            ps.setString(2, "%hom%");
        }, (rs, rowNum) -> new Entity(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));

        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());

        Entity entity = results.get(0);
        Assert.assertEquals("TEST", entity.getUserID());
        Assert.assertEquals("1", entity.getID());
        Assert.assertEquals("Freese", entity.getNachname());
        Assert.assertEquals("Thomas", entity.getVorname());

        entity = results.get(1);
        Assert.assertEquals("TEST", entity.getUserID());
        Assert.assertEquals("2", entity.getID());
        Assert.assertEquals("Freesee", entity.getNachname());
        Assert.assertEquals("Thomass", entity.getVorname());
    }
}
