// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import com.sun.mail.imap.IMAPFolder;

/**
 * Basis-Implementierung einer JavaMail {@link IMail}.
 *
 * @author Thomas Freese
 * @param <F> Konkreter MailFolder
 */
public abstract class AbstractJavaMail<F extends IMailFolder> extends AbstractMail<F>
{
    /**
     *
     */
    private final Message message;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractJavaMail}
     *
     * @param mailFolder {@link IMailFolder}
     * @param message {@link Message}
     */
    public AbstractJavaMail(final F mailFolder, final Message message)
    {
        super(mailFolder);

        Objects.requireNonNull(message, "message required");

        this.message = message;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMail#getFrom()
     */
    @Override
    public InternetAddress getFrom()
    {
        try
        {
            InternetAddress address = Optional.ofNullable(getMessage().getFrom()).map(f -> (InternetAddress) f[0]).orElse(null);

            return address;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.pim.core.mail.model.IMail#getID()
     */
    @Override
    public String getID()
    {
        try
        {
            String id = null;

            if (getMailFolder() instanceof IMAPFolder)
            {
                id = Long.toString(((IMAPFolder) getMailFolder()).getUID(getMessage()));
            }

            if (id == null)
            {
                id = Optional.ofNullable(getMessage().getHeader("Message-ID")).map(h -> h[0]).orElse(null);
                id = getMessage().getHeader("Message-ID")[0];
            }

            return id;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return {@link Message}
     */
    protected Message getMessage()
    {
        return this.message;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMail#getReceivedDate()
     */
    @Override
    public Date getReceivedDate()
    {
        try
        {
            Date receivedDate = getMessage().getReceivedDate();

            // receivedDate = Optional.ofNullable(receivedDate).orElse(Date.from(Instant.parse(getMessage().getHeader("RECEIVED-DATE")[0])));

            return receivedDate;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.pim.core.mail.model.IMail#getSendDate()
     */
    @Override
    public Date getSendDate()
    {
        try
        {
            Date sendDate = getMessage().getSentDate();

            return sendDate;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.pim.core.mail.model.IMail#getSubject()
     */
    @Override
    public String getSubject()
    {
        try
        {
            String subject = getMessage().getSubject();

            return subject;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.pim.core.mail.model.IMail#isSeen()
     */
    @Override
    public boolean isSeen()
    {
        try
        {
            return getMessage().isSet(Flags.Flag.SEEN);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
