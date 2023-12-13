// Created: 31.01.2017
package de.freese.pim.core.mail.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import jakarta.mail.Session;

import org.eclipse.angus.mail.iap.Argument;
import org.eclipse.angus.mail.iap.ProtocolException;
import org.eclipse.angus.mail.iap.Response;
import org.eclipse.angus.mail.imap.IMAPFolder;
import org.eclipse.angus.mail.imap.protocol.BODY;
import org.eclipse.angus.mail.imap.protocol.FetchResponse;
import org.eclipse.angus.mail.imap.protocol.IMAPProtocol;
import org.eclipse.angus.mail.imap.protocol.IMAPResponse;

/**
 * Eigener IMAP-Befehl für Performance-Optimierung beim Download einer Mail.<br>
 * Usage: inbox.doCommand(new CustomProtocolCommand(start, end));
 * <a href="http://stackoverflow.com/questions/8322836/javamail-imap-over-ssl-quite-slow-bulk-fetching-multiple-messages">javamail-imap-over-ssl-quite-slow-bulk-fetching-multiple-messages</a><br>
 * <a href="http://stackoverflow.com/questions/28166182/prefetch-preview-text-from-javamail-message">prefetch-preview-text-from-javamail-message</a>
 *
 * @author Thomas Freese
 */
public class ImapDownloadCommand implements IMAPFolder.ProtocolCommand {
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
                            // Consume InputStream
                            is.transferTo(OutputStream.nullOutputStream());
                            //                            MimeMessage mm = new MimeMessage(this.session, is);

                            // TODO Save Mail
                            // Contents.getContents(mm, i);
                        }
                    }
                    //                    catch (MessagingException ex) {
                    //                        ex.printStackTrace();
                    //                    }
                    catch (IOException ex) {
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
