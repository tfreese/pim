/**
 * Created: 14.01.2017
 */

package de.freese.pim.core.mail.dao;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.freese.pim.core.dao.AbstractDAO;
import de.freese.pim.core.mail.MailProvider;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.persistence.RowMapper;
import de.freese.pim.core.service.SettingService;
import de.freese.pim.core.utils.Crypt;
import de.freese.pim.core.utils.Utils;

/**
 * Basis DAO-Implementierung für die Mailverwaltung.<br>
 * Das Connection- und Transaction-Handling muss ausserhalb erfolgen und ist hier nicht implementiert.
 *
 * @author Thomas Freese
 */
public class AbstractMailDAO extends AbstractDAO implements IMailDAO
{
    /**
     * @author Thomas Freese
     */
    private static class MailAccountRowMapper implements RowMapper<MailAccount>
    {
        /**
         * Erzeugt eine neue Instanz von {@link MailAccountRowMapper}
         */
        public MailAccountRowMapper()
        {
            super();
        }

        /**
         * @see de.freese.pim.core.persistence.RowMapper#map(java.sql.ResultSet, int)
         */
        @Override
        public MailAccount map(final ResultSet rs, final int rowNum) throws SQLException
        {
            MailAccount account = new MailAccount();

            account.setID(rs.getLong("ID"));
            account.setMail(rs.getString("MAIL"));

            account.setImapHost(rs.getString("IMAP_HOST"));
            account.setImapPort(rs.getInt("IMAP_PORT"));
            account.setImapLegitimation(rs.getBoolean("IMAP_LEGITIMATION"));
            account.setSmtpHost(rs.getString("SMTP_HOST"));
            account.setSmtpPort(rs.getInt("SMTP_PORT"));
            account.setSmtpLegitimation(rs.getBoolean("SMTP_LEGITIMATION"));

            String passwort = rs.getString("PASSWORT");

            try
            {
                String decryptedPassword = Crypt.getUTF8Instance().decrypt(passwort);
                account.setPassword(decryptedPassword);
            }
            catch (Exception ex)
            {
                if (ex instanceof SQLException)
                {
                    throw (SQLException) ex;
                }

                throw new SQLException(ex);
            }

            return account;
        }
    }

    /**
     * Erstellt ein neues {@link AbstractMailDAO} Object.
     */
    public AbstractMailDAO()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#getMailAccounts()
     */
    @Override
    public List<MailAccount> getMailAccounts() throws Exception
    {
        String userID = getUserID();

        StringBuilder sql = new StringBuilder();
        sql.append("select * from MAILACCOUNT where user_id = ? order by mail asc");

        List<MailAccount> accountList = getJdbcTemplate().query(sql.toString(), ps -> ps.setString(1, userID), new MailAccountRowMapper());

        // accountList.addAll(getMailAccountsJSON());

        return accountList;
    }

    /**
     * Liefert alle MailAccounts aus der lokalen JSON-Datei.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    protected List<MailAccount> getMailAccountsJSON() throws Exception
    {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        Path path = SettingService.getInstance().getHome().resolve(".mailaccounts");
        List<MailAccount> accountList = new ArrayList<>();

        if (Files.exists(path))
        {
            try (InputStream is = Files.newInputStream(path))
            {
                // MailAccount mailAccount = jsonMapper.readValue(is, MailAccount.class);
                // root.getChildren().add(new TreeItem<>(mailAccount));

                JavaType type = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, MailAccount.class);
                accountList.addAll(jsonMapper.readValue(is, type));
            }
        }
        else
        {
            MailAccount account = new MailAccount();
            account.setMail("commercial@freese-home.de");
            account.setImapHost(MailProvider.EinsUndEins.getImapHost());
            account.setSmtpHost(MailProvider.EinsUndEins.getSmtpHost());
            accountList.add(account);

            try (OutputStream os = Files.newOutputStream(path))
            {
                jsonMapper.writer().writeValue(os, Arrays.asList(account));
            }
        }

        // IDs hart setzen
        for (int i = 0; i < accountList.size(); i++)
        {
            accountList.get(i).setID(i + 1);
        }

        return accountList;
    }

    /**
     * @return String
     */
    protected String getUserID()
    {
        return Utils.getSystemUserName();
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#insert(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    public void insert(final MailAccount account) throws Exception
    {
        String userID = getUserID();
        long id = getNextID("MAIL_SEQ");

        String password = account.getPassword();
        String encryptedPassword = Crypt.getUTF8Instance().encrypt(password);

        StringBuilder sql = new StringBuilder();
        sql.append("insert into MAILACCOUNT");
        sql.append(" (");
        sql.append(" USER_ID, ID, MAIL, PASSWORT");
        sql.append(", IMAP_HOST, IMAP_PORT, IMAP_LEGITIMATION");
        sql.append(", SMTP_HOST, SMTP_PORT, SMTP_LEGITIMATION");
        sql.append(") values (");
        sql.append("?, ?, ?, ?");
        sql.append(", ?, ?, ?");
        sql.append(", ?, ?, ?");
        sql.append(")");

        getJdbcTemplate().update(sql.toString(), ps -> {
            ps.setString(1, userID);
            ps.setLong(2, id);
            ps.setString(3, account.getMail());
            ps.setString(4, encryptedPassword);
            ps.setString(5, account.getImapHost());
            ps.setInt(6, account.getImapPort());
            ps.setBoolean(7, account.isImapLegitimation());
            ps.setString(8, account.getSmtpHost());
            ps.setInt(9, account.getSmtpPort());
            ps.setBoolean(10, account.isSmtpLegitimation());
        });

        account.setID(id);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#update(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    public void update(final MailAccount account) throws Exception
    {
        String password = account.getPassword();
        String encryptedPassword = Crypt.getUTF8Instance().encrypt(password);

        StringBuilder sql = new StringBuilder();
        sql.append("update MAILACCOUNT");
        sql.append(" set");
        sql.append(" MAIL = ?");
        sql.append(", PASSWORT = ?");
        sql.append(", IMAP_HOST = ?");
        sql.append(", IMAP_PORT = ?");
        sql.append(", IMAP_LEGITIMATION = ?");
        sql.append(", SMTP_HOST = ?");
        sql.append(", SMTP_PORT = ?");
        sql.append(", SMTP_LEGITIMATION = ?");
        sql.append(" where id = ?");

        // int affectedRows =
        getJdbcTemplate().update(sql.toString(), ps -> {
            ps.setString(1, account.getMail());
            ps.setString(2, encryptedPassword);
            ps.setString(3, account.getImapHost());
            ps.setInt(4, account.getImapPort());
            ps.setBoolean(5, account.isImapLegitimation());
            ps.setString(6, account.getSmtpHost());
            ps.setInt(7, account.getSmtpPort());
            ps.setBoolean(8, account.isSmtpLegitimation());
            ps.setLong(9, account.getID());
        });
    }
}
