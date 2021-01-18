// Created: 29.11.2016
package de.freese.pim.common.utils;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.rightPad;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.common.function.ExceptionalRunnable;
import de.freese.pim.common.function.ExceptionalSupplier;

/**
 * Utils.
 *
 * @author Thomas Freese
 */
public final class Utils
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    /**
     * ^(.+)@(.+)\\.\\w{2,3}$
     */
    public static final String MAIL_REGEX = "^(.+)@(.+)\\.[a-zA-Z]{2,3}$";

    /**
     *
     */
    public static final Predicate<Path> PREDICATE_IS_DIR = Files::isDirectory;

    /**
     *
     */
    public static final Predicate<Path> PREDICATE_IS_DIR_NOT = PREDICATE_IS_DIR.negate();
    /**
     * p -> p.getFileName().toString().startsWith(".");
     */
    public static final Predicate<Path> PREDICATE_IS_HIDDEN = p -> {
        try
        {
            return Files.isHidden(p);
        }
        catch (Exception ex)
        {
            return false;
        }
    };

    /**
     *
     */
    public static final Predicate<Path> PREDICATE_IS_HIDDEN_NOT = PREDICATE_IS_HIDDEN.negate();

    /**
     *
     */
    public static final Predicate<Path> PREDICATE_MAIL_FOLDER = PREDICATE_IS_DIR_NOT.or(PREDICATE_IS_HIDDEN_NOT);

    /**
    *
    */
    public static final Predicate<Path> PREDICATE_MAIL_FOLDER_LEAF = p -> p.getFileName().toString().equals(".leaf");

    /**
    *
    */
    public static final Predicate<Path> PREDICATE_MAIL_FOLDER_LEAF_NOT = PREDICATE_MAIL_FOLDER_LEAF.negate();

    /**
    *
    */
    private static final String SYSTEM_USER_NAME = System.getProperty("user.name").toUpperCase();

    /**
     * Fügt am Index 1 der Liste eine Trenn-Linie ein.<br>
     * Die Breite pro Spalte orientiert sich am ersten Wert (Header) der Spalte. Ist der Separator null oder leer wird nichts gemacht.<br>
     *
     * @param <T> Konkreter Typ
     * @param rows {@link List}
     * @param separator String
     */
    @SuppressWarnings("unchecked")
    public static <T extends CharSequence> void addHeaderSeparator(final List<T[]> rows, final String separator)
    {
        if ((rows == null) || rows.isEmpty() || isEmpty(separator))
        {
            return;
        }

        int columnCount = rows.get(0).length;

        // Trenner zwischen Header und Daten.
        // T[] row = (T[]) Array.newInstance(String.class, columnCount);
        // T[] row = Arrays.copyOf(rows.get(0), columnCount);
        // T[] row = rows.get(0).clone();
        String[] row = new String[columnCount];

        for (int column = 0; column < columnCount; column++)
        {
            row[column] = org.apache.commons.lang3.StringUtils.repeat(separator, rows.get(0)[column].length());
        }

        rows.add(1, (T[]) row);
    }

    /**
     * Löscht das Verzeichnis rekursiv inklusive Dateien und Unterverzeichnisse.
     *
     * @param path {@link Path}
     * @throws IOException Falls was schief geht.
     */
    public static void deleteDirectoryRecursiv(final Path path) throws IOException
    {
        if (!Files.isDirectory(path))
        {
            throw new IllegalArgumentException("path is not a dirctory: " + path);
        }

        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            /**
             * @see java.nio.file.SimpleFileVisitor#postVisitDirectory(java.lang.Object, java.io.IOException)
             */
            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException
            {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            /**
             * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
             */
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
            {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Führt den {@link ExceptionalRunnable} in einem try-catch aus, der im Fehlerfall eine {@link RuntimeException} wirft.
     *
     * @param task {@link ExceptionalRunnable}
     */
    public static void executeSafely(final ExceptionalRunnable<?> task)
    {
        try
        {
            task.run();
        }
        catch (RuntimeException rex)
        {
            throw rex;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Führt den {@link ExceptionalSupplier} in einem try-catch aus, der im Fehlerfall eine {@link RuntimeException} wirft.
     *
     * @param <R> Konkreter Return-Typ
     * @param supplier {@link ExceptionalSupplier}
     * @return Object
     */
    public static <R> R executeSafely(final ExceptionalSupplier<R, ?> supplier)
    {
        try
        {
            return supplier.get();
        }
        catch (RuntimeException rex)
        {
            throw rex;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Liefert alle Attribute einer Klasse, die annotiert sind.
     *
     * @param object Object
     * @param annotation Class
     * @return {@link Set}
     */
    public static Set<Field> getAnnotatedFields(final Object object, final Class<? extends Annotation> annotation)
    {
        // @formatter:off
        Set<Field> set = Stream.of(object.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(annotation))
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toSet());
        // @formatter:on

        return set;
    }

    /**
     * Liefert die eigentliche {@link Exception}, falls diese z.b. noch in einer {@link RuntimeException} verpackt ist.
     *
     * @param exception {@link Exception}
     * @return {@link Exception}
     */
    public static Exception getCause(final Exception exception)
    {
        Throwable th = exception;

        if (exception instanceof RuntimeException)
        {
            th = ((RuntimeException) exception).getCause();
        }

        return (Exception) th;
    }

    /**
     * @return {@link ClassLoader}
     */
    public static ClassLoader getDefaultClassLoader()
    {
        ClassLoader cl = null;

        try
        {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex)
        {
            // NO-OP
        }

        if (cl == null)
        {
            try
            {
                cl = Utils.class.getClassLoader();
            }
            catch (Throwable ex)
            {
                // NO-OP
            }
        }

        if (cl == null)
        {
            try
            {
                cl = ClassLoader.getSystemClassLoader();
            }
            catch (Throwable ex)
            {
                // NO-OP
            }
        }

        return cl;
    }

    /**
     * Liefert die {@link Method} aus der Bean.<br>
     *
     * @param bean Object
     * @param name String
     * @param parameterTypes Class[]
     * @return {@link Method}
     * @throws RuntimeException Falls was schief geht.
     */
    public static Method getMethod(final Object bean, final String name, final Class<?>...parameterTypes) throws RuntimeException
    {
        try
        {
            return bean.getClass().getMethod(name, parameterTypes);
        }
        catch (NoSuchMethodException | SecurityException ex)
        {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Liefert den nächsten nicht belegten Port.
     *
     * @param fallbackPort int; Falls die Port-Ermittlung fehlschlägt
     * @return int
     */
    public static int getNextFreePort(final int fallbackPort)
    {
        int nextFreePort = -1;

        try (ServerSocket serverSocket = new ServerSocket(0))
        {
            nextFreePort = serverSocket.getLocalPort();
        }
        catch (IOException ioe)
        {
            // Ignore
        }

        if (nextFreePort == -1)
        {
            nextFreePort = fallbackPort;
        }

        return nextFreePort;
    }

    /**
     * Liefert den Namen des angemeldeten Systemusers für HSQLDB Function SYSTEM_USER_NAME.
     *
     * @return String
     */
    public static String getSystemUserName()
    {
        return SYSTEM_USER_NAME;
    }

    /**
     * Liefert das Value aus dem {@link Field} der Bean.<br>
     *
     * @param field {@link Field}
     * @param bean Object
     * @return Object
     * @throws RuntimeException Falls was schief geht.
     */
    public static Object getValue(final Field field, final Object bean) throws RuntimeException
    {
        try
        {
            return field.get(bean);
        }
        catch (IllegalArgumentException | IllegalAccessException ex)
        {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Ruft die {@link Method#invoke(Object, Object...)} Mehode der Bean auf.
     *
     * @param method {@link Method}
     * @param bean Object
     * @param args Object[]
     * @return Object
     */
    public static Object invokeMethod(final Method method, final Object bean, final Object...args)
    {
        try
        {
            return method.invoke(bean, args);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Die Spaltenbreite der Elemente wird auf den breitesten Wert durch das Padding aufgefüllt.<br>
     * Ist das Padding null oder leer wird nichts gemacht.<br>
     * Beim Padding werden die CharSequences durch Strings ersetzt.
     *
     * @param <T> Konkreter Typ
     * @param rows {@link List}
     * @param padding String
     * @see #write(List, PrintStream, String)
     */
    @SuppressWarnings("unchecked")
    public static <T extends CharSequence> void padding(final List<T[]> rows, final String padding)
    {
        if ((rows == null) || rows.isEmpty() || isEmpty(padding))
        {
            return;
        }

        int columnCount = rows.get(0).length;

        // Breite pro Spalte rausfinden.
        int[] columnWidth = new int[columnCount];

        // @formatter:off
        IntStream.range(0, columnCount).forEach(column ->
                    columnWidth[column] = rows.stream()
                            .parallel()
                            .map(r -> r[column])
                            .mapToInt(CharSequence::length)
                            .max()
                            .orElse(0)
        );
        // @formatter:on

        // Strings pro Spalte formatieren und schreiben.
        rows.stream().parallel().forEach(r -> {
            for (int column = 0; column < columnCount; column++)
            {
                String value = rightPad(r[column].toString(), columnWidth[column], padding);

                r[column] = (T) value;
            }
        });
    }

    /**
     * Shutdown eines {@link ExecutorService}.
     *
     * @param executorService {@link ExecutorService}
     */
    public static void shutdown(final ExecutorService executorService)
    {
        executorService.shutdown();

        int timeOut = 10;

        while (!executorService.isTerminated())
        {
            try
            {
                // Warten bis laufende Tasks sich beenden.
                if (!executorService.awaitTermination(timeOut, TimeUnit.SECONDS))
                {
                    executorService.shutdownNow();

                    // Laufende Tasks abbrechen und warten auf Rückmeldung.
                    if (!executorService.awaitTermination(timeOut, TimeUnit.SECONDS))
                    {
                        LOGGER.error("Pool did not terminate");
                    }
                }
            }
            catch (InterruptedException ie)
            {
                // Abbruch wenn laufender Thread interruped ist.
                executorService.shutdownNow();

                // Interruptd Status signalisieren.
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Lässt den aktuellen Thread schlafen.
     *
     * @param duration long
     * @param timeUnit {@link TimeUnit}
     */
    public static void sleep(final long duration, final TimeUnit timeUnit)
    {
        try
        {
            Thread.sleep(timeUnit.toMillis(duration));
        }
        catch (Exception ex)
        {
            // Ignore
        }
    }

    /**
     * Erzeugt aus dem {@link ResultSet} eine Liste mit den Column-Namen in der ersten Zeile und den Daten.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link List}
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public static List<String[]> toList(final ResultSet resultSet) throws SQLException
    {
        Objects.requireNonNull(resultSet, () -> "resultSet required");

        List<String[]> rows = new ArrayList<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Spaltennamen / Header
        String[] header = new String[columnCount];
        rows.add(header);

        for (int column = 1; column <= columnCount; column++)
        {
            header[column - 1] = metaData.getColumnLabel(column).toUpperCase();
        }

        // Daten
        while (resultSet.next())
        {
            String[] row = new String[columnCount];
            rows.add(row);

            for (int column = 1; column <= columnCount; column++)
            {
                Object obj = resultSet.getObject(column);
                row[column - 1] = (obj == null) ? "" : obj.toString();
            }
        }

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
        {
            resultSet.first();
        }

        return rows;
    }

    /**
     * Schreibt die Liste in den PrintStream.<br>
     * Der Stream wird nicht geschlossen.
     *
     * @param <T> Konkreter Typ von CharSequence
     * @param rows {@link List}
     * @param ps {@link PrintStream}
     * @param delimiter String
     * @see #padding(List, String)
     */
    @SuppressWarnings("resource")
    public static <T extends CharSequence> void write(final List<T[]> rows, final PrintStream ps, final String delimiter)
    {
        Objects.requireNonNull(rows, () -> "rows required");
        Objects.requireNonNull(ps, () -> "printStream required");

        if (rows.isEmpty())
        {
            return;
        }

        int columnCount = rows.get(0).length;

        // Strings pro Spalte schreiben, parallel() verfälscht die Reihenfolge.
        rows.forEach(r -> {
            for (int column = 0; column < columnCount; column++)
            {
                ps.print(r[column]);

                if ((column < (columnCount - 1)) && isNotBlank(delimiter))
                {
                    ps.print(delimiter);
                }
            }

            ps.println();
        });

        ps.flush();
    }

    /**
     * Schreibt das ResultSet in den PrintStream.<br>
     * Dabei wird die Spaltenbreite auf den breitesten Wert angepasst.<br>
     * Der Stream wird nicht geschlossen.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @param ps {@link PrintStream}
     * @throws SQLException Falls was schief geht.
     */
    public static void write(final ResultSet resultSet, final PrintStream ps) throws SQLException
    {
        List<String[]> rows = toList(resultSet);
        padding(rows, " ");
        addHeaderSeparator(rows, "-");

        write(rows, ps, " | ");

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
        {
            resultSet.first();
        }
    }

    /**
     * Erzeugt eine neue Instanz von {@link Utils}
     */
    private Utils()
    {
        super();
    }
}
