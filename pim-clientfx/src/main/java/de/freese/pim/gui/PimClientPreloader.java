package de.freese.pim.gui;

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

import de.freese.pim.gui.view.ErrorDialog;

/**
 * @author Thomas Freese
 */
public class PimClientPreloader extends Preloader {
    private Label labelStatus;

    private boolean noLoadingProgress = true;

    private ProgressIndicator progressIndicator;

    private Stage stage;

    @Override
    public void handleApplicationNotification(final PreloaderNotification info) {
        if (info instanceof PimClientPreloaderNotification noti) {
            final double progress = noti.getProgress();
            final String status = noti.getStatus();

            labelStatus.setText(status);
            progressIndicator.setProgress(progress);

            // // expect application to send us progress notifications
            // // with progress ranging from 0 to 1.0
            // double progress = ((PIMPreloaderNotification) info).getProgress();
            //
            // if (!noLoadingProgress) {
            // // // if we were receiving loading progress notifications
            // // // then progress is already at 50%.
            // // // Rescale application progress to start from 50%
            // progress = 0.5D + (progress / 2);
            // }
            //
            // // progress.setProgress(v);
        }
        else if (info instanceof StateChangeNotification) {
            // hide after get any state update from application
            stage.hide();
        }
    }

    @Override
    public boolean handleErrorNotification(final ErrorNotification info) {
        PimClientApplication.LOGGER.error(info.getDetails(), info.getCause());

        new ErrorDialog().forThrowable(info.getCause()).showAndWait();

        // Platform.exit();
        System.exit(0);

        return true;
    }

    @Override
    public void handleProgressNotification(final ProgressNotification info) {
        // bar.setProgress(pn.getProgress());

        // application loading progress is rescaled to be first 50%
        // Even if there is nothing to load 0% and 100% events can be
        // delivered
        if (Double.compare(info.getProgress(), 1.0D) != 0 || !noLoadingProgress) {
            // progress.setProgress(info.getProgress() / 2);

            if (info.getProgress() > 0D) {
                noLoadingProgress = false;
            }
        }
    }

    @Override
    public void handleStateChangeNotification(final StateChangeNotification info) {
        // Ignore, hide after application signals it is ready.
        // System.out.println(info);

        // if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
        // stage.hide();
        // }
    }

    @Override
    public void init() {
        // Empty
    }

    @Override
    public void start(final Stage stage) {
        this.stage = stage;
        stage.setTitle("Start P.I.M.");
        stage.getIcons().add(new Image("images/pim.png"));
        stage.setScene(createPreloaderScene());
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() {
        // Empty
    }

    private Scene createPreloaderScene() {
        labelStatus = new Label();
        progressIndicator = new ProgressBar();
        // progress = new ProgressIndicator();
        // progress.setProgress(-1.0D);

        progressIndicator.setPrefWidth(200);

        final GridPane pane = new GridPane();
        pane.setPadding(new Insets(50));
        pane.getStyleClass().add("gridpane");

        pane.add(labelStatus, 0, 0);

        GridPane.setHgrow(progressIndicator, Priority.ALWAYS);
        pane.add(progressIndicator, 0, 1);

        // Scene scene = new Scene(pane, 300, 100);
        final Scene scene = new Scene(pane);
        scene.getStylesheets().add("/styles/styles.css");

        return scene;
    }
}
