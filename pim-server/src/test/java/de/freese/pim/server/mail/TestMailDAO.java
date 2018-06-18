/**
 * Created on 24.05.2016
 */
package de.freese.pim.server.mail;

import java.sql.BatchUpdateException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import de.freese.pim.common.model.mail.InternetAddress;
import de.freese.pim.common.model.mail.MailPort;
import de.freese.pim.server.mail.dao.MailDAO;
import de.freese.pim.server.mail.model.Mail;
import de.freese.pim.server.mail.model.MailAccount;
import de.freese.pim.server.mail.model.MailFolder;

/**
 * TestCase für das {@link MailDAO}.
 *
 * @author Thomas Freese
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes =
{
        TestMailConfig.class
})
@Transactional(transactionManager = "transactionManager")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
// @DirtiesContext
public class TestMailDAO
{
    /**
     *
     */
    @Resource
    private MailDAO mailDAO = null;

    /**
     * Erstellt ein neues {@link TestMailDAO} Object.
     */
    public TestMailDAO()
    {
        super();
    }

    /**
     * @throws Throwable Falls was schief geht.
     */
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    @Rollback
    public void test010InsertAccountFail() throws Throwable
    {
        MailAccount account = new MailAccount();

        try
        {
            this.mailDAO.insertAccount(account);
        }
        catch (RuntimeException ex)
        {
            throw ex.getCause();
        }
    }

    /**
     */
    @Test
    @Commit
    public void test011InsertAccount()
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

        this.mailDAO.insertAccount(account);

        Assert.assertEquals(2, account.getID());
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test012SelectAccount()
    {
        List<MailAccount> accounts = this.mailDAO.getMailAccounts();

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
     */
    @Test
    @Commit
    public void test013UpdateAccount()
    {
        List<MailAccount> accounts = this.mailDAO.getMailAccounts();

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

        this.mailDAO.updateAccount(account);
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test014UpdateAccountCheck()
    {
        List<MailAccount> accounts = this.mailDAO.getMailAccounts();

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
     * @throws Throwable Falls was schief geht.
     */
    @Test(expected = BatchUpdateException.class)
    @Rollback
    public void test020InsertFolderFail() throws Throwable
    {
        MailFolder folder = new MailFolder();

        try
        {
            this.mailDAO.insertFolder(2, Arrays.asList(folder));
        }
        catch (RuntimeException ex)
        {
            throw ex.getCause();
        }
    }

    /**
     */
    @Test
    @Commit
    public void test021InsertFolder()
    {
        MailFolder folder = new MailFolder();
        folder.setFullName("a/b");
        folder.setName("b");
        folder.setAbonniert(false);

        this.mailDAO.insertFolder(2, Arrays.asList(folder));

        Assert.assertEquals(4, folder.getID());
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test022SelectFolder()
    {
        List<MailFolder> folders = this.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(1, folders.size());

        MailFolder folder = folders.get(0);
        Assert.assertEquals(4, folder.getID());
        Assert.assertEquals("a/b", folder.getFullName());
        Assert.assertEquals("b", folder.getName());
        Assert.assertFalse(folder.isAbonniert());
    }

    /**
     */
    @Test
    @Commit
    public void test023UpdateFolder()
    {
        List<MailFolder> folders = this.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(1, folders.size());

        MailFolder folder = folders.get(0);

        folder.setFullName("b/c");
        folder.setName("c");
        folder.setAbonniert(true);

        this.mailDAO.updateFolder(folder);
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test024UpdateFolderCheck()
    {
        List<MailFolder> folders = this.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(1, folders.size());

        MailFolder folder = folders.get(0);
        Assert.assertEquals(4, folder.getID());
        Assert.assertEquals("b/c", folder.getFullName());
        Assert.assertEquals("c", folder.getName());
        Assert.assertTrue(folder.isAbonniert());
    }

    /**
     * @throws Throwable Falls was schief geht.
     */
    @Test(expected = BatchUpdateException.class)
    @Rollback
    public void test030InsertMailFail() throws Throwable
    {
        Mail mail = new Mail();

        try
        {
            this.mailDAO.insertMail(4, Arrays.asList(mail));
        }
        catch (RuntimeException ex)
        {
            throw ex.getCause();
        }
    }

    /**
     */
    @Test
    @Commit
    public void test031InsertMail()
    {
        Mail mail = new Mail();
        mail.setFrom(new InternetAddress("a@a.aa"));
        mail.setMsgNum(1);
        mail.setReceivedDate(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 00).atZone(ZoneId.systemDefault()).toInstant()));
        mail.setSeen(false);
        mail.setSendDate(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 01).atZone(ZoneId.systemDefault()).toInstant()));
        mail.setSize(13);
        mail.setSubject("-TEST-");
        mail.setTo(new InternetAddress[]
        {
                new InternetAddress("b@b.bb")
        });
        mail.setCc(new InternetAddress[]
        {
                new InternetAddress("c@c.cc")
        });
        mail.setBcc(new InternetAddress[]
        {
                new InternetAddress("d@d.dd")
        });
        mail.setUID(2);

        this.mailDAO.insertMail(4, Arrays.asList(mail));
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test032SelectMail()
    {
        List<Mail> mails = this.mailDAO.getMails(4);

        Assert.assertNotNull(mails);
        Assert.assertEquals(1, mails.size());

        Mail mail = mails.get(0);
        Assert.assertEquals("a@a.aa", mail.getFrom().getAddress());
        Assert.assertEquals(1, mail.getMsgNum());
        Assert.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 00).atZone(ZoneId.systemDefault()).toInstant()),
                mail.getReceivedDate());
        Assert.assertFalse(mail.isSeen());
        Assert.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 01).atZone(ZoneId.systemDefault()).toInstant()),
                mail.getSendDate());
        Assert.assertEquals(13, mail.getSize());
        Assert.assertEquals("-TEST-", mail.getSubject());
        Assert.assertEquals("b@b.bb", mail.getTo()[0].getAddress());
        Assert.assertEquals("c@c.cc", mail.getCc()[0].getAddress());
        Assert.assertEquals("d@d.dd", mail.getBcc()[0].getAddress());
        Assert.assertEquals(2, mail.getUID());
    }

    /**
     */
    @Test
    @Commit
    public void test033UpdateMail()
    {
        List<Mail> mails = this.mailDAO.getMails(4);

        Assert.assertNotNull(mails);
        Assert.assertEquals(1, mails.size());

        Mail mail = mails.get(0);
        mail.setSeen(true);
        mail.setMsgNum(99);

        // Nur SEEN-Flag sollte aktualisiert werden.
        this.mailDAO.updateMail(4, mail);
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test034UpdateMailCheck()
    {
        List<Mail> mails = this.mailDAO.getMails(4);

        Assert.assertNotNull(mails);
        Assert.assertEquals(1, mails.size());

        Mail mail = mails.get(0);
        Assert.assertEquals("a@a.aa", mail.getFrom().getAddress());
        Assert.assertEquals(1, mail.getMsgNum());
        Assert.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 00).atZone(ZoneId.systemDefault()).toInstant()),
                mail.getReceivedDate());
        Assert.assertTrue(mail.isSeen());
        Assert.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 01).atZone(ZoneId.systemDefault()).toInstant()),
                mail.getSendDate());
        Assert.assertEquals(13, mail.getSize());
        Assert.assertEquals("-TEST-", mail.getSubject());
        Assert.assertEquals("b@b.bb", mail.getTo()[0].getAddress());
        Assert.assertEquals("c@c.cc", mail.getCc()[0].getAddress());
        Assert.assertEquals("d@d.dd", mail.getBcc()[0].getAddress());
        Assert.assertEquals(2, mail.getUID());
    }

    /**
     */
    @Test
    @Commit
    public void test040DeleteMail()
    {
        this.mailDAO.deleteMail(4, 2);
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test041DeleteMailCheck()
    {
        List<Mail> mails = this.mailDAO.getMails(4);

        Assert.assertNotNull(mails);
        Assert.assertEquals(0, mails.size());
    }

    /**
     */
    @Test
    @Commit
    public void test050DeleteFolder()
    {
        this.mailDAO.deleteFolder(4);
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test051DeleteFolderCheck()
    {
        List<MailFolder> folders = this.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(0, folders.size());
    }

    /**
     */
    @Test
    @Commit
    public void test060DeleteAccount()
    {
        this.mailDAO.deleteAccount(2);
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test061DeleteAccountCheck()
    {
        List<MailFolder> folders = this.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(0, folders.size());
    }
}
