// Created: 14.01.2017
package de.freese.pim.core.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jakarta.mail.internet.AddressException;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.freese.pim.core.mail.InternetAddress;
import de.freese.pim.core.mail.MailPort;
import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;
import de.freese.pim.core.utils.Crypt;
import de.freese.pim.core.utils.MailUtils;
import de.freese.pim.core.utils.Utils;

/**
 * Basis DAO-Implementierung für die Mailverwaltung.<br>
 * Das Connection- und Transaction-Handling muss ausserhalb erfolgen und ist hier nicht implementiert.
 *
 * @author Thomas Freese
 */
@Repository("mailDAO")
@Profile("!ClientREST")
public class DefaultMailDao extends AbstractDao implements MailDao {
    /**
     * @author Thomas Freese
     */
    private static final class MailAccountRowMapper implements RowMapper<MailAccount> {
        @Override
        public MailAccount mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final MailAccount account = new MailAccount();

            account.setID(rs.getLong("ID"));
            account.setMail(rs.getString("MAIL"));

            account.setImapHost(rs.getString("IMAP_HOST"));
            account.setImapPort(MailPort.findByPort(rs.getInt("IMAP_PORT")));
            account.setImapLegitimation(rs.getBoolean("IMAP_LEGITIMATION"));
            account.setSmtpHost(rs.getString("SMTP_HOST"));
            account.setSmtpPort(MailPort.findByPort(rs.getInt("SMTP_PORT")));
            account.setSmtpLegitimation(rs.getBoolean("SMTP_LEGITIMATION"));

            final String passwort = rs.getString("PASSWORT");

            try {
                final String decryptedPassword = Crypt.getUTF8Instance().decrypt(passwort);
                account.setPassword(decryptedPassword);
            }
            catch (Exception ex) {
                throw new SQLException(ex);
            }

