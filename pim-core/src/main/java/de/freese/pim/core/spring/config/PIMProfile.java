// Created: 22.02.2017
package de.freese.pim.core.spring.config;

/**
 * Mögliche Spring-Profile für PIM.
 *
 * @author Thomas Freese
 */
public enum PIMProfile {
    /**
     * Client für REST-Server.
     */
    CLIENT_REST,
    /**
     * Client als Standalone
     */
    CLIENT_STANDALONE,
    /**
     * HSQLDB als Embedded Server
     */
    HSQLDB_EMBEDDED_SERVER,
    /**
     * HSQLDB als Local-File DB
     */
    HSQLDB_LOCALFILE,
    /**
     * HSQLDB als Memory-DB
     */
    HSQLDB_MEMORY,
    /**
     * Dedizierter Server
     */
    SERVER,
    /**
     * SQLITE als Local-File DB
     */
    SQLITE_LOCALFILE
}
