// Created: 13.12.2016
package de.freese.pim.gui.mail;

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
import de.freese.pim.core.mail.MailProvider;
import de.freese.pim.core.mail.model.IMailAccount;
import de.freese.pim.core.mail.model.IMailFolder;
import de.freese.pim.core.mail.model.ImapMailAccount;
import de.freese.pim.core.mail.model.MailConfig;
import de.freese.pim.gui.controller.AbstractController;
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

                if ((item == null) || empty)
                {
                    setText(null);

                    return;
                }

                if (item instanceof IMailAccount)
                {
                    setText(((IMailAccount) item).getName());
                }
                else if (item instanceof IMailFolder)
                {
                    setText(((IMailFolder) item).getName());
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

        Path configs = getSettingService().getHome().resolve(".mailconfigs");
        List<MailConfig> configList = new ArrayList<>();

        if (Files.exists(configs))
        {
            try (InputStream is = Files.newInputStream(configs))
            {
                // MailAccount mailAccount = jsonMapper.readValue(is, MailAccount.class);
                // root.getChildren().add(new TreeItem<>(mailAccount));

                JavaType type = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, MailConfig.class);
                configList.addAll(jsonMapper.readValue(is, type));
            }
            catch (IOException ex)
            {
                getLogger().error(null, ex);

                Alert alert = new Alert(AlertType.ERROR, ex.getMessage());
                alert.showAndWait();
            }
        }
        else
        {
            MailConfig config = new MailConfig();
            config.setMail("commercial@freese-home.de");
            config.setImapHost(MailProvider.EinsUndEins.getImapHost());
            config.setSmtpHost(MailProvider.EinsUndEins.getSmtpHost());
            configList.add(config);

            try (OutputStream os = Files.newOutputStream(configs))
            {
                jsonMapper.writer().writeValue(os, Arrays.asList(config));
            }
            catch (IOException ex)
            {
                Alert alert = new Alert(AlertType.ERROR, ex.getMessage());
                alert.showAndWait();

                // Files.delete(accounts);
            }
        }

        try
        {
            for (MailConfig mailConfig : configList)
            {
                IMailAccount mailAccount = new ImapMailAccount();
                TreeItem<Object> treeItem = new TreeItem<>(mailAccount);
                root.getChildren().add(treeItem);

                getLogger().info("Init MailAccount {}", mailConfig.getMail());
                InitMailAccountService service = new InitMailAccountService(treeItem, mailConfig);
                service.start();
            }
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);

            Alert alert = new Alert(AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }
}
