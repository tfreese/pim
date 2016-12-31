// Created: 13.12.2016
package de.freese.pim.gui.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.freese.pim.core.mail.model.MailAccount;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Controller des Mail-Clients.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class MailController extends AbstractController
{
    /**
    *
    */
    @FXML
    private Node mainNode = null;

    /**
    *
    */
    @FXML
    private Node naviNode = null;

    /**
    *
    */
    private final SimpleObjectProperty<TreeItem<?>> selectedTreeItem = new SimpleObjectProperty<>();

    /**
    *
    */
    @FXML
    private TreeView<Object> treeViewMail = null;

    /**
     * Erzeugt eine neue Instanz von {@link MailController}
     */
    public MailController()
    {
        super();
    }

    /**
     * @see de.freese.pim.gui.controller.IController#getMainNode()
     */
    @Override
    public Node getMainNode()
    {
        return this.mainNode;
    }

    /**
     * @see de.freese.pim.gui.controller.IController#getNaviNode()
     */
    @Override
    public Node getNaviNode()
    {
        return this.naviNode;
    }

    /**
     * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources)
    {
        this.treeViewMail.setShowRoot(false);
        this.treeViewMail.setCellFactory(v -> new TreeCell<Object>()
        {
            /**
             * @param item Object
             * @param empty boolean
             */
            @Override
            public void updateItem(final Object item, final boolean empty)
            {
                super.updateItem(item, empty);

                if (item == null)
                {
                    return;
                }

                if (item instanceof MailAccount)
                {
                    setText(((MailAccount) item).getMail());
                }
            }
        });

        this.selectedTreeItem.bind(this.treeViewMail.getSelectionModel().selectedItemProperty());

        TreeItem<Object> root = new TreeItem<>("Mail-Accounts");
        this.treeViewMail.setRoot(root);

        // TODO Aus DB laden.
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        Path accounts = getSettingService().getHome().resolve(".mailaccounts");

        if (Files.exists(accounts))
        {
            try (InputStream is = Files.newInputStream(accounts))
            {
                // MailAccount mailAccount = jsonMapper.readValue(is, MailAccount.class);
                // root.getChildren().add(new TreeItem<>(mailAccount));

                JavaType type = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, MailAccount.class);
                List<MailAccount> list = jsonMapper.readValue(is, type);

                for (MailAccount mailAccount : list)
                {
                    root.getChildren().add(new TreeItem<>(mailAccount));
                }
            }
            catch (IOException ex)
            {
                Alert alert = new Alert(AlertType.ERROR, ex.getMessage());
                alert.showAndWait();
            }
        }
        else
        {
            MailAccount account = new MailAccount();
            account.setMail("commercial@freese-home.de");
            account.setImapHost("imap.1und1.de");
            account.setSmtpHost("smtp.1und1.de");

            try (OutputStream os = Files.newOutputStream(accounts))
            {
                jsonMapper.writer().writeValue(os, Arrays.asList(account));
            }
            catch (IOException ex)
            {
                Alert alert = new Alert(AlertType.ERROR, ex.getMessage());
                alert.showAndWait();

                // Files.delete(accounts);
            }
        }
    }
}
