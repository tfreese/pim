// Created: 30.05.2016
package de.freese.pim.server.addressbook.dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import de.freese.pim.common.utils.Utils;
import de.freese.pim.server.addressbook.model.Kontakt;
import de.freese.pim.server.dao.AbstractDAO;
import de.freese.pim.server.jdbc.ResultSetExtractor;
import de.freese.pim.server.jdbc.RowMapper;

/**
 * Basis DAO-Implementierung für das Addressbuch.<br>
 * Das Connection- und Transaction-Handling muss ausserhalb erfolgen und ist hier nicht implementiert.
 *
 * @author Thomas Freese
 */
public abstract class AbstractAddressBookDAO extends AbstractDAO<AddressBookDAO> implements AddressBookDAO
{
    /**
     * Mapped die Kontakte mit Attributen.
     *
     * @author Thomas Freese
     */
    private static class KontaktDetailsResultSetExtractor implements ResultSetExtractor<List<Kontakt>>
    {
        /**
         *
         */
        private final RowMapper<Kontakt> kontaktRowMapper = new KontaktRowMapper();

        /**
         * Erzeugt eine neue Instanz von {@link KontaktDetailsResultSetExtractor}
         */
        public KontaktDetailsResultSetExtractor()
        {
            super();
        }

        /**
         * @see de.freese.pim.server.jdbc.ResultSetExtractor#extract(java.sql.ResultSet)
         */
        @Override
        public List<Kontakt> extract(final ResultSet rs) throws SQLException
        {
            List<Kontakt> kontakte = new ArrayList<>();

            while (rs.next())
            {
                long id = rs.getLong("ID");

                Kontakt kontakt = null;

                if ((kontakte.size() == 0) || (id != kontakte.get(kontakte.size() - 1).getID()))
                {
                    kontakt = this.kontaktRowMapper.map(rs, 0);
                    kontakte.add(kontakt);
                }
                else
                {
                    kontakt = kontakte.get(0);
                }

                String attribut = rs.getString("ATTRIBUT");

                if (StringUtils.isNotBlank(attribut))
                {
                    kontakt.addAttribut(attribut, rs.getString("WERT"));
                }
            }

            return kontakte;
        }
    }

    /**
     * Mapped nur die Kontakte ohne Attribute.
     *
     * @author Thomas Freese
     */
    private static class KontaktRowMapper implements RowMapper<Kontakt>
    {
        /**
         * Erzeugt eine neue Instanz von {@link KontaktRowMapper}
         */
        public KontaktRowMapper()
        {
            super();
        }

        /**
         * @see de.freese.pim.server.jdbc.RowMapper#map(java.sql.ResultSet, int)
         */
        @Override
        public Kontakt map(final ResultSet rs, final int rowNum) throws SQLException
        {
            Kontakt kontakt = new Kontakt();

            kontakt.setID(rs.getLong("ID"));
            kontakt.setNachname(rs.getString("NACHNAME"));
            kontakt.setVorname(rs.getString("VORNAME"));

            return kontakt;
        }
    }

