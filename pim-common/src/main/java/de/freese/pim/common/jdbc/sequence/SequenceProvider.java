/**
 * Created: 04.02.2017
 */

package de.freese.pim.common.jdbc.sequence;

import java.sql.SQLException;

/**
 * Liefert die nächste ID der Sequence.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SequenceProvider
{
    /**
     * @param sequence String
     * @return long
     * @throws SQLException Falls was schief geht.
     */
    public long getNextID(String sequence) throws SQLException;
}
