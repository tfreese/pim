// Created: 04.03.2021
package de.freese.pim.gui;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import com.sun.javafx.application.LauncherImpl;
import de.freese.pim.gui.view.ErrorDialog;

/**
 * @author Thomas Freese
 */
public class PIMLauncher
{
    /**
     * Liefert die möglichen Optionen der Kommandozeile.<br>
     * Dies sind die JRE Programm Argumente.
     *
     * @return {@link Options}
     */
    private static Options getCommandOptions()
    {
        Options options = new Options();

        // --spring.profiles.active=ClientStandalone,HsqldbEmbeddedServer
        // --spring.profiles.active=ClientREST --server.host=localhost --server.port=61222
        // --spring.profiles.active=ClientEmbeddedServer,HsqldbEmbeddedServer
        // --spring.profiles.active=ClientEmbeddedServer,SqliteLocalFile

        // OptionGroup group = new PreserveOrderOptionGroup();
        // group.addOption(Option.builder().longOpt("spring.profiles.active").required().desc("=Profile1,Profile2").build());
        // group.addOption(Option.builder().longOpt("server.host").required().hasArg().argName("=host").desc("Server Name").build());
        // group.addOption(Option.builder().longOpt("server.port").required().hasArg().argName("=port").desc("Server Port").build());
        // options.addOptionGroup(group);

        options.addOption(Option.builder().longOpt("spring.profiles.active").required().hasArg().argName("=Profile1,Profile2").valueSeparator('=')
                .desc("Profiles: [ClientStandalone,HsqldbEmbeddedServer], [ClientEmbeddedServer,HsqldbEmbeddedServer], [ClientREST]").build());
        options.addOption(Option.builder().longOpt("server.host").hasArg().argName("=host").valueSeparator('=').desc("Server Name").build());
        options.addOption(Option.builder().longOpt("server.port").hasArg().argName("=port").valueSeparator('=').desc("Server Port").build());

        return options;
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        if (args.length == 0)
        {
            usage();
        }

        // CommandLine line = null;

        try
        {
            CommandLineParser parser = new DefaultParser();
            parser.parse(getCommandOptions(), args);
            // line = parser.parse(getCommandOptions(), args);
        }
        catch (Exception ex)
        {
            PIMApplication.LOGGER.error(ex.getMessage());

            usage();
        }

        // java.util.logging ausschalten.
        // LogManager.getLogManager().reset();
        // for (String name : Arrays.asList(java.util.logging.Logger.GLOBAL_LOGGER_NAME, "com.sun.webkit.perf.Locks",
        // "com.sun.webkit.perf.WCGraphicsPerfLogger",
        // "com.sun.webkit.perf.WCFontPerfLogger"))
        // {
        // java.util.logging.Logger javaLogger = java.util.logging.Logger.getLogger(name);
        // javaLogger.setLevel(java.util.logging.Level.OFF);
        // }

        // System.setProperty("org.slf4j.simpleLogger.log.de.freese.pim", "DEBUG");
        // SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
            PIMApplication.LOGGER.error("***Default exception handler***");
            PIMApplication.LOGGER.error(null, ex);

            new ErrorDialog().forThrowable(ex).showAndWait();
        });

        Runnable task = () -> {
            // launch(args);
            LauncherImpl.launchApplication(PIMApplication.class, PIMPreloader.class, args);
        };

        task.run();

        // // Eigene ThreadGroup für Handling von Runtime-Exceptions.
        // PIMFXThreadGroup threadGroup = new PIMFXThreadGroup();
        //
        // // Kein Thread des gesamten Clients kann eine höhere Prio haben.
        // threadGroup.setMaxPriority(Thread.NORM_PRIORITY + 1);
        //
        // Thread thread = new Thread(threadGroup, task, "PIM-Startup");
        // // thread.setDaemon(false);
        // thread.start();
    }

    /**
    *
    */
    private static void usage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        // formatter.setWidth(120);
        // formatter.printHelp("P.I.M.\n", getCommandOptions(), true);

        StringBuilder footer = new StringBuilder();
        // footer.append("\nNamen / Werte mit Leerzeichen sind mit \"'... ...'\" anzugeben.");
        footer.append("\n@Thomas Freese");

        formatter.printHelp(120, "P.I.M. Client\n", "\nParameter:", getCommandOptions(), footer.toString(), true);

        System.exit(-1);
    }
}
