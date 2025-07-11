// Created: 16.01.2017
package de.freese.pim.gui.mail;

import java.util.Optional;
import java.util.ResourceBundle;

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

import de.freese.pim.core.mail.MailPort;
import de.freese.pim.core.mail.MailProvider;
import de.freese.pim.core.utils.Utils;
import de.freese.pim.gui.PimClientApplication;
import de.freese.pim.gui.mail.model.FxMailAccount;
import de.freese.pim.gui.mail.model.FxMailFolder;
import de.freese.pim.gui.mail.service.FxMailService;
import de.freese.pim.gui.utils.FxUtils;

/**
 * Dialog zum Anlegen eines neuen {@link FxMailAccount}.
 *
 * @author Thomas Freese
 */
public class EditMailAccountDialog {
    private final ListView<FxMailFolder> aboView = new ListView<>();
    private final Button buttonTest = new Button("Test");
    private final TextField imapHost = new TextField();
    private final ComboBox<MailPort> imapPort = new ComboBox<>();
    private final Label labelTestResult = new Label();
    private final TextField mail = new TextField();
    private final PasswordField password1 = new PasswordField();
    private final PasswordField password2 = new PasswordField();
    private final ComboBox<MailProvider> provider = new ComboBox<>();
    private final TextField smtpHost = new TextField();
    private final ComboBox<MailPort> smtpPort = new ComboBox<>();

    public Optional<FxMailAccount> addAccount(final FxMailService mailService, final ResourceBundle bundle) {
        return openDialog(mailService, bundle, null, "mailaccount.add", "imageview-add");
    }

    public Optional<FxMailAccount> editAccount(final FxMailService mailService, final ResourceBundle bundle, final FxMailAccount account) {
        return openDialog(mailService, bundle, account, "mailaccount.edit", "imageview-edit");
    }

    private void checkValidConfig(final ActionEvent event, final ResourceBundle bundle) {
        final StringBuilder message = new StringBuilder();

        if (!mail.getText().matches(Utils.MAIL_REGEX)) {
            message.append(bundle.getString("mail.format.invalid")).append("\n");
        }

        final String pw1 = password1.getText();
        final String pw2 = password2.getText();

        if (pw1 == null || pw1.isBlank()) {
            message.append(bundle.getString("passwoerter.nicht_ausgefuellt")).append("\n");
        }
        else if (!pw1.equals(pw2)) {
            message.append(bundle.getString("passwoerter.nicht_identisch")).append("\n");
        }

        if (aboView.getItems() != null && aboView.getItems().stream().noneMatch(FxMailFolder::isAbonniert)) {
            message.append(bundle.getString("mail.folder.abonniert.nicht")).append("\n");
        }

        if (!message.isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(message.toString());
            alert.showAndWait();

            if (event != null) {
                event.consume();
            }
        }
    }

    private Optional<FxMailAccount> openDialog(final FxMailService mailService, final ResourceBundle bundle, final FxMailAccount account, final String titleKey,
                                               final String imageStyleClass) {
        // DialogObject
        final FxMailAccount bean = new FxMailAccount();

        // Attribute kopieren.
        if (account != null) {
            bean.copyFrom(account);
        }

        final Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(PimClientApplication.getMainWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(bundle.getString(titleKey));
        dialog.setHeaderText(bundle.getString(titleKey));
        // dialog.setResizable(true);
        // dialog.getDialogPane().setPrefSize(500, 500);

        final ImageView imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add(imageStyleClass);
        dialog.setGraphic(imageView);

        // Laden des Images triggern für ImageView#getImage.
        imageView.applyCss();

        final Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().clear(); // Icons des Owners entfernen.
        stage.getIcons().add(imageView.getImage());

        // ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Provider
        provider.setPrefWidth(200);
        provider.getItems().addAll(MailProvider.values());

        // Mail
        mail.setPromptText(bundle.getString("mail"));
        mail.setPrefColumnCount(30);
        // mail.setTextFormatter(new TextFormatter<>( change -> {
        // String mailRegEx = MailUtils.MAIL_REGEX;
        //
        // change.setText(change.getText().replaceAll("[^a-zA-Z0-9]", ""));
        // return change;
        //
        // }));

        // IMAP
        imapHost.setPromptText("IMAP-Host");
        imapPort.setPromptText("IMAP-Port");
        imapPort.setPrefWidth(200);
        imapPort.getItems().addAll(MailPort.IMAP, MailPort.IMAPS);
        imapPort.getSelectionModel().select(MailPort.IMAPS);

        // SMTP
        imapHost.setPromptText("SMTP-Host");
        smtpPort.setPromptText("SMTP-Port");
        smtpPort.setPrefWidth(200);
        smtpPort.getItems().addAll(MailPort.SMTP, MailPort.SMTP_SSL, MailPort.SMTPS);
        smtpPort.getSelectionModel().select(MailPort.SMTPS);

        // Passwort
        password1.setPromptText(bundle.getString("passwort"));
        password2.setPromptText(bundle.getString("passwort") + " (" + bundle.getString("wiederholung") + ")");

        // Object-Attribute an GUI binden.
        mail.textProperty().bindBidirectional(bean.mailProperty());
        imapHost.textProperty().bindBidirectional(bean.imapHostProperty());
        imapPort.valueProperty().bindBidirectional(bean.imapPortProperty());
        smtpHost.textProperty().bindBidirectional(bean.smtpHostProperty());
        smtpPort.valueProperty().bindBidirectional(bean.smtpPortProperty());
        password1.textProperty().bindBidirectional(bean.passwordProperty());
        password2.setText(bean.getPassword());

        final Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);

        // Mail-Format und Passwörter vergleichen.
        okButton.addEventFilter(ActionEvent.ACTION, event -> checkValidConfig(event, bundle));

        // OK-Button disablen, wenn eines dieser Felder leer ist.
        mail.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.isEmpty());

