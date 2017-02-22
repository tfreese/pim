// Created: 14.02.2017
package de.freese.pim.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import de.freese.pim.common.spring.config.PIMProfile;

/**
 * Startklasse des PIM-Servers.<br>
 *
 * @author Thomas Freese
 */
@SpringBootApplication
@EnableAsync
public class PIMServerApplication
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // SpringApplication.run(Application.class, args);
        //
        // @formatter:off
        SpringApplication application = new SpringApplicationBuilder(PIMServerApplication.class)
//                .properties("spring.config.name:application-serverstandalone")
                .headless(true) // Default true
                .registerShutdownHook(true) // Default true
                .profiles(PIMProfile.Server.toString(), PIMProfile.HsqldbEmbeddedServer.toString())
                //.banner(new MyBanner())
                //.listeners(new ApplicationPidFileWriter("pim-server.pid"))
                //.run(args)
                .build();
        // @formatter:on

        application.run(args);
    }

    /**
     * Erzeugt eine neue Instanz von {@link PIMServerApplication}
     */
    public PIMServerApplication()
    {
        super();
    }
}
