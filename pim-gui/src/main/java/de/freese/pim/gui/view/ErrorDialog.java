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

/**
 * Allgemeiner Fehler-Dialog.
 *
 * @author Thomas Freese
 */
public class ErrorDialog
{
    /**
     *
     */
    private final Alert alert;

    /**
     * Erzeugt eine neue Instanz von {@link ErrorDialog}
     */
    public ErrorDialog()
    {
        super();

        this.alert = new Alert(AlertType.ERROR);
        this.alert.initModality(Modality.APPLICATION_MODAL);
        // this.alert.initOwner(PIMApplication.getMainWindow());

        this.alert.getDialogPane().setMinSize(640, 480);

        title("Error");
    }

    /**
     * @param text String
     * @return {@link ErrorDialog}
     */
    public ErrorDialog contentText(final String text)
    {
        this.alert.setContentText(text);

        return this;
    }

    /**
     * Ersetzt den Header- und Content-Text.
     *
     * @param throwable {@link Throwable}
     * @return {@link ErrorDialog}
     */
    public ErrorDialog forThrowable(final Throwable throwable)
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        throwable.printStackTrace(printWriter);

        String stackTrace = stringWriter.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(stackTrace);
        textArea.setEditable(false);
        textArea.setWrapText(false);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(label, 0, 0);
        gridPane.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        this.alert.getDialogPane().setExpandableContent(gridPane);

        return this;
    }

    /**
     * @param text String
     * @return {@link ErrorDialog}
     */
    public ErrorDialog headerText(final String text)
    {
        this.alert.setHeaderText(text);

        return this;
    }

    /**
     * @see javafx.scene.control.Dialog#showAndWait()
     */
    public void showAndWait()
    {
        this.alert.showAndWait();
    }

    /**
     * @param title String
     * @return {@link ErrorDialog}
     */
    public ErrorDialog title(final String title)
    {
        this.alert.setTitle(title);

        return this;
    }
}