            return account;
        }
    }

    /**
     * @author Thomas Freese
     */
    private static final class MailFolderRowMapper implements RowMapper<MailFolder> {
        @Override
        public MailFolder mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final MailFolder folder = new MailFolder();

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
    private static final class MailRowMapper implements RowMapper<Mail> {
        @Override
        public Mail mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Mail mail = new Mail();

            try {
                final InternetAddress fromAddress = Optional.ofNullable(parseInternetAddress(rs.getString("SENDER"))).map(f -> f[0]).orElse(null);

                Clob clob = rs.getClob("RECIPIENT_TO");
                String clobString = clobToString(clob);
                final InternetAddress[] toRecipient = parseInternetAddress(clobString);

                clob = rs.getClob("RECIPIENT_CC");
                clobString = clobToString(clob);
                final InternetAddress[] ccRecipient = parseInternetAddress(clobString);

                clob = rs.getClob("RECIPIENT_BCC");
                clobString = clobToString(clob);
                final InternetAddress[] bccRecipient = parseInternetAddress(clobString);

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
            catch (AddressException | IOException ex) {
                throw new SQLException(ex);
            }
        }

        private String clobToString(final Clob clob) throws IOException, SQLException {
            String clobString = null;

            // clobString = clob.getSubString(0,(int) clob.length());

            try (BufferedReader buffer = new BufferedReader(clob.getCharacterStream())) {
                clobString = buffer.lines().collect(Collectors.joining());
            }

            // StringBuilder sb = new StringBuilder();
            //
            // try (Reader reader = clob.getCharacterStream();
            // BufferedReader br = new BufferedReader(reader)) {
            // int b = 0;
            //
            // while (-1 != (b = br.read())) {
            // sb.append((char) b);
            // }
            // }

            // try (InputStream in = clob.getAsciiStream();
            // Reader read = new InputStreamReader(in);
            // StringWriter sw = new StringWriter()) {
            // int c = -1;
            //
            // while ((c = read.read()) != -1) {
            // sw.write(c);
            // }
            // }

            return clobString;
        }

        private InternetAddress[] parseInternetAddress(final String value) throws AddressException {
            if (value == null || value.isBlank()) {
                return null;
            }

            return MailUtils.map(jakarta.mail.internet.InternetAddress.parse(value));
        }
    }

    @Override
    public int deleteAccount(final long accountID) {
        final StringBuilder sql = new StringBuilder();
        sql.append("delete from MAILACCOUNT where ID = ?");

        return getJdbcTemplate().update(sql.toString(), accountID);
    }

    @Override
    public int deleteFolder(final long folderID) {
        final StringBuilder sql = new StringBuilder();
        sql.append("delete from MAILFOLDER where ID = ?");

        return getJdbcTemplate().update(sql.toString(), folderID);
    }

    @Override
    public int deleteFolders(final long accountID) {
        final StringBuilder sql = new StringBuilder();
        sql.append("delete from MAILFOLDER where ACCOUNT_ID = ?");

        return getJdbcTemplate().update(sql.toString(), accountID);
    }

    @Override
    public int deleteMail(final long folderID, final long uid) {
        final StringBuilder sql = new StringBuilder();
        sql.append("delete from MAIL where FOLDER_ID = ? and UID = ?");

        return getJdbcTemplate().update(sql.toString(), folderID, uid);
    }

    @Override
    public int deleteMails(final long folderID) {
        final StringBuilder sql = new StringBuilder();
        sql.append("delete from MAIL where FOLDER_ID = ?");

        return getJdbcTemplate().update(sql.toString(), folderID);
    }

    @Override
    public List<MailAccount> getMailAccounts() {
        final String userID = getUserID();

        final StringBuilder sql = new StringBuilder();
        sql.append("select * from MAILACCOUNT where USER_ID = ? order by MAIL asc");

        // accountList.addAll(getMailAccountsJSON());
        return getJdbcTemplate().query(sql.toString(), new MailAccountRowMapper(), userID);
    }

    // protected List<MailAccount> getMailAccountsJSON() {
    // ObjectMapper jsonMapper = new ObjectMapper();
    // jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    // jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    //
    // Path path = SettingService.getInstance().getHome().resolve(".mailaccounts");
    // List<MailAccount> accountList = new ArrayList<>();
    //
    // if (Files.exists(path)) {
    // try (InputStream is = Files.newInputStream(path))
    // {
    // // MailAccount mailAccount = jsonMapper.readValue(is, MailAccount.class);
    // // root.getChildren().add(new TreeItem<>(mailAccount));
    //
    // JavaType type = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, MailAccount.class);
    // accountList.addAll(jsonMapper.readValue(is, type));
    // }
    // }
    // else {
    // MailAccount account = new MailAccount();
    // account.setMail("commercial@freese-home.de");
    // account.setImapHost(MailProvider.EinsUndEins.getImapHost());
    // account.setSmtpHost(MailProvider.EinsUndEins.getSmtpHost());
    // accountList.add(account);
    //
    // try (OutputStream os = Files.newOutputStream(path)) {
    // jsonMapper.writer().writeValue(os, Arrays.asList(account));
    // }
    // }
    //
    // // IDs hart setzen
    // for (int i = 0; i < accountList.size(); i++) {
    // accountList.get(i).setID(i + 1);
    // }
    //
    // return accountList;
    // }

    @Override
    public List<MailFolder> getMailFolder(final long accountID) {
        final StringBuilder sql = new StringBuilder();
        sql.append("select * from MAILFOLDER where ACCOUNT_ID = ? order by FULLNAME asc");

        return getJdbcTemplate().query(sql.toString(), new MailFolderRowMapper(), accountID);
    }

    @Override
    public List<Mail> getMails(final long folderID) {
        final StringBuilder sql = new StringBuilder();
        sql.append("select * from MAIL where FOLDER_ID = ?");

        return getJdbcTemplate().query(sql.toString(), new MailRowMapper(), folderID);
    }

    @Override
    public int insertAccount(final MailAccount account) {
        final String userID = getUserID();
        final long id = getNextID("MAIL_SEQ");

        final String password = account.getPassword();
        final String encryptedPassword = Crypt.getUTF8Instance().encrypt(password);

        final StringBuilder sql = new StringBuilder();
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

        final int affectedRows = getJdbcTemplate().update(sql.toString(), ps -> {
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

    @Override
    public int[] insertFolder(final long accountID, final Collection<MailFolder> folders) {
        if (folders.isEmpty()) {
            return new int[0];
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("insert into MAILFOLDER");
        sb.append(" (");
        sb.append(" ID, ACCOUNT_ID, FULLNAME, NAME, ABONNIERT");
        sb.append(") values (");
        sb.append("?, ?, ?, ?, ?");
        sb.append(")");
        final String sql = sb.toString();

        final List<MailFolder> list = (List<MailFolder>) folders;

        return getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return list.size();
            }

            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                final long id = getNextID("MAIL_SEQ");

                final MailFolder mf = list.get(i);

                ps.setLong(1, id);
                ps.setLong(2, accountID);
                ps.setString(3, mf.getFullName());
                ps.setString(4, mf.getName());
                ps.setBoolean(5, mf.isAbonniert());

                mf.setAccountID(accountID);
                mf.setID(id);
            }
        });
    }

    @Override
    public int[] insertMail(final long folderID, final Collection<Mail> mails) {
        if (mails.isEmpty()) {
            return new int[0];
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("insert into MAIL");
        sb.append(" (");
        sb.append(" FOLDER_ID, UID, MSG_NUM, SENDER, RECIPIENT_TO, RECIPIENT_CC, RECIPIENT_BCC, RECEIVED_DATE, SEND_DATE, SUBJECT, SIZE, SEEN");
        sb.append(") values (");
        sb.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
        sb.append(")");
        final String sql = sb.toString();

        final int[][] affectedRows = getJdbcTemplate().batchUpdate(sql, mails, mails.size(), (ps, mail) -> {
            String from = null;

            if (mail.getFrom() != null) {
                try {
                    from = new jakarta.mail.internet.InternetAddress(mail.getFrom().getAddress(), mail.getFrom().getPersonal()).toUnicodeString();
                }
                catch (UnsupportedEncodingException ex) {
                    throw new SQLException(ex);
                }
            }

            final String to = Optional.ofNullable(mail.getTo()).map(InternetAddress::toString).orElse(null);
            final String cc = Optional.ofNullable(mail.getCc()).map(InternetAddress::toString).orElse(null);
            final String bcc = Optional.ofNullable(mail.getBcc()).map(InternetAddress::toString).orElse(null);
            final Timestamp receivedTimestamp = Optional.ofNullable(mail.getReceivedDate()).map(rd -> new Timestamp(rd.getTime())).orElse(null);
            final Timestamp sendTimestamp = Optional.ofNullable(mail.getSendDate()).map(rd -> new Timestamp(rd.getTime())).orElse(null);

            final Clob clobTo = ps.getConnection().createClob();
            clobTo.setString(1, Objects.toString(to, ""));

            final Clob clobCc = ps.getConnection().createClob();
            clobCc.setString(1, Objects.toString(cc, ""));

            final Clob clobBcc = ps.getConnection().createClob();
            clobBcc.setString(1, Objects.toString(bcc, ""));

            ps.setLong(1, folderID);
            ps.setLong(2, mail.getUID());
            ps.setInt(3, mail.getMsgNum());
            ps.setString(4, from);
            ps.setClob(5, clobTo);
            ps.setClob(6, clobCc);
            ps.setClob(7, clobBcc);
            ps.setTimestamp(8, receivedTimestamp);
            ps.setTimestamp(9, sendTimestamp);
            ps.setString(10, mail.getSubject());
            ps.setInt(11, mail.getSize());
            ps.setBoolean(12, mail.isSeen());
        });

        return Stream.of(affectedRows).flatMapToInt(IntStream::of).toArray();

        // }
        // catch (Exception ex) {
        // getLogger().debug("{}: folder={}, from={}, subject={}, receiveDate={}", ex.getMessage(), mail.getFolder().getFullName(),
        // mail.getFrom(),
        // mail.getSubject(), mail.getReceivedDate());
        //
        // throw ex;
        // }
        // }
    }

    @Override
    public int updateAccount(final MailAccount account) {
        final String password = account.getPassword();
        final String encryptedPassword = Crypt.getUTF8Instance().encrypt(password);

        final StringBuilder sql = new StringBuilder();
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

        return getJdbcTemplate().update(sql.toString(), ps -> {
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

    @Override
    public int updateFolder(final MailFolder folder) {
        final StringBuilder sql = new StringBuilder();
        sql.append("update MAILFOLDER");
        sql.append(" set");
        sql.append(" FULLNAME = ?");
        sql.append(", NAME = ?");
        sql.append(", ABONNIERT = ?");
        sql.append(" where id = ?");

        return getJdbcTemplate().update(sql.toString(), ps -> {
            ps.setString(1, folder.getFullName());
            ps.setString(2, folder.getName());
            ps.setBoolean(3, folder.isAbonniert());
            ps.setLong(4, folder.getID());
        });
    }

    @Override
    public int updateMail(final long folderID, final Mail mail) {
        final StringBuilder sql = new StringBuilder();
        sql.append("update MAIL");
        sql.append(" set");
        sql.append(" SEEN = ?");
        sql.append(" where");
        sql.append(" FOLDER_ID = ?");
        sql.append(" and UID = ?");

        return getJdbcTemplate().update(sql.toString(), ps -> {
            ps.setBoolean(1, mail.isSeen());
            ps.setLong(2, folderID);
            ps.setLong(3, mail.getUID());
        });
    }

    protected String getUserID() {
        return Utils.getSystemUserName();
    }
}
