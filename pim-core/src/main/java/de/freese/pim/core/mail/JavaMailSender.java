// Created: 09.12.2016
package de.freese.pim.core.mail;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

/**
 * Siehe org.springframework.mail.javamail.JavaMailSenderImpl
 *
 * @author Thomas Freese
 */
public class JavaMailSender {
    public static final String DEFAULT_PROTOCOL = "smtp";
    private static final String HEADER_MESSAGE_ID = "Message-ID";

    /**
     * @author Thomas Freese
     */
    private static class MailAuthenticator extends Authenticator {
        private final PasswordAuthentication authentication;

        MailAuthenticator(final String userName, final String password) {
            super();

            Objects.requireNonNull(userName, "userName required");
            Objects.requireNonNull(password, "password required");

            authentication = new PasswordAuthentication(userName, password);
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }

    private MailAuthenticator authenticator;
    private String host;
    private Properties javaMailProperties = new Properties();
    private int port = -1;
    private String protocol = DEFAULT_PROTOCOL;
    private Session session;

    public String getHost() {
        return host;
    }

    public Properties getJavaMailProperties() {
        return javaMailProperties;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public synchronized Session getSession() {
        if (session == null) {
            session = Session.getInstance(getJavaMailProperties(), authenticator);
        }

        return session;
    }

    public void send(final MimeMessage mimeMessage) throws Exception {
        doSend(new MimeMessage[]{mimeMessage}, null);
    }

    public void send(final List<MimeMessage> mimeMessages) throws Exception {
        doSend(mimeMessages.toArray(new MimeMessage[0]), null);
    }

    public void setAuthentication(final String userName, final String password) {
        authenticator = new MailAuthenticator(userName, password);
    }

    public void setHost(final String host) {
        this.host = Objects.requireNonNull(host, "host required");
    }

    public void setJavaMailProperties(final Properties javaMailProperties) {
        this.javaMailProperties = Objects.requireNonNull(javaMailProperties, "javaMailProperties required");
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public void setProtocol(final String protocol) {
        this.protocol = Objects.requireNonNull(protocol, "protocol required");
    }

    public synchronized void setSession(final Session session) {
        this.session = Objects.requireNonNull(session, "Session must not be null");
    }

    /**
     * Validate that this instance can connect to the server that it is configured for. Throws a {@link MessagingException} if the connection attempt failed.
     */
    public void testConnection() throws MessagingException {
        try (Transport transport = connectTransport()) {
            transport.isConnected();
        }
    }

    /**
     * Obtain and connect Transport from the underlying JavaMail Session, passing in the specified host, port, username, and password.
     *
     * @throws MessagingException if connect attempt failed
     */
    protected Transport connectTransport() throws MessagingException {
        final String username = Optional.ofNullable(getUsername()).filter(s -> !s.isBlank()).orElse(null);
        final String password = Optional.ofNullable(getPassword()).filter(s -> !s.isBlank()).orElse(null);

        final Transport transport = getTransport(getSession());
        transport.connect(getHost(), getPort(), username, password);

        return transport;
    }

    /**
     * Actually send the given array of MimeMessages via JavaMail.
     *
     * @param mimeMessages MimeMessage objects to send
     * @param originalMessages corresponding original message objects that the MimeMessages have been created from (with same array length and indices as the
     * "mimeMessages" array), if any
     */
    protected void doSend(final MimeMessage[] mimeMessages, final Object[] originalMessages) throws Exception {
        final Map<Object, Exception> failedMessages = new LinkedHashMap<>();
        Transport transport = null;

        try {
            for (int i = 0; i < mimeMessages.length; i++) {
                // Check transport connection first.
                if (transport == null || !transport.isConnected()) {
                    if (transport != null) {
                        try {
                            transport.close();
                        }
                        catch (Exception ex) {
                            // Ignore
                        }

                        transport = null;
                    }

                    try {
                        transport = connectTransport();
                    }
                    catch (AuthenticationFailedException ex) {
                        throw ex;
                    }
                    catch (Exception ex) {
                        // Effectively, all remaining messages failed.
                        for (int j = i; j < mimeMessages.length; j++) {
                            final Object original = originalMessages != null ? originalMessages[j] : mimeMessages[j];
                            failedMessages.put(original, ex);
                        }

                        throw new MessagingException("Mail server connection failed", ex);
                        // throw new MailSendException("Mail server connection failed", ex, failedMessages);
                    }
                }

                final MimeMessage mimeMessage = mimeMessages[i];

                try {
                    if (mimeMessage.getSentDate() == null) {
                        mimeMessage.setSentDate(new Date());
                    }

                    final String messageID = mimeMessage.getMessageID();

                    mimeMessage.saveChanges();

                    if (messageID != null) {
                        mimeMessage.setHeader(HEADER_MESSAGE_ID, messageID);
                    }

                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                }
                catch (Exception ex) {
                    final Object original = originalMessages != null ? originalMessages[i] : mimeMessage;
                    failedMessages.put(original, ex);
                }
            }
        }
        finally {
            try {
                if (transport != null) {
                    transport.close();
                }
            }
            catch (Exception ex) {
                if (!failedMessages.isEmpty()) {
                    // throw new MailSendException("Failed to close server connection after message failures", ex, failedMessages);
                    throw new MessagingException("Failed to close server connection after message failures", ex);
                }

                // throw new MailSendException("Failed to close server connection after message sending", ex);
                throw new MessagingException("Failed to close server connection after message sending", ex);
            }
        }

        if (!failedMessages.isEmpty()) {
            // throw new MailSendException(failedMessages);
            throw new Exception(failedMessages.values().toString());
        }
    }

    protected String getPassword() {
        return authenticator.getPasswordAuthentication().getPassword();
    }

    /**
     * Obtain a Transport object from the given JavaMail Session, using the configured protocol.
     */
    protected Transport getTransport(final Session session) throws NoSuchProviderException {
        String proto = getProtocol();

        if (proto == null) {
            proto = session.getProperty("mail.transport.protocol");
        }

        if (proto == null) {
            proto = DEFAULT_PROTOCOL;
        }

        return session.getTransport(proto);
    }

    protected String getUsername() {
        return authenticator.getPasswordAuthentication().getUserName();
    }
}
