package de.freese.pim.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;

import com.sun.javafx.application.LauncherImpl;

import de.freese.pim.gui.main.MainController;
import de.freese.pim.gui.utils.FXUtils;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Startklasse der Personal Information Management Anwendung.<br>
 * https://www.iconfinder.com/
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
@SpringBootApplication()
// @EnableScheduling
// @EnableAsync // @Async("executorService")
// @EnableTransactionManagement // Wird durch Spring-Boot automatisch konfiguriert, wenn DataSource-Bean vorhanden.
public class PIMApplication extends Application implements ApplicationContextAware
{
    /**
     *
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(PIMApplication.class);

    /**
     *
     */
    private static ApplicationContext applicationContext = null;

    /**
     *
     */
    private static Window mainWindow = null;

    /**
     * Bildschirm auf dem PIM läuft.
     */
    private static Screen screen = null;

    /**
     * Blockiert die GUI.
     */
    public static void blockGUI()
    {
        FXUtils.blockGUI(getMainWindow());
    }

    /**
     * @return {@link ApplicationContext}
     */
    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * @return {@link Window}
     */
    public static Window getMainWindow()
    {
        return mainWindow;
    }

    /**
     * Liefert die Resource.
     *
     * @param location String
     * @return {@link Resource}
     */
    public static Resource getResource(final String location)
    {
        return getApplicationContext().getResource(location);
    }

    /**
     * Liefert den Bildschirm auf dem PIM läuft.
     *
     * @return {@link Screen}
     */
    public static Screen getScreen()
    {
        return screen;
    }

    /**
     * @return {@link AsyncTaskExecutor}
     */
    public static AsyncTaskExecutor getTaskExecutor()
    {
        return getApplicationContext().getBean("taskExecutor", AsyncTaskExecutor.class);
    }

