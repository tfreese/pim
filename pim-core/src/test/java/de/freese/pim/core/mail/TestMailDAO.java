/**
 * Created on 24.05.2016
 */
package de.freese.pim.core.mail;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
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
import de.freese.pim.core.mail.dao.DefaultMailDAO;
import de.freese.pim.core.mail.dao.IMailDAO;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.persistence.ConnectionHolder;
import de.freese.pim.core.persistence.JdbcTemplate;
import de.freese.pim.core.persistence.SimpleDataSource;

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

        TestMailDAO.mailDAO.insert(account);
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

        TestMailDAO.mailDAO.insert(account);

        Assert.assertEquals(2, account.getID());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020SelectAccount() throws Exception
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
    public void test030UpdateAccount() throws Exception
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

        TestMailDAO.mailDAO.update(account);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030UpdateAccountCheck() throws Exception
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
}
