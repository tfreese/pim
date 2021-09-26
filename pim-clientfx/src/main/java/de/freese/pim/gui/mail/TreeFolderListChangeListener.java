// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import de.freese.pim.gui.mail.model.FXMailFolder;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

/**
 * {@link ListChangeListener} für eine Liste aus MailFolder zum Aufbau des Trees.
 *
 * @author Thomas Freese
 */
public class TreeFolderListChangeListener implements ListChangeListener<FXMailFolder>
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

        this.parent = Objects.requireNonNull(parent, "parent required");
    }

    /**
     * Hinzufügen des Childs zum Parent.
     *
     * @param parent {@link TreeItem}
     * @param child {@link FXMailFolder}
     */
    private void addChild(final TreeItem<Object> parent, final FXMailFolder child)
    {
        Runnable runnable = () -> parent.getChildren().add(new TreeItem<>(child));

        runnable.run();
        // Platform.runLater(runnable;
    }

    /**
     * Liefert den abgeflachten Stream aller {@link TreeItem} der Hierarchie.
     *
     * @param treeItem {@link TreeItem}
     *
     * @return {@link Stream}
     */
    private Stream<TreeItem<Object>> getFlattenedStream(final TreeItem<Object> treeItem)
    {
        return Stream.concat(Stream.of(treeItem), treeItem.getChildren().stream().flatMap(this::getFlattenedStream));
        // return treeItem.getChildren().stream().flatMap(this::getFlattenedStream);
    }

    /**
     * @return {@link TreeItem}
     */
    private TreeItem<Object> getParent()
    {
        return this.parent;
    }

    /**
     * @see javafx.collections.ListChangeListener#onChanged(javafx.collections.ListChangeListener.Change)
     */
    @Override
    public void onChanged(final ListChangeListener.Change<? extends FXMailFolder> change)
    {
        while (change.next())
        {
            if (change.wasAdded())
            {
                for (FXMailFolder mf : change.getAddedSubList())
                {
                    // System.out.printf("Stream: count=%d%n", getFlattenedStream(getParent()).count());

                    // @formatter:off
                    Optional<TreeItem<Object>> parentItem = getFlattenedStream(getParent())
                            //.peek(System.out::println)
                            .filter(ti -> ti.getValue() instanceof FXMailFolder)
                            .filter(ti -> mf.getFullName().startsWith(((FXMailFolder)ti.getValue()).getFullName()))
                            .findFirst();
                    // @formatter:on

                    if (parentItem.isPresent())
                    {
                        addChild(parentItem.get(), mf);
                    }
                    else
                    {
                        addChild(getParent(), mf);
                    }
                }
            }
            else if (change.wasRemoved())
            {
                for (FXMailFolder mf : change.getRemoved())
                {
                    // Knoten suchen.
                    // @formatter:off
                    Optional<TreeItem<Object>> treeItem = getFlattenedStream(getParent())
                        .filter(ti -> ti.getValue() instanceof FXMailFolder)
                        .filter(ti -> ((FXMailFolder)ti.getValue()).getFullName().equals(mf.getFullName()))
                        .findFirst();
                    // @formatter:on

                    treeItem.ifPresent(ti -> removeChild(ti.getParent(), ti));
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
    private void removeChild(final TreeItem<Object> parent, final TreeItem<Object> child)
    {
        Runnable runnable = () -> {
            if (parent.getValue()instanceof FXMailFolder mfParent)
            {
                FXMailFolder mfChild = (FXMailFolder) child.getValue();
                mfParent.removeChild(mfChild);
            }

            parent.getChildren().remove(child);
        };

        runnable.run();
        // Platform.runLater(runnable);
    }
}
