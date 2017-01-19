// Created: 16.01.2017
package de.freese.pim.gui.mail;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import de.freese.pim.core.mail.MailPort;
import de.freese.pim.core.mail.MailProvider;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.service.IMailService;
import de.freese.pim.core.mail.service.JavaMailService;
import de.freese.pim.core.mail.utils.MailUtils;
import de.freese.pim.gui.PIMApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Dialog zum anlegen eines neuen {@link MailAccount}.
 *
 * @author Thomas Freese
 */
public class EditMailAccountDialog
{
    /**
     * Erzeugt eine neue Instanz von {@link EditMailAccountDialog}
     */
    public EditMailAccountDialog()
    {
        super();
    }

    /**
     * Legt einen neuen {@link MailAccount} an.
     *
     * @param bundle {@link ResourceBundle}
     * @return {@link Optional}
     */
    public Optional<MailAccount> addAccount(final ResourceBundle bundle)
    {
        return openDialog(bundle, null, "mail.add.account", "imageview-new");
    }

    /**
     * Editiert einen {@link MailAccount}.
     *
     * @param bundle {@link ResourceBundle}
     * @param account {@link MailAccount}
     * @return {@link Optional}
     */
    public Optional<MailAccount> editAccount(final ResourceBundle bundle, final MailAccount account)
    {
        return openDialog(bundle, account, "mail.edit.account", "imageview-edit");
    }

