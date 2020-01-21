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
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
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
// @ExtendWith(SpringExtension.class) // Ist bereits in SpringBootTest enthalten
@RunWith(SpringRunner.class)
@SpringBootTest(classes =
{
        TestMailConfig.class
})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@Transactional(transactionManager = "transactionManager")
@ActiveProfiles("test")
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

        Assertions.assertEquals(2, account.getID());
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test012SelectAccount()
    {
        List<MailAccount> accounts = this.mailDAO.getMailAccounts();

        Assertions.assertNotNull(accounts);
        Assertions.assertEquals(1, accounts.size());

        MailAccount account = accounts.get(0);
        Assertions.assertEquals(2, account.getID());
        Assertions.assertEquals("a@b.de", account.getMail());
        Assertions.assertEquals("gehaim", account.getPassword());
        Assertions.assertEquals("imap-host", account.getImapHost());
        Assertions.assertEquals(MailPort.IMAP, account.getImapPort());
        Assertions.assertEquals(true, account.isImapLegitimation());
        Assertions.assertEquals("smtp-host", account.getSmtpHost());
        Assertions.assertEquals(MailPort.SMTP, account.getSmtpPort());
        Assertions.assertEquals(false, account.isSmtpLegitimation());
    }

    /**
     */
    @Test
    @Commit
    public void test013UpdateAccount()
    {
        List<MailAccount> accounts = this.mailDAO.getMailAccounts();

        Assertions.assertNotNull(accounts);
        Assertions.assertEquals(1, accounts.size());

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

        Assertions.assertNotNull(accounts);
        Assertions.assertEquals(1, accounts.size());

        MailAccount account = accounts.get(0);
        Assertions.assertEquals(2, account.getID());
        Assertions.assertEquals("c@d.com", account.getMail());
        Assertions.assertEquals("gehaim2", account.getPassword());
        Assertions.assertEquals("host-imap", account.getImapHost());
        Assertions.assertEquals(MailPort.IMAPS, account.getImapPort());
        Assertions.assertEquals(false, account.isImapLegitimation());
        Assertions.assertEquals("host-smtp", account.getSmtpHost());
        Assertions.assertEquals(MailPort.SMTPS, account.getSmtpPort());
        Assertions.assertEquals(true, account.isSmtpLegitimation());
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

        Assertions.assertEquals(4, folder.getID());
    }

    /**
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test022SelectFolder()
    {
        List<MailFolder> folders = this.mailDAO.getMailFolder(2);

        Assertions.assertNotNull(folders);
        Assertions.assertEquals(1, folders.size());

        MailFolder folder = folders.get(0);
        Assertions.assertEquals(4, folder.getID());
        Assertions.assertEquals("a/b", folder.getFullName());
        Assertions.assertEquals("b", folder.getName());
        Assertions.assertFalse(folder.isAbonniert());
    }

    /**
     */
    @Test
    @Commit
    public void test023UpdateFolder()
    {
        List<MailFolder> folders = this.mailDAO.getMailFolder(2);

        Assertions.assertNotNull(folders);
        Assertions.assertEquals(1, folders.size());

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

        Assertions.assertNotNull(folders);
        Assertions.assertEquals(1, folders.size());

        MailFolder folder = folders.get(0);
        Assertions.assertEquals(4, folder.getID());
        Assertions.assertEquals("b/c", folder.getFullName());
        Assertions.assertEquals("c", folder.getName());
        Assertions.assertTrue(folder.isAbonniert());
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

        Assertions.assertNotNull(mails);
        Assertions.assertEquals(1, mails.size());

        Mail mail = mails.get(0);
        Assertions.assertEquals("a@a.aa", mail.getFrom().getAddress());
        Assertions.assertEquals(1, mail.getMsgNum());
        Assertions.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 00).atZone(ZoneId.systemDefault()).toInstant()), mail.getReceivedDate());
        Assertions.assertFalse(mail.isSeen());
        Assertions.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 01).atZone(ZoneId.systemDefault()).toInstant()), mail.getSendDate());
        Assertions.assertEquals(13, mail.getSize());
        Assertions.assertEquals("-TEST-", mail.getSubject());
        Assertions.assertEquals("b@b.bb", mail.getTo()[0].getAddress());
        Assertions.assertEquals("c@c.cc", mail.getCc()[0].getAddress());
        Assertions.assertEquals("d@d.dd", mail.getBcc()[0].getAddress());
        Assertions.assertEquals(2, mail.getUID());
    }

    /**
     */
    @Test
    @Commit
    public void test033UpdateMail()
    {
        List<Mail> mails = this.mailDAO.getMails(4);

        Assertions.assertNotNull(mails);
        Assertions.assertEquals(1, mails.size());

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

        Assertions.assertNotNull(mails);
        Assertions.assertEquals(1, mails.size());

        Mail mail = mails.get(0);
        Assertions.assertEquals("a@a.aa", mail.getFrom().getAddress());
        Assertions.assertEquals(1, mail.getMsgNum());
        Assertions.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 00).atZone(ZoneId.systemDefault()).toInstant()), mail.getReceivedDate());
        Assertions.assertTrue(mail.isSeen());
        Assertions.assertEquals(java.util.Date.from(LocalDateTime.of(2017, 02, 03, 15, 01).atZone(ZoneId.systemDefault()).toInstant()), mail.getSendDate());
        Assertions.assertEquals(13, mail.getSize());
        Assertions.assertEquals("-TEST-", mail.getSubject());
        Assertions.assertEquals("b@b.bb", mail.getTo()[0].getAddress());
        Assertions.assertEquals("c@c.cc", mail.getCc()[0].getAddress());
        Assertions.assertEquals("d@d.dd", mail.getBcc()[0].getAddress());
        Assertions.assertEquals(2, mail.getUID());
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

        Assertions.assertNotNull(mails);
        Assertions.assertEquals(0, mails.size());
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

        Assertions.assertNotNull(folders);
        Assertions.assertEquals(0, folders.size());
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

        Assertions.assertNotNull(folders);
        Assertions.assertEquals(0, folders.size());
    }
}
