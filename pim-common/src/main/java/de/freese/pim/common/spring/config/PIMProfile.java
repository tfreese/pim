// Created: 22.02.2017
package de.freese.pim.common.spring.config;

/**
 * Mögliche Spring-Profile für PIM.
 *
 * @author Thomas Freese
 */
public enum PIMProfile
{
    /**
     * Client mit Embedded Server
     */
    ClientEmbeddedServer,

    /**
     * Client für REST-Server.
     */
    ClientREST,

    /**
     * Client als Standalone
     */
    ClientStandalone,

    /**
     * HSQLDB als Embedded Server
     */
    HsqldbEmbeddedServer,

    /**
     * HSQLDB als Local-File DB
     */
    HsqldbLocalFile,

    /**
     * HSQLDB als Memory-DB
     */
    HsqldbMemory,

    /**
     * Dedizierter Server
     */
    Server,

    /**
     * SQLITE als Local-File DB
     */
    SqliteLocalFile;
}
