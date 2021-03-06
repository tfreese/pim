// Created: 01.12.2016
package de.freese.pim.gui.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import com.sun.javafx.binding.StringFormatter;
import de.freese.pim.common.utils.Utils;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.controller.AbstractController;
import de.freese.pim.gui.view.View;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanLongProperty;
import javafx.beans.property.adapter.JavaBeanLongPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * JavaFX-Utils.
 *
 * @author Thomas Freese
 */
public final class FXUtils
{
    /**
    *
    */
    private static final EventHandler<InputEvent> EVENT_HANDLER_CONSUME_ALL = Event::consume;

    /**
     *
     */
    private static final KeyCodeCombination KEYCODE_COPY = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);

    /**
     *
     */
    private static final KeyCodeCombination KEYCODE_PASTE = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FXUtils.class);

    /**
     * Bildet die Funktionalität des {@link FXMLLoader} nach.<br>
     * <ol>
     * <li>Kopieren der mit {@link FXML} annotierten Attribute der View in den Controller
     * <li>Übersetzen der Komponenten mit SceneBuilder-Prefix (%)
     * <li>Aufruf der {@link Initializable#initialize(java.net.URL, ResourceBundle)} Methode des Controllers, falls vorhanden
     * <li>Ausgabe der nicht passenden/kopierten Attribute von View und Controller
     * </ol>
     *
     * @param view {@link View}
     * @param controller {@link AbstractController}
     * @param resources {@link ResourceBundle}
     */
    public static void bind(final View view, final AbstractController controller, final ResourceBundle resources)
    {
        Set<Field> viewSet = Utils.getAnnotatedFields(view, FXML.class);
        Set<Field> controllerSet = Utils.getAnnotatedFields(controller, FXML.class);

        Map<String, Field> viewMap = viewSet.stream().collect(Collectors.toMap(Field::getName, Function.identity()));
        Map<String, Field> controllerMap = controllerSet.stream().collect(Collectors.toMap(Field::getName, Function.identity()));

        // viewSet.forEach(System.out::println);
        // viewMap.forEach((key, value) -> System.out.println(key + " " + value));
        copyFields(view, viewMap, controller, controllerMap);

        translate(view, viewSet, resources);

        controller.initialize(null, resources);

        // Prüfung ob Komponenten im Controller nicht in View.
        if (!controllerMap.isEmpty())
        {
            controllerMap.forEach((k, v) -> LOGGER.warn("Controller-Component not bind: {} / {}", k, v));
        }

        // Prüfung ob Komponenten in View nicht im Controller.
        if (!viewMap.isEmpty())
        {
            controllerMap.forEach((k, v) -> LOGGER.warn("View-Component not bind: {} / {}", k, v));
        }
    }

    /**
     * Blockiert die GUI.
     *
     * @param window {@link Window}
     */
    public static void blockGUI(final Window window)
    {
        if (window == null)
        {
            return;
        }

        window.addEventFilter(InputEvent.ANY, EVENT_HANDLER_CONSUME_ALL);
    }

    /**
     * Kopieren der mit {@link FXML} annotierten Attribute der View in den Controller.<br>
     * Die übereinstimmenden Attribute, werden aus den Maps entfernt.
     *
     * @param view {@link View}
     * @param viewMap {@link Map}
     * @param controller {@link AbstractController}
     * @param controllerMap {@link Map}
     */
    public static void copyFields(final View view, final Map<String, Field> viewMap, final AbstractController controller,
                                  final Map<String, Field> controllerMap)
    {
        for (Field viewField : new HashSet<>(viewMap.values()))
        {
            String name = viewField.getName();
            Field controllerField = controllerMap.get(name);

            if (controllerField != null)
            {
                try
                {
                    controllerField.set(controller, viewField.get(view));

                    viewMap.remove(name);
                    controllerMap.remove(name);
                }
                catch (IllegalAccessException ex)
                {
                    LOGGER.error(ex.getMessage());
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    /**
     * Get table selection and copy it to the clipboard.
     *
     * @param table {@link TableView}
     */
    public static void copySelectionToClipboard(final TableView<?> table)
    {
        StringBuilder clipboardString = new StringBuilder();

        for (Iterator<?> iteratorPosition = table.getSelectionModel().getSelectedCells().iterator(); iteratorPosition.hasNext();)
        {
            TablePosition<?, ?> position = (TablePosition<?, ?>) iteratorPosition.next();

            int row = position.getRow();

            // Ganze Zeile kopieren.
            for (Iterator<?> iteratorCell = table.getColumns().iterator(); iteratorCell.hasNext();)
            {
                TableColumn<?, ?> column = (TableColumn<?, ?>) iteratorCell.next();

                Object cell = column.getCellData(row);

                if (cell == null)
                {
                    cell = "";
                }

                String text = cell.toString();

                clipboardString.append(text);

                if (iteratorCell.hasNext())
                {
                    clipboardString.append('\t');
                }
            }

            if (iteratorPosition.hasNext())
            {
                clipboardString.append('\n');
            }
        }

        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(clipboardString.toString());

        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    /**
     * @param bean Object
     * @param method String
     * @return {@link JavaBeanLongProperty}
     */
    public static JavaBeanLongProperty createLongProperty(final Object bean, final String method)

    {
        try
        {
            return JavaBeanLongPropertyBuilder.create().bean(bean).name(method).build();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param bean Object
     * @param method String
     * @return {@link JavaBeanStringProperty}
     */
    public static JavaBeanStringProperty createStringProperty(final Object bean, final String method)

    {
        try
        {
            return JavaBeanStringPropertyBuilder.create().bean(bean).name(method).build();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Vorgegebener Farbverlauf für Progress-Wert.<br>
     * Progress muss zwischen 0-1 liegen und es werden min 2 Farben benötigt, ansonsten wird Schwarz (0,0,0) geliefert.
     *
     * @param progress double; 0-1
     * @param colors {@link Color}[]; min. 2 Farben
     * @return int[], RGB
     */
    public static int[] getProgressRGB(final double progress, final Color...colors)
    {
        if ((progress < 0D) || (progress > 1D) || (colors.length < 2))
        {
            return new int[]
            {
                    0, 0, 0
            };
        }

        if (progress == 1D)
        {
            Color c = colors[colors.length - 1];

            return new int[]
            {
                    (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255)
            };
        }

        int r = 0;
        int g = 0;
        int b = 0;

        final double segment = (colors.length - 1) * progress;
        final int step = (int) segment;
        final double stepProgress = segment - step;

        // System.out.printf("Progress=%f, Segment=%f, Step=%d%n", progress, segment, step);

        Color c1 = colors[step];
        Color c2 = colors[step + 1];

        Color color = c1.interpolate(c2, stepProgress);

        r = (int) (color.getRed() * 255);
        g = (int) (color.getGreen() * 255);
        b = (int) (color.getBlue() * 255);

        return new int[]
        {
                r, g, b
        };
    }

    /**
     * {@link KeyEvent} Handler eines {@link Node} für Copy.
     *
     * @param node {@link Node}
     */
    public static void installCopyHandler(final Node node)
    {
        node.setOnKeyPressed(event -> {
            if (KEYCODE_COPY.match(event))
            {
                if (event.getSource() instanceof TableView)
                {
                    copySelectionToClipboard((TableView<?>) event.getSource());
                }
                else
                {
                    LOGGER.warn("No implementation for #copySelectionToClipboard found for {}", event.getSource().getClass().getSimpleName());
                }

                event.consume();
            }
        });
    }

    /**
     * {@link KeyEvent} Handler eines {@link Node} für Paste.
     *
     * @param node {@link TableView}
     */
    public static void installPasteHandler(final Node node)
    {
        node.setOnKeyPressed(event -> {
            if (KEYCODE_PASTE.match(event))
            {
                if (event.getSource() instanceof TableView)
                {
                    pasteClipboard((TableView<?>) event.getSource());
                }
                else
                {
                    LOGGER.warn("No implementation for #pasteClipboard found for {}", event.getSource().getClass().getSimpleName());
                }

                event.consume();
            }
        });
    }

    /**
     * @param table {@link TableView}
     */
    public static void pasteClipboard(final TableView<?> table)
    {
        // abort if there's not cell selected to start with
        if (table.getSelectionModel().getSelectedCells().isEmpty())
        {
            return;
        }

        // get the cell position to start with
        TablePosition<?, ?> pasteCellPosition = table.getSelectionModel().getSelectedCells().get(0);

        String pasteString = Clipboard.getSystemClipboard().getString();

        int rowClipboard = -1;

        StringTokenizer rowTokenizer = new StringTokenizer(pasteString, "\n");

        while (rowTokenizer.hasMoreTokens())
        {
            rowClipboard++;

            String rowString = rowTokenizer.nextToken();

            StringTokenizer columnTokenizer = new StringTokenizer(rowString, "\t");

            int colClipboard = -1;

            while (columnTokenizer.hasMoreTokens())
            {
                colClipboard++;

                // calculate the position in the table cell
                int rowTable = pasteCellPosition.getRow() + rowClipboard;
                int colTable = pasteCellPosition.getColumn() + colClipboard;

                // skip if we reached the end of the table
                if (rowTable >= table.getItems().size())
                {
                    continue;
                }

                if (colTable >= table.getColumns().size())
                {
                    continue;
                }

                String clipboardCellContent = columnTokenizer.nextToken();

                // get cell
                TableColumn<?, ?> tableColumn = table.getColumns().get(colTable);
                ObservableValue<?> observableValue = tableColumn.getCellObservableValue(rowTable);

                // TODO: handle double, etc
                if (observableValue instanceof StringProperty)
                {
                    ((StringProperty) observableValue).set(clipboardCellContent);
                }
                else if (observableValue instanceof IntegerProperty)
                {
                    int value = 0;

                    try
                    {
                        value = NumberFormat.getInstance().parse(clipboardCellContent).intValue();
                        ((IntegerProperty) observableValue).set(value);
                    }
                    catch (ParseException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Laden des Images antriggern für ImageView#getImage.<br>
     * Dies ist nur nötig, wenn das Image der {@link ImageView} VOR dem setzen in einen Parent explizit verwendet werden soll.<br>
     * Beispiel:<br>
     * <code>
     * stage.getIcons().add(imageView.getImage());<br>
     * scene.getChildren().add(imageView);<br>
     * stage.setScene(scene);<br>
     * </code>
     *
     * @param imageView {@link ImageView}
     */
    public static void preloadImage(final ImageView imageView)
    {
        Dialog<?> dialog = new Dialog<>();
        dialog.initOwner(PIMApplication.getMainWindow());
        dialog.setGraphic(imageView);

        imageView.applyCss();
        // imageView.impl_reapplyCSS();
        // imageView.imageProperty().get();

        dialog = null;
    }

    /**
     * Ändern der Display-Zeiten im Tooltip, da diese Möglichkeit noch nicht besteht.
     */
    public static void tooltipBehaviorHack()
    {
        Tooltip tooltip = new Tooltip();
        Constructor<?> constructor = null;

        // Versuchen die Klasse TooltipBehavior zu finden.
        for (Class<?> behaviorClass : tooltip.getClass().getDeclaredClasses())
        {
            try
            {
                constructor = behaviorClass.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
                constructor.setAccessible(true);

                // @formatter:off
                Object tooltipBehavior = constructor.newInstance(
                        new Duration(10), // open
                        new Duration(5000), // visible
                        new Duration(200), // close
                        false);
                // @formatter:on

                Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
                fieldBehavior.setAccessible(true);
                fieldBehavior.set(tooltip, tooltipBehavior);

                break;
            }
            catch (Exception ex)
            {
                // Falsche interne Klasse des Tooltips
            }
        }
    }

    /**
     * Formattiert das Objekt als String.
     *
     * @param <T> Konkreter Typ des Values
     * @param converter {@link Function}
     * @return {@link ObservableValue}<String>
     */
    public static <T> StringConverter<T> toStringConverter(final Function<T, String> converter)
    {
        Objects.requireNonNull(converter, "converter required");

        final StringConverter<T> stringConverter = new StringConverter<>()
        {
            /**
             * @see javafx.util.StringConverter#fromString(java.lang.String)
             */
            @Override
            public T fromString(final String string)
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            /**
             * @see javafx.util.StringConverter#toString(java.lang.Object)
             */
            @Override
            public String toString(final T object)
            {
                return converter.apply(object);
            }
        };

        return stringConverter;
    }

    // /**
    // * Formattiert das Objekt als String.
    // *
    // * @param <T> Konkreter Typ des Values
    // * @param ov {@link ObservableValue}
    // * @param formatter {@link Function}
    // * @return {@link ObservableValue}<String>
    // */
    // public static <T> ObservableValue<String> toStringObservable(final ObservableValue<T> ov, final Function<T, String> formatter)
    // {
    // Objects.requireNonNull(ov, "observableValue required");
    // Objects.requireNonNull(formatter, "formatter required");
    //
    // final StringFormatter stringFormatter = new StringFormatter()
    // {
    // {
    // super.bind(ov);
    // }
    //
    // /**
    // * @see javafx.beans.binding.StringBinding#computeValue()
    // */
    // @Override
    // protected String computeValue()
    // {
    // return formatter.apply(ov.getValue());
    // }
    //
    // /**
    // * @see javafx.beans.binding.StringBinding#dispose()
    // */
    // @Override
    // public void dispose()
    // {
    // super.unbind(ov);
    // }
    //
    // /**
    // * @see javafx.beans.binding.StringBinding#getDependencies()
    // */
    // @Override
    // public ObservableList<ObservableValue<?>> getDependencies()
    // {
    // ObservableList<ObservableValue<?>> ol = FXCollections.observableArrayList();
    // ol.add(ov);
    //
    // return FXCollections.unmodifiableObservableList(ol);
    // }
    // };
    //
    // // Check Formatierung
    // stringFormatter.get();
    //
    // return stringFormatter;
    // }

    /**
     * @param control {@link Control}
     * @param resources {@link ResourceBundle}
     */
    public static void translate(final Control control, final ResourceBundle resources)
    {
        Tooltip tooltip = control.getTooltip();

        if (tooltip == null)
        {
            return;
        }

        if (StringUtils.startsWith(tooltip.getText(), "%"))
        {
            tooltip.setText(resources.getString(tooltip.getText().substring(1)));
        }
    }

    /**
     * @param labeled {@link Labeled}
     * @param resources {@link ResourceBundle}
     */
    public static void translate(final Labeled labeled, final ResourceBundle resources)
    {
        if (StringUtils.startsWith(labeled.getText(), "%"))
        {
            labeled.setText(resources.getString(labeled.getText().substring(1)));
        }

        translate((Control) labeled, resources);
    }

    /**
     * @param tableColumn {@link TableColumnBase}
     * @param resources {@link ResourceBundle}
     */
    public static void translate(final TableColumnBase<?, ?> tableColumn, final ResourceBundle resources)
    {
        if (StringUtils.startsWith(tableColumn.getText(), "%"))
        {
            tableColumn.setText(resources.getString(tableColumn.getText().substring(1)));
        }
    }

    /**
     * @param text {@link Text}
     * @param resources {@link ResourceBundle}
     */
    public static void translate(final Text text, final ResourceBundle resources)
    {
        if (StringUtils.startsWith(text.getText(), "%"))
        {
            text.setText(resources.getString(text.getText().substring(1)));
        }
    }

    /**
     * Übersetzen der Komponenten mit SceneBuilder-Prefix (%).
     *
     * @param view {@link View}
     * @param components {@link Collection}
     * @param resources {@link ResourceBundle}
     */
    public static void translate(final View view, final Collection<Field> components, final ResourceBundle resources)
    {
        // Set aus allen erreichbaren Nodes der Komponenten bauen, alles was Übersetzt werden kann.

        // @formatter:off
        Set<Node> nodes = components.stream()
                .map(f -> Utils.getValue(f, view))
                .filter(v -> v instanceof Node)
                .map(Node.class::cast)
                .flatMap(node -> node.lookupAll("*").stream())
                .collect(Collectors.toSet());
        // @formatter:on

        // Labeled / Text
        Predicate<Object> isLabeled = v -> v instanceof Labeled;
        Predicate<Object> isText = v -> v instanceof Text;

        // @formatter:off
        nodes.stream()
                .filter(isLabeled.or(isText))
                .forEach(node ->
                {
                    Method getMethod = Utils.getMethod(node, "getText");
                    Method setMethod = Utils.getMethod(node, "setText", String.class);

                    String text = (String) Utils.invokeMethod(getMethod, node);

                    if ((text != null) && text.startsWith("%"))
                    {
                        String translated = resources.getString(text.substring(1));

                        if ((translated != null) && !translated.isEmpty())
                        {
                            Utils.invokeMethod(setMethod, node, translated);
                        }
                    }
                });
        // @formatter:on

        // Control
        Predicate<Object> isControl = v -> v instanceof Control;

        // Tooltip
        // @formatter:off
        nodes.stream()
                .filter(isControl)
                .forEach(node ->
                {
                    Method getMethod = Utils.getMethod(node, "getTooltip");
                    Tooltip tooltip = (Tooltip) Utils.invokeMethod(getMethod, node);

                    if ((tooltip != null) && tooltip.getText().startsWith("%"))
                    {
                        String translated = resources.getString(tooltip.getText().substring(1));

                        tooltip.setText(translated);
                    }
                });
        // @formatter:on

        // ContextMenu
        // @formatter:off
        nodes.stream()
                .filter(isControl)
                .forEach(node ->
                {
                    Method getMethod = Utils.getMethod(node, "getContextMenu");
                    ContextMenu contextMenu = (ContextMenu) Utils.invokeMethod(getMethod, node);

                    if (contextMenu != null)
                    {
                        for (MenuItem menuItem : contextMenu.getItems())
                        {
                            if (menuItem.getText().startsWith("%"))
                            {
                                String translated = resources.getString(menuItem.getText().substring(1));

                                menuItem.setText(translated);
                            }
                        }
                    }
                });
        // @formatter:on

        // TableView
        Predicate<Object> isTableView = v -> v instanceof TableView<?>;

        // @formatter:off
        nodes.stream()
                .filter(isTableView)
                .map(n -> (TableView<?>) n)
                .flatMap(tv -> tv.getColumns().stream())
                .forEach(column -> translate(column, resources));
        // @formatter:on
    }

    /**
     * Gibt die GUI Blockade wieder frei.
     *
     * @param window {@link Window}
     */
    public static void unblockGUI(final Window window)
    {
        if (window == null)
        {
            return;
        }

        window.removeEventFilter(InputEvent.ANY, EVENT_HANDLER_CONSUME_ALL);
    }

    /**
     * Erzeugt eine neue Instanz von {@link FXUtils}
     */
    private FXUtils()
    {
        super();
    }
}
