/**
 * Created on 15.01.2017 14:55:36
 */
package de.freese.pim.gui.mail;

import java.util.Objects;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Thomas Freese
 */
public abstract class AbstractObjectPropertyFormatter extends StringBinding
{
    /**
     *
     */
    private final ObservableValue observableValue;

    /**
     * Erstellt ein neues Object.
     * 
     * @param observableValue ObservableValue
     */
    public AbstractObjectPropertyFormatter(final ObservableValue observableValue)
    {
        super();

        Objects.requireNonNull(observableValue, "observableValue required");

        this.observableValue = observableValue;
        super.bind(observableValue);
    }

    /**
     * @see javafx.beans.binding.StringBinding#computeValue()
     */
    @Override
    protected String computeValue()
    {
        final Object value = this.observableValue.getValue();
        return (value == null) ? "null" : value.toString();
    }

    /**
     * @see javafx.beans.binding.StringBinding#dispose()
     */
    @Override
    public void dispose()
    {
        super.unbind(this.observableValue);
    }

    /**
     * @see javafx.beans.binding.StringBinding#getDependencies()
     */
    @Override
    public ObservableList<ObservableValue<?>> getDependencies()
    {
        return FXCollections.<ObservableValue<?>> singletonObservableList(this.observableValue);
    }
}
