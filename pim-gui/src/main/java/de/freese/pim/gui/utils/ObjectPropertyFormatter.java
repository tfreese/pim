// Created: 16.01.2017
package de.freese.pim.gui.utils;

import java.util.Objects;
import java.util.function.Function;

import com.sun.javafx.binding.StringFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Formatter um ein {@link ObjectProperty} zu formattieren.
 *
 * @author Thomas Freese
 */
public class ObjectPropertyFormatter
{
    /**
     * Formattiert das Objekt als String.
     *
     * @param ov {@link ObservableValue}
     * @param formatter {@link Function}
     * @return {@link ObservableValue}<String>
     */
    public static <T> ObservableValue<String> toString(final ObservableValue<T> ov, final Function<T, String> formatter)
    {
        Objects.requireNonNull(ov, "observableValue required");
        Objects.requireNonNull(formatter, "formatter required");

        final StringFormatter stringFormatter = new StringFormatter()
        {
            {
                super.bind(ov);
            }

            /**
             * @see javafx.beans.binding.StringBinding#dispose()
             */
            @Override
            public void dispose()
            {
                super.unbind(ov);
            }

            /**
             * @see javafx.beans.binding.StringBinding#getDependencies()
             */
            @Override
            public ObservableList<ObservableValue<?>> getDependencies()
            {
                ObservableList<ObservableValue<?>> ol = FXCollections.observableArrayList();
                ol.add(ov);

                return FXCollections.unmodifiableObservableList(ol);
            }

            /**
             * @see javafx.beans.binding.StringBinding#computeValue()
             */
            @Override
            protected String computeValue()
            {
                return formatter.apply(ov.getValue());
            }
        };

        // Force calculation to check format
        stringFormatter.get();

        return stringFormatter;
    }
}
