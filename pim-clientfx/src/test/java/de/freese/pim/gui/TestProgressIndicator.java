/**
 * Created: 28.01.2017
 */

package de.freese.pim.gui;

import java.util.concurrent.ForkJoinPool;
import de.freese.pim.gui.utils.FXUtils;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author Thomas Freese
 */
public class TestProgressIndicator extends Application
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        launch(args);
    }

    /**
     * Erstellt ein neues {@link TestProgressIndicator} Object.
     */
    public TestProgressIndicator()
    {
        super();
    }

    /**
     * Verschiedene Farben.
     *
     * @param progress 0-1
     * @return int[], RGB
     */
    int[] getRGB_1(final double progress)
    {
        int r = 0;
        int g = 0;
        int b = 0;

        // http://en.wikibooks.org/wiki/Color_Theory/Color_gradient#Linear_RGB_gradient_with_6_segments
        // http://dgrieve.blogspot.de/2014/05/styling-progress-color-of-indeterminate.html
        final double m = (6d * progress); // segment
        final int n = (int) m; // integer of m
        final double f = m - n; // fraction of m
        final int t = (int) (255 * f);
        switch (n)
        {
            case 0:
            {
                r = 255;
                g = t;
                b = 0;
                break;
            }
            case 1:
            {
                r = 255 - t;
                g = 255;
                b = 0;
                break;
            }
            case 2:
            {
                r = 0;
                g = 255;
                b = t;
                break;
            }
            case 3:
            {
                r = 0;
                g = 255 - t;
                b = 255;
                break;
            }
            case 4:
            {
                r = t;
                g = 0;
                b = 255;
                break;
            }
            case 5:
            {
                r = 255;
                g = 0;
                b = 255 - t;
                break;
            }
            default:
            {
                r = 255;
                g = 0;
                b = 0;
                break;
            }
        }

        return new int[]
        {
                r, g, b
        };
    }

    /**
     * Von Rot nach Grün
     *
     * @param progress 0-1
     * @return int[], RGB
     */
    int[] getRGB_2(final double progress)
    {
        int r = 0;
        int g = 0;
        int b = 0;

        g = (int) (progress * 255);
        r = 255 - g;

        return new int[]
        {
                r, g, b
        };
    }

    /**
     * Von Rot nach Grün
     *
     * @param progress 0-1
     * @return int[], RGB
     */
    int[] getRGB_3(final double progress)
    {
        int r = 0;
        int g = 0;
        int b = 0;

        Color color = Color.RED.interpolate(Color.GREEN, progress);
        r = (int) (color.getRed() * 255);
        g = (int) (color.getGreen() * 255);
        b = (int) (color.getBlue() * 255);

        return new int[]
        {
                r, g, b
        };
    }

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws Exception
    {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        // progressIndicator.setMaxSize(250D, 250D);

        Task<Void> task = new Task<>()
        {
            /**
             * @see javafx.concurrent.Task#call()
             */
            @Override
            protected Void call() throws Exception
            {
                int progress = 0;

                while (progress <= 100)
                {
                    updateProgress(++progress, 100);

                    Thread.sleep(40); // 25 Frames/Sekunde

                    if (progress == 100)
                    {
                        progress = 0;
                    }
                }

                return null;
            }
        };

        progressIndicator.progressProperty().bind(task.progressProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.styleProperty().bind(Bindings.createStringBinding(() -> {
            final double percent = progressIndicator.getProgress();
            if (percent < 0)
            {
                // indeterminate
                return null;
            }

            // int[] rgb = getRGB_1(percent);
            // int[] rgb = getRGB_2(percent);
            // int[] rgb = getRGB_3(percent);
            int[] rgb = FXUtils.getProgressRGB(percent, Color.RED, Color.ORANGE, Color.GREEN);

            final String style = String.format("-fx-progress-color: rgb(%d,%d,%d)", rgb[0], rgb[1], rgb[2]);
            return style;
        }, progressIndicator.progressProperty()));

        Button button = new Button("Start");
        button.setOnAction(event -> ForkJoinPool.commonPool().execute(task));

        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(0));
        stackPane.getChildren().add(button);
        stackPane.getChildren().add(progressIndicator);

        Scene scene = new Scene(stackPane, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
