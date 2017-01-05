// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;

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
    public InternetAddress getFrom() throws Exception
    {
        InternetAddress address = Optional.ofNullable(getMessage().getFrom()).map(f -> (InternetAddress) f[0]).orElse(null);

        return address;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMail#getMessageID()
     */
    @Override
    public String getMessageID() throws Exception
    {
        String messageID = Optional.ofNullable(getMessage().getHeader("Message-ID")).map(h -> h[0]).orElse(null);
        // String messageID = getMessage().getHeader("Message-ID")[0];

        return messageID;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMail#getReceivedDate()
     */
    @Override
    public Date getReceivedDate() throws Exception
    {
        Date receivedDate = getMessage().getReceivedDate();

        receivedDate = Optional.ofNullable(receivedDate).orElse(Date.from(Instant.parse(getMessage().getHeader("RECEIVED-DATE")[0])));

        return receivedDate;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMail#getSubject()
     */
    @Override
    public String getSubject() throws Exception
    {
        String subject = getMessage().getSubject();

        return subject;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMail#isSeen()
     */
    @Override
    public boolean isSeen() throws Exception
    {
        return getMessage().isSet(Flags.Flag.SEEN);
    }

    /**
     * @return {@link Message}
     */
    protected Message getMessage()
    {
        return this.message;
    }
}
