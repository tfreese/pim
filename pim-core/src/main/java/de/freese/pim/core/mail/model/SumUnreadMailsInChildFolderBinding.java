// Created: 30.01.2017
package de.freese.pim.core.mail.model;

import java.util.Objects;
import java.util.stream.Stream;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Bildet ein {@link IntegerBinding}, welches die Summe der ungelesenen Mails des Folders liefert.
 *
 * @author Thomas Freese
 */
public class SumUnreadMailsInChildFolderBinding extends IntegerBinding
{
    /**
     * Listener that has to call rebinding in response of any change in observable list.
     */
    private final ListChangeListener<MailFolder> BOUND_LIST_CHANGE_LISTENER =
            (final ListChangeListener.Change<? extends MailFolder> change) -> refreshBinding();

    /**
     * Reference to our observable list.
     */
    private final ObservableList<MailFolder> boundList;

    /**
     * Array of currently observed properties of elements of our list.
     */
    private ObservableIntegerValue[] observedProperties = null;

    /**
     * Erzeugt eine neue Instanz von {@link SumUnreadMailsInChildFolderBinding}
     *
     * @param boundList {@link ObservableList}
     */
    public SumUnreadMailsInChildFolderBinding(final ObservableList<MailFolder> boundList)
    {
        super();

        Objects.requireNonNull(boundList, "boundList required");

        this.boundList = boundList;
        this.boundList.addListener(this.BOUND_LIST_CHANGE_LISTENER);
        refreshBinding();
    }

    /**
     * @see javafx.beans.binding.IntegerBinding#computeValue()
     */
    @Override
    protected int computeValue()
    {
        int sum = 0;

        if (this.observedProperties != null)
        {
            sum = Stream.of(this.observedProperties).parallel().mapToInt(op -> op.intValue()).sum();
        }

        return sum;
    }

    /**
     * @see javafx.beans.binding.IntegerBinding#dispose()
     */
    @Override
    public void dispose()
    {
        this.boundList.removeListener(this.BOUND_LIST_CHANGE_LISTENER);
        unbind(this.observedProperties);
    }

    /**
     *
     */
    private void refreshBinding()
    {
        // Clean old properties from IntegerBinding's inner listener
        unbind(this.observedProperties);

        // Load new properties
        this.observedProperties = null;

        if (!this.boundList.isEmpty())
        {
            this.observedProperties = this.boundList.parallelStream().map(bl -> bl.unreadMailsCountTotalBinding()).toArray(ObservableIntegerValue[]::new);
        }

        // Bind IntegerBinding's inner listener to all new properties
        super.bind(this.observedProperties);

        // Invalidate binding to generate events
        // Eager/Lazy recalc depends on type of listeners attached to this instance
        // see IntegerBinding sources
        invalidate();
    }
}
