/**
 * Created: 14.01.2017
 */

package de.freese.pim.core.mail.dao;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.freese.pim.core.mail.MailProvider;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.service.SettingService;
import de.freese.pim.core.utils.Utils;

/**
 * Basis DAO-Implementierung für die Mailverwaltung.<br>
 * Das Connection- und Transaction-Handling muss ausserhalb erfolgen und ist hier nicht implementiert.
 *
 * @author Thomas Freese
 */
public class AbstractMailDAO implements IMailDAO
{
    /**
     * Erstellt ein neues {@link AbstractMailDAO} Object.
     */
    public AbstractMailDAO()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#getMailAccounts()
     */
    @Override
    public List<MailAccount> getMailAccounts() throws Exception
    {
        // TODO Aus DB laden.
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        Path path = SettingService.getInstance().getHome().resolve(".mailaccounts");
        List<MailAccount> accountList = new ArrayList<>();

        if (Files.exists(path))
        {
            try (InputStream is = Files.newInputStream(path))
            {
                // MailAccount mailAccount = jsonMapper.readValue(is, MailAccount.class);
                // root.getChildren().add(new TreeItem<>(mailAccount));

                JavaType type = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, MailAccount.class);
                accountList.addAll(jsonMapper.readValue(is, type));
            }
        }
        else
        {
            MailAccount account = new MailAccount();
            account.setMail("commercial@freese-home.de");
            account.setImapHost(MailProvider.EinsUndEins.getImapHost());
            account.setSmtpHost(MailProvider.EinsUndEins.getSmtpHost());
            accountList.add(account);

            try (OutputStream os = Files.newOutputStream(path))
            {
                jsonMapper.writer().writeValue(os, Arrays.asList(account));
            }
        }

        return accountList;
    }

    /**
     * @return String
     */
    protected String getUserID()
    {
        return Utils.getSystemUserName();
    }
}
