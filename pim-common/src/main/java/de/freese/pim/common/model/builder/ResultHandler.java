// Created: 23.02.2017
package de.freese.pim.common.model.builder;

/**
 * @author Thomas Freese
 */
public interface ResultHandler
{
    /**
     * @param result {@link RequestResult}
     * @throws Exception Falls was schief geht.
     */
    public void handle(RequestResult result) throws Exception;
}
