// Created: 14.02.2017
package de.freese.pim.server;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Startklasse des PIM-Servers.<br>
 *
 * @author Thomas Freese
 */
@SpringBootApplication
@EnableAsync
// @EnableTransactionManagement // Wird durch Spring-Boot automatisch konfiguriert, wenn DataSource-Bean vorhanden.
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class PimServerApplication {
    public static final Logger LOGGER = LoggerFactory.getLogger(PimServerApplication.class);

    public static void main(final String[] args) {
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
            LOGGER.error(ex.getMessage());

            usage();
        }

        // SpringApplication.run(Application.class, args);
        //
        // @formatter:off
        final SpringApplication application = new SpringApplicationBuilder(PimServerApplication.class)
//                .properties("spring.config.name:application-Server")
                .headless(true) // Default true
                .registerShutdownHook(true) // Default true
//                .profiles(PIMProfile.Server.toString(), PIMProfile.HsqldbEmbeddedServer.toString())
                //.banner(new MyBanner())
                //.listeners(new ApplicationPidFileWriter("pim-server.pid"))
                //.run(args)
                .build();
        // @formatter:on

        application.run(args);
    }

    /**
     * Liefert die möglichen Optionen der Kommandozeile.<br>
     * Dies sind die JRE Programm Argumente.
     */
    private static Options getCommandOptions() {
        final Options options = new Options();

        // --spring.profiles.active=Server,HsqldbEmbeddedServer --server.port=61222
        // --spring.profiles.active=Server,SqliteLocalFile --server.port=61222
        options.addOption(Option.builder().longOpt("spring.profiles.active").required().hasArg().argName("Profile1,Profile2").valueSeparator('=').desc("Profiles: [Server,HsqldbEmbeddedServer]").build());
        options.addOption(Option.builder().longOpt("server.port").required().hasArg().argName("port").valueSeparator('=').desc("Server Port").build());

        return options;
    }

    private static void usage() {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        // formatter.setWidth(120);
        // formatter.printHelp("P.I.M.\n", getCommandOptions(), true);

        final StringBuilder footer = new StringBuilder();
        // footer.append("\nNamen / Werte mit Leerzeichen sind mit \"'... ...'\" anzugeben.");
        footer.append("\n@Thomas Freese");

        formatter.printHelp(120, "P.I.M. Server\n", "\nParameter:", getCommandOptions(), footer.toString(), true);

        System.exit(-1);
    }
}