    /**
     * @return {@link TaskScheduler}
     */
    public static TaskScheduler getTaskScheduler()
    {
        return getApplicationContext().getBean("taskScheduler", TaskScheduler.class);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the application can not
     * be launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
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
            LOGGER.error(ex.getMessage());

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
        Thread.setDefaultUncaughtExceptionHandler((t, ex) ->
        {
            LOGGER.error("***Default exception handler***");
            LOGGER.error(null, ex);

            new ErrorDialog().forThrowable(ex).showAndWait();
        });

        Runnable task = () ->
        {
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
     * Hebt die GUI Blockade wieder auf..
     */
    public static void unblockGUI()
    {
        FXUtils.unblockGUI(getMainWindow());
    }

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
        // --spring.profiles.active=ClientREST --server.host=localhost --server.port=61223
        // --spring.profiles.active=ClientEmbeddedServer,HsqldbEmbeddedServer

        // OptionGroup group = new PreserveOrderOptionGroup();
        // group.addOption(Option.builder().longOpt("spring.profiles.active").required().desc("=Profile1,Profile2").build());
        // group.addOption(Option.builder().longOpt("server.host").required().hasArg().argName("=host").desc("Server Name").build());
        // group.addOption(Option.builder().longOpt("server.port").required().hasArg().argName("=port").desc("Server Port").build());
        // options.addOptionGroup(group);

        options.addOption(
                Option.builder().longOpt("spring.profiles.active").required().hasArg().argName("=Profile1,Profile2").valueSeparator('=')
                        .desc("Profiles: [ClientStandalone,HsqldbEmbeddedServer], [ClientEmbeddedServer,HsqldbEmbeddedServer], [ClientREST]")
                        .build());
        options.addOption(
                Option.builder().longOpt("server.host").hasArg().argName("=host").valueSeparator('=').desc("Server Name").build());
        options.addOption(
                Option.builder().longOpt("server.port").hasArg().argName("=port").valueSeparator('=').desc("Server Port").build());

        return options;
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

    /**
     *
     */
    private BooleanProperty ready = new SimpleBooleanProperty(false);

    /**
     * @see javafx.application.Application#init()
     */
    @Override
    public void init() throws Exception
    {
        // "JavaFX-Launcher" umbenennen.
        Thread.currentThread().setName("JavaFX-Init.");

        String[] args = getParameters().getRaw().toArray(new String[0]);
        // List<String> parameters = getParameters().getRaw();
        // String[] profiles = null;
        //
        // if (CollectionUtils.isEmpty(parameters))
        // {
        // profiles = new String[]
        // {
        // PIMProfile.ClientStandalone.toString(), PIMProfile.HsqldbEmbeddedServer.toString()
        // };
        // }
        // else
        // {
        // profiles = parameters.toArray(new String[0]);
        // }

        notifyPreloader(new PIMPreloaderNotification("Init Springframework"));

        // SpringApplication.run(Application.class, args);
        //
        // @formatter:off
        SpringApplication application = new SpringApplicationBuilder(PIMApplication.class)
                .headless(false) // Default true, hier false wegen JavaFX
//                .web(false) // Wird eigentlich automatisch ermittelt.
//                .profiles(profiles)
                .registerShutdownHook(true) // Default true
                //.banner(new MyBanner())
                //.listeners(new ApplicationPidFileWriter("pim-client.pid"))
                //.run(args)
                .build();
//        SpringApplicationBuilder()
//        .parent(RootContext.class)
//        .child(ChildContext1.class)
//        .sibling(ChildContext2.class);
//        SpringApplicationBuilder()
//        .bannerMode(Banner.Mode.OFF)
//        .sources(Parent.class)
//        .child(Application.class)
//        .run(args);
//        SpringApplication application = new SpringApplicationBuilder(PIMCommonConfig.class)
//                .headless(false) // Default true, hier false wegen Swing
//                .web(false) // Wird eigentlich automatisch ermittelt.
//                .profiles(profile)
//                .registerShutdownHook(true) // Default true
//                //.banner(new MyBanner())
//                //.listeners(new ApplicationPidFileWriter("eps-monitor.pid"))
//                //.run(args)
////                .parent(PIMCommonConfig.class)
//                .child(PIMClientConfig.class)
//                .child(PIMServerApplication.class)
//                .build();
        // @formatter:on

        application.run(args);

        // getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);

        LOGGER.info("Start P.I.M.");
        notifyPreloader(new PIMPreloaderNotification("Start P.I.M."));
        // Utils.sleep(1, TimeUnit.SECONDS);

        String pimHome = getApplicationContext().getEnvironment().getProperty("pim.home");
        Path homePath = Paths.get(pimHome);

        if (!Files.exists(homePath))
        {
            Files.createDirectories(homePath);
        }

        // getHostServices().showDocument("https://eclipse.org");
        FXUtils.tooltipBehaviorHack();
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext ctx) throws BeansException
    {
        applicationContext = ctx;
    }

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws Exception
    {
        try
        {
            // "JavaFX Application Thread" umbenennen.
            Thread.currentThread().setName("JavaFX-Appl.");

            PIMApplication.mainWindow = primaryStage;

            notifyPreloader(new PIMPreloaderNotification("Init GUI"));
            // setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
            // setUserAgentStylesheet(Application.STYLESHEET_MODENA);

            ResourceBundle resources = ResourceBundle.getBundle("bundles/pim");

            MainController mainController = new MainController(resources);

            Scene scene = new Scene((Parent) mainController.getMainNode());
            // Scene scene = new Scene((Parent) mainController.getMainNode(), 1400, 900);
            scene.getStylesheets().add("/styles/styles.css");

            primaryStage.getIcons().add(new Image("images/pim.png"));
            primaryStage.setTitle(resources.getString("titel"));
            primaryStage.setScene(scene);
            // primaryStage.centerOnScreen();
            // primaryStage.setMaximized(true);

            // Default: GUI auf 2. Monitor, wenn vorhanden.
            ObservableList<Screen> screens = Screen.getScreens();

            if (screens.size() > 1)
            {
                screen = screens.get(1);
            }
            else
            {
                screen = screens.get(0);
            }

            // Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Rectangle2D screenBounds = screen.getVisualBounds();

            primaryStage.setX(screenBounds.getMinX() + 100);
            primaryStage.setY(screenBounds.getMinY() + 100);
            primaryStage.setWidth(screenBounds.getWidth() - 200);
            primaryStage.setHeight(screenBounds.getHeight() - 200);

            // After the app is ready, show the stage
            this.ready.addListener((observable, oldValue, newValue) ->
            {
                if (Boolean.TRUE.equals(newValue))
                {
                    Platform.runLater(() ->
                    {
                        primaryStage.show();

                        // System.setOut(new PrintStream(new TextAreaOutputStream(MainView.LOG_TEXT_AREA)));
                        // System.setErr(new PrintStream(new TextAreaOutputStream(MainView.LOG_TEXT_AREA)));
                        mainController.selectDefaultView();
                    });
                }
            });

            // After init is ready, the app is ready to be shown.
            // Do this before hiding the preloader stage to prevent the app from exiting prematurely.
            // notifyPreloader(new ProgressNotification(1.0D));
            this.ready.setValue(Boolean.TRUE);
            notifyPreloader(new StateChangeNotification(StateChangeNotification.Type.BEFORE_START));

            // // After init is ready, the app is ready to be shown.
            // // Do this before hiding the preloader stage to prevent the app from exiting prematurely.
            // PIMApplication.this.ready.setValue(Boolean.TRUE);
            // notifyPreloader(new StateChangeNotification(StateChangeNotification.Type.BEFORE_START));
            // }).start();
            // getScheduledExecutorService().scheduleWithFixedDelay(() -> LOGGER.info(""), 1L, 3L, TimeUnit.SECONDS);
        }
        catch (Throwable th)
        {
            LOGGER.error(null, th);
            throw th;
        }
    }

    /**
     * @see javafx.application.Application#stop()
     */
    @Override
    public void stop() throws Exception
    {
        LOGGER.info("Stop P.I.M.");

        // Verhindert Fehlerdialog, wenn es Probleme beim Start gibt, z.B. fehlende Resourcen.
        System.exit(0);
    }
}
