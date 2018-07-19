// Created: 29.11.2016
package de.freese.pim.gui.main;

import java.net.URL;
import java.util.ResourceBundle;
import de.freese.pim.gui.addressbook.ContactController;
import de.freese.pim.gui.addressbook.ContactView;
import de.freese.pim.gui.controller.AbstractController;
import de.freese.pim.gui.mail.MailController;
import de.freese.pim.gui.mail.view.MailView;
import de.freese.pim.gui.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

/**
 * Main-Controller.
 *
 * @author Thomas Freese
 */
public class MainController extends AbstractController
{
    /**
    *
    */
    @FXML
    private Button buttonContactView = null;

    /**
    *
    */
    @FXML
    private Button buttonMailView = null;

    /**
     *
     */
    private ContactController contactController = null;

    /**
     *
     */
    private MailController mailController = null;

    /**
     *
     */
    private MainView mainView = null;

    /**
     * Erzeugt eine neue Instanz von {@link MainController}
     *
     * @param resources {@link ResourceBundle}
     */
    public MainController(final ResourceBundle resources)
    {
        super();

        this.mainView = new MainView();
        FXUtils.bind(this.mainView, this, resources);
    }

    /**
     * @see de.freese.pim.gui.controller.AbstractController#activate()
     */
    @Override
    public void activate()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see de.freese.pim.gui.controller.AbstractController#getMainNode()
     */
    @Override
    public Node getMainNode()
    {
        return this.mainView;
    }

    /**
     * @see de.freese.pim.gui.controller.AbstractController#getNaviNode()
     */
    @Override
    public Node getNaviNode()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see de.freese.pim.gui.controller.AbstractController#getToolBar()
     */
    @Override
    public ToolBar getToolBar()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources)
    {
        this.mailController = new MailController();
        FXUtils.bind(new MailView(), this.mailController, resources);

        this.contactController = new ContactController();
        FXUtils.bind(new ContactView(), this.contactController, resources);

        FXUtils.translate(this.buttonMailView, resources);
        FXUtils.translate(this.buttonContactView, resources);

        this.buttonMailView.setOnAction(event -> {
            this.mainView.setToolbar(this.mailController.getToolBar());
            this.mainView.setNavNode(this.mailController.getNaviNode());
            this.mainView.setMainNode(this.mailController.getMainNode());
            this.mailController.activate();
        });

        this.buttonContactView.setOnAction(event -> {
            this.mainView.setToolbar(this.contactController.getToolBar());
            this.mainView.setNavNode(this.contactController.getNaviNode());
            this.mainView.setMainNode(this.contactController.getMainNode());
            this.contactController.activate();
        });
    }

    /**
     * Selektiert die Default-Ansicht.
     */
    public void selectDefaultView()
    {
        this.buttonMailView.fire();
        // this.buttonContactView.fire();
    }
}
