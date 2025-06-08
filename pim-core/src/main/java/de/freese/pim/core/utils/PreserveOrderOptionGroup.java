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
public class PreserveOrderOptionGroup extends OptionGroup {
    @Serial
    private static final long serialVersionUID = 5453910230985427926L;

    private final transient Map<String, Option> optionMap = new LinkedHashMap<>();

    @Override
    public OptionGroup addOption(final Option option) {
        optionMap.put(option.getOpt() == null ? option.getLongOpt() : option.getOpt(), option);

        return this;
    }

    @Override
    public Collection<String> getNames() {
        return optionMap.keySet();
    }

    @Override
    public Collection<Option> getOptions() {
        return optionMap.values();
    }
}
