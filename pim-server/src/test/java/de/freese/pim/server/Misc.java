// Created: 15.01.2017
package de.freese.pim.server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;

import de.freese.pim.common.utils.Utils;

/**
 * @author Thomas Freese
 */
public class Misc
{
    /**
     * @throws Exception Falls was schief geht.
     */
    public static void mailFiles() throws Exception
    {
        Path basePath = AbstractPimTest.HOME_DEFAULT.resolve("commercial@freese-home.de");

        Consumer<Path> log = p -> System.out.printf("%s: FileName = %s%n", p, p.getFileName());

        Files.list(basePath).filter(Utils.PREDICATE_MAIL_FOLDER).forEach(log);
        System.out.println();
    }

    /**
     * @param args String[]
     *
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // testFiles();
        // mailFiles();

        Path basePath = AbstractPimTest.TMP_TEST_PATH;
        Path path = basePath.resolve("arbeit/java/mail.txt");
        System.out.println(path);
        Files.createDirectories(path.getParent());
        Files.createFile(path);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    public static void testFiles() throws Exception
    {
        Path basePath = AbstractPimTest.TMP_TEST_PATH.resolve("misc");

        Path path = basePath.resolve("dir1");
        Files.createDirectories(path);

        path = basePath.resolve("test1.dat");
        if (!Files.exists(path))
        {
            Files.createFile(path);
        }

        path = basePath.resolve(".test2.dat");
        if (!Files.exists(path))
        {
            Files.createFile(path);
        }

        Consumer<Path> log = p -> System.out.printf("%s: FileName = %s%n", p, p.getFileName());

        Predicate<Path> isDirectory = Files::isDirectory;
        // Predicate<Path> isHidden = p -> p.getFileName().toString().startsWith(".");
        Predicate<Path> isHidden = p -> {
            try
            {
                return Files.isHidden(p);
            }
            catch (Exception ex)
            {
                return false;
            }
        };

        Files.list(basePath).filter(isDirectory.negate().and(isHidden.negate())).forEach(log);
        System.out.println();
        Files.list(basePath).filter(isDirectory.and(isHidden.negate())).forEach(log);
    }
}
