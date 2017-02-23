// Created: 23.02.2017
package de.freese.pim.common.model.builder;

/**
 * Führt den {@link RequestBuilder} aus.
 * 
 * @author Thomas Freese
 */
public interface RequestPerformer
{
    /**
     * @param builder {@link RequestBuilder}
     * @throws Exception Falls was schief geht.
     */
    public void perform(RequestBuilder builder) throws Exception;
}
