// Created: 23.02.2017
package de.freese.pim.common.model.builder;

/**
 * @author Thomas Freese
 */
public interface ResultMatcher
{
    /**
     * @param result {@link RequestResult}
     * @throws Exception Falls was schief geht.
     */
    public void match(RequestResult result) throws Exception;
}
