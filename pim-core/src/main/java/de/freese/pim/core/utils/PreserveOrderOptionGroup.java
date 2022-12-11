// Created: 26.05.2016
package de.freese.pim.core.utils;

import java.io.Serial;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;

/**
 * Diese {@link OptionGroup} behält die Reihenfolge der {@link Option} bei.
 *
 * @author Thomas Freese
 */
public class PreserveOrderOptionGroup extends OptionGroup
{
    @Serial
    private static final long serialVersionUID = 5453910230985427926L;

    private final Map<String, Option> optionMap = new LinkedHashMap<>();

    /**
     * @see org.apache.commons.cli.OptionGroup#addOption(org.apache.commons.cli.Option)
     */
    @Override
    public OptionGroup addOption(final Option option)
    {
        this.optionMap.put(option.getOpt() == null ? option.getLongOpt() : option.getOpt(), option);

        return this;
    }

    /**
     * @see org.apache.commons.cli.OptionGroup#getNames()
     */
    @Override
    public Collection<String> getNames()
    {
        return this.optionMap.keySet();
    }

    /**
     * @see org.apache.commons.cli.OptionGroup#getOptions()
     */
    @Override
    public Collection<Option> getOptions()
    {
        return this.optionMap.values();
    }
}
