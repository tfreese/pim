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
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.core.utils.Utils;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.controller.IController;
import de.freese.pim.gui.view.IView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanLongProperty;
import javafx.beans.property.adapter.JavaBeanLongPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * JavaFX-Utils.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public final class FXUtils
{
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
     * @param view {@link IView}
     * @param controller {@link IController}
     * @param resources {@link ResourceBundle}
     */
    public static void bind(final IView view, final IController controller, final ResourceBundle resources)
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
     * Kopieren der mit {@link FXML} annotierten Attribute der View in den Controller.<br>
     * Die übereinstimmenden Attribute, werden aus den Maps entfernt.
     *
     * @param view {@link IView}
     * @param viewMap {@link Map}
     * @param controller {@link IController}
     * @param controllerMap {@link Map}
     */
    public static void copyFields(final IView view, final Map<String, Field> viewMap, final IController controller, final Map<String, Field> controllerMap)
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
        if (table.getSelectionModel().getSelectedCells().size() == 0)
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
     *</code>
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
                        new Duration(10),   // open
                        new Duration(5000), // visible
                        new Duration(200),  // close
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
     * Übersetzen der Komponenten mit SceneBuilder-Prefix (%).
     *
     * @param view {@link IView}
     * @param components {@link Collection}
     * @param resources {@link ResourceBundle}
     */
    public static void translate(final IView view, final Collection<Field> components, final ResourceBundle resources)
    {
        // Set aus allen erreichbaren Nodes der Komponenten bauen, alles was Übersetzt werden kann.

        // @formatter:off
        Set<Node> nodes = components.stream()
            .map(f -> Utils.getValue(f, view))
            .filter(v -> v instanceof Node)
            .map(v -> (Node) v)
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
                Method getMethod = Utils.getMethod(node,"getText");
                Method setMethod = Utils.getMethod(node,"setText", String.class);

                String text = (String) Utils.invokeMethod(getMethod,node);

                if((text != null) &&  text.startsWith("%"))
                {
                    String translated = resources.getString(text.substring(1));

                    if((translated != null) && !translated.isEmpty())
                    {
                        Utils.invokeMethod(setMethod, node, translated);
                    }
                }
            });
        // @formatter:on

        // Control
        Predicate<Object> isControl = v -> v instanceof Control;

        // @formatter:off
        nodes.stream()
            .filter(isControl)
            .forEach(node ->
            {
                Method getMethod = Utils.getMethod(node,"getTooltip");
                Tooltip tooltip = (Tooltip) Utils.invokeMethod(getMethod,node);

                if((tooltip != null) &&  tooltip.getText().startsWith("%"))
                {
                    String translated = resources.getString(tooltip.getText().substring(1));

                    tooltip.setText(translated);
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
     * Erzeugt eine neue Instanz von {@link FXUtils}
     */
    private FXUtils()
    {
        super();
    }
}
