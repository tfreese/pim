// Created: 30.11.2016
package de.freese.pim.core.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Erstellt die DB-Struktur anhand der definierten SQL-Skripte.<br>
 * Ein SQL muss immer mit einem ';' abgeschlossen sein.
 *
 * @author Thomas Freese
 */
public class DatabasePopulator
{
    /**
    *
    */
    public static final Logger LOGGER = LoggerFactory.getLogger(DatabasePopulator.class);

    /**
     *
     */
    private final List<String> scripts = new ArrayList<>();

    /**
     * Erzeugt eine neue Instanz von {@link DatabasePopulator}
     */
    public DatabasePopulator()
    {
        super();
    }

    /**
     * Fügt ein SQL-Skript hinzu,
     *
     * @param script String
     */
    public void addScript(final String script)
    {
        this.scripts.add(script);
    }

    /**
     * Erstellt die DB-Struktur anhand der definierten SQL-Skripte.
     *
     * @param connection {@link Connection}
     * @throws Exception Falls was schief geht.
     */
    public void populate(final Connection connection) throws Exception
    {
        for (String script : this.scripts)
        {
            List<String> sqls = getScriptSQLs(script);

            // sqls.forEach(System.out::println);
            try (Statement statement = connection.createStatement();)
            {
                for (String sql : sqls)
                {
                    LOGGER.debug(sql);
                    statement.execute(sql);

                    // int rowsAffected = statement.getUpdateCount();
                    //
                    // LOGGER.info("{}: Rows affected = {}", sql, rowsAffected);
                }
            }
        }
    }

    /**
     * Erstellt die DB-Struktur anhand der definierten SQL-Skripte.
     *
     * @param dataSource {@link DataSource}
     * @throws Exception Falls was schief geht.
     */
    public void populate(final DataSource dataSource) throws Exception
    {
        try (Connection connection = dataSource.getConnection())
        {
            populate(connection);
        }
    }

    /**
     * Liefert die Zeilen aus dem SQL-Skript.
     *
     * @param script String
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    protected List<String> getScriptLines(final String script) throws Exception
    {
        List<String> fileLines = null;

        URL url = getClass().getClassLoader().getResource(script);

        if (url != null)
        {
            // Funktioniert nicht, wenn die Skripte in eine anderen Archiv liegen.
            try
            {
                Path path = Paths.get(url.toURI());

                fileLines = Files.lines(path).collect(Collectors.toList());
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }

        if (fileLines == null)
        {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(script);
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
            {
                fileLines = bufferedReader.lines().collect(Collectors.toList());
            }
        }

        // @formatter:off
        List<String> scriptLines = fileLines.stream()
                .map(String::trim)
                .filter(l -> !l.startsWith("--"))
                .filter(l -> l.length() > 0)
                .collect(Collectors.toList());
        // @formatter:on

        return scriptLines;
    }

    /**
     * Liefert die SQLs aus dem Skript.
     *
     * @param script String
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    protected List<String> getScriptSQLs(final String script) throws Exception
    {
        List<String> scriptLines = getScriptLines(script);

        List<String> sqls = new ArrayList<>();
        sqls.add(scriptLines.get(0));

        // SQLs sind immer mit einem ';' abgeschlossen.
        for (int i = 1; i < scriptLines.size(); i++)
        {
            String prevSql = sqls.get(sqls.size() - 1);
            String line = scriptLines.get(i);

            if (!prevSql.endsWith(";"))
            {
                sqls.set(sqls.size() - 1, prevSql + " " + line);
            }
            else
            {
                sqls.add(line);
            }
        }

        return sqls;
    }
}
