// Created: 30.01.2017
package de.freese.pim.gui.mail.model;

import java.util.Objects;
import java.util.stream.Stream;

import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Bildet ein {@link IntegerBinding}, welches die Summe der ungelesenen Mails liefert.
 *
 * @author Thomas Freese
 */
public class SumUnreadMailsBinding extends IntegerBinding {
    /**
     * Reference to our observable list.
     */
    private final ObservableList<FxMail> boundList;
    /**
     * Listener that has to call rebinding in response of any change in observable list.
     */
    private final ListChangeListener<FxMail> boundListChangeListener = (final ListChangeListener.Change<? extends FxMail> change) -> refreshBinding();
    /**
     * Array of currently observed properties of elements of our list.
     */
    private BooleanProperty[] observedProperties;

    /**
     * Erzeugt eine neue Instanz von {@link SumUnreadMailsBinding}
     */
    public SumUnreadMailsBinding(final ObservableList<FxMail> boundList) {
        super();

        this.boundList = Objects.requireNonNull(boundList, "boundList required");
        this.boundList.addListener(boundListChangeListener);

        refreshBinding();
    }

    @Override
    public void dispose() {
        boundList.removeListener(boundListChangeListener);

        unbind(observedProperties);
    }

    @Override
    protected int computeValue() {
        int sum = 0;

        if (observedProperties != null) {
            sum = Stream.of(observedProperties).parallel().mapToInt(op -> op.get() ? 0 : 1).sum();
        }

        return sum;
    }

    private void refreshBinding() {
        // Clean old properties from IntegerBinding's inner listener
        unbind(observedProperties);

        // Load new properties
        observedProperties = null;

        if (!boundList.isEmpty()) {
            observedProperties = boundList.parallelStream().map(FxMail::seenProperty).toArray(BooleanProperty[]::new);
        }

        // Bind IntegerBinding's inner listener to all new properties
        bind(observedProperties);

        // Invalidate binding to generate events
        // Eager/Lazy recalc depends on type of listeners attached to this instance
        // see IntegerBinding sources
        invalidate();
    }
}
