package de.freese.pim.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import de.freese.pim.core.spring.SpringContext;
import de.freese.pim.gui.main.MainController;
import de.freese.pim.gui.utils.FxUtils;

/**
 * Startklasse der Personal Information Management Anwendung.<br>
 * <a href="https://www.iconfinder.com">iconFinder</a><br>
 * <br>
 * Geht momentan nicht aus der IDE, sondern nur per Console: mvn compile exec:java<br>
 * <br>
 * In Eclipse:<br>
 * <ol>
 * <li>Konstruktor muss public empty-arg sein oder nicht vorhanden sein.</li>
 * <li>VM-Parameter: --add-modules javafx.controls</li>
 * <li>Module-Classpath: OpenJFX die jeweils 2 Jars für javafx-base, javafx-controls und javafx-graphics hinzufügen</li>
 * </ol>
 *
 * @author Thomas Freese
 */
@SpringBootApplication//(scanBasePackages = "de.freese.pim")
// @EnableScheduling
// @EnableAsync // @Async("executorService")
// @EnableTransactionManagement // Wird durch Spring-Boot automatisch konfiguriert, wenn DataSource-Bean vorhanden.
public class PimClientApplication extends Application {
    public static final Logger LOGGER = LoggerFactory.getLogger(PimClientApplication.class);

    private static Window mainWindow;
    /**
     * Bildschirm auf dem PIM läuft.
     */
    private static Screen screen;

    /**
     * Blockiert die GUI.
     */
    public static void blockGUI() {
        FxUtils.blockGUI(getMainWindow());
    }

    public static Window getMainWindow() {
        return mainWindow;
    }

    /**
     * Liefert den Bildschirm, auf dem PIM läuft.
     */
    public static Screen getScreen() {
        return screen;
    }

    /**
     * Hebt die GUI Blockade wieder auf..
     */
    public static void unblockGUI() {
        FxUtils.unblockGUI(getMainWindow());
    }

    private final BooleanProperty ready = new SimpleBooleanProperty(false);

    @Override
    public void init() throws Exception {
        // "JavaFX-Launcher" umbenennen.
        Thread.currentThread().setName("JavaFX-Init.");

        final String[] args = getParameters().getRaw().toArray(new String[0]);
        // List<String> parameters = getParameters().getRaw();
        // String[] profiles = null;
        //
        // if (CollectionUtils.isEmpty(parameters)) {
        // profiles = new String[] {
        // PIMProfile.ClientStandalone.toString(), PIMProfile.HsqldbEmbeddedServer.toString()
        // };
        // }
        // else {
        // profiles = parameters.toArray(new String[0]);
        // }

        notifyPreloader(new PimClientPreloaderNotification("Init Springframework"));

        // SpringApplication.run(Application.class, args);
        //
        final SpringApplication application = new SpringApplicationBuilder(PimClientApplication.class)
                .headless(false) // Default true, hier false wegen JavaFX
                //.web(WebApplicationType.NONE) // Wird eigentlich automatisch ermittelt.
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
        // //                .parent(PIMCommonConfig.class)
        //                .child(PIMClientConfig.class)
        //                .child(PIMServerApplication.class)
        //                .build();

        application.run(args);

        // getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);

        LOGGER.info("Start P.I.M.");
        notifyPreloader(new PimClientPreloaderNotification("Start P.I.M."));
        // Utils.sleep(1, TimeUnit.SECONDS);

        final String pimHome = SpringContext.getEnvironment().getProperty("pim.home");
        final Path homePath = Paths.get(pimHome);

        if (!Files.exists(homePath)) {
            Files.createDirectories(homePath);
        }

        // getHostServices().showDocument("https://eclipse.org");
        FxUtils.tooltipBehaviorHack();
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        try {
            // "JavaFX Application Thread" umbenennen.
            Thread.currentThread().setName("JavaFX-Appl.");

            PimClientApplication.mainWindow = primaryStage;

            notifyPreloader(new PimClientPreloaderNotification("Init GUI"));
            // setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
            // setUserAgentStylesheet(Application.STYLESHEET_MODENA);

            final ResourceBundle resources = ResourceBundle.getBundle("bundles/pim");

            final MainController mainController = new MainController(resources);

            final Scene scene = new Scene((Parent) mainController.getMainNode());
            // Scene scene = new Scene((Parent) mainController.getMainNode(), 1400, 900);
            scene.getStylesheets().add("/styles/styles.css");

            primaryStage.getIcons().add(new Image("images/pim.png"));
            primaryStage.setTitle(resources.getString("titel"));
            primaryStage.setScene(scene);
            // primaryStage.centerOnScreen();
            // primaryStage.setMaximized(true);

            // Default: GUI auf 2. Monitor, wenn vorhanden.
            final ObservableList<Screen> screens = Screen.getScreens();

            if (screens.size() > 1) {
                screen = screens.get(1);
            }
            else {
                screen = screens.get(0);
            }

            // Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            final Rectangle2D screenBounds = screen.getVisualBounds();

            primaryStage.setX(screenBounds.getMinX() + 100);
            primaryStage.setY(screenBounds.getMinY() + 100);
            primaryStage.setWidth(screenBounds.getWidth() - 200);
            primaryStage.setHeight(screenBounds.getHeight() - 200);

            // After the app is ready, show the stage
            this.ready.addListener((observable, oldValue, newValue) -> {
                if (Boolean.TRUE.equals(newValue)) {
                    Platform.runLater(() -> {
                        primaryStage.show();

                        // System.setOut(new PrintStream(new TextAreaOutputStream(MainView.LOG_TEXT_AREA)));
                        // System.setErr(new PrintStream(new TextAreaOutputStream(MainView.LOG_TEXT_AREA)));
                        mainController.selectDefaultView();
                    });
                }
            });

            // After init is ready, the app is ready to be shown.
            // Do this before hiding the PreLoader stage to prevent the app from exiting prematurely.
            // notifyPreloader(new ProgressNotification(1.0D));
            this.ready.setValue(Boolean.TRUE);
            notifyPreloader(new StateChangeNotification(StateChangeNotification.Type.BEFORE_START));

            // // After init is ready, the app is ready to be shown.
            // // Do this before hiding the PreLoader stage to prevent the app from exiting prematurely.
            // PIMApplication.this.ready.setValue(Boolean.TRUE);
            // notifyPreloader(new StateChangeNotification(StateChangeNotification.Type.BEFORE_START));
            // }).start();
            // getScheduledExecutorService().scheduleWithFixedDelay(() -> LOGGER.info(""), 1L, 3L, TimeUnit.SECONDS);
        }
        catch (Throwable th) {
            LOGGER.error(th.getMessage(), th);
            throw th;
        }
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Stop P.I.M.");

        // Platform.exit();

        // Verhindert Fehlerdialog, wenn es Probleme beim Start gibt, z.B. fehlende Ressourcen.
        System.exit(0);
    }
}
