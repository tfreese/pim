// Created: 16.01.2017
package de.freese.pim.gui.mail;

import java.util.Optional;
import java.util.ResourceBundle;

import de.freese.pim.core.mail.MailPort;
import de.freese.pim.core.mail.MailProvider;
import de.freese.pim.core.utils.Utils;
import de.freese.pim.gui.PimClientApplication;
import de.freese.pim.gui.mail.model.FXMailAccount;
import de.freese.pim.gui.mail.model.FXMailFolder;
import de.freese.pim.gui.mail.service.FXMailService;
import de.freese.pim.gui.utils.FXUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Dialog zum anlegen eines neuen {@link FXMailAccount}.
 *
 * @author Thomas Freese
 */
public class EditMailAccountDialog
{
    /**
     *
     */
    private final ListView<FXMailFolder> aboView = new ListView<>();
    /**
     *
     */
    private final Button buttonTest = new Button("Test");
    /**
     *
     */
    private final TextField imapHost = new TextField();
    /**
     *
     */
    private final ComboBox<MailPort> imapPort = new ComboBox<>();
    /**
     *
     */
    private final Label labelTestResult = new Label();
    /**
     *
     */
    private final TextField mail = new TextField();
    /**
     *
     */
    private final PasswordField password1 = new PasswordField();
    /**
     *
     */
    private final PasswordField password2 = new PasswordField();
    /**
     *
     */
    private final ComboBox<MailProvider> provider = new ComboBox<>();
    /**
     *
     */
    private final TextField smtpHost = new TextField();
    /**
     *
     */
    private final ComboBox<MailPort> smtpPort = new ComboBox<>();

    /**
     * Legt einen neuen MailAccount an.
     *
     * @param mailService {@link FXMailService}
     * @param bundle {@link ResourceBundle}
     *
     * @return {@link Optional}
     */
    public Optional<FXMailAccount> addAccount(final FXMailService mailService, final ResourceBundle bundle)
    {
        return openDialog(mailService, bundle, null, "mailaccount.add", "imageview-add");
    }

    /**
     * Editiert einen MailAccount.
     *
     * @param mailService {@link FXMailService}
     * @param bundle {@link ResourceBundle}
     * @param account {@link FXMailAccount}
     *
     * @return {@link Optional}
     */
    public Optional<FXMailAccount> editAccount(final FXMailService mailService, final ResourceBundle bundle, final FXMailAccount account)
    {
        return openDialog(mailService, bundle, account, "mailaccount.edit", "imageview-edit");
    }

    /**
     * Testet die Mail-Einstellungen.
     *
     * @param event {@link ActionEvent}
     * @param bundle {@link ResourceBundle}
     */
    private void checkValidConfig(final ActionEvent event, final ResourceBundle bundle)
    {
        StringBuilder message = new StringBuilder();

        if (!this.mail.getText().matches(Utils.MAIL_REGEX))
        {
            message.append(bundle.getString("mail.format.invalid")).append("\n");
        }

        String pw1 = this.password1.getText();
        String pw2 = this.password2.getText();

        if ((pw1 == null) || pw1.isBlank())
        {
            message.append(bundle.getString("passwoerter.nicht_ausgefuellt")).append("\n");
        }
        else if (!pw1.equals(pw2))
        {
            message.append(bundle.getString("passwoerter.nicht_identisch")).append("\n");
        }

        if ((this.aboView.getItems() != null) && (this.aboView.getItems().stream().noneMatch(FXMailFolder::isAbonniert)))
        {
            message.append(bundle.getString("mail.folder.abonniert.nicht")).append("\n");
        }

        if (message.length() > 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(message.toString());
            alert.showAndWait();

            if (event != null)
            {
                event.consume();
            }
        }
    }

