// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

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
                    // Prüfen auf Hierarchie.
                    if (!getParent().getChildren().isEmpty())
                    {
                        // Parent finden.
//                        // @formatter:off
//                        Optional<TreeItem<Object>> treeItem = getFlattenedStream(getParent())
//                            .filter(ti -> ti.getValue() instanceof MailFolder)
//                            .filter(ti -> mf.getFullName().startsWith(((MailFolder)ti.getValue()).getFullName()))
//                            .findFirst();
//                        // @formatter:on
                        //
                        // if (treeItem.isPresent())
                        // {
                        // addChild(treeItem.get(), mf);
                        // continue;
                        // }

                        TreeItem<Object> lastTreeItem = getParent().getChildren().get(getParent().getChildren().size() - 1);
                        MailFolder lastFolder = (MailFolder) lastTreeItem.getValue();

                        if (mf.getFullName().startsWith(lastFolder.getFullName()))
                        {
                            addChild(lastTreeItem, mf);

                            continue;
                        }
                    }

                    addChild(getParent(), mf);
                }
            }
            else if (change.wasRemoved())
            {
                for (MailFolder mf : change.getRemoved())
                {
                    // Knoten suchen.
                    // @formatter:off
                    Optional<TreeItem<Object>> treeItem = getFlattenedStream(getParent())
                        .filter(ti -> ti.getValue() instanceof MailFolder)
                        .filter(ti -> ((MailFolder)ti.getValue()).getFullName().equals(mf.getFullName()))
                        .findFirst();
                    // @formatter:on

                    if (treeItem.isPresent())
                    {
                        removeChild(treeItem.get().getParent(), treeItem.get());
                    }
                }
            }
        }
    }

    /**
     * Hinzufügen des Childs zum Parent.
     *
     * @param parent {@link TreeItem}
     * @param child {@link MailFolder}
     */
    private void addChild(final TreeItem<Object> parent, final MailFolder child)
    {
        Platform.runLater(() ->
        {
            // if (parent.getValue() instanceof MailFolder)
            // {
            // MailFolder mfParent = (MailFolder) parent.getValue();
            // mfParent.addChild(child);
            // }

            parent.getChildren().add(new TreeItem<>(child));
        });
    }

    /**
     * Liefert den abgeflachten Stream aller {@link TreeItem} der Hierarchie.
     *
     * @param treeItem {@link TreeItem}
     * @return {@link Stream}
     */
    private Stream<TreeItem<Object>> getFlattenedStream(final TreeItem<Object> treeItem)
    {
        return Stream.concat(Stream.of(treeItem), treeItem.getChildren().stream().flatMap(this::getFlattenedStream));
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
        Platform.runLater(() ->
        {
            if (parent.getValue() instanceof MailFolder)
            {
                MailFolder mfParent = (MailFolder) parent.getValue();
                MailFolder mfChild = (MailFolder) child.getValue();
                mfParent.removeChild(mfChild);
            }

            parent.getChildren().remove(child);
        });
    }
}
