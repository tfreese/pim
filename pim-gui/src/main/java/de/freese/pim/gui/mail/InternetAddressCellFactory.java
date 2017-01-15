/**
 * Created on 15.01.2017 14:48:49
 */
package de.freese.pim.gui.mail;

import javax.mail.internet.InternetAddress;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * @author Thomas Freese
 * @param <S> Konkretr Typ des Row-Objektes
 */
public class InternetAddressCellFactory<S> implements Callback<TableColumn<S, InternetAddress>, TableCell<S, InternetAddress>>
{
    /**
     * Erstellt ein neues Object.
     */
    public InternetAddressCellFactory()
    {
        super();
    }

    /**
     * @see javafx.util.Callback#call(java.lang.Object)
     */
    @Override
    public TableCell<S, InternetAddress> call(final TableColumn<S, InternetAddress> param)
    {
        return new TableCell<S, InternetAddress>()
        {
            /**
             * @param item Date
             * @param empty boolean
             */
            @Override
            protected void updateItem(final InternetAddress item, final boolean empty)
            {
                super.updateItem(item, empty);

                if ((item == null) || empty)
                {
                    setText(null);
                    return;
                }

                String personal = item.getPersonal();
                String address = item.getAddress();

                if (personal == null)
                {
                    setText(address);
                }
                else
                {
                    String p = personal.replaceAll("\"", "");

                    setText(p + " <" + address + ">");
                }
            }
        };
    }
}
