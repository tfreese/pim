// Created: 15.03.2017
package de.freese.pim.gui.mail.view;

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import jakarta.activation.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.core.mail.InternetAddress;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.spring.SpringContext;
import de.freese.pim.gui.PimClientApplication;
import de.freese.pim.gui.mail.model.FxMail;
import de.freese.pim.gui.mail.utils.InlineUrlStreamHandler;
import de.freese.pim.gui.view.ErrorDialog;

/**
 * View für den Inhalt einer Mail.
 *
 * @author Thomas Freese
 */
public class MailContentView extends GridPane {
    public static final Logger LOGGER = LoggerFactory.getLogger(MailContentView.class);

    public static Logger getLogger() {
        return LOGGER;
    }

    private final Label an;
    private final HBox attachments;
    private final Label bcc;
    private final ResourceBundle bundle;
    private final Label cc;
    private final Label von;
    private final WebView webView;

    public MailContentView() {
        super();

        bundle = ResourceBundle.getBundle("bundles/pim");

        // Von
        Label label = new Label(bundle.getString("mail.from"));
        label.setPrefWidth(50);
        add(label, 0, 0);

        von = new Label();
        GridPane.setHgrow(von, Priority.ALWAYS);
        add(von, 1, 0);

        // An
        label = new Label(bundle.getString("mail.to"));
        add(label, 0, 1);

        an = new Label();
        add(an, 1, 1);

        // CC
        label = new Label(bundle.getString("mail.cc"));
        add(label, 0, 2);

        cc = new Label();
        add(cc, 1, 2);

        // BCC
        label = new Label(bundle.getString("mail.bcc"));
        add(label, 0, 3);

        bcc = new Label();
        add(bcc, 1, 3);

        // Attachments
        add(new Label(bundle.getString("attachments")), 0, 4);

        attachments = new HBox();
        add(attachments, 1, 4);

        add(new Separator(), 0, 5, 10, 1);

        webView = new WebView();
        add(webView, 0, 6, 10, 1);

        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            try {
                final URI address = new URI(newValue);
                // getLogger().info(address.toString());

                // Desktop.getDesktop().browse(address);
                final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(address);

                    Platform.runLater(() -> webView.getEngine().load(oldValue));
                }

                // Ansonsten wird der Link in der WebEngine geladen.
                // if ((address.getQuery() + "").indexOf("_openmodal=true") > -1)
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);

                new ErrorDialog().forThrowable(ex).showAndWait();
            }
        });
    }

    public void displayThrowable(final Throwable throwable) {
        webView.getEngine().loadContent(ErrorDialog.toString(throwable), "text/plain");
    }

    public void newMailContent(final FxMail mail, final MailContent mailContent) {
        // Delete cache for navigating back.
        webView.getEngine().load("about:blank");
        // webView.getEngine().loadContent("");

        // Delete cookies.
        java.net.CookieHandler.setDefault(new java.net.CookieManager());

        von.setText(null);
        an.setText(null);
        cc.setText(null);
        bcc.setText(null);
        attachments.getChildren().clear();

        if (mail == null) {
            return;
        }

        von.setText(Optional.ofNullable(mail.getFrom()).map(InternetAddress::toString).orElse(null));
        an.setText(Optional.ofNullable(mail.getTo()).map(InternetAddress::toString).orElse(null));
        cc.setText(Optional.ofNullable(mail.getCc()).map(InternetAddress::toString).orElse(null));
        bcc.setText(Optional.ofNullable(mail.getBcc()).map(InternetAddress::toString).orElse(null));

        if (mailContent == null) {
            // String msg = String.format("no content: %s/%s%n", mail.getFolder().getFullName(), mail.getSubject());
            // getLogger().error(msg);
            // new ErrorDialog().headerText(msg).showAndWait();

            final String msg = String.format("<b>Error: no content found for</b><br>folder=%s<br>subject=%s<br>", mail.getFolderFullName(), mail.getSubject());
            webView.getEngine().loadContent("<h2><font color=\"red\">" + msg + "</font></h2>");

            return;
        }

        InlineUrlStreamHandler.setMailContent(mailContent);

        for (DataSource dataSource : mailContent.getAttachments().values()) {
            final Hyperlink hyperlink = new Hyperlink(dataSource.getName());
            hyperlink.setStyle("-fx-font-size: 75%");
            hyperlink.setOnAction(event -> saveDataSource(dataSource));
            // hyperlink.setOnAction(event -> Platform.runLater(() -> saveDataSource(dataSource)));

            attachments.getChildren().add(hyperlink);
        }

        // webView.getEngine().load(mailContent.getUrl().toExternalForm());
        webView.getEngine().loadContent(mailContent.getMessageContent(), mailContent.getMessageContentType());
    }

    private void saveDataSource(final DataSource dataSource) {
        if (dataSource == null) {
            return;
        }

        // File initDirectory = new File(System.getProperty("user.home"));
        final File initDirectory = new File(System.getProperty("java.io.tmpdir"));

        // DirectoryChooser directoryChooser = new DirectoryChooser();
        // directoryChooser.setTitle("Verzeichnis für DataSource");
        // directoryChooser.setInitialDirectory(initDirectory));
        // File attachmentDir = directoryChooser.showDialog(PIMApplication.getMainWindow());

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("attachment.save"));
        fileChooser.setInitialDirectory(initDirectory);
        fileChooser.setInitialFileName(dataSource.getName());
        // fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Excel", "*.xls", "*.xlsx"));

        final File file = fileChooser.showSaveDialog(PimClientApplication.getMainWindow());

        if (file == null) {
            return;
        }

        final Path target = file.toPath();

        final Runnable task = () -> {
            try (InputStream inputStream = dataSource.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);

                // Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                //
                // if ((desktop != null) && desktop.isSupported(Desktop.Action.OPEN)) {
                // desktop.open(file);
                // }
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);

                new ErrorDialog().forThrowable(ex).showAndWait();
            }
        };

        SpringContext.getAsyncTaskExecutor().execute(task);
    }
}
