package de.freese.pim.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.sun.javafx.application.LauncherImpl;

import de.freese.pim.gui.main.MainController;
import de.freese.pim.gui.utils.FXUtils;
import de.freese.pim.gui.utils.PIMFXThreadGroup;
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
// @SpringBootApplication(exclude =
// {
// DataSourceAutoConfiguration.class
// })
// @EnableTransactionManagement // Wird durch Spring-Boot automatisch konfiguriert, wenn DataSource-Bean vorhanden.
@SpringBootApplication
@EnableScheduling
// @EnableAsync // @Async("executorService")
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
     * @return {@link ExecutorService}
     */
    public static ExecutorService getExecutorService()
    {
        return getApplicationContext().getBean("executorService", ExecutorService.class);
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
     * @return {@link ScheduledExecutorService}
     */
    public static ScheduledExecutorService getScheduledExecutorService()
    {
        return getApplicationContext().getBean("scheduledExecutorService", ScheduledExecutorService.class);
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
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the application can not
     * be launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args)
    {
        // if (args.length == 0)
        // {
        // usage();
        // }

        CommandLine line = null;

        try
        {
            CommandLineParser parser = new DefaultParser();
            line = parser.parse(getCommandOptions(), args);
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

        // Eigene ThreadGroup für Handling von Runtime-Exceptions.
        PIMFXThreadGroup threadGroup = new PIMFXThreadGroup();

        // Kein Thread des gesamten Clients kann eine höhere Prio haben.
        threadGroup.setMaxPriority(Thread.NORM_PRIORITY + 1);

        Thread thread = new Thread(threadGroup, () ->
        {
            // launch(args);
            LauncherImpl.launchApplication(PIMApplication.class, PIMPreloader.class, args);
        }, "PIM-Startup");
        // thread.setDaemon(false);
        thread.start();
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
        // OptionGroup group = new OptionGroup();

        Options options = new Options();
        // options.addOptionGroup(group);

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

        formatter.printHelp(120, "P.I.M.\n", "\nParameter:", getCommandOptions(), footer.toString(), true);

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

        List<String> parameters = getParameters().getRaw();
        String profile = null;

        if (CollectionUtils.isEmpty(parameters))
        {
            profile = "HsqldbEmbeddedServer";
        }
        else
        {
            profile = parameters.get(0);
        }

        String[] args = getParameters().getRaw().toArray(new String[0]);

        notifyPreloader(new PIMPreloaderNotification("Init Springframework"));

        // SpringApplication.run(Application.class, args);
        //
        // @formatter:off
        SpringApplication application = new SpringApplicationBuilder(PIMApplication.class)
                .headless(false) // Default true, hier false wegen Swing
                .web(false) // Wird eigentlich automatisch ermittelt.
                .profiles(profile)
                .registerShutdownHook(true) // Default true
                //.banner(new MyBanner())
                //.listeners(new ApplicationPidFileWriter("eps-monitor.pid"))
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