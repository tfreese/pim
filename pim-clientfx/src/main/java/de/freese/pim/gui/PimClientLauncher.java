// Created: 04.03.2021
package de.freese.pim.gui;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.sun.javafx.application.LauncherImpl;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.help.HelpFormatter;

import de.freese.pim.gui.view.ErrorDialog;

/**
 * @author Thomas Freese
 */
public final class PimClientLauncher {
    static void main(final String[] args) {
        if (args.length == 0) {
            usage();
        }

        // CommandLine line = null;

        try {
            final CommandLineParser parser = new DefaultParser();
            parser.parse(getCommandOptions(), args);
            // line = parser.parse(getCommandOptions(), args);
        }
        catch (Exception ex) {
            PimClientApplication.LOGGER.error(ex.getMessage());

            usage();
        }

        // java.util.logging ausschalten.
        // LogManager.getLogManager().reset();
        // for (String name : Arrays.asList(java.util.logging.Logger.GLOBAL_LOGGER_NAME, "com.sun.webkit.perf.Locks",
        // "com.sun.webkit.perf.WCGraphicsPerfLogger",
        // "com.sun.webkit.perf.WCFontPerfLogger")) {
        // java.util.logging.Logger javaLogger = java.util.logging.Logger.getLogger(name);
        // javaLogger.setLevel(java.util.logging.Level.OFF);
        // }

        // System.setProperty("org.slf4j.simpleLogger.log.de.freese.pim", "DEBUG");
        // SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
            PimClientApplication.LOGGER.error("***Default exception handler***");
            PimClientApplication.LOGGER.error(ex.getMessage(), ex);

            new ErrorDialog().forThrowable(ex).showAndWait();
        });

        LauncherImpl.launchApplication(PimClientApplication.class, PimClientPreloader.class, args);
    }

    /**
     * Liefert die möglichen Optionen der Kommandozeile.<br>
     * Dies sind die JRE Programm Argumente.
     */
    private static Options getCommandOptions() {
        final Options options = new Options();

        // --spring.profiles.active=ClientStandalone,HsqldbEmbeddedServer
        // --spring.profiles.active=ClientREST --server.host=localhost --server.port=61222

        // OptionGroup group = new PreserveOrderOptionGroup();
        // group.addOption(Option.builder().longOpt("spring.profiles.active").required().desc("=Profile1,Profile2").build());
        // group.addOption(Option.builder().longOpt("server.host").required().hasArg().argName("=host").desc("Server Name").build());
        // group.addOption(Option.builder().longOpt("server.port").required().hasArg().argName("=port").desc("Server Port").build());
        // options.addOptionGroup(group);

        options.addOption(Option.builder().longOpt("spring.profiles.active").required().hasArg().argName("Profile1,Profile2").valueSeparator('=')
                .desc("Profiles: [ClientStandalone,HsqldbEmbeddedServer], [ClientEmbeddedServer,HsqldbEmbeddedServer], [ClientRest]").get());
        options.addOption(Option.builder().longOpt("server.host").hasArg().argName("=host").valueSeparator('=').desc("Server Name").get());
        options.addOption(Option.builder().longOpt("server.port").hasArg().argName("=port").valueSeparator('=').desc("Server Port").get());

        return options;
    }

    private static void usage() {
        final HelpFormatter formatter = HelpFormatter.builder()
                .setShowSince(false)
                .get();

        final StringBuilder footer = new StringBuilder();
        // footer.append("\nNamen / Werte mit Leerzeichen sind mit \"'... ...'\" anzugeben.");
        footer.append("\n@Thomas Freese");

        try {
            formatter.printHelp("P.I.M. Client\n", "\nParameter:", getCommandOptions(), footer.toString(), true);
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        System.exit(-1);
    }

    private PimClientLauncher() {
        super();
    }
}