    /**
     * Initialisiert und öffnet den Dialog.
     *
     * @param mailService {@link FXMailService}
     * @param bundle {@link ResourceBundle}
     * @param account {@link FXMailAccount}
     * @param titleKey String
     * @param imageStyleClass String
     *
     * @return {@link Optional}
     */
    private Optional<FXMailAccount> openDialog(final FXMailService mailService, final ResourceBundle bundle, final FXMailAccount account, final String titleKey,
                                               final String imageStyleClass)
    {
        // DialogObject
        FXMailAccount bean = new FXMailAccount();

        // Attribute kopieren.
        if (account != null)
        {
            bean.copyFrom(account);
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(PimClientApplication.getMainWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(bundle.getString(titleKey));
        dialog.setHeaderText(bundle.getString(titleKey));
        // dialog.setResizable(true);
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
        this.provider.setPrefWidth(200);
        this.provider.getItems().addAll(MailProvider.values());

        // Mail
        this.mail.setPromptText(bundle.getString("mail"));
        this.mail.setPrefColumnCount(30);
        // mail.setTextFormatter(new TextFormatter<>( change -> {
        // String mailRegEx = MailUtils.MAIL_REGEX;
        //
        // change.setText(change.getText().replaceAll("[^a-zA-Z0-9]", ""));
        // return change;
        //
        // }));

        // IMAP
        this.imapHost.setPromptText("IMAP-Host");
        this.imapPort.setPromptText("IMAP-Port");
        this.imapPort.setPrefWidth(200);
        this.imapPort.getItems().addAll(MailPort.IMAP, MailPort.IMAPS);
        this.imapPort.getSelectionModel().select(MailPort.IMAPS);

        // SMTP
        this.imapHost.setPromptText("SMTP-Host");
        this.smtpPort.setPromptText("SMTP-Port");
        this.smtpPort.setPrefWidth(200);
        this.smtpPort.getItems().addAll(MailPort.SMTP, MailPort.SMTP_SSL, MailPort.SMTPS);
        this.smtpPort.getSelectionModel().select(MailPort.SMTPS);

        // Passwort
        this.password1.setPromptText(bundle.getString("passwort"));
        this.password2.setPromptText(bundle.getString("passwort") + " (" + bundle.getString("wiederholung") + ")");

        // Object-Attribute an GUI binden.
        this.mail.textProperty().bindBidirectional(bean.mailProperty());
        this.imapHost.textProperty().bindBidirectional(bean.imapHostProperty());
        this.imapPort.valueProperty().bindBidirectional(bean.imapPortProperty());
        this.smtpHost.textProperty().bindBidirectional(bean.smtpHostProperty());
        this.smtpPort.valueProperty().bindBidirectional(bean.smtpPortProperty());
        this.password1.textProperty().bindBidirectional(bean.passwordProperty());
        this.password2.setText(bean.getPassword());

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);

        // Mail-Format und Passwörter vergleichen.
        okButton.addEventFilter(ActionEvent.ACTION, event -> checkValidConfig(event, bundle));

        // OK-Button disablen, wenn eines dieser Felder leer ist.
        this.mail.textProperty().addListener((observable, oldValue, newValue) ->
        {
            okButton.setDisable(newValue.trim().isEmpty());

            this.labelTestResult.setText(null);
            this.labelTestResult.setStyle(null);
        });
        this.imapHost.textProperty().addListener((observable, oldValue, newValue) ->
        {
            this.labelTestResult.setText(null);
            this.labelTestResult.setStyle(null);
        });
        this.smtpHost.textProperty().addListener((observable, oldValue, newValue) ->
        {
            this.labelTestResult.setText(null);
            this.labelTestResult.setStyle(null);
        });
        this.password1.textProperty().addListener((observable, oldValue, newValue) ->
        {
            this.labelTestResult.setText(null);
            this.labelTestResult.setStyle(null);
            this.password2.clear();
        });
        this.password2.textProperty().addListener((observable, oldValue, newValue) ->
        {
            this.labelTestResult.setText(null);
            this.labelTestResult.setStyle(null);
        });

        this.aboView.setCellFactory(CheckBoxListCell.forListView(FXMailFolder::abonniertProperty, FXUtils.toStringConverter(FXMailFolder::getFullName)));
        this.aboView.setItems(bean.getFolder());

        // Laypout
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().addAll("gridpane", "padding");

        int row = -1;

        if (account == null)
        {
            okButton.setDisable(true);

            this.provider.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
            {
                if (newValue == null)
                {
                    return;
                }

                this.imapHost.setText(newValue.getImapHost());
                this.imapPort.getSelectionModel().select(newValue.getImapPort());
                this.smtpHost.setText(newValue.getSmtpHost());
                this.smtpPort.getSelectionModel().select(newValue.getSmtpPort());
            });

            gridPane.add(new Label(bundle.getString("provider")), 0, ++row);
            gridPane.add(this.provider, 1, row);
        }

        gridPane.add(new Label(bundle.getString("mail")), 0, ++row);
        gridPane.add(this.mail, 1, row);
        gridPane.add(new Label("IMAP-Host"), 0, ++row);
        gridPane.add(this.imapHost, 1, row);
        gridPane.add(new Label("IMAP-Port"), 0, ++row);
        gridPane.add(this.imapPort, 1, row);
        gridPane.add(new Label("SMTP-Host"), 0, ++row);
        gridPane.add(this.smtpHost, 1, row);
        gridPane.add(new Label("SMTP-Port"), 0, ++row);
        gridPane.add(this.smtpPort, 1, row);
        gridPane.add(new Label(bundle.getString("passwort")), 0, ++row);
        gridPane.add(this.password1, 1, row);
        gridPane.add(new Label(bundle.getString("passwort") + " (" + bundle.getString("wiederholung") + ")"), 0, ++row);
        gridPane.add(this.password2, 1, row);

        // Test
        this.buttonTest.setPrefWidth(150);
        this.buttonTest.setOnAction(event -> test(mailService, bean, bundle));
        gridPane.add(this.buttonTest, 0, ++row);
        gridPane.add(this.labelTestResult, 1, row);

        Label label = new Label(bundle.getString("mail.folder.abonniert"));
        gridPane.add(label, 0, ++row);
        GridPane.setValignment(label, VPos.TOP);
        gridPane.add(this.aboView, 1, row);

        // Rest
        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> this.mail.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.filter(response -> response != ButtonType.OK).isPresent())
        {
            return Optional.empty();
        }

