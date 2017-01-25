// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.Objects;

import de.freese.pim.core.mail.model.MailFolder;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

/**
 * {@link ListChangeListener} für eine Liste aus {@link MailFolder} zum Aufbau des Trees.
 *
 * @author Thomas Freese
 */
public class TreeFolderListChangeListener implements ListChangeListener<MailFolder>
{
    /**
     *
     */
    private final TreeItem<Object> parent;

    /**
     * Erzeugt eine neue Instanz von {@link TreeFolderListChangeListener}
     *
     * @param parent {@link TreeItem}
     */
    public TreeFolderListChangeListener(final TreeItem<Object> parent)
    {
        super();

        Objects.requireNonNull(parent, "parent required");

        this.parent = parent;
    }

    /**
     * @see javafx.collections.ListChangeListener#onChanged(javafx.collections.ListChangeListener.Change)
     */
    @Override
    public void onChanged(final javafx.collections.ListChangeListener.Change<? extends MailFolder> change)
    {
        while (change.next())
        {
            if (change.wasAdded())
            {
                for (MailFolder mf : change.getAddedSubList())
                {
                    TreeItem<Object> treeItem = new TreeItem<>(mf);

                    // Prüfen auf Hierarchie.
                    if (getParent().getChildren().size() > 0)
                    {
                        TreeItem<Object> lastTreeItem = getParent().getChildren().get(getParent().getChildren().size() - 1);
                        MailFolder lastFolder = (MailFolder) lastTreeItem.getValue();

                        if (mf.getFullName().startsWith(lastFolder.getFullName()))
                        {
                            mf.setParent(lastFolder);
                            addChild(lastTreeItem, treeItem);
                        }

                        continue;
                    }

                    addChild(getParent(), treeItem);
                }
            }
            else if (change.wasRemoved())
            {
                for (MailFolder mf : change.getRemoved())
                {
                    TreeItem<Object> treeItem = null;

                    // Knoten finden.
                    for (TreeItem<Object> ti : getParent().getChildren())
                    {
                        MailFolder tiMF = (MailFolder) ti.getValue();

                        if (tiMF.getFullName().equals(mf.getFullName()))
                        {
                            treeItem = ti;
                            break;
                        }
                    }

                    if (treeItem != null)
                    {
                        removeChild(getParent(), treeItem);
                    }
                }
            }
        }
    }

    /**
     * Hinzufügen des Childs zum Parent.
     *
     * @param parent {@link TreeItem}
     * @param child {@link TreeItem}
     */
    private void addChild(final TreeItem<Object> parent, final TreeItem<Object> child)
    {
        Platform.runLater(() -> parent.getChildren().add(child));
    }

    /**
     * @return {@link TreeItem}
     */
    private TreeItem<Object> getParent()
    {
        return this.parent;
    }

    /**
     * Hinzufügen des Childs zum Parent.
     *
     * @param parent {@link TreeItem}
     * @param child {@link TreeItem}
     */
    private void removeChild(final TreeItem<Object> parent, final TreeItem<Object> child)
    {
        Platform.runLater(() -> parent.getChildren().remove(child));
    }
}
