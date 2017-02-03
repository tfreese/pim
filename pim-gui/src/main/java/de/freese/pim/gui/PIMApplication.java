package de.freese.pim.gui;

import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.javafx.application.LauncherImpl;
import de.freese.pim.core.addressbook.dao.DefaultAddressBookDAO;
import de.freese.pim.core.addressbook.service.DefaultAddressBookService;
import de.freese.pim.core.addressbook.service.IAddressBookService;
import de.freese.pim.core.db.HsqldbEmbeddedServer;
import de.freese.pim.core.db.IDataSourceBean;
import de.freese.pim.core.mail.dao.DefaultMailDAO;
import de.freese.pim.core.mail.service.DefaultMailService;
import de.freese.pim.core.mail.service.IMailService;
import de.freese.pim.core.persistence.TransactionalInvocationHandler;
import de.freese.pim.core.service.ISettingsService;
import de.freese.pim.core.service.SettingService;
import de.freese.pim.core.thread.PIMThreadFactory;
import de.freese.pim.core.utils.Utils;
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
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
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
public class PIMApplication extends Application
{
    /**
     *
     */
    private static IAddressBookService addressBookService = null;

    /**
     *
     */
    public static final List<AutoCloseable> CLOSEABLES = new ArrayList<>();

    /**
     *
     */
    private static IDataSourceBean dataSourceBean = null;

    /**
     *
     */
    private static ExecutorService executorService = null;

    /**
     *
     */
    private static final EventHandler<InputEvent> GUI_BLOCKER = Event::consume;

    /**
     *
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(PIMApplication.class);

    /**
     *
     */
    private static IMailService mailService = null;

    /**
     *
     */
    private static Window mainWindow = null;

    /**
     *
     */
    private static ScheduledExecutorService scheduledExecutorService = null;

    /**
     * Bildschirm auf dem PIM läuft.
     */
    private static Screen screen = null;

    /**
     * Blockiert die GUI.
     */
    public static void blockGUI()
    {
        getMainWindow().addEventFilter(InputEvent.ANY, GUI_BLOCKER);
    }

    /**
     * @return {@link IAddressBookService}
     */
    public static IAddressBookService getAddressBookService()
    {
        if (addressBookService == null)
        {
            addressBookService = (IAddressBookService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[]
            {
                    IAddressBookService.class
            }, new TransactionalInvocationHandler(getDataSource(), new DefaultAddressBookService(new DefaultAddressBookDAO().dataSource(getDataSource()))));
        }

        return addressBookService;
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
     * @return {@link DataSource}
     */
    public static DataSource getDataSource()
    {
        return dataSourceBean.getDataSource();
    }

    /**
     * @return {@link ExecutorService}
     */
    public static ExecutorService getExecutorService()
    {
        return executorService;
    }

    /**
     * @return {@link IMailService}
     */
    public static IMailService getMailService()
    {
        if (mailService == null)
        {
            mailService = (IMailService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[]
            {
                    IMailService.class
            }, new TransactionalInvocationHandler(getDataSource(), new DefaultMailService(new DefaultMailDAO().dataSource(getDataSource()))));
        }

        return mailService;
    }

    /**
     * @return {@link Window}
     */
    public static Window getMainWindow()
    {
        return mainWindow;
    }

    /**
     * @return {@link ScheduledExecutorService}
     */
    public static ScheduledExecutorService getScheduledExecutorService()
    {
        return scheduledExecutorService;
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
     * @return {@link ISettingsService}
     */
    public static ISettingsService getSettingService()
    {
        return SettingService.getInstance();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the application can not be launched through
     * deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores main().
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
        for (String name : Arrays.asList(java.util.logging.Logger.GLOBAL_LOGGER_NAME, "com.sun.webkit.perf.Locks", "com.sun.webkit.perf.WCGraphicsPerfLogger",
                "com.sun.webkit.perf.WCFontPerfLogger"))
        {
            java.util.logging.Logger javaLogger = java.util.logging.Logger.getLogger(name);
            javaLogger.setLevel(java.util.logging.Level.OFF);
        }

        // System.setProperty("org.slf4j.simpleLogger.log.de.freese.pim", "DEBUG");
        // SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
            LOGGER.error("***Default exception handler***");
            LOGGER.error(null, ex);

            new ErrorDialog().forThrowable(ex).showAndWait();
        });

        // Eigene ThreadGroup für Handling von Runtime-Exceptions.
        PIMFXThreadGroup threadGroup = new PIMFXThreadGroup();

        // Kein Thread des gesamten Clients kann eine höhere Prio haben.
        threadGroup.setMaxPriority(Thread.NORM_PRIORITY + 1);

        Thread thread = new Thread(threadGroup, () -> {
            LOGGER.info("Startup P.I.M.");

            // launch(args);
            LauncherImpl.launchApplication(PIMApplication.class, PIMPreloader.class, args);
        }, "PIM-Startup");
        // thread.setDaemon(false);
        thread.start();
    }

