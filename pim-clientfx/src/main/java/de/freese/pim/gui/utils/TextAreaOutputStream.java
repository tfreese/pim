// Created: 03.02.2017
package de.freese.pim.gui.utils;

import java.io.IOException;
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
public class TextAreaOutputStream extends OutputStream
{
    /**
     * Behält nur die letzten n Zeilen in der {@link TextArea}.
     *
     * @author Thomas Freese
     */
    private static class LimitedTextArea extends TextArea
    {
        /**
         *
         */
        private final int keepLastNLines;

        /**
         * Erzeugt eine neue Instanz von {@link LimitedTextArea}
         *
         * @param keepLastNLines int
         */
        public LimitedTextArea(final int keepLastNLines)
        {
            super();

            this.keepLastNLines = keepLastNLines;
        }

        /**
         * @see javafx.scene.control.TextInputControl#replaceText(int, int, java.lang.String)
         */
        @Override
        public void replaceText(final int start, final int end, final String text)
        {
            super.replaceText(start, end, text);

            String[] splits = getText().split("\n", -1);

            while (splits.length > this.keepLastNLines)
            {
                int index = getText().indexOf("\n");

                super.replaceText(0, index + 1, "");
            }

            positionCaret(getText().length());
        }
    }

    /**
     * Verhindert, das mehr als n Zeilen eingefügt werden können.
     *
     * @param <T> Konkreter Typ
     * @param maxLines int
     * @return {@link TextFormatter}
     */
    private static <T> TextFormatter<T> createLimitedTextFormatter(final int maxLines)
    {
        final IntegerProperty lines = new SimpleIntegerProperty(1);

        return new TextFormatter<>(change -> {
            if (change.isAdded())
            {
                if (change.getText().indexOf('\n') > -1)
                {
                    lines.set(lines.get() + 1);
                }

                if (lines.get() > maxLines)
                {
                    change.setText("");
                }
            }
            return change;
        });
    }

    /**
     *
     */
    private final TextArea textArea;

    /**
     * Erzeugt eine neue Instanz von {@link TextAreaOutputStream}
     */
    public TextAreaOutputStream()
    {
        this(new LimitedTextArea(20));
    }

    /**
     * Erzeugt eine neue Instanz von {@link TextAreaOutputStream}
     *
     * @param textArea {@link TextArea}
     */
    public TextAreaOutputStream(final TextArea textArea)
    {
        super();

        this.textArea = Objects.requireNonNull(textArea, "textArea requried");
        // this.textArea.setTextFormatter(createTextFormatter());
    }

    /**
     * @return {@link TextArea}
     */
    public TextArea getTextArea()
    {
        return this.textArea;
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException
    {
        getTextArea().appendText(String.valueOf((char) b));
    }
}
