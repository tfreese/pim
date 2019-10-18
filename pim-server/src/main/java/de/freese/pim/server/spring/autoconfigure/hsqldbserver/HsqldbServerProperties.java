/**
 * Created: 13.02.2017
 */

package de.freese.pim.server.spring.autoconfigure.hsqldbserver;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties für {@link HsqldbServerAutoConfiguration}.<br>
 * Format:
 *
 * <pre>
 * Als Properties:
 * hsqldb.server.enabled = true
 * hsqldb.server.port = ${port}
 * hsqldb.server.noSystemExit = true
 * hsqldb.server.silent = true
 * hsqldb.server.trace = false
 * hsqldb.server.db[0].name=${name0}
 * hsqldb.server.db[0].path=file:/${path}/${name0}
 * hsqldb.server.db[1].name=${name1}
 * hsqldb.server.db[1].path=mem:${name1}
 *
 * Als YAML:
 * hsqldb:
 *     server:
 *         enabled: true
 *         port: ${port}
 *         noSystemExit: true
 *         silent: true
 *         trace: trace
 *         db:
 *             - name: ${name0}
 *               path: file:/${path}/${name0}
 *             - name: ${name1}
 *               path: mem:${name1}
 * </pre>
 *
 * @author Thomas Freese
 */
@ConfigurationProperties(prefix = "hsqldb.server")
public class HsqldbServerProperties
{
    /**
     * @author Thomas Freese
     */
    public static class DB
    {
        /**
         *
         */
        private String name = null;

        /**
         *
         */
        private String path = null;

        /**
         * Erstellt ein neues {@link DB} Object.
         */
        public DB()
        {
            super();
        }

        /**
         * @return String
         */
        public String getName()
        {
            return this.name;
        }

        /**
         * @return String
         */
        public String getPath()
        {
            return this.path;
        }

        /**
         * @param name String
         */
        public void setName(final String name)
        {
            this.name = name;
        }

        /**
         * @param path String
         */
        public void setPath(final String path)
        {
            this.path = path;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("DB [name=").append(this.name);
            builder.append(", path=").append(this.path);
            builder.append("]");

            return builder.toString();
        }
    }

    /**
     *
     */
    private List<DB> db = null;

    /**
     *
     */
    private boolean enabled = true;

    /**
    *
    */
    private boolean noSystemExit = true;

    /**
    *
    */
    private int port = 0;

    /**
     *
     */
    private boolean silent = true;

    /**
      *
      */
    private boolean trace = false;

    /**
     * Erstellt ein neues {@link HsqldbServerProperties} Object.
     */
    public HsqldbServerProperties()
    {
        super();
    }

    /**
     * @return List<DB>
     */
    public List<DB> getDb()
    {
        return this.db;
    }

    /**
     * @return int
     */
    public int getPort()
    {
        return this.port;
    }

    /**
     * @return boolean
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     * @return boolean
     */
    public boolean isNoSystemExit()
    {
        return this.noSystemExit;
    }

    /**
     * @return boolean
     */
    public boolean isSilent()
    {
        return this.silent;
    }

    /**
     * @return boolean
     */
    public boolean isTrace()
    {
        return this.trace;
    }

    /**
     * @param db List<DB>
     */
    public void setDb(final List<DB> db)
    {
        this.db = db;
    }

    /**
     * @param enabled boolean
     */
    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * @param noSystemExit boolean
     */
    public void setNoSystemExit(final boolean noSystemExit)
    {
        this.noSystemExit = noSystemExit;
    }

    /**
     * @param port int
     */
    public void setPort(final int port)
    {
        this.port = port;
    }

    /**
     * @param silent boolean
     */
    public void setSilent(final boolean silent)
    {
        this.silent = silent;
    }

    /**
     * @param trace boolean
     */
    public void setTrace(final boolean trace)
    {
        this.trace = trace;
    }
}
