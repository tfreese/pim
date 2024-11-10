// Created: 06.02.2017
package de.freese.pim.gui.mail.utils;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import de.freese.pim.core.mail.MailContent;

/**
 * {@link URLStreamHandler} für ein Inline einer HTML-Mail.
 *
 * @author Thomas Freese
 */
public class InlineUrlStreamHandler extends URLStreamHandler {
    private static MailContent mailContent;

    public static MailContent getMailContent() {
        return mailContent;
    }

    public static void setMailContent(final MailContent mailContent) {
        InlineUrlStreamHandler.mailContent = mailContent;
    }

    @Override
    protected URLConnection openConnection(final URL url) {
        return new InlineUrlConnection(InlineUrlStreamHandler.mailContent, url);
    }
}