    /**
     * Initialisiert und öffnet den Dialog.
     *
     * @param bundle {@link ResourceBundle}
     * @param account {@link MailAccount}
     * @param titleKey String
     * @param imageStyleClass String
     * @return {@link Optional}
     */
    private Optional<MailAccount> openDialog(final ResourceBundle bundle, final MailAccount account, final String titleKey,
            final String imageStyleClass)
    {
        Dialog<MailAccount> dialog = new Dialog<>();
        dialog.initOwner(PIMApplication.getMainWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(bundle.getString(titleKey));
        dialog.setHeaderText(bundle.getString(titleKey));

        // dialog.getDialogPane().setPrefSize(500, 500);
        ImageView imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add(imageStyleClass);
        dialog.setGraphic(imageView);

        // Laden des Images antriggern für ImageView#getImage.
        imageView.applyCss();

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().clear(); // Icons des Owners entfernen.
        stage.getIcons().add(imageView.getImage());

        // ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Provider
        ComboBox<MailProvider> provider = new ComboBox<>();
        provider.setPrefWidth(200);
        provider.getItems().addAll(MailProvider.values());
        // PIMApplication.getExecutorService().execute(() ->
        // {
        // for (MailProvider mp : MailProvider.values())
        // {
        // Platform.runLater(() -> provider.getItems().add(mp));
        // }
        // });

        // Mail
        TextField mail = new TextField();
        mail.setPromptText(bundle.getString("mail"));
        // mail.setPrefWidth(200);
        mail.setPrefColumnCount(30);
        // mail.setTextFormatter(new TextFormatter<>( change -> {
        // String mailRegEx = "^(.+)@(.+).(.+)$";
        //
        // change.setText(change.getText().replaceAll("[^a-zA-Z0-9]", ""));
        // return change;
        //
        // }));

        // IMAP
        TextField imapHost = new TextField();
        imapHost.setPromptText("IMAP-Host");
        // TextField imapPort = new TextField();
        // imapHost.setPromptText("IMAP-Port");
        // imapPort.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        // Spinner<Integer> imapPort = new Spinner<>(25, 1000, MailProvider.EinsUndEins.getImapPort());
        // imapPort.setEditable(true);
        ComboBox<MailPort> imapPort = new ComboBox<>();
        imapPort.setPromptText("IMAP-Port");
        imapPort.setPrefWidth(200);
        imapPort.getItems().addAll(MailPort.IMAP, MailPort.IMAPS);
        imapPort.getSelectionModel().select(MailPort.IMAPS);
        // imapPort.setEditable(true);

        // SMTP
        TextField smtpHost = new TextField();
        imapHost.setPromptText("SMTP-Host");

        ComboBox<MailPort> smtpPort = new ComboBox<>();
        smtpPort.setPromptText("SMTP-Port");
        smtpPort.setPrefWidth(200);
        smtpPort.getItems().addAll(MailPort.SMTP, MailPort.SMTP_SSL, MailPort.SMTPS);
        smtpPort.getSelectionModel().select(MailPort.SMTPS);

        // Passwort
        PasswordField password1 = new PasswordField();
        password1.setPromptText(bundle.getString("passwort"));
        PasswordField password2 = new PasswordField();
        password2.setPromptText(bundle.getString("passwort") + " (" + bundle.getString("wiederholung") + ")");

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);

        // Mail-Format und Passwörter vergleichen.
        okButton.addEventFilter(ActionEvent.ACTION, event ->
        {
            String mailRegEx = MailUtils.MAIL_REGEX;

            if (!mail.getText().matches(mailRegEx))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(bundle.getString("mail.ungueltiges_format"));
                alert.showAndWait();

                event.consume();
            }

            String pw1 = password1.getText();
            String pw2 = password2.getText();

            if (StringUtils.isBlank(pw1) || StringUtils.isBlank(pw1))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(bundle.getString("passwoerter.nicht_ausgefuellt"));
                alert.showAndWait();

                event.consume();
            }
            else if (!pw1.equals(pw2))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(bundle.getString("passwoerter.nicht_identisch"));
                alert.showAndWait();

                event.consume();
            }
        });

        // OK-Button disablen, wenn eines dieser Felder leer ist.
        mail.textProperty().addListener((observable, oldValue, newValue) ->
        {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        // Laypout
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().addAll("gridpane", "padding");

        int row = -1;

        if (account != null)
        {
            mail.setText(account.getMail());

            imapHost.setText(account.getImapHost());
            // imapPort.setv(Integer.toString(account.getImapPort()));
            // imapPort.getValueFactory().setValue(account.getImapPort());
            imapPort.getSelectionModel().select(MailPort.findByPort(account.getImapPort()));

            smtpHost.setText(account.getSmtpHost());
            smtpPort.getSelectionModel().select(MailPort.findByPort(account.getSmtpPort()));

            password1.setText(account.getPassword());
            password2.setText(account.getPassword());
        }
        else
        {
            okButton.setDisable(true);

            provider.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
            {
                if (newValue == null)
                {
                    return;
                }

                imapHost.setText(newValue.getImapHost());
                imapPort.getSelectionModel().select(newValue.getImapPort());
                smtpHost.setText(newValue.getSmtpHost());
                smtpPort.getSelectionModel().select(newValue.getSmtpPort());
            });

            gridPane.add(new Label(bundle.getString("provider")), 0, ++row);
            gridPane.add(provider, 1, row);
        }

        gridPane.add(new Label(bundle.getString("mail")), 0, ++row);
        gridPane.add(mail, 1, row);
        gridPane.add(new Label("IMAP-Host"), 0, ++row);
        gridPane.add(imapHost, 1, row);
        gridPane.add(new Label("IMAP-Port"), 0, ++row);
        gridPane.add(imapPort, 1, row);
        gridPane.add(new Label("SMTP-Host"), 0, ++row);
        gridPane.add(smtpHost, 1, row);
        gridPane.add(new Label("SMTP-Port"), 0, ++row);
        gridPane.add(smtpPort, 1, row);
        gridPane.add(new Label(bundle.getString("passwort")), 0, ++row);
        gridPane.add(password1, 1, row);
        gridPane.add(new Label(bundle.getString("passwort") + " (" + bundle.getString("wiederholung") + ")"), 0, ++row);
        gridPane.add(password2, 1, row);

        // Test
        Button buttonTest = new Button("Test");
        buttonTest.setPrefWidth(150);
        gridPane.add(buttonTest, 0, ++row);
        Label labelTestResult = new Label();
        gridPane.add(labelTestResult, 1, row);

        buttonTest.setOnAction(event ->
        {
            labelTestResult.setText(null);
            labelTestResult.setStyle(null);

            MailAccount ma = new MailAccount();
            ma.setMail(mail.getText());
            ma.setImapHost(imapHost.getText());
            ma.setImapPort(imapPort.getSelectionModel().getSelectedItem().getPort());
            ma.setSmtpHost(smtpHost.getText());
            ma.setSmtpPort(smtpPort.getSelectionModel().getSelectedItem().getPort());
            ma.setPassword(password1.getText());

            IMailService mailService = new JavaMailService(ma, Paths.get("."));

            try
            {
                mailService.connect();
                mailService.testConnection();

                labelTestResult.setText("OK");
                labelTestResult.setStyle("-fx-text-fill: darkgreen;"); // fx-text-inner-color
            }
            catch (Exception ex)
            {
                labelTestResult.setText(ex.getMessage());
                labelTestResult.setStyle("-fx-text-fill: red;"); // fx-text-inner-color
            }
            finally
            {
                try
                {
                    mailService.disconnect();
                }
                catch (Exception ex)
                {
                    // Ignore
                }
            }
        });

        // Rest
        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> mail.requestFocus());

        dialog.setResultConverter(buttonType ->
        {
            if (buttonType == ButtonType.OK)
            {
                MailAccount ma = account;

                if (account == null)
                {
                    ma = new MailAccount();
                }

                ma.setMail(mail.getText());

                ma.setImapHost(imapHost.getText());
                // ma.setImapPort(Integer.parseInt(imapPort.getText()));
                // ma.setImapPort(imapPort.getValue());
                ma.setImapPort(imapPort.getSelectionModel().getSelectedItem().getPort());

                ma.setSmtpHost(smtpHost.getText());
                ma.setSmtpPort(smtpPort.getSelectionModel().getSelectedItem().getPort());

                ma.setPassword(password1.getText());

                return account;
            }

            return null;
        });

        Optional<MailAccount> result = dialog.showAndWait();

        return result;
    }
}
