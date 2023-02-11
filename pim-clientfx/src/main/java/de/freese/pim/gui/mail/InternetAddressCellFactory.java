// Created: 25.01.2017
package de.freese.pim.gui.mail;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import de.freese.pim.core.mail.InternetAddress;

/**
 * @param <S> Konkreter Typ des Row-Objektes
 *
 * @author Thomas Freese
 */
public class InternetAddressCellFactory<S> implements Callback<TableColumn<S, InternetAddress[]>, TableCell<S, InternetAddress[]>> {
    /**
     * @see javafx.util.Callback#call(java.lang.Object)
     */
    @Override
    public TableCell<S, InternetAddress[]> call(final TableColumn<S, InternetAddress[]> param) {
        return new TableCell<>() {
            /**
             * @param item Date
             * @param empty boolean
             */
            @Override
            protected void updateItem(final InternetAddress[] item, final boolean empty) {
                super.updateItem(item, empty);

                if ((item == null) || empty) {
                    setText(null);
                    return;
                }

                String value = InternetAddress.toString(item);
                setText(value);

                // String personal = item.getPersonal();
                // String address = item.getAddress();
                //
                // if (personal == null)
                // {
                // setText(address);
                // }
                // else
                // {
                // String p = personal.replaceAll("\"", "");
                //
                // setText(p + " <" + address + ">");
                // }
            }
        };
    }
}
