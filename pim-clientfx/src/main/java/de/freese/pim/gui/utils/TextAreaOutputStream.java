// Created: 03.02.2017
package de.freese.pim.gui.utils;

import java.io.OutputStream;
import java.util.Objects;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;

/**
 * {@link OutputStream} der die Ausgaben in eine {@link TextArea} umleitet.
 *
 * @author Thomas Freese
 */
public class TextAreaOutputStream extends OutputStream {
    /**
     * Behält nur die letzten n Zeilen in der {@link TextArea}.
     *
     * @author Thomas Freese
     */
    private static class LimitedTextArea extends TextArea {
        private final int keepLastNLines;

        LimitedTextArea(final int keepLastNLines) {
            super();

            this.keepLastNLines = keepLastNLines;
        }

        @Override
        public void replaceText(final int start, final int end, final String text) {
            super.replaceText(start, end, text);

            final String[] splits = getText().split("\n", -1);

            while (splits.length > this.keepLastNLines) {
                final int index = getText().indexOf('\n');

                super.replaceText(0, index + 1, "");
            }

            positionCaret(getText().length());
        }
    }

    /**
     * Verhindert, das mehr als n Zeilen eingefügt werden können.
     */
    static <T> TextFormatter<T> createLimitedTextFormatter(final int maxLines) {
        final IntegerProperty lines = new SimpleIntegerProperty(1);

        return new TextFormatter<>(change -> {
            if (change.isAdded()) {
                if (change.getText().indexOf('\n') > -1) {
                    lines.set(lines.get() + 1);
                }

                if (lines.get() > maxLines) {
                    change.setText("");
                }
            }
            return change;
        });
    }

    private final TextArea textArea;

    public TextAreaOutputStream() {
        this(new LimitedTextArea(20));
    }

    public TextAreaOutputStream(final TextArea textArea) {
        super();

        this.textArea = Objects.requireNonNull(textArea, "textArea required");
        // this.textArea.setTextFormatter(createTextFormatter());
    }

    public TextArea getTextArea() {
        return this.textArea;
    }

    @Override
    public void write(final int b) {
        getTextArea().appendText(String.valueOf((char) b));
    }
}
