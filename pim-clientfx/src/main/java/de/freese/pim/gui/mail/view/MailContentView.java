// Created: 15.03.2017
package de.freese.pim.gui.mail.view;

import java.awt.Desktop;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.utils.InlineUrlStreamHandler;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

/**
 * View für den Inhalt einer Mail.
 *
 * @author Thomas Freese
 */
public class MailContentView extends GridPane
{
    /**
    *
    */
    public static final Logger LOGGER = LoggerFactory.getLogger(MailContentView.class);

    /**
     * @return {@link Logger}
     */
    public static Logger getLogger()
    {
        return LOGGER;
    }

    /**
    *
    */
    private final WebView webView;

    /**
     * Erzeugt eine neue Instanz von {@link MailContentView}
     */
    public MailContentView()
    {
        super();

        this.webView = new WebView();
        add(this.webView, 0, 0);

        this.webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) ->
        {
            try
            {
                URI address = new URI(newValue);
                getLogger().info(address.toString());

                // Desktop.getDesktop().browse(address);
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

                if ((desktop != null) && desktop.isSupported(Desktop.Action.BROWSE))
                {
                    desktop.browse(address);

                    Platform.runLater(() -> this.webView.getEngine().load(oldValue));
                }

                // Ansonsten wird der Link in der WebEngine geladen.
                // if ((address.getQuery() + "").indexOf("_openmodal=true") > -1)
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);

                new ErrorDialog().forThrowable(ex).showAndWait();
            }
        });
    }

    /**
     * @param throwable {@link Throwable}
     */
    public void displayThrowable(final Throwable throwable)
    {
        this.webView.getEngine().loadContent(ErrorDialog.toString(throwable), "text/plain");
    }

    /**
     * @param mail {@link FXMail}
     * @param mailContent {@link MailContent}
     */
    public void newMailContent(final FXMail mail, final MailContent mailContent)
    {
        // Delete cache for navigate back.
        this.webView.getEngine().load("about:blank");
        // this.webView.getEngine().loadContent("");

        // Delete cookies.
        java.net.CookieHandler.setDefault(new java.net.CookieManager());

        if (mail == null)
        {
            return;
        }

        if (mailContent == null)
        {
            // String msg = String.format("no content: %s/%s%n", mail.getFolder().getFullName(), mail.getSubject());
            // getLogger().error(msg);
            // new ErrorDialog().headerText(msg).showAndWait();

            String msg = String.format("<b>Error: no content found for</b><br>folder=%s<br>subject=%s<br>", mail.getFolderFullName(),
                    mail.getSubject());
            this.webView.getEngine().loadContent("<h2><font color=\"red\">" + msg + "</font></h2>");

            return;
        }

        InlineUrlStreamHandler.setMailContent(mailContent);

        // this.webView.getEngine().load(mailContent.getUrl().toExternalForm());
        this.webView.getEngine().loadContent(mailContent.getMessageContent(), mailContent.getMessageContentType());
    }
}