            labelTestResult.setText(null);
            labelTestResult.setStyle(null);
        });
        imapHost.textProperty().addListener((observable, oldValue, newValue) -> {
            labelTestResult.setText(null);
            labelTestResult.setStyle(null);
        });
        smtpHost.textProperty().addListener((observable, oldValue, newValue) -> {
            labelTestResult.setText(null);
            labelTestResult.setStyle(null);
        });
        password1.textProperty().addListener((observable, oldValue, newValue) -> {
            labelTestResult.setText(null);
            labelTestResult.setStyle(null);
            password2.clear();
        });
        password2.textProperty().addListener((observable, oldValue, newValue) -> {
            labelTestResult.setText(null);
            labelTestResult.setStyle(null);
        });

        aboView.setCellFactory(CheckBoxListCell.forListView(FxMailFolder::abonniertProperty, FxUtils.toStringConverter(FxMailFolder::getFullName)));
        aboView.setItems(bean.getFolder());

        // Layout
        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().addAll("gridpane", "padding");

        int row = -1;

        if (account == null) {
            okButton.setDisable(true);

            provider.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
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
        buttonTest.setPrefWidth(150);
        buttonTest.setOnAction(event -> test(mailService, bean, bundle));
        gridPane.add(buttonTest, 0, ++row);
        gridPane.add(labelTestResult, 1, row);

        final Label label = new Label(bundle.getString("mail.folder.abonniert"));
        gridPane.add(label, 0, ++row);
        GridPane.setValignment(label, VPos.TOP);
        gridPane.add(aboView, 1, row);

        // Rest
        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(mail::requestFocus);

        final Optional<ButtonType> result = dialog.showAndWait();

        if (result.filter(response -> response != ButtonType.OK).isPresent()) {
            return Optional.empty();
        }

        // Attribute kopieren.
        if (account != null) {
            account.copyFrom(bean);

            return Optional.ofNullable(account);
        }

        return Optional.ofNullable(bean);
    }

    private void test(final FxMailService mailService, final FxMailAccount bean, final ResourceBundle bundle) {
        PimClientApplication.getMainWindow().getScene().setCursor(Cursor.WAIT);

        labelTestResult.setText(null);
        labelTestResult.setStyle(null);
        aboView.setItems(null);

        try {
            checkValidConfig(null, bundle);

            bean.getFolder().clear();
            bean.getFolder().addAll(mailService.test(bean));

            labelTestResult.setText("OK");
            labelTestResult.setStyle("-fx-text-fill: darkgreen;"); // fx-text-inner-color

            aboView.setItems(bean.getFolder());
        }
        catch (Exception ex) {
            labelTestResult.setText(ex.getMessage());
            labelTestResult.setStyle("-fx-text-fill: red;"); // fx-text-inner-color
        }
        finally {
            PimClientApplication.getMainWindow().getScene().setCursor(Cursor.DEFAULT);
        }
    }
}
