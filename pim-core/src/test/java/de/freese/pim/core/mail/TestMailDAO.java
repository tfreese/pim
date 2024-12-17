// Created: 24.05.2016
package de.freese.pim.core.mail;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import de.freese.pim.core.TestConfig;
import de.freese.pim.core.dao.MailDao;
import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;

/**
 * TestCase für das {@link MailDao}.
 *
 * @author Thomas Freese
 */
@SpringBootTest(classes = {TestConfig.class})
@TestMethodOrder(MethodOrderer.MethodName.class)
@Transactional(transactionManager = "transactionManager")
@ActiveProfiles("test")
@DirtiesContext
class TestMailDAO {
    @Resource
    private MailDao mailDAO;

    @Test
    @Rollback
    void test010InsertAccountFail() {
        final MailAccount account = new MailAccount();

        final DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
                () -> mailDAO.insertAccount(account)
        );

        assertNotNull(exception);
    }

    @Test
    @Commit
    void test011InsertAccount() {
        final MailAccount account = new MailAccount();
        account.setMail("a@b.de");
        account.setPassword("gehaim");
        account.setImapHost("imap-host");
        account.setImapPort(MailPort.IMAP);
        account.setImapLegitimation(true);
        account.setSmtpHost("smtp-host");
        account.setSmtpPort(MailPort.SMTP);
        account.setSmtpLegitimation(false);

        mailDAO.insertAccount(account);

        assertEquals(2, account.getID());
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    void test012SelectAccount() {
        final List<MailAccount> accounts = mailDAO.getMailAccounts();

        assertNotNull(accounts);
        assertEquals(1, accounts.size());

        final MailAccount account = accounts.getFirst();
        assertEquals(2, account.getID());
        assertEquals("a@b.de", account.getMail());
        assertEquals("gehaim", account.getPassword());
        assertEquals("imap-host", account.getImapHost());
        assertEquals(MailPort.IMAP, account.getImapPort());
        assertTrue(account.isImapLegitimation());
        assertEquals("smtp-host", account.getSmtpHost());
        assertEquals(MailPort.SMTP, account.getSmtpPort());
        assertFalse(account.isSmtpLegitimation());
    }

    @Test
    @Commit
    void test013UpdateAccount() {
        final List<MailAccount> accounts = mailDAO.getMailAccounts();

        assertNotNull(accounts);
        assertEquals(1, accounts.size());

        final MailAccount account = accounts.getFirst();

        account.setMail("c@d.com");
        account.setPassword("gehaim2");
        account.setImapHost("host-imap");
        account.setImapPort(MailPort.IMAPS);
        account.setImapLegitimation(false);
        account.setSmtpHost("host-smtp");
        account.setSmtpPort(MailPort.SMTPS);
        account.setSmtpLegitimation(true);

        assertDoesNotThrow(() -> mailDAO.updateAccount(account));
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    void test014UpdateAccountCheck() {
        final List<MailAccount> accounts = mailDAO.getMailAccounts();

        assertNotNull(accounts);
        assertEquals(1, accounts.size());

        final MailAccount account = accounts.getFirst();
        assertEquals(2, account.getID());
        assertEquals("c@d.com", account.getMail());
        assertEquals("gehaim2", account.getPassword());
        assertEquals("host-imap", account.getImapHost());
        assertEquals(MailPort.IMAPS, account.getImapPort());
        assertFalse(account.isImapLegitimation());
        assertEquals("host-smtp", account.getSmtpHost());
        assertEquals(MailPort.SMTPS, account.getSmtpPort());
        assertTrue(account.isSmtpLegitimation());
    }

    @Test
    @Rollback
    void test020InsertFolderFail() {
        final List<MailFolder> folder = List.of(new MailFolder());

        final DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
                () -> mailDAO.insertFolder(2, folder)
        );

        assertNotNull(exception);
    }

    @Test
    @Commit
    void test021InsertFolder() {
        final MailFolder folder = new MailFolder();
        folder.setFullName("a/b");
        folder.setName("b");
        folder.setAbonniert(false);

        mailDAO.insertFolder(2, Arrays.asList(folder));

        assertEquals(4, folder.getID());
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    void test022SelectFolder() {
        final List<MailFolder> folders = mailDAO.getMailFolder(2);

        assertNotNull(folders);
        assertEquals(1, folders.size());

        final MailFolder folder = folders.getFirst();
        assertEquals(4, folder.getID());
        assertEquals("a/b", folder.getFullName());
        assertEquals("b", folder.getName());
        assertFalse(folder.isAbonniert());
    }

    @Test
    @Commit
    void test023UpdateFolder() {
        final List<MailFolder> folders = mailDAO.getMailFolder(2);

        assertNotNull(folders);
        assertEquals(1, folders.size());

        final MailFolder folder = folders.getFirst();

        folder.setFullName("b/c");
        folder.setName("c");
        folder.setAbonniert(true);

        mailDAO.updateFolder(folder);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    void test024UpdateFolderCheck() {
        final List<MailFolder> folders = mailDAO.getMailFolder(2);

        Assertions.assertNotNull(folders);
        assertEquals(1, folders.size());

        final MailFolder folder = folders.getFirst();
        assertEquals(4, folder.getID());
        assertEquals("b/c", folder.getFullName());
        assertEquals("c", folder.getName());
        assertTrue(folder.isAbonniert());
    }

    @Test
    @Rollback
    void test030InsertMailFail() {
        final List<Mail> mails = List.of(new Mail());

        final DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
                () -> mailDAO.insertMail(4, mails)
        );

        assertNotNull(exception);
    }

    @Test
    @Commit
    void test031InsertMail() {
        final Mail mail = new Mail();
        mail.setFrom(new InternetAddress("a@a.aa"));
        mail.setMsgNum(1);
        mail.setReceivedDate(java.util.Date.from(LocalDateTime.of(2017, 2, 3, 15, 0).atZone(ZoneId.systemDefault()).toInstant()));
        mail.setSeen(false);
        mail.setSendDate(java.util.Date.from(LocalDateTime.of(2017, 2, 3, 15, 1).atZone(ZoneId.systemDefault()).toInstant()));
        mail.setSize(13);
        mail.setSubject("-TEST-");
        mail.setTo(new InternetAddress[]{new InternetAddress("b@b.bb")});
        mail.setCc(new InternetAddress[]{new InternetAddress("c@c.cc")});
        mail.setBcc(new InternetAddress[]{new InternetAddress("d@d.dd")});
        mail.setUID(2);

        assertDoesNotThrow(() -> mailDAO.insertMail(4, Arrays.asList(mail)));
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    void test032SelectMail() {
        final List<Mail> mails = mailDAO.getMails(4);

        assertNotNull(mails);
        assertEquals(1, mails.size());

        final Date dateExpectedReceived = Timestamp.from(LocalDateTime.of(2017, 2, 3, 15, 0).atZone(ZoneId.systemDefault()).toInstant());
        final Date dateExpectedSend = Timestamp.from(LocalDateTime.of(2017, 2, 3, 15, 1).atZone(ZoneId.systemDefault()).toInstant());

        final Mail mail = mails.getFirst();
        assertEquals("a@a.aa", mail.getFrom().getAddress());
        assertEquals(1, mail.getMsgNum());
        assertEquals(dateExpectedReceived, mail.getReceivedDate());
        assertFalse(mail.isSeen());
        assertEquals(dateExpectedSend, mail.getSendDate());
        assertEquals(13, mail.getSize());
        assertEquals("-TEST-", mail.getSubject());
        assertEquals("b@b.bb", mail.getTo()[0].getAddress());
        assertEquals("c@c.cc", mail.getCc()[0].getAddress());
        assertEquals("d@d.dd", mail.getBcc()[0].getAddress());
        assertEquals(2, mail.getUID());
    }

    @Test
    @Commit
    void test033UpdateMail() {
        final List<Mail> mails = mailDAO.getMails(4);

        assertNotNull(mails);
        assertEquals(1, mails.size());

        final Mail mail = mails.getFirst();
        mail.setSeen(true);
        mail.setMsgNum(99);

        // Nur SEEN-Flag sollte aktualisiert werden.
        assertDoesNotThrow(() -> mailDAO.updateMail(4, mail));
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    void test034UpdateMailCheck() {
        final List<Mail> mails = mailDAO.getMails(4);

        Assertions.assertNotNull(mails);
        assertEquals(1, mails.size());

        final Mail mail = mails.getFirst();
        assertEquals("a@a.aa", mail.getFrom().getAddress());
        assertEquals(1, mail.getMsgNum());
        assertEquals(java.util.Date.from(LocalDateTime.of(2017, 2, 3, 15, 0).atZone(ZoneId.systemDefault()).toInstant()), mail.getReceivedDate());
        Assertions.assertTrue(mail.isSeen());
        assertEquals(java.util.Date.from(LocalDateTime.of(2017, 2, 3, 15, 1).atZone(ZoneId.systemDefault()).toInstant()), mail.getSendDate());
        assertEquals(13, mail.getSize());
        assertEquals("-TEST-", mail.getSubject());
        assertEquals("b@b.bb", mail.getTo()[0].getAddress());
        assertEquals("c@c.cc", mail.getCc()[0].getAddress());
        assertEquals("d@d.dd", mail.getBcc()[0].getAddress());
        assertEquals(2, mail.getUID());
    }

    @Test
    @Commit
    void test040DeleteMail() {
        assertDoesNotThrow(() -> mailDAO.deleteMail(4, 2));
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    void test041DeleteMailCheck() {
        final List<Mail> mails = mailDAO.getMails(4);

        assertNotNull(mails);
        assertEquals(0, mails.size());
    }

    @Test
    @Commit
    void test050DeleteFolder() {
        assertDoesNotThrow(() -> mailDAO.deleteFolder(4));
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    void test051DeleteFolderCheck() {
        final List<MailFolder> folders = mailDAO.getMailFolder(2);

        assertNotNull(folders);
        assertEquals(0, folders.size());
    }

    @Test
    @Commit
    void test060DeleteAccount() {
        final int affectedRows = mailDAO.deleteAccount(2);

        assertTrue(affectedRows > 0);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    void test061DeleteAccountCheck() {
        final List<MailFolder> folders = mailDAO.getMailFolder(2);

        assertNotNull(folders);
        assertEquals(0, folders.size());
    }
}
