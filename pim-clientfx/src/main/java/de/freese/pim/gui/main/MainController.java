// Created: 29.11.2016
package de.freese.pim.gui.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

import de.freese.pim.gui.addressbook.ContactController;
import de.freese.pim.gui.addressbook.ContactView;
import de.freese.pim.gui.controller.AbstractController;
import de.freese.pim.gui.mail.MailController;
import de.freese.pim.gui.mail.view.MailView;
import de.freese.pim.gui.utils.FxUtils;

/**
 * Main-Controller.
 *
 * @author Thomas Freese
 */
public class MainController extends AbstractController {
    private final MainView mainView;

    @FXML
    private Button buttonContactView;

    @FXML
    private Button buttonMailView;

    public MainController(final ResourceBundle resources) {
        super();

        mainView = new MainView();
        FxUtils.bind(mainView, this, resources);
    }

    @Override
    public void activate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getMainNode() {
        return mainView;
    }

    @Override
    public Node getNaviNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ToolBar getToolBar() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final MailController mailController = new MailController();
        FxUtils.bind(new MailView(), mailController, resources);

        final ContactController contactController = new ContactController();
        FxUtils.bind(new ContactView(), contactController, resources);

        FxUtils.translate(buttonMailView, resources);
        FxUtils.translate(buttonContactView, resources);

        buttonMailView.setOnAction(event -> {
            mainView.setToolbar(mailController.getToolBar());
            mainView.setNavNode(mailController.getNaviNode());
            mainView.setMainNode(mailController.getMainNode());
            mailController.activate();
        });

        buttonContactView.setOnAction(event -> {
            mainView.setToolbar(contactController.getToolBar());
            mainView.setNavNode(contactController.getNaviNode());
            mainView.setMainNode(contactController.getMainNode());
            contactController.activate();
        });
    }

    public void selectDefaultView() {
        buttonMailView.fire();
        // buttonContactView.fire();
    }
}