        // Attribute kopieren.
        if (account != null)
        {
            account.copyFrom(bean);

            return Optional.ofNullable(account);
        }

        return Optional.ofNullable(bean);
    }

    /**
     * Testet die Mail-Einstellungen.
     *
     * @param mailService {@link FXMailService}
     * @param bean {@link FXMailAccount}
     * @param bundle {@link ResourceBundle}
     */
    private void test(final FXMailService mailService, final FXMailAccount bean, final ResourceBundle bundle)
    {
        PimClientApplication.getMainWindow().getScene().setCursor(Cursor.WAIT);

        this.labelTestResult.setText(null);
        this.labelTestResult.setStyle(null);
        this.aboView.setItems(null);

        try
        {
            checkValidConfig(null, bundle);

            bean.getFolder().clear();
            bean.getFolder().addAll(mailService.test(bean));

            this.labelTestResult.setText("OK");
            this.labelTestResult.setStyle("-fx-text-fill: darkgreen;"); // fx-text-inner-color

            this.aboView.setItems(bean.getFolder());
        }
        catch (Exception ex)
        {
            this.labelTestResult.setText(ex.getMessage());
            this.labelTestResult.setStyle("-fx-text-fill: red;"); // fx-text-inner-color
        }
        finally
        {
            PimClientApplication.getMainWindow().getScene().setCursor(Cursor.DEFAULT);
        }
    }
}