    /**
     * @param closeable {@link AutoCloseable}
     */
    public static void registerCloseable(final AutoCloseable closeable)
    {
        CLOSEABLES.add(closeable);
    }

    /**
     * Hebt die GUI Blockade wieder auf..
     */
    public static void unblockGUI()
    {
        getMainWindow().removeEventFilter(InputEvent.ANY, GUI_BLOCKER);
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

        LOGGER.info("Init P.I.M.");
        notifyPreloader(new PIMPreloaderNotification("Init P.I.M."));
        Utils.sleep(1, TimeUnit.SECONDS);

        Path home = getSettingService().getHome();

        if (!Files.exists(home))
        {
            Files.createDirectories(home);
        }

        // getHostServices().showDocument("https://eclipse.org");
        FXUtils.tooltipBehaviorHack();

        LOGGER.info("Init ThreadPools");
        notifyPreloader(new PIMPreloaderNotification("Init ThreadPools"));
        Utils.sleep(1, TimeUnit.SECONDS);

        // Threads leben max. 60 Sekunden, wenn es nix zu tun gibt, min. 3 Threads, max. 50.
        BlockingQueue<Runnable> workQueue = new SynchronousQueue<>(true);
        // BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(50);

        ExecutorService executor =
                new ThreadPoolExecutor(3, 50, 60, TimeUnit.SECONDS, workQueue, new PIMThreadFactory("thread"), new ThreadPoolExecutor.AbortPolicy());
        PIMApplication.executorService = Executors.unconfigurableExecutorService(executor);
        registerCloseable(() -> {
            LOGGER.info("Close ExecutorService");
            Utils.shutdown(PIMApplication.executorService);
            PIMApplication.executorService = null;
        });

        ScheduledExecutorService scheduledExecutor =
                new ScheduledThreadPoolExecutor(3, new PIMThreadFactory("scheduler"), new ThreadPoolExecutor.AbortPolicy());
        PIMApplication.scheduledExecutorService = Executors.unconfigurableScheduledExecutorService(scheduledExecutor);
        registerCloseable(() -> {
            LOGGER.info("Close ScheduledExecutorService");
            Utils.shutdown(PIMApplication.scheduledExecutorService);
            PIMApplication.scheduledExecutorService = null;
        });

        LOGGER.info("Init Database");
        notifyPreloader(new PIMPreloaderNotification("Init Database"));
        Utils.sleep(1, TimeUnit.SECONDS);
        PIMApplication.dataSourceBean = new HsqldbEmbeddedServer();
        PIMApplication.dataSourceBean.configure(getSettingService());
        PIMApplication.dataSourceBean.testConnection();
        PIMApplication.dataSourceBean.populateIfEmpty(() -> {
            LOGGER.info("Populate Database");
            notifyPreloader(new PIMPreloaderNotification("Populate Database"));
            Utils.sleep(1, TimeUnit.SECONDS);
        });
        registerCloseable(() -> {
            LOGGER.info("Stop Database");
            PIMApplication.dataSourceBean.disconnect();
            PIMApplication.dataSourceBean = null;
        });

        getSettingService().setDataSource(getDataSource());
    }

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws Exception
    {
        // "JavaFX Application Thread" umbenennen.
        Thread.currentThread().setName("JavaFX-Appl.");

        LOGGER.info("Start P.I.M.");

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
        this.ready.addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue))
            {
                Platform.runLater(() -> {
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

        for (AutoCloseable closeable : CLOSEABLES)
        {
            try
            {
                closeable.close();
            }
            catch (Exception ex)
            {
                LOGGER.error(null, ex);
            }
        }

        // Verhindert Fehlerdialog, wenn es Probleme beim Start gibt, z.B. fehlende Resourcen.
        System.exit(0);
    }
}
