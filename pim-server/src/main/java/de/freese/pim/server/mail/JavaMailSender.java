// Created: 09.12.2016
package de.freese.pim.server.mail;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;

/**
 * Siehe org.springframework.mail.javamail.JavaMailSenderImpl
 *
 * @author Thomas Freese
 */
public class JavaMailSender
{
    /**
     * @author Thomas Freese
     */
    private static class MailAuthenticator extends Authenticator
    {
        /**
        *
        */
        private final String password;

        /**
        *
        */
        private final String userName;

        /**
         * Erstellt ein neues {@link MailAuthenticator} Object.
         *
         * @param userName String
         * @param password String
         */
        public MailAuthenticator(final String userName, final String password)
        {
            super();

            this.userName = userName;
            this.password = password;
        }

        /**
         * @see javax.mail.Authenticator#getPasswordAuthentication()
         */
        @Override
        protected PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(this.userName, this.password);
        }
    }

    /**
     *
     */
    public static final int DEFAULT_PORT = -1;

    /**
     *
     */
    public static final String DEFAULT_PROTOCOL = "smtp";

    /**
     *
     */
    private static final String HEADER_MESSAGE_ID = "Message-ID";

    /**
     *
     */
    private Authenticator authenticator = null;

    /**
     *
     */
    private String encoding = null;

    /**
     *
     */
    private FileTypeMap fileTypeMap = null;

    /**
     *
     */
    private String host = null;

    /**
     *
     */
    private Properties javaMailProperties = new Properties();

    /**
     *
     */
    private String password = null;

    /**
     *
     */
    private int port = DEFAULT_PORT;

    /**
     *
     */
    private String protocol = null;

    /**
     *
     */
    private Session session = null;

    /**
     *
     */
    private String username = null;

    /**
     * Erzeugt eine neue Instanz von {@link JavaMailSender}
     */
    public JavaMailSender()
    {
        super();
    }

    /**
     * Obtain and connect a Transport from the underlying JavaMail Session, passing in the specified host, port, username, and password.
     *
     * @return the connected Transport object
     * @throws MessagingException if the connect attempt failed
     * @since 4.1.2
     * @see #getTransport
     * @see #getHost()
     * @see #getPort()
     * @see #getUsername()
     * @see #getPassword()
     */
    protected Transport connectTransport() throws MessagingException
    {
        String username = Optional.ofNullable(getUsername()).filter(StringUtils::isNotBlank).orElse(null);
        String password = Optional.ofNullable(getPassword()).filter(StringUtils::isNotBlank).orElse(null);

        Transport transport = getTransport(getSession());
        transport.connect(getHost(), getPort(), username, password);

        return transport;
    }

    /**
     * Actually send the given array of MimeMessages via JavaMail.
     *
     * @param mimeMessages MimeMessage objects to send
     * @param originalMessages corresponding original message objects that the MimeMessages have been created from (with same array length and indices as the
     *            "mimeMessages" array), if any
     * @throws Exception Falls was schief geht.
     */
    @SuppressWarnings("resource")
    protected void doSend(final MimeMessage[] mimeMessages, final Object[] originalMessages) throws Exception
    {
        Map<Object, Exception> failedMessages = new LinkedHashMap<>();
        Transport transport = null;

        try
        {
            for (int i = 0; i < mimeMessages.length; i++)
            {
                // Check transport connection first...
                if ((transport == null) || !transport.isConnected())
                {
                    if (transport != null)
                    {
                        try
                        {
                            transport.close();
                        }
                        catch (Exception ex)
                        {
                            // Ignore
                        }

                        transport = null;
                    }

                    try
                    {
                        transport = connectTransport();
                    }
                    catch (AuthenticationFailedException ex)
                    {
                        throw ex;
                    }
                    catch (Exception ex)
                    {
                        // Effectively, all remaining messages failed...
                        for (int j = i; j < mimeMessages.length; j++)
                        {
                            Object original = (originalMessages != null ? originalMessages[j] : mimeMessages[j]);
                            failedMessages.put(original, ex);
                        }

                        throw new MessagingException("Mail server connection failed", ex);
                        // throw new MailSendException("Mail server connection failed", ex, failedMessages);
                    }
                }

                MimeMessage mimeMessage = mimeMessages[i];

                try
                {
                    if (mimeMessage.getSentDate() == null)
                    {
                        mimeMessage.setSentDate(new Date());
                    }

                    String messageID = mimeMessage.getMessageID();

                    mimeMessage.saveChanges();

                    if (messageID != null)
                    {
                        mimeMessage.setHeader(HEADER_MESSAGE_ID, messageID);
                    }

                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                }
                catch (Exception ex)
                {
                    Object original = (originalMessages != null ? originalMessages[i] : mimeMessage);
                    failedMessages.put(original, ex);
                }
            }
        }
        finally
        {
            try
            {
                if (transport != null)
                {
                    transport.close();
                }
            }
            catch (Exception ex)
            {
                if (!failedMessages.isEmpty())
                {
                    // throw new MailSendException("Failed to close server connection after message failures", ex, failedMessages);
                    throw new MessagingException("Failed to close server connection after message failures", ex);
                }

                // throw new MailSendException("Failed to close server connection after message sending", ex);
                throw new MessagingException("Failed to close server connection after message sending", ex);
            }
        }

        if (!failedMessages.isEmpty())
        {
            // throw new MailSendException(failedMessages);
            throw new Exception(failedMessages.values().toString());
        }
    }

    /**
     * @return {@link Authenticator}
     */
    public Authenticator getAuthenticator()
    {
        return this.authenticator;
    }

    /**
     * @return String
     */
    public String getEncoding()
    {
        return this.encoding;
    }

    /**
     * @return {@link FileTypeMap}
     */
    public FileTypeMap getFileTypeMap()
    {
        if (this.fileTypeMap == null)
        {
            // org.springframework.mail.javamail.ConfigurableMimeFileTypeMap
            this.fileTypeMap = new MimetypesFileTypeMap();
        }

        return this.fileTypeMap;
    }

    /**
     * @return String
     */
    public String getHost()
    {
        return this.host;
    }

    /**
     * @return {@link Properties}
     */
    public Properties getJavaMailProperties()
    {
        return this.javaMailProperties;
    }

    /**
     * @return String
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @return int
     */
    public int getPort()
    {
        return this.port;
    }

    /**
     * @return String
     */
    public String getProtocol()
    {
        return this.protocol;
    }

    /**
     * @return {@link Session}
     */
    public synchronized Session getSession()
    {
        if (this.session == null)
        {
            this.session = Session.getInstance(getJavaMailProperties(), getAuthenticator());
        }

        return this.session;
    }

    /**
     * Obtain a Transport object from the given JavaMail Session, using the configured protocol.
     *
     * @param session {@link Session}
     * @return {@link Transport}
     * @throws NoSuchProviderException Falls was schief geht.
     * @see javax.mail.Session#getTransport(String)
     * @see #getSession()
     * @see #getProtocol()
     */
    protected Transport getTransport(final Session session) throws NoSuchProviderException
    {
        String protocol = getProtocol();

        if (protocol == null)
        {
            protocol = session.getProperty("mail.transport.protocol");
        }

        if (protocol == null)
        {
            protocol = DEFAULT_PROTOCOL;
        }

        return session.getTransport(protocol);
    }

    /**
     * @return String
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * @param mimeMessage {@link MimeMessage}
     * @throws Exception Falls was schief geht.
     */
    public void send(final MimeMessage mimeMessage) throws Exception
    {
        send(new MimeMessage[]
        {
                mimeMessage
        });
    }

    /**
     * @param mimeMessages {@link MimeMessage}[]
     * @throws Exception Falls was schief geht.
     */
    public void send(final MimeMessage...mimeMessages) throws Exception
    {
        doSend(mimeMessages, null);
    }

    /**
     * @param userName String
     * @param password String
     */
    public void setAuthenticator(final String userName, final String password)
    {
        this.authenticator = new MailAuthenticator(userName, password);
    }

    /**
     * @param encoding String
     */
    public void setEncoding(final String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * @param fileTypeMap {@link FileTypeMap}
     */
    public void setFileTypeMap(final FileTypeMap fileTypeMap)
    {
        this.fileTypeMap = fileTypeMap;
    }

    /**
     * @param host String
     */
    public void setHost(final String host)
    {
        this.host = host;
    }

    /**
     * @param javaMailProperties {@link Properties}
     */
    public void setJavaMailProperties(final Properties javaMailProperties)
    {
        this.javaMailProperties = javaMailProperties;
    }

    /**
     * @param password String
     */
    public void setPassword(final String password)
    {
        this.password = password;
    }

    /**
     * @param port int
     */
    public void setPort(final int port)
    {
        this.port = port;
    }

    /**
     * @param protocol String
     */
    public void setProtocol(final String protocol)
    {
        this.protocol = protocol;
    }

    /**
     * @param session {@link Session}
     */
    public synchronized void setSession(final Session session)
    {
        this.session = Objects.requireNonNull(session, () -> "Session must not be null");
    }

    /**
     * @param username String
     */
    public void setUsername(final String username)
    {
        this.username = username;
    }

    /**
     * Validate that this instance can connect to the server that it is configured for. Throws a {@link MessagingException} if the connection attempt failed.
     *
     * @throws MessagingException Falls was schief geht.
     */
    public void testConnection() throws MessagingException
    {
        try (Transport transport = connectTransport())
        {
            // NOOP
        }
    }
}
