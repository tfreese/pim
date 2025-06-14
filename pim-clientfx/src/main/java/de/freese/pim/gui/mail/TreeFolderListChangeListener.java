// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

import de.freese.pim.gui.mail.model.FxMailFolder;

/**
 * {@link ListChangeListener} für eine Liste aus MailFolder zum Aufbau des Trees.
 *
 * @author Thomas Freese
 */
public class TreeFolderListChangeListener implements ListChangeListener<FxMailFolder> {
    private final TreeItem<Object> parent;

    public TreeFolderListChangeListener(final TreeItem<Object> parent) {
        super();

        this.parent = Objects.requireNonNull(parent, "parent required");
    }

    @Override
    public void onChanged(final ListChangeListener.Change<? extends FxMailFolder> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                for (FxMailFolder mf : change.getAddedSubList()) {
                    final Optional<TreeItem<Object>> parentItem = getFlattenedStream(getParent())
                            //.peek(System.out::println)
                            .filter(ti -> ti.getValue() instanceof FxMailFolder)
                            .filter(ti -> mf.getFullName().startsWith(((FxMailFolder) ti.getValue()).getFullName()))
                            .findFirst();

                    if (parentItem.isPresent()) {
                        addChild(parentItem.get(), mf);
                    }
                    else {
                        addChild(getParent(), mf);
                    }
                }
            }
            else if (change.wasRemoved()) {
                for (FxMailFolder mf : change.getRemoved()) {
                    // Knoten suchen.
                    final Optional<TreeItem<Object>> treeItem = getFlattenedStream(getParent())
                            .filter(ti -> ti.getValue() instanceof FxMailFolder)
                            .filter(ti -> ((FxMailFolder) ti.getValue()).getFullName().equals(mf.getFullName()))
                            .findFirst();

                    treeItem.ifPresent(ti -> removeChild(ti.getParent(), ti));
                }
            }
        }
    }

    private void addChild(final TreeItem<Object> parent, final FxMailFolder child) {
        final Runnable runnable = () -> parent.getChildren().add(new TreeItem<>(child));

        runnable.run();
        // Platform.runLater(runnable;
    }

    private Stream<TreeItem<Object>> getFlattenedStream(final TreeItem<Object> treeItem) {
        return Stream.concat(Stream.of(treeItem), treeItem.getChildren().stream().flatMap(this::getFlattenedStream));
    }

    private TreeItem<Object> getParent() {
        return parent;
    }

    private void removeChild(final TreeItem<Object> parent, final TreeItem<Object> child) {
        final Runnable runnable = () -> {
            if (parent.getValue() instanceof FxMailFolder mfParent) {
                final FxMailFolder mfChild = (FxMailFolder) child.getValue();
                mfParent.removeChild(mfChild);
            }

            parent.getChildren().remove(child);
        };

        runnable.run();
        // Platform.runLater(runnable);
    }
}
