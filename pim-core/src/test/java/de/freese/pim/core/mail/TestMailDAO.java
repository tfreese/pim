/**
 * Created on 24.05.2016
 */
package de.freese.pim.core.mail;

import java.sql.BatchUpdateException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import de.freese.pim.core.mail.dao.IMailDAO;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.model.MailPort;

/**
 * TestCase für das {@link IMailDAO}.
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
@DirtiesContext
public class TestMailDAO
{
    /**
     *
     */
    @Resource
    private IMailDAO mailDAO = null;

    /**
     * Erstellt ein neues {@link TestMailDAO} Object.
     */
    public TestMailDAO()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    @Rollback
    public void test010InsertAccountFail() throws Exception
    {
        MailAccount account = new MailAccount();

        this.mailDAO.insertAccount(account);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
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

        this.mailDAO.insertAccount(account);

        Assert.assertEquals(2, account.getID());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test012SelectAccount() throws Exception
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
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
    public void test013UpdateAccount() throws Exception
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
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test014UpdateAccountCheck() throws Exception
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
     * @throws Exception Falls was schief geht.
     */
    @Test(expected = BatchUpdateException.class)
    @Rollback
    public void test020InsertFolderFail() throws Exception
    {
        MailFolder folder = new MailFolder();

        this.mailDAO.insertFolder(2, Arrays.asList(folder));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
    public void test021InsertFolder() throws Exception
    {
        MailFolder folder = new MailFolder();
        folder.setFullName("a/b");
        folder.setName("b");
        folder.setAbonniert(false);

        this.mailDAO.insertFolder(2, Arrays.asList(folder));

        Assert.assertEquals(4, folder.getID());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test022SelectFolder() throws Exception
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
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
    public void test023UpdateFolder() throws Exception
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
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test024UpdateFolderCheck() throws Exception
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
     * @throws Exception Falls was schief geht.
     */
    @Test(expected = BatchUpdateException.class)
    @Rollback
    public void test030InsertMailFail() throws Exception
    {
        Mail mail = new Mail();

        this.mailDAO.insertMail(4, Arrays.asList(mail));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
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
        mail.setTo(InternetAddress.parse("b@b.bb"));
        mail.setCc(InternetAddress.parse("c@c.cc"));
        mail.setBcc(InternetAddress.parse("d@d.dd"));
        mail.setUID(2);

        this.mailDAO.insertMail(4, Arrays.asList(mail));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test032SelectMail() throws Exception
    {
        List<Mail> mails = this.mailDAO.getMails(4);

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
        Assert.assertEquals("b@b.bb", mail.getTo()[0].getAddress());
        Assert.assertEquals("c@c.cc", mail.getCc()[0].getAddress());
        Assert.assertEquals("d@d.dd", mail.getBcc()[0].getAddress());
        Assert.assertEquals(2, mail.getUID());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
    public void test033UpdateMail() throws Exception
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
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test034UpdateMailCheck() throws Exception
    {
        List<Mail> mails = this.mailDAO.getMails(4);

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
        Assert.assertEquals("b@b.bb", mail.getTo()[0].getAddress());
        Assert.assertEquals("c@c.cc", mail.getCc()[0].getAddress());
        Assert.assertEquals("d@d.dd", mail.getBcc()[0].getAddress());
        Assert.assertEquals(2, mail.getUID());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
    public void test040DeleteMail() throws Exception
    {
        this.mailDAO.deleteMail(4, 2);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test041DeleteMailCheck() throws Exception
    {
        List<Mail> mails = this.mailDAO.getMails(4);

        Assert.assertNotNull(mails);
        Assert.assertEquals(0, mails.size());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
    public void test050DeleteFolder() throws Exception
    {
        this.mailDAO.deleteFolder(4);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test051DeleteFolderCheck() throws Exception
    {
        List<MailFolder> folders = this.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(0, folders.size());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Commit
    public void test060DeleteAccount() throws Exception
    {
        this.mailDAO.deleteAccount(2);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void test061DeleteAccountCheck() throws Exception
    {
        List<MailFolder> folders = this.mailDAO.getMailFolder(2);

        Assert.assertNotNull(folders);
        Assert.assertEquals(0, folders.size());
    }
}
