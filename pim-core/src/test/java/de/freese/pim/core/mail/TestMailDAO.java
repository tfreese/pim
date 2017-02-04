/**
 * Created on 24.05.2016
 */
package de.freese.pim.core.mail;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import javax.mail.internet.InternetAddress;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import de.freese.pim.core.jdbc.JdbcTemplate;
import de.freese.pim.core.jdbc.SimpleDataSource;
import de.freese.pim.core.jdbc.tx.ConnectionHolder;
import de.freese.pim.core.mail.dao.DefaultMailDAO;
import de.freese.pim.core.mail.dao.IMailDAO;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;

/**
 * TestCase für das {@link IMailDAO}.
 *
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMailDAO
{
    /**
     *
     */
    private static DataSource dataSource = null;

    /**
     *
     */
    private static JdbcTemplate jdbcTemplate = null;

    /**
     *
     */
    private static IMailDAO mailDAO = null;

    /**
     *
     */
    @AfterClass
    public static void afterClass()
    {
        ((SimpleDataSource) dataSource).destroy();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeClass
    public static void beforeClass() throws Exception
    {
        SimpleDataSource ds = new SimpleDataSource();
        ds.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        ds.setUrl("jdbc:hsqldb:mem:addressbook_" + System.currentTimeMillis());
        ds.setAutoCommit(false);
        // ds.setSuppressClose(true);
        dataSource = ds;

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/hsqldb/V3__pim_mail_schema.sql"));
        populator.execute(dataSource);

        jdbcTemplate = new JdbcTemplate().setDataSource(dataSource);
        DefaultMailDAO dao = new DefaultMailDAO();
        dao.setJdbcTemplate(jdbcTemplate);
        mailDAO = dao;
    }

    /**
     * Erstellt ein neues {@link TestMailDAO} Object.
     */
    public TestMailDAO()
    {
        super();
    }

    /**
     * Beendet die Transaction.
     *
     * @throws Exception Falls was schief geht.
     */
    @After
    public void after() throws Exception
    {
        ConnectionHolder.commitTX();
        ConnectionHolder.close();
    }

    /**
     * Startet die Transaction.
     *
     * @throws Exception Falls was schief geht.
     */
    @Before
    public void before() throws Exception
    {
        ConnectionHolder.set(dataSource.getConnection());
        ConnectionHolder.beginTX();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test010InsertAccountFail() throws Exception
    {
        MailAccount account = new MailAccount();

        TestMailDAO.mailDAO.insertAccount(account);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test011InsertAccount() throws Exception
    {
        MailAccount account = new MailAccount();
        account.setMail("a@b.de");
        account.setPassword("gehaim");
        account.setImapHost("imap-host");
        account.setImapPort(MailPort.IMAP);
        account.setImapLegitimation(true);
        account.setSmtpHost("smtp-host");
        account.setSmtpPort(MailPort.SMTP);
        account.setSmtpLegitimation(false);

        TestMailDAO.mailDAO.insertAccount(account);

        Assert.assertEquals(2, account.getID());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test012SelectAccount() throws Exception
    {
        List<MailAccount> accounts = TestMailDAO.mailDAO.getMailAccounts();

        Assert.assertNotNull(accounts);
        Assert.assertEquals(1, accounts.size());

        MailAccount account = accounts.get(0);
        Assert.assertEquals(2, account.getID());
        Assert.assertEquals("a@b.de", account.getMail());
        Assert.assertEquals("gehaim", account.getPassword());
        Assert.assertEquals("imap-host", account.getImapHost());
        Assert.assertEquals(MailPort.IMAP, account.getImapPort());
        Assert.assertEquals(true, account.isImapLegitimation());
        Assert.assertEquals("smtp-host", account.getSmtpHost());
        Assert.assertEquals(MailPort.SMTP, account.getSmtpPort());
        Assert.assertEquals(false, account.isSmtpLegitimation());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test013UpdateAccount() throws Exception
    {
        List<MailAccount> accounts = TestMailDAO.mailDAO.getMailAccounts();

        Assert.assertNotNull(accounts);
        Assert.assertEquals(1, accounts.size());

        MailAccount account = accounts.get(0);

        account.setMail("c@d.com");
        account.setPassword("gehaim2");
        account.setImapHost("host-imap");
        account.setImapPort(MailPort.IMAPS);
        account.setImapLegitimation(false);
        account.setSmtpHost("host-smtp");
        account.setSmtpPort(MailPort.SMTPS);
        account.setSmtpLegitimation(true);

        TestMailDAO.mailDAO.updateAccount(account);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test014UpdateAccountCheck() throws Exception
    {
        List<MailAccount> accounts = TestMailDAO.mailDAO.getMailAccounts();

        Assert.assertNotNull(accounts);
        Assert.assertEquals(1, accounts.size());

        MailAccount account = accounts.get(0);
        Assert.assertEquals(2, account.getID());
        Assert.assertEquals("c@d.com", account.getMail());
        Assert.assertEquals("gehaim2", account.getPassword());
        Assert.assertEquals("host-imap", account.getImapHost());
        Assert.assertEquals(MailPort.IMAPS, account.getImapPort());
        Assert.assertEquals(false, account.isImapLegitimation());
        Assert.assertEquals("host-smtp", account.getSmtpHost());
        Assert.assertEquals(MailPort.SMTPS, account.getSmtpPort());
        Assert.assertEquals(true, account.isSmtpLegitimation());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test020InsertFolderFail() throws Exception
    {
        MailFolder folder = new MailFolder();

        TestMailDAO.mailDAO.insertFolder(2, Arrays.asList(folder));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test021InsertFolder() throws Exception
    {
        MailFolder folder = new MailFolder();
        folder.setFullName("a/b");
        folder.setName("b");
        folder.setAbonniert(false);

        TestMailDAO.mailDAO.insertFolder(2, Arrays.asList(folder));

        Assert.assertEquals(4, folder.getID());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test022SelectFolder() throws Exception
    {
        List<MailFolder> folders = TestMailDAO.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(1, folders.size());

        MailFolder folder = folders.get(0);
        Assert.assertEquals(4, folder.getID());
        Assert.assertEquals("a/b", folder.getFullName());
        Assert.assertEquals("b", folder.getName());
        Assert.assertFalse(folder.isAbonniert());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test023UpdateFolder() throws Exception
    {
        List<MailFolder> folders = TestMailDAO.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(1, folders.size());

        MailFolder folder = folders.get(0);

        folder.setFullName("b/c");
        folder.setName("c");
        folder.setAbonniert(true);

        TestMailDAO.mailDAO.updateFolder(folder);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test024UpdateFolderCheck() throws Exception
    {
        List<MailFolder> folders = TestMailDAO.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(1, folders.size());

        MailFolder folder = folders.get(0);
        Assert.assertEquals(4, folder.getID());
        Assert.assertEquals("b/c", folder.getFullName());
        Assert.assertEquals("c", folder.getName());
        Assert.assertTrue(folder.isAbonniert());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test(expected = NullPointerException.class)
    public void test030InsertMailFail() throws Exception
    {
        Mail mail = new Mail();

        TestMailDAO.mailDAO.insertMail(4, Arrays.asList(mail));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test031InsertMail() throws Exception
    {
        Mail mail = new Mail();
        mail.setFrom(InternetAddress.parse("a@a.aa")[0]);
        mail.setMsgNum(1);
        mail.setReceivedDate(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 00).atZone(ZoneId.systemDefault()).toInstant()));
        mail.setSeen(false);
        mail.setSendDate(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 01).atZone(ZoneId.systemDefault()).toInstant()));
        mail.setSize(13);
        mail.setSubject("-TEST-");
        mail.setTo(InternetAddress.parse("b@b.bb")[0]);
        mail.setUID(2);

        TestMailDAO.mailDAO.insertMail(4, Arrays.asList(mail));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test032SelectMail() throws Exception
    {
        List<Mail> mails = TestMailDAO.mailDAO.getMails(4);

        Assert.assertNotNull(mails);
        Assert.assertEquals(1, mails.size());

        Mail mail = mails.get(0);
        Assert.assertEquals("a@a.aa", mail.getFrom().getAddress());
        Assert.assertEquals(1, mail.getMsgNum());
        Assert.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 00).atZone(ZoneId.systemDefault()).toInstant()), mail.getReceivedDate());
        Assert.assertFalse(mail.isSeen());
        Assert.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 01).atZone(ZoneId.systemDefault()).toInstant()), mail.getSendDate());
        Assert.assertEquals(13, mail.getSize());
        Assert.assertEquals("-TEST-", mail.getSubject());
        Assert.assertEquals("b@b.bb", mail.getTo().getAddress());
        Assert.assertEquals(2, mail.getUID());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test033UpdateMail() throws Exception
    {
        List<Mail> mails = TestMailDAO.mailDAO.getMails(4);

        Assert.assertNotNull(mails);
        Assert.assertEquals(1, mails.size());

        Mail mail = mails.get(0);

        MailFolder mf = new MailFolder();
        mf.setID(4);
        mail.setFolder(mf);

        mail.setSeen(true);
        mail.setMsgNum(99);

        // Nur SEEN-Flag sollte aktualisiert werden.
        TestMailDAO.mailDAO.updateMail(mail);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test034UpdateMailCheck() throws Exception
    {
        List<Mail> mails = TestMailDAO.mailDAO.getMails(4);

        Assert.assertNotNull(mails);
        Assert.assertEquals(1, mails.size());

        Mail mail = mails.get(0);
        Assert.assertEquals("a@a.aa", mail.getFrom().getAddress());
        Assert.assertEquals(1, mail.getMsgNum());
        Assert.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 00).atZone(ZoneId.systemDefault()).toInstant()), mail.getReceivedDate());
        Assert.assertTrue(mail.isSeen());
        Assert.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 01).atZone(ZoneId.systemDefault()).toInstant()), mail.getSendDate());
        Assert.assertEquals(13, mail.getSize());
        Assert.assertEquals("-TEST-", mail.getSubject());
        Assert.assertEquals("b@b.bb", mail.getTo().getAddress());
        Assert.assertEquals(2, mail.getUID());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test040DeleteMail() throws Exception
    {
        TestMailDAO.mailDAO.deleteMail(4, 2);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test041DeleteMailCheck() throws Exception
    {
        List<Mail> mails = TestMailDAO.mailDAO.getMails(4);

        Assert.assertNotNull(mails);
        Assert.assertEquals(0, mails.size());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test050DeleteFolder() throws Exception
    {
        TestMailDAO.mailDAO.deleteFolder(4);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test051DeleteFolderCheck() throws Exception
    {
        List<MailFolder> folders = TestMailDAO.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(0, folders.size());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test060DeleteAccount() throws Exception
    {
        TestMailDAO.mailDAO.deleteAccount(2);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test061DeleteAccountCheck() throws Exception
    {
        List<MailFolder> folders = TestMailDAO.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(0, folders.size());
    }
}
