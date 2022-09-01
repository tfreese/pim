// Created: 30.05.2016
package de.freese.pim.core.dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import de.freese.pim.core.model.addressbook.Kontakt;
import de.freese.pim.core.utils.Utils;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Thomas Freese
 */
@Repository("addressBookDAO")
@Profile("!ClientREST")
public class DefaultAddressBookDAO extends AbstractDAO implements AddressBookDAO
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
         * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
         */
        @Override
        public List<Kontakt> extractData(final ResultSet rs) throws SQLException, DataAccessException
        {
            List<Kontakt> kontakte = new ArrayList<>();

            while (rs.next())
            {
                long id = rs.getLong("ID");

                final Kontakt kontakt;

                if ((kontakte.isEmpty()) || (id != kontakte.get(kontakte.size() - 1).getID()))
                {
                    kontakt = this.kontaktRowMapper.mapRow(rs, 0);
                    kontakte.add(kontakt);
                }
                else
                {
                    kontakt = kontakte.get(0);
                }

                String attribut = rs.getString("ATTRIBUT");

                if ((attribut != null) && !attribut.isBlank())
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
         * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
         */
        @Override
        public Kontakt mapRow(final ResultSet rs, final int rowNum) throws SQLException
        {
            Kontakt kontakt = new Kontakt();

            kontakt.setID(rs.getLong("ID"));
            kontakt.setNachname(rs.getString("NACHNAME"));
            kontakt.setVorname(rs.getString("VORNAME"));

            return kontakt;
        }
    }

    /**
     * @see de.freese.pim.core.dao.AddressBookDAO#backup(java.nio.file.Path)
     */
    @Override
    public boolean backup(final Path directory)
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
     * @see de.freese.pim.core.dao.AddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    public int deleteAttribut(final long kontaktID, final String attribut)
    {
        String sql = "delete from KONTAKT_ATTRIBUT where kontakt_id = ? and attribut = ?";

        return getJdbcTemplate().update(sql, kontaktID, attribut.toUpperCase());
    }

    /**
     * @see de.freese.pim.core.dao.AddressBookDAO#deleteKontakt(long)
     */
    @Override
    public int deleteKontakt(final long id)
    {
        String userID = getUserID();

        String sqlAttribut = "delete from KONTAKT_ATTRIBUT where kontakt_id = ?";
        String sqlKontakt = "delete from KONTAKT where user_id = ? and id = ?";

        getJdbcTemplate().update(sqlAttribut, id);

        return getJdbcTemplate().update(sqlKontakt, userID, id);
    }

    /**
     * @see de.freese.pim.core.dao.AddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    public List<Kontakt> getKontaktDetails(final long... ids)
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

        return getJdbcTemplate().query(sql.toString(), new KontaktDetailsResultSetExtractor(), userID);
    }

    /**
     * @see de.freese.pim.core.dao.AddressBookDAO#getKontakte()
     */
    @Override
    public List<Kontakt> getKontakte()
    {
        String userID = getUserID();

        StringBuilder sql = new StringBuilder();
        sql.append("select id, nachname, vorname from KONTAKT");
        sql.append(" where");
        sql.append(" user_id = ?");
        sql.append(" order by nachname asc, vorname asc");

        return getJdbcTemplate().query(sql.toString(), new KontaktRowMapper(), userID);
    }

    /**
     * @return String
     */
    protected String getUserID()
    {
        return Utils.getSystemUserName();
    }

    /**
     * @see de.freese.pim.core.dao.AddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    public int insertAttribut(final long kontaktID, final String attribut, final String wert)
    {
        String sql = "insert into KONTAKT_ATTRIBUT (kontakt_id, attribut, wert) values (?, ?, ?)";

        return getJdbcTemplate().update(sql, kontaktID, attribut == null ? null : attribut.toUpperCase(), wert);
    }

    /**
     * @see de.freese.pim.core.dao.AddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    public long insertKontakt(final String nachname, final String vorname)
    {
        String userID = getUserID();
        long id = getNextID("KONTAKT_SEQ");

        String sql = "insert into KONTAKT (id, user_id, nachname, vorname) values (?, ?, ?, ?)";

        getJdbcTemplate().update(sql, id, userID, nachname, vorname);

        return id;
    }

    /**
     * @see de.freese.pim.core.dao.AddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    public List<Kontakt> searchKontakte(final String name)
    {
        String userID = getUserID();

        StringBuilder sql = new StringBuilder();
        sql.append("select id, nachname, vorname, attribut, wert from V_ADDRESSBOOK");
        sql.append(" where");
        sql.append(" user_id = ?");
        sql.append(" and (lower(nachname) like ? or lower(vorname) like ?)");
        sql.append(" order by nachname asc, vorname asc");

        return getJdbcTemplate().query(sql.toString(), new KontaktDetailsResultSetExtractor(), userID, "%" + name.toLowerCase() + "%",
                "%" + name.toLowerCase() + "%");
    }

    /**
     * @see de.freese.pim.core.dao.AddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    public int updateAttribut(final long kontaktID, final String attribut, final String wert)
    {
        String sql = "update KONTAKT_ATTRIBUT set wert = ? where kontakt_id = ? and attribut = ?";

        return getJdbcTemplate().update(sql, wert, kontaktID, attribut.toUpperCase());
    }

    /**
     * @see de.freese.pim.core.dao.AddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    public int updateKontakt(final long id, final String nachname, final String vorname)
    {
        String userID = getUserID();

        String sql = "update KONTAKT set nachname = ?, vorname = ? where user_id = ? and id = ?";

        return getJdbcTemplate().update(sql, nachname, vorname, userID, id);
    }
}
