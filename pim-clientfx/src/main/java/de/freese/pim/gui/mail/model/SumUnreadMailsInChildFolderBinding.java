// Created: 30.01.2017
package de.freese.pim.gui.mail.model;

import java.util.Objects;
import java.util.stream.Stream;

import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Bildet ein {@link IntegerBinding}, welches die Summe der ungelesenen Mails der Folder liefert.
 *
 * @author Thomas Freese
 */
public class SumUnreadMailsInChildFolderBinding extends IntegerBinding {
    /**
     * Reference to our observable list.
     */
    private final ObservableList<FxMailFolder> boundList;
    /**
     * Listener that has to call rebinding in response of any change in observable list.
     */
    private final ListChangeListener<FxMailFolder> boundListChangeListener = (final ListChangeListener.Change<? extends FxMailFolder> change) -> refreshBinding();
    /**
     * Array of currently observed properties of elements of our list.
     */
    private ObservableIntegerValue[] observedProperties;

    /**
     * Erzeugt eine neue Instanz von {@link SumUnreadMailsInChildFolderBinding}
     */
    public SumUnreadMailsInChildFolderBinding(final ObservableList<FxMailFolder> boundList) {
        super();

        this.boundList = Objects.requireNonNull(boundList, "boundList required");
        this.boundList.addListener(this.boundListChangeListener);

        refreshBinding();
    }

    @Override
    public void dispose() {
        this.boundList.removeListener(this.boundListChangeListener);
        
        unbind(this.observedProperties);
    }

    @Override
    protected int computeValue() {
        int sum = 0;

        if (this.observedProperties != null) {
            sum = Stream.of(this.observedProperties).parallel().mapToInt(ObservableIntegerValue::intValue).sum();
        }

        return sum;
    }

    private void refreshBinding() {
        // Clean old properties from IntegerBinding's inner listener
        unbind(this.observedProperties);

        // Load new properties
        this.observedProperties = null;

        if (!this.boundList.isEmpty()) {
            this.observedProperties = this.boundList.parallelStream().map(FxMailFolder::unreadMailsCountTotalBinding).toArray(ObservableIntegerValue[]::new);
        }

        // Bind IntegerBinding's inner listener to all new properties
        bind(this.observedProperties);

        // Invalidate binding to generate events
        // Eager/Lazy recalc depends on type of listeners attached to this instance
        // see IntegerBinding sources
        invalidate();
    }
}