    /**
     * Erzeugt eine neue Instanz von {@link AbstractAddressBookDAO}
     */
    public AbstractAddressBookDAO()
    {
        super();
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#backup(java.nio.file.Path)
     */
    @Override
    public boolean backup(final Path directory) throws Exception
    {
        if (!Files.isWritable(directory))
        {
            throw new IllegalArgumentException("Pfad " + directory + " existiert nicht oder ist nicht beschreibbar");
        }

        if (!Files.isDirectory(directory))
        {
            throw new IllegalArgumentException("Pfad " + directory + " ist kein Verzeichnis");
        }

        String path = directory.toString();

        if (!path.endsWith("/"))
        {
            path += "/";
        }

        String sql = "BACKUP DATABASE TO '" + path + "' BLOCKING"; // AS FILES

        getJdbcTemplate().execute(sql);

        return true;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    public int deleteAttribut(final long kontaktID, final String attribut) throws Exception
    {
        String sql = "delete from KONTAKT_ATTRIBUT where kontakt_id = ? and attribut = ?";
        // int affectedRows = 0;
        //
        // Connection con = getConnection();
        //
        // try (PreparedStatement stmt = con.prepareStatement(sql))
        // {
        // stmt.setString(1, userID);
        // stmt.setLong(2, kontaktID);
        // stmt.setString(3, attribut.toUpperCase());
        //
        // affectedRows = stmt.executeUpdate();
        // }
        int affectedRows = getJdbcTemplate().update(sql, ps ->
        {
            ps.setLong(1, kontaktID);
            ps.setString(2, attribut.toUpperCase());
        });

        return affectedRows;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#deleteKontakt(long)
     */
    @Override
    public int deleteKontakt(final long id) throws Exception
    {
        String userID = getUserID();

        String sqlAttribut = "delete from KONTAKT_ATTRIBUT where kontakt_id = ?";
        String sqlKontakt = "delete from KONTAKT where user_id = ? and id = ?";
        // int affectedRows = 0;
        //
        // Connection con = getConnection();
        //
        // try (PreparedStatement stmtAttribut = con.prepareStatement(sqlAttribut);
        // PreparedStatement stmtKontakt = con.prepareStatement(sqlKontakt))
        // {
        // stmtAttribut.setString(1, userID);
        // stmtAttribut.setLong(2, id);
        // stmtAttribut.execute();
        //
        // stmtKontakt.setString(1, userID);
        // stmtKontakt.setLong(2, id);
        // affectedRows = stmtKontakt.executeUpdate();
        // }

        getJdbcTemplate().update(sqlAttribut, ps ->
        {
            ps.setLong(1, id);
        });

        int affectedRows = getJdbcTemplate().update(sqlKontakt, ps ->
        {
            ps.setString(1, userID);
            ps.setLong(2, id);
        });

        return affectedRows;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    public List<Kontakt> getKontaktDetails(final long... ids) throws Exception
    {
        String userID = getUserID();

        StringBuilder whereClause = new StringBuilder();

        if (ids.length == 1)
        {
            whereClause.append(" and id = ").append(ids[0]);
        }
        else if (ids.length > 1)
        {
            whereClause.append(" and id in (");

            IntStream.range(0, ids.length).forEach(index ->
            {
                whereClause.append(ids[index]);

                if (index < (ids.length - 1))
                {
                    whereClause.append(",");
                }
            });

            whereClause.append(")");
        }

        StringBuilder sql = new StringBuilder();
        sql.append("select id, nachname, vorname, attribut, wert");
        sql.append(" from V_ADDRESSBOOK");
        sql.append(" where user_id = ?");
        sql.append(" ").append(whereClause);
        sql.append(" order by attribut asc");

        // List<Kontakt> kontakte = null;
        // Connection con = getConnection();
        //
        // try (PreparedStatement stmt = con.prepareStatement(sql.toString()))
        // {
        // stmt.setString(1, userID);
        //
        // try (ResultSet rs = stmt.executeQuery())
        // {
        // kontakte = new KontaktDetailsResultSetExtractor().extract(rs);
        // }
        // }
        List<Kontakt> kontakte = getJdbcTemplate().query(sql.toString(), ps -> ps.setString(1, userID),
                new KontaktDetailsResultSetExtractor());

        return kontakte;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#getKontakte()
     */
    @Override
    public List<Kontakt> getKontakte() throws Exception
    {
        String userID = getUserID();

        StringBuilder sql = new StringBuilder();
        sql.append("select id, nachname, vorname from KONTAKT");
        sql.append(" where");
        sql.append(" user_id = ?");
        sql.append(" order by nachname asc, vorname asc");

        // List<Kontakt> kontakte = new ArrayList<>();
        // Connection con = getConnection();
        //
        // try (PreparedStatement stmt = con.prepareStatement(sql.toString()))
        // {
        // stmt.setString(1, userID);
        //
        // try (ResultSet rs = stmt.executeQuery())
        // {
        // while (rs.next())
        // {
        // Kontakt kontakt = new Kontakt();
        // kontakte.add(kontakt);
        //
        // kontakt.setID(rs.getLong("ID"));
        // kontakt.setNachname(rs.getString("NACHNAME"));
        // kontakt.setVorname(rs.getString("VORNAME"));
        // }
        // }
        // }

        List<Kontakt> kontakte = getJdbcTemplate().query(sql.toString(), ps -> ps.setString(1, userID), new KontaktRowMapper());

        return kontakte;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    public int insertAttribut(final long kontaktID, final String attribut, final String wert) throws Exception
    {
        String sql = "insert into KONTAKT_ATTRIBUT (kontakt_id, attribut, wert) values (?, ?, ?)";

        // int affectedRows = 0;
        //
        // Connection con = getConnection();
        //
        // try (PreparedStatement stmt = con.prepareStatement(sql))
        // {
        // stmt.setString(1, userID);
        // stmt.setLong(2, kontaktID);
        // stmt.setString(3, attribut == null ? null : attribut.toUpperCase());
        // stmt.setString(4, wert);
        //
        // affectedRows = stmt.executeUpdate();
        // }

        int affectedRows = getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, kontaktID);
            ps.setString(2, attribut == null ? null : attribut.toUpperCase());
            ps.setString(3, wert);
        });

        return affectedRows;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    public long insertKontakt(final String nachname, final String vorname) throws Exception
    {
        String userID = getUserID();
        long id = getJdbcTemplate().getNextID("KONTAKT_SEQ");

        String sql = "insert into KONTAKT (id, user_id, nachname, vorname) values (?, ?, ?, ?)";

        // long id = 0;
        //
        // Connection con = getConnection();
        //
        // // "call next value for KONTAKT_SEQ"
        // try (Statement seqStmt = con.createStatement();
        // ResultSet seqResultSet = seqStmt.executeQuery("select nvl(max(id), 0) + 1 from KONTAKT");
        // PreparedStatement insertStmt = con.prepareStatement(sql))
        // {
        // seqResultSet.next();
        // id = seqResultSet.getInt(1);
        //
        // insertStmt.setString(1, userID);
        // insertStmt.setLong(2, id);
        // insertStmt.setString(3, nachname);
        // insertStmt.setString(4, vorname);
        //
        // insertStmt.executeUpdate();
        // }

        getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, id);
            ps.setString(2, userID);
            ps.setString(3, nachname);
            ps.setString(4, vorname);
        });

        return id;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    public List<Kontakt> searchKontakte(final String name) throws Exception
    {
        String userID = getUserID();

        StringBuilder sql = new StringBuilder();
        sql.append("select id, nachname, vorname, attribut, wert from V_ADDRESSBOOK");
        sql.append(" where");
        sql.append(" user_id = ?");
        sql.append(" and (lower(nachname) like ? or lower(vorname) like ?)");
        sql.append(" order by nachname asc, vorname asc");

        // List<Kontakt> kontakte = null;
        // Connection con = getConnection();
        //
        // try (PreparedStatement stmt = con.prepareStatement(sql.toString()))
        // {
        // stmt.setString(1, userID);
        // stmt.setString(2, "%" + name.toLowerCase() + "%");
        // stmt.setString(3, "%" + name.toLowerCase() + "%");
        //
        // try (ResultSet rs = stmt.executeQuery())
        // {
        // kontakte = new KontaktDetailsResultSetExtractor().extract(rs);
        // }
        // }

        List<Kontakt> kontakte = getJdbcTemplate().query(sql.toString(), ps ->
        {
            ps.setString(1, userID);
            ps.setString(2, "%" + name.toLowerCase() + "%");
            ps.setString(3, "%" + name.toLowerCase() + "%");
        }, new KontaktDetailsResultSetExtractor());

        return kontakte;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    public int updateAttribut(final long kontaktID, final String attribut, final String wert) throws Exception
    {
        String sql = "update KONTAKT_ATTRIBUT set wert = ? where kontakt_id = ? and attribut = ?";
        // int affectedRows = 0;
        //
        // Connection con = getConnection();
        //
        // try (PreparedStatement stmt = con.prepareStatement(sql))
        // {
        // stmt.setString(1, wert);
        // stmt.setString(2, userID);
        // stmt.setLong(3, kontaktID);
        // stmt.setString(4, attribut.toUpperCase());
        //
        // affectedRows = stmt.executeUpdate();
        // }

        int affectedRows = getJdbcTemplate().update(sql, ps ->
        {
            ps.setString(1, wert);
            ps.setLong(2, kontaktID);
            ps.setString(3, attribut.toUpperCase());
        });

        return affectedRows;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    public int updateKontakt(final long id, final String nachname, final String vorname) throws Exception
    {
        String userID = getUserID();

        String sql = "update KONTAKT set nachname = ?, vorname = ? where user_id = ? and id = ?";
        // int affectedRows = 0;
        //
        // Connection con = getConnection();
        //
        // try (PreparedStatement stmt = con.prepareStatement(sql))
        // {
        // stmt.setString(1, nachname);
        // stmt.setString(2, vorname);
        // stmt.setString(3, userID);
        // stmt.setLong(4, id);
        //
        // affectedRows = stmt.executeUpdate();
        // }

        int affectedRows = getJdbcTemplate().update(sql, ps ->
        {
            ps.setString(1, nachname);
            ps.setString(2, vorname);
            ps.setString(3, userID);
            ps.setLong(4, id);
        });

        return affectedRows;
    }

    /**
     * @return String
     */
    protected String getUserID()
    {
        return Utils.getSystemUserName();
    }
}
