// Created: 31.01.2017
package de.freese.pim.core.mail.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import com.sun.mail.iap.Argument;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder.ProtocolCommand;
import com.sun.mail.imap.protocol.BODY;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.IMAPResponse;

/**
 * Eigener IMAP-Befehl für Performance-Optimierung beim Download einer Mail.<br>
 * Usage: inbox.doCommand(new CustomProtocolCommand(start, end));
 * <a href="http://stackoverflow.com/questions/8322836/javamail-imap-over-ssl-quite-slow-bulk-fetching-multiple-messages">javamail-imap-over-ssl-quite-slow-bulk-fetching-multiple-messages</a><br>
 * <a href="http://stackoverflow.com/questions/28166182/prefetch-preview-text-from-javamail-message">prefetch-preview-text-from-javamail-message</a>
 *
 * @author Thomas Freese
 */
public class ImapDownloadCommand implements ProtocolCommand {
    private final Session session;

    private final long uid;

    public ImapDownloadCommand(final long uid) {
        this(null, uid);
    }

    public ImapDownloadCommand(final Session session, final long uid) {
        super();

        this.session = session;
        this.uid = uid;
    }

    /**
     * @see com.sun.mail.imap.IMAPFolder.ProtocolCommand#doCommand(com.sun.mail.imap.protocol.IMAPProtocol)
     */
    @Override
    public Object doCommand(final IMAPProtocol protocol) throws ProtocolException {
        // UID fetch 234789 bodystructure followed by b uid fetch 234789 (body.peek[1.1] body.peek[2])

        Argument args = new Argument();
        args.writeNumber(this.uid).writeString("BODY[]");

        Response[] r = protocol.command("UID FETCH", args);
        Response response = r[r.length - 1];

        if (response.isOK()) {
            Session s = this.session;

            if (s == null) {
                Properties props = new Properties();
                props.setProperty("mail.store.protocol", "imap");
                props.setProperty("mail.mime.base64.ignoreerrors", "true");
                props.setProperty("mail.imap.partialfetch", "false");
                props.setProperty("mail.imaps.partialfetch", "false");

                s = Session.getInstance(props, null);
            }

            // last response is only result summary: not contents
            for (int i = 0; i < (r.length - 1); i++) {
                if (r[i] instanceof IMAPResponse) {
                    FetchResponse fetch = (FetchResponse) r[i];
                    BODY body = (BODY) fetch.getItem(0);

                    try {
                        try (InputStream is = body.getByteArrayInputStream()) {

                            @SuppressWarnings("unused") MimeMessage mm = new MimeMessage(this.session, is);

                            // TODO Save Mail
                            // Contents.getContents(mm, i);
                        }
                    }
                    catch (MessagingException | IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        // dispatch remaining untagged responses
        protocol.notifyResponseHandlers(r);
        protocol.handleResult(response);

        return "" + (r.length - 1);
    }
}
