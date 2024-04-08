// Created: 06.01.2017
package de.freese.pim.gui.view;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

import de.freese.pim.core.PIMException;
import de.freese.pim.gui.PimClientApplication;

/**
 * Allgemeiner Fehler-Dialog.
 *
 * @author Thomas Freese
 */
public class ErrorDialog {
    public static String toString(final Throwable throwable) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        throwable.printStackTrace(printWriter);

        return stringWriter.toString();
    }

    private final Alert alert;

    public ErrorDialog() {
        super();

        this.alert = new Alert(AlertType.ERROR);
        this.alert.initModality(Modality.APPLICATION_MODAL);
        // this.alert.initOwner(PIMApplication.getMainWindow());

        this.alert.getDialogPane().setMinSize(640, 480);

        title("Error");
    }

    public ErrorDialog contentText(final String text) {
        this.alert.setContentText(text);

        return this;
    }

    /**
     * Ersetzt den Header- und Content-Text.
     */
    public ErrorDialog forThrowable(final Throwable throwable) {
        Throwable th = throwable;

        if (th instanceof PIMException pex) {
            if (pex.getCause() != null) {
                th = pex.getCause();
            }
        }

        headerText(th.getMessage());

        final String stackTrace = toString(th);

        final Label label = new Label("The exception stacktrace was:");

        final TextArea textArea = new TextArea(stackTrace);
        textArea.setEditable(false);
        textArea.setWrapText(false);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(label, 0, 0);
        gridPane.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        this.alert.getDialogPane().setExpandableContent(gridPane);
        this.alert.getDialogPane().setExpanded(true);

        return this;
    }

    public ErrorDialog headerText(final String text) {
        this.alert.setHeaderText(text);

        return this;
    }

    public void showAndWait() {
        PimClientApplication.unblockGUI();
        this.alert.showAndWait();
    }

    public ErrorDialog title(final String title) {
        this.alert.setTitle(title);

        return this;
    }
}
