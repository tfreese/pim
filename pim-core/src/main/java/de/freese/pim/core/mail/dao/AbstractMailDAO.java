/**
 * Created: 14.01.2017
 */

package de.freese.pim.core.mail.dao;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.freese.pim.core.dao.AbstractDAO;
import de.freese.pim.core.mail.MailPort;
import de.freese.pim.core.mail.MailProvider;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
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
public class AbstractMailDAO extends AbstractDAO<IMailDAO> implements IMailDAO
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
            account.setImapPort(MailPort.findByPort(rs.getInt("IMAP_PORT")));
            account.setImapLegitimation(rs.getBoolean("IMAP_LEGITIMATION"));
            account.setSmtpHost(rs.getString("SMTP_HOST"));
            account.setSmtpPort(MailPort.findByPort(rs.getInt("SMTP_PORT")));
            account.setSmtpLegitimation(rs.getBoolean("SMTP_LEGITIMATION"));

            String passwort = rs.getString("PASSWORT");

            try
            {
                String decryptedPassword = Crypt.getUTF8Instance().decrypt(passwort);
                account.setPassword(decryptedPassword);
            }
            catch (Exception ex)
            {
                throw new SQLException(ex);
            }

            return account;
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class MailFolderRowMapper implements RowMapper<MailFolder>
    {
        /**
         * Erzeugt eine neue Instanz von {@link MailFolderRowMapper}
         */
        public MailFolderRowMapper()
        {
            super();
        }

        /**
         * @see de.freese.pim.core.persistence.RowMapper#map(java.sql.ResultSet, int)
         */
        @Override
        public MailFolder map(final ResultSet rs, final int rowNum) throws SQLException
        {
            MailFolder folder = new MailFolder();

            folder.setID(rs.getLong("ID"));
            folder.setFullName(rs.getString("FULLNAME"));
            folder.setName(rs.getString("NAME"));
            folder.setAbonniert(rs.getBoolean("ABONNIERT"));

            return folder;
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class MailRowMapper implements RowMapper<Mail>
    {
        /**
         * Erzeugt eine neue Instanz von {@link MailRowMapper}
         */
        public MailRowMapper()
        {
            super();
        }

        /**
         * @see de.freese.pim.core.persistence.RowMapper#map(java.sql.ResultSet, int)
         */
        @Override
        public Mail map(final ResultSet rs, final int rowNum) throws SQLException
        {
            Mail mail = new Mail();

            try
            {
                mail.setUID(rs.getLong("UID"));
                mail.setMsgNum(rs.getInt("MSG_NUM"));
                mail.setFrom(InternetAddress.parse(rs.getString("SENDER"))[0]);
                mail.setTo(InternetAddress.parse(rs.getString("RECIPIENT"))[0]);
                mail.setReceivedDate(rs.getDate("RECEIVED_DATE"));
                mail.setSendDate(rs.getDate("SEND_DATE"));
                mail.setSeen(rs.getBoolean("SEEN"));

                return mail;
            }
            catch (AddressException aex)
            {
                throw new SQLException(aex);
            }
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
     * @see de.freese.pim.core.mail.dao.IMailDAO#deleteAccount(long)
     */
    @Override
    public void deleteAccount(final long accountID) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from MAILACCOUNT where ID = ?");

        getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, accountID);
        });
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#deleteFolder(long)
     */
    @Override
    public void deleteFolder(final long folderID) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from MAILFOLDER where ID = ?");

        getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, folderID);
        });
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#deleteMail(long, long)
     */
    @Override
    public void deleteMail(final long folderID, final long uid) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from MAIL where FOLDER_ID = ? and UID = ?");

        getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, folderID);
            ps.setLong(2, uid);
        });
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#getMailAccounts()
     */
    @Override
    public List<MailAccount> getMailAccounts() throws Exception
    {
        String userID = getUserID();

        StringBuilder sql = new StringBuilder();
        sql.append("select * from MAILACCOUNT where USER_ID = ? order by MAIL asc");

        List<MailAccount> accountList = getJdbcTemplate().query(sql.toString(), ps -> ps.setString(1, userID), new MailAccountRowMapper());

        // accountList.addAll(getMailAccountsJSON());

        return accountList;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#getMailFolder(long)
     */
    @Override
    public List<MailFolder> getMailFolder(final long accountID) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from MAILFOLDER where ACCOUNT_ID = ? order by FULLNAME asc");

        List<MailFolder> folderList = getJdbcTemplate().query(sql.toString(), ps -> ps.setLong(1, accountID), new MailFolderRowMapper());

        return folderList;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#getMails(long)
     */
    @Override
    public List<Mail> getMails(final long folderID) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from MAIL where FOLDER_ID = ?");

        List<Mail> mailList = getJdbcTemplate().query(sql.toString(), ps -> ps.setLong(1, folderID), new MailRowMapper());

        return mailList;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#insertAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    public void insertAccount(final MailAccount account) throws Exception
    {
        String userID = getUserID();
        long id = getNextID("MAIL_SEQ");

        String password = account.getPassword();
        String encryptedPassword = Crypt.getUTF8Instance().encrypt(password);

        StringBuilder sql = new StringBuilder();
        sql.append("insert into MAILACCOUNT");
        sql.append(" (");
        sql.append(" ID, USER_ID, MAIL, PASSWORT");
        sql.append(", IMAP_HOST, IMAP_PORT, IMAP_LEGITIMATION");
        sql.append(", SMTP_HOST, SMTP_PORT, SMTP_LEGITIMATION");
        sql.append(") values (");
        sql.append("?, ?, ?, ?");
        sql.append(", ?, ?, ?");
        sql.append(", ?, ?, ?");
        sql.append(")");

        getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, id);
            ps.setString(2, userID);
            ps.setString(3, account.getMail());
            ps.setString(4, encryptedPassword);
            ps.setString(5, account.getImapHost());
            ps.setInt(6, account.getImapPort().getPort());
            ps.setBoolean(7, account.isImapLegitimation());
            ps.setString(8, account.getSmtpHost());
            ps.setInt(9, account.getSmtpPort().getPort());
            ps.setBoolean(10, account.isSmtpLegitimation());
        });

        account.setID(id);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#insertFolder(de.freese.pim.core.mail.model.MailFolder, long)
     */
    @Override
    public void insertFolder(final MailFolder folder, final long accountID) throws Exception
    {
        long id = getNextID("MAIL_SEQ");

        StringBuilder sql = new StringBuilder();
        sql.append("insert into MAILFOLDER");
        sql.append(" (");
        sql.append(" ID, ACCOUNT_ID, FULLNAME, NAME, ABONNIERT");
        sql.append(") values (");
        sql.append("?, ?, ?, ?, ?");
        sql.append(")");

        getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, id);
            ps.setLong(2, accountID);
            ps.setString(3, folder.getFullName());
            ps.setString(4, folder.getName());
            ps.setBoolean(5, folder.isAbonniert());
        });

        folder.setID(id);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#insertMail(de.freese.pim.core.mail.model.Mail, long)
     */
    @Override
    public void insertMail(final Mail mail, final long folderID) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into MAIL");
        sql.append(" (");
        sql.append(" FOLDER_ID, UID, MSG_NUM, SENDER, RECIPIENT, RECEIVED_DATE, SEND_DATE, SEEN");
        sql.append(") values (");
        sql.append("?, ?, ?, ?, ?, ?, ?, ?");
        sql.append(")");

        getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, folderID);
            ps.setLong(2, mail.getUID());
            ps.setInt(3, mail.getMsgNum());
            ps.setString(4, mail.getFrom().toUnicodeString());
            ps.setString(5, mail.getTo().toUnicodeString());
            ps.setDate(6, new Date(mail.getReceivedDate().getTime()));
            ps.setDate(7, new Date(mail.getSendDate().getTime()));
            ps.setBoolean(8, mail.isSeen());
        });
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#updateAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    public void updateAccount(final MailAccount account) throws Exception
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
        getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setString(1, account.getMail());
            ps.setString(2, encryptedPassword);
            ps.setString(3, account.getImapHost());
            ps.setInt(4, account.getImapPort().getPort());
            ps.setBoolean(5, account.isImapLegitimation());
            ps.setString(6, account.getSmtpHost());
            ps.setInt(7, account.getSmtpPort().getPort());
            ps.setBoolean(8, account.isSmtpLegitimation());
            ps.setLong(9, account.getID());
        });
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#updateFolder(de.freese.pim.core.mail.model.MailFolder)
     */
    @Override
    public void updateFolder(final MailFolder folder) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("update MAILFOLDER");
        sql.append(" set");
        sql.append(" FULLNAME = ?");
        sql.append(", NAME = ?");
        sql.append(", ABONNIERT = ?");
        sql.append(" where id = ?");

        // int affectedRows =
        getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setString(1, folder.getFullName());
            ps.setString(2, folder.getName());
            ps.setBoolean(3, folder.isAbonniert());
            ps.setLong(4, folder.getID());
        });
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#updateMail(de.freese.pim.core.mail.model.Mail)
     */
    @Override
    public void updateMail(final Mail mail) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("update MAIL");
        sql.append(" set");
        sql.append(" SEEN = ?");
        sql.append(" where");
        sql.append(" FOLDER_ID = ?");
        sql.append(" and UID = ?");

        getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setBoolean(1, mail.isSeen());
            ps.setLong(2, mail.getFolder().getID());
            ps.setLong(3, mail.getUID());
        });
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
}
