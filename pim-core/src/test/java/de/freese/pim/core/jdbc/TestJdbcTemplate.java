// Created: 12.01.2017
package de.freese.pim.core.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import de.freese.pim.common.jdbc.JdbcTemplate;
import de.freese.pim.core.addressbook.TestAddressbookConfig;
import de.freese.pim.core.addressbook.model.Kontakt;

/**
 * TestCase des eigenen {@link JdbcTemplate}.
 *
 * @author Thomas Freese
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes =
{
        TestAddressbookConfig.class
})
@Transactional(transactionManager = "transactionManager")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext
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
    @Resource
    private DataSource dataSource = null;

    /**
     *
     */
    private JdbcTemplate jdbcTemplate = null;

    /**
     * Erzeugt eine neue Instanz von {@link TestJdbcTemplate}
     */
    public TestJdbcTemplate()
    {
        super();
    }

    /**
     *
     */
    @Before
    public void beforeMethod()
    {
        if (this.jdbcTemplate == null)
        {
            this.jdbcTemplate = new JdbcTemplate().dataSource(this.dataSource);
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test(expected = SQLException.class)
    @Transactional(readOnly = true)
    @Rollback
    public void test010InsertReadOnly() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO KONTAKT (user_id, id, nachname, vorname)");
        sql.append(" VALUES");
        sql.append(" ('TEST', next value for kontakt_seq, 'Freese', 'Thomas')");

        this.jdbcTemplate.update(sql.toString());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
    public void test011Insert() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO KONTAKT (user_id, id, nachname, vorname)");
        sql.append(" VALUES");
        sql.append(" ('TEST', next value for kontakt_seq, 'Freese', 'Thomas')");

        int affectedRows = this.jdbcTemplate.update(sql.toString());

        Assert.assertEquals(1, affectedRows);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
    public void test012InsertBatch() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO KONTAKT (user_id, id, nachname, vorname)");
        sql.append(" VALUES");
        sql.append(" (?, ?, ?, ?)");

        List<Kontakt> kontakte = new ArrayList<>();
        kontakte.add(new Kontakt(0, "Nachname1", "Vorname1"));
        kontakte.add(new Kontakt(0, "Nachname2", "Vorname2"));

        int[] affectedRows = this.jdbcTemplate.updateBatch(sql.toString(), kontakte, (ps, kontakt, sequenceProvider) -> {
            long id = sequenceProvider.getNextID("KONTAKT_SEQ");
            ps.setString(1, "TEST");
            ps.setLong(2, id);
            ps.setString(3, kontakt.getNachname());
            ps.setString(4, kontakt.getVorname());
        });

        Assert.assertEquals(2, affectedRows.length);
        Assert.assertEquals(1, affectedRows[0]);
        Assert.assertEquals(1, affectedRows[1]);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test020QueryAsListReadOnly() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select user_id, id, nachname, vorname from KONTAKT");

        List<Map<String, Object>> results = this.jdbcTemplate.queryForList(sql.toString());

        Assert.assertNotNull(results);
        Assert.assertEquals(3, results.size());

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
    @Transactional(readOnly = true)
    @Rollback
    public void test021QueryAsList() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select user_id, id, nachname, vorname from KONTAKT");

        List<Map<String, Object>> results = this.jdbcTemplate.queryForList(sql.toString());

        Assert.assertNotNull(results);
        Assert.assertEquals(3, results.size());

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
    @Transactional(readOnly = true)
    @Rollback
    public void test022QueryReadOnlyWithRowMapper() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select user_id, id, nachname, vorname from KONTAKT");

        List<Entity> results =
                this.jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new Entity(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));

        Assert.assertNotNull(results);
        Assert.assertEquals(3, results.size());

        Entity entity = results.get(0);
        Assert.assertEquals("TEST", entity.getUserID());
        Assert.assertEquals("1", entity.getID());
        Assert.assertEquals("Freese", entity.getNachname());
        Assert.assertEquals("Thomas", entity.getVorname());

        entity = results.get(1);
        Assert.assertEquals("TEST", entity.getUserID());
        Assert.assertEquals("2", entity.getID());
        Assert.assertEquals("Nachname1", entity.getNachname());
        Assert.assertEquals("Vorname1", entity.getVorname());

        entity = results.get(2);
        Assert.assertEquals("TEST", entity.getUserID());
        Assert.assertEquals("3", entity.getID());
        Assert.assertEquals("Nachname2", entity.getNachname());
        Assert.assertEquals("Vorname2", entity.getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
    public void test030InsertPrepared() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO KONTAKT (user_id, id, nachname, vorname)");
        sql.append(" VALUES");
        sql.append(" (?, ?, ?, ?)");

        // long id = jdbcTemplate.query("call next value for kontakt_seq", rs -> {
        // rs.next();
        //
        // return rs.getLong(1);
        // });
        long id = this.jdbcTemplate.getNextID("KONTAKT_SEQ");

        Assert.assertEquals(4L, id);

        int affectedRows = this.jdbcTemplate.update(sql.toString(), ps -> {
            ps.setString(1, "TEST");
            ps.setLong(2, id);
            ps.setString(3, "Freesee");
            ps.setString(4, "Thomass");
        });

        Assert.assertEquals(1, affectedRows);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test040QueryReadOnlyPrepared() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select user_id, id, nachname, vorname from KONTAKT");
        sql.append(" where lower(nachname) like ? or lower(vorname) like ?");
        sql.append(" order by id asc");

        List<Entity> results = this.jdbcTemplate.query(sql.toString(), ps -> {
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
        Assert.assertEquals("4", entity.getID());
        Assert.assertEquals("Freesee", entity.getNachname());
        Assert.assertEquals("Thomass", entity.getVorname());
    }
}
