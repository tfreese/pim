/**
 * Created: 14.01.2017
 */

package de.freese.pim.core.mail.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

import de.freese.pim.core.dao.AbstractDAO;
import de.freese.pim.core.jdbc.RowMapper;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.model.MailPort;
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
         * @see de.freese.pim.core.jdbc.RowMapper#map(java.sql.ResultSet, int)
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
         * @see de.freese.pim.core.jdbc.RowMapper#map(java.sql.ResultSet, int)
         */
        @Override
        public MailFolder map(final ResultSet rs, final int rowNum) throws SQLException
        {
            MailFolder folder = new MailFolder();

            folder.setID(rs.getLong("ID"));
            folder.setAccountID(rs.getLong("ACCOUNT_ID"));
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
         * @see de.freese.pim.core.jdbc.RowMapper#map(java.sql.ResultSet, int)
         */
        @Override
        public Mail map(final ResultSet rs, final int rowNum) throws SQLException
        {
            Mail mail = new Mail();

            try
            {
                InternetAddress fromAddress = Optional.ofNullable(parseInternetAddress(rs.getString("SENDER"))).map(f -> f[0]).orElse(null);

                Clob clob = rs.getClob("RECIPIENT_TO");
                String clobString = clobToString(clob);
                InternetAddress[] toRecipient = parseInternetAddress(clobString);

                clob = rs.getClob("RECIPIENT_CC");
                clobString = clobToString(clob);
                InternetAddress[] ccRecipient = parseInternetAddress(clobString);

                clob = rs.getClob("RECIPIENT_BCC");
                clobString = clobToString(clob);
                InternetAddress[] bccRecipient = parseInternetAddress(clobString);

                mail.setFolderID(rs.getLong("FOLDER_ID"));
                mail.setUID(rs.getLong("UID"));
                mail.setMsgNum(rs.getInt("MSG_NUM"));
                mail.setFrom(fromAddress);
                mail.setTo(toRecipient);
                mail.setCc(ccRecipient);
                mail.setBcc(bccRecipient);
                mail.setReceivedDate(rs.getTimestamp("RECEIVED_DATE"));
                mail.setSendDate(rs.getTimestamp("SEND_DATE"));
                mail.setSubject(rs.getString("SUBJECT"));
                mail.setSize(rs.getInt("SIZE"));
                mail.setSeen(rs.getBoolean("SEEN"));

                return mail;
            }
            catch (AddressException | IOException ex)
            {
                throw new SQLException(ex);
            }
        }

        /**
         * @param clob {@link Clob}
         * @return String
         * @throws SQLException Falls was schief geht.
         * @throws IOException Falls was schief geht.
         */
        private String clobToString(final Clob clob) throws IOException, SQLException
        {
            String clobString = null;

            // clobString = clob.getSubString(0,(int) clob.length());

            try (BufferedReader buffer = new BufferedReader(clob.getCharacterStream()))
            {
                clobString = buffer.lines().collect(Collectors.joining());
            }

            // StringBuilder sb = new StringBuilder();
            //
            // try (Reader reader = clob.getCharacterStream();
            // BufferedReader br = new BufferedReader(reader))
            // {
            // int b = 0;
            //
            // while (-1 != (b = br.read()))
            // {
            // sb.append((char) b);
            // }
            // }

            // try (InputStream in = clob.getAsciiStream();
            // Reader read = new InputStreamReader(in);
            // StringWriter sw = new StringWriter())
            // {
            // int c = -1;
            //
            // while ((c = read.read()) != -1)
            // {
            // sw.write(c);
            // }
            // }

            return clobString;
        }

        /**
         * @param value String
         * @return {@link InternetAddress}
         * @throws AddressException Falls was schief geht.
         */
        private InternetAddress[] parseInternetAddress(final String value) throws AddressException
        {
            if (StringUtils.isBlank(value))
            {
                return null;
            }

            InternetAddress[] recipients = null;

            if (StringUtils.isNotBlank(value))
            {
                recipients = InternetAddress.parse(value);
            }

            return recipients;
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
    public int deleteAccount(final long accountID) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from MAILACCOUNT where ID = ?");

        int affectedRows = getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, accountID);
        });

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#deleteFolder(long)
     */
    @Override
    public int deleteFolder(final long folderID) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from MAILFOLDER where ID = ?");

        int affectedRows = getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, folderID);
        });

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#deleteFolders(long)
     */
    @Override
    public int deleteFolders(final long accountID) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from MAILFOLDER where ACCOUNT_ID = ?");

        int affectedRows = getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, accountID);
        });

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#deleteMail(long, long)
     */
    @Override
    public int deleteMail(final long folderID, final long uid) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from MAIL where FOLDER_ID = ? and UID = ?");

        int affectedRows = getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, folderID);
            ps.setLong(2, uid);
        });

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#deleteMails(long)
     */
    @Override
    public int deleteMails(final long folderID) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from MAIL where FOLDER_ID = ?");

        int affectedRows = getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setLong(1, folderID);
        });

        return affectedRows;
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

    // /**
    // * Liefert alle MailAccounts aus der lokalen JSON-Datei.
    // *
    // * @return {@link List}
    // * @throws Exception Falls was schief geht.
    // */
    // protected List<MailAccount> getMailAccountsJSON() throws Exception
    // {
    // ObjectMapper jsonMapper = new ObjectMapper();
    // jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    // jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    //
    // Path path = SettingService.getInstance().getHome().resolve(".mailaccounts");
    // List<MailAccount> accountList = new ArrayList<>();
    //
    // if (Files.exists(path))
    // {
    // try (InputStream is = Files.newInputStream(path))
    // {
    // // MailAccount mailAccount = jsonMapper.readValue(is, MailAccount.class);
    // // root.getChildren().add(new TreeItem<>(mailAccount));
    //
    // JavaType type = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, MailAccount.class);
    // accountList.addAll(jsonMapper.readValue(is, type));
    // }
    // }
    // else
    // {
    // MailAccount account = new MailAccount();
    // account.setMail("commercial@freese-home.de");
    // account.setImapHost(MailProvider.EinsUndEins.getImapHost());
    // account.setSmtpHost(MailProvider.EinsUndEins.getSmtpHost());
    // accountList.add(account);
    //
    // try (OutputStream os = Files.newOutputStream(path))
    // {
    // jsonMapper.writer().writeValue(os, Arrays.asList(account));
    // }
    // }
    //
    // // IDs hart setzen
    // for (int i = 0; i < accountList.size(); i++)
    // {
    // accountList.get(i).setID(i + 1);
    // }
    //
    // return accountList;
    // }

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
    public int insertAccount(final MailAccount account) throws Exception
    {
        String userID = getUserID();
        long id = getJdbcTemplate().getNextID("MAIL_SEQ");

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

        int affectedRows = getJdbcTemplate().update(sql.toString(), ps ->
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

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#insertFolder(long, java.util.Collection)
     */
    @Override
    public int[] insertFolder(final long accountID, final Collection<MailFolder> folders) throws Exception
    {
        if (folders.size() == 0)
        {
            return new int[0];
        }

        StringBuilder sb = new StringBuilder();
        sb.append("insert into MAILFOLDER");
        sb.append(" (");
        sb.append(" ID, ACCOUNT_ID, FULLNAME, NAME, ABONNIERT");
        sb.append(") values (");
        sb.append("?, ?, ?, ?, ?");
        sb.append(")");
        String sql = sb.toString();

        int[] affectedRows = getJdbcTemplate().updateBatch(sql, folders, (ps, mf, sequenceProvider) ->
        {
            long id = sequenceProvider.getNextID("MAIL_SEQ");

            ps.setLong(1, id);
            ps.setLong(2, accountID);
            ps.setString(3, mf.getFullName());
            ps.setString(4, mf.getName());
            ps.setBoolean(5, mf.isAbonniert());

            mf.setID(id);
        });

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#insertMail(long, java.util.Collection)
     */
    @Override
    public int[] insertMail(final long folderID, final Collection<Mail> mails) throws Exception
    {
        if (mails.size() == 0)
        {
            return new int[0];
        }

        StringBuilder sb = new StringBuilder();
        sb.append("insert into MAIL");
        sb.append(" (");
        sb.append(
                " FOLDER_ID, UID, MSG_NUM, SENDER, RECIPIENT_TO, RECIPIENT_CC, RECIPIENT_BCC, RECEIVED_DATE, SEND_DATE, SUBJECT, SIZE, SEEN");
        sb.append(") values (");
        sb.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
        sb.append(")");
        String sql = sb.toString();

        int[] affectedRows = getJdbcTemplate().updateBatch(sql, mails, (ps, mail, sequenceProvider) ->
        {
            String from = Optional.ofNullable(mail.getFrom()).map(InternetAddress::toUnicodeString).orElse(null);
            String to = Optional.ofNullable(mail.getTo()).map(InternetAddress::toString).orElse(null);
            String cc = Optional.ofNullable(mail.getCc()).map(InternetAddress::toString).orElse(null);
            String bcc = Optional.ofNullable(mail.getBcc()).map(InternetAddress::toString).orElse(null);
            Date reveicedDate = Optional.ofNullable(mail.getReceivedDate()).map(rd -> new Date(rd.getTime())).orElse(null);
            Date sendDate = Optional.ofNullable(mail.getSendDate()).map(rd -> new Date(rd.getTime())).orElse(null);

            Clob clobTo = ps.getConnection().createClob();
            clobTo.setString(1, StringUtils.defaultString(to, ""));

            Clob clobCc = ps.getConnection().createClob();
            clobCc.setString(1, StringUtils.defaultString(cc, ""));

            Clob clobBcc = ps.getConnection().createClob();
            clobBcc.setString(1, StringUtils.defaultString(bcc, ""));

            ps.setLong(1, folderID);
            ps.setLong(2, mail.getUID());
            ps.setInt(3, mail.getMsgNum());
            ps.setString(4, from);
            ps.setClob(5, clobTo);
            ps.setClob(6, clobCc);
            ps.setClob(7, clobBcc);
            ps.setDate(8, reveicedDate);
            ps.setDate(9, sendDate);
            ps.setString(10, mail.getSubject());
            ps.setInt(11, mail.getSize());
            ps.setBoolean(12, mail.isSeen());
        });

        return affectedRows;

        // }
        // catch (Exception ex)
        // {
        // getLogger().debug("{}: folder={}, from={}, subject={}, receiveDate={}", ex.getMessage(), mail.getFolder().getFullName(),
        // mail.getFrom(),
        // mail.getSubject(), mail.getReceivedDate());
        //
        // throw ex;
        // }
        // }
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#updateAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    public int updateAccount(final MailAccount account) throws Exception
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

        int affectedRows = getJdbcTemplate().update(sql.toString(), ps ->
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

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#updateFolder(de.freese.pim.core.mail.model.MailFolder)
     */
    @Override
    public int updateFolder(final MailFolder folder) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("update MAILFOLDER");
        sql.append(" set");
        sql.append(" FULLNAME = ?");
        sql.append(", NAME = ?");
        sql.append(", ABONNIERT = ?");
        sql.append(" where id = ?");

        int affectedRows = getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setString(1, folder.getFullName());
            ps.setString(2, folder.getName());
            ps.setBoolean(3, folder.isAbonniert());
            ps.setLong(4, folder.getID());
        });

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#updateMail(long, de.freese.pim.core.mail.model.Mail)
     */
    @Override
    public int updateMail(final long folderID, final Mail mail) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("update MAIL");
        sql.append(" set");
        sql.append(" SEEN = ?");
        sql.append(" where");
        sql.append(" FOLDER_ID = ?");
        sql.append(" and UID = ?");

        int affectedRows = getJdbcTemplate().update(sql.toString(), ps ->
        {
            ps.setBoolean(1, mail.isSeen());
            ps.setLong(2, folderID);
            ps.setLong(3, mail.getUID());
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
