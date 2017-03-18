// Created: 15.03.2017
package de.freese.pim.gui.mail.view;

import java.awt.Desktop;
import java.net.URI;
import java.util.Optional;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.common.model.mail.InternetAddress;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.utils.InlineUrlStreamHandler;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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
    private final Label an;

    /**
     *
     */
    private final Label bcc;

    /**
     *
     */
    private final Label cc;

    /**
     *
     */
    private final Label von;

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

        ResourceBundle bundle = ResourceBundle.getBundle("bundles/pim");

        // Von
        Label label = new Label(bundle.getString("mail.from"));
        label.setPrefWidth(50);
        add(label, 0, 0);

        this.von = new Label();
        GridPane.setHgrow(this.von, Priority.ALWAYS);
        add(this.von, 1, 0);

        // An
        label = new Label(bundle.getString("mail.to"));
        add(label, 0, 1);

        this.an = new Label();
        add(this.an, 1, 1);

        // CC
        label = new Label(bundle.getString("mail.cc"));
        add(label, 0, 2);

        this.cc = new Label();
        add(this.cc, 1, 2);

        // BCC
        label = new Label(bundle.getString("mail.bcc"));
        add(label, 0, 3);

        this.bcc = new Label();
        add(this.bcc, 1, 3);

        add(new Separator(), 0, 4, 10, 1);

        this.webView = new WebView();
        add(this.webView, 0, 5, 10, 1);

        this.webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            try
            {
                URI address = new URI(newValue);
                // getLogger().info(address.toString());

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

        this.von.setText(null);
        this.an.setText(null);
        this.cc.setText(null);
        this.bcc.setText(null);

        if (mail == null)
        {
            return;
        }

        this.von.setText(Optional.ofNullable(mail.getFrom()).map(InternetAddress::toString).orElse(null));
        this.an.setText(Optional.ofNullable(mail.getTo()).map(InternetAddress::toString).orElse(null));
        this.cc.setText(Optional.ofNullable(mail.getCc()).map(InternetAddress::toString).orElse(null));
        this.bcc.setText(Optional.ofNullable(mail.getBcc()).map(InternetAddress::toString).orElse(null));

        if (mailContent == null)
        {
            // String msg = String.format("no content: %s/%s%n", mail.getFolder().getFullName(), mail.getSubject());
            // getLogger().error(msg);
            // new ErrorDialog().headerText(msg).showAndWait();

            String msg = String.format("<b>Error: no content found for</b><br>folder=%s<br>subject=%s<br>", mail.getFolderFullName(), mail.getSubject());
            this.webView.getEngine().loadContent("<h2><font color=\"red\">" + msg + "</font></h2>");

            return;
        }

        InlineUrlStreamHandler.setMailContent(mailContent);

        // this.webView.getEngine().load(mailContent.getUrl().toExternalForm());
        this.webView.getEngine().loadContent(mailContent.getMessageContent(), mailContent.getMessageContentType());
    }
}
