package de.freese.pim.gui;

import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * @author Thomas Freese
 */
public class PimClientPreloader extends Preloader
{
    /**
     *
     */
    private Label labelStatus;
    /**
     *
     */
    private boolean noLoadingProgress = true;
    /**
     *
     */
    private ProgressIndicator progress;
    /**
     *
     */
    private Stage stage;

    /**
     * @return {@link Scene}
     */
    private Scene createPreloaderScene()
    {
        this.labelStatus = new Label();
        this.progress = new ProgressBar();
        // this.progress = new ProgressIndicator();
        // this.progress.setProgress(-1.0D);

        this.progress.setPrefWidth(200);

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(50));
        pane.getStyleClass().add("gridpane");

        pane.add(this.labelStatus, 0, 0);

        GridPane.setHgrow(this.progress, Priority.ALWAYS);
        pane.add(this.progress, 0, 1);

        // Scene scene = new Scene(pane, 300, 100);
        Scene scene = new Scene(pane);
        scene.getStylesheets().add("/styles/styles.css");

        return scene;
    }

    /**
     * @see javafx.application.Preloader#handleApplicationNotification(javafx.application.Preloader.PreloaderNotification)
     */
    @Override
    public void handleApplicationNotification(final PreloaderNotification info)
    {
        if (info instanceof PimClientPreloaderNotification noti)
        {
            double progress = noti.getProgress();
            String status = noti.getStatus();

            this.labelStatus.setText(status);
            this.progress.setProgress(progress);

            // // expect application to send us progress notifications
            // // with progress ranging from 0 to 1.0
            // double progress = ((PIMPreloaderNotification) info).getProgress();
            //
            // if (!this.noLoadingProgress)
            // {
            // // // if we were receiving loading progress notifications
            // // // then progress is already at 50%.
            // // // Rescale application progress to start from 50%
            // progress = 0.5D + (progress / 2);
            // }
            //
            // // this.progress.setProgress(v);
        }
        else if (info instanceof StateChangeNotification)
        {
            // hide after get any state update from application
            this.stage.hide();
        }
    }

    /**
     * @see javafx.application.Preloader#handleErrorNotification(javafx.application.Preloader.ErrorNotification)
     */
    @Override
    public boolean handleErrorNotification(final ErrorNotification info)
    {
        PimClientApplication.LOGGER.error(info.getDetails(), info.getCause());

        new ErrorDialog().forThrowable(info.getCause()).showAndWait();

        // Platform.exit();
        System.exit(0);

        return true;
    }

    /**
     * @see javafx.application.Preloader#handleProgressNotification(javafx.application.Preloader.ProgressNotification)
     */
    @Override
    public void handleProgressNotification(final ProgressNotification info)
    {
        // this.bar.setProgress(pn.getProgress());

        // application loading progress is rescaled to be first 50%
        // Even if there is nothing to load 0% and 100% events can be
        // delivered
        if ((info.getProgress() != 1.0D) || !this.noLoadingProgress)
        {
            // this.progress.setProgress(info.getProgress() / 2);

            if (info.getProgress() > 0D)
            {
                this.noLoadingProgress = false;
            }
        }
    }

    /**
     * @see javafx.application.Preloader#handleStateChangeNotification(javafx.application.Preloader.StateChangeNotification)
     */
    @Override
    public void handleStateChangeNotification(final StateChangeNotification info)
    {
        // ignore, hide after application signals it is ready
        // System.out.println(info);

        // if (info.getType() == StateChangeNotification.Type.BEFORE_START)
        // {
        // this.stage.hide();
        // }
    }

    /**
     * @see javafx.application.Application#init()
     */
    @Override
    public void init() throws Exception
    {
        // Empty
    }

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage stage) throws Exception
    {
        this.stage = stage;
        stage.setTitle("Start P.I.M.");
        stage.getIcons().add(new Image("images/pim.png"));
        stage.setScene(createPreloaderScene());
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * @see javafx.application.Application#stop()
     */
    @Override
    public void stop() throws Exception
    {
        // Empty
    }
}
