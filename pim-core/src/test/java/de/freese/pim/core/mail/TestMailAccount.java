/**
 * Created: 26.12.2016
 */

package de.freese.pim.core.mail;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.freese.pim.core.AbstractPimTest;
import de.freese.pim.core.mail.model.MailAccount;

/**
 * http://www.baeldung.com/jackson-annotations
 *
 * @author Thomas Freese
 */
// @RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMailAccount extends AbstractPimTest
{
    /**
    *
    */
    private static ObjectMapper jsonMapper = null;

    /**
    *
    */
    @BeforeClass
    public static void beforeClass()
    {
        jsonMapper = new ObjectMapper();

        // Name des Root-Objektes mit anzeigen.
        // jsonMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        // jsonMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        // Globales PrettyPrinting; oder einzeln über jsonMapper.writerWithDefaultPrettyPrinter().writeValue(...) nutzbar.
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        jsonMapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
        jsonMapper.setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY);
        jsonMapper.setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);
    }

    /**
     * Erstellt ein neues {@link TestMailAccount} Object.
     */
    public TestMailAccount()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010SingleSerialize() throws Exception
    {
        Path path = TMP_TEST_PATH.resolve("testMailAccount_010SingleSerialize.json");

        MailAccount account = new MailAccount();
        account.setMail("commercial@freese-home.de");
        account.setPassword("gehaim");

        // Files.newOutputStream(path) = Files.newInputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE))
        try (OutputStream os = Files.newOutputStream(path))
        {
            jsonMapper.writer().writeValue(os, account);
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test011SingleDeserialize() throws Exception
    {
        Path path = TMP_TEST_PATH.resolve("testMailAccount_010SingleSerialize.json");

        // Files.newInputStream(path) = Files.newInputStream(path, StandardOpenOption.READ)
        try (InputStream is = Files.newInputStream(path))
        {
            MailAccount account = jsonMapper.readValue(is, MailAccount.class);

            Assert.assertNotNull(account);
            Assert.assertEquals("commercial@freese-home.de", account.getMail());
            Assert.assertEquals("gehaim", account.getPassword());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020ListSerialize() throws Exception
    {
        Path path = TMP_TEST_PATH.resolve("testMailAccount_020ListSerialize.json");

        MailAccount account1 = new MailAccount();
        account1.setMail("1commercial@freese-home.de");
        account1.setPassword("1gehaim");

        MailAccount account2 = new MailAccount();
        account2.setMail("2commercial@freese-home.de");
        account2.setPassword("2gehaim");

        // MailAccountList list = new MailAccountList();
        List<MailAccount> list = new ArrayList<>();
        list.add(account1);
        list.add(account2);

        // Files.newOutputStream(path) = Files.newInputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE))
        try (OutputStream os = Files.newOutputStream(path))
        {
            jsonMapper.writer().writeValue(os, list);
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test021ListDeserialize() throws Exception
    {
        Path path = TMP_TEST_PATH.resolve("testMailAccount_020ListSerialize.json");

        // Files.newInputStream(path) = Files.newInputStream(path, StandardOpenOption.READ)
        try (InputStream is = Files.newInputStream(path))
        {
            // MailAccountList list = jsonMapper.readValue(is, MailAccountList.class);

            JavaType type = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, MailAccount.class);
            List<MailAccount> list = jsonMapper.readValue(is, type);

            Assert.assertNotNull(list);
            Assert.assertEquals(Boolean.FALSE, list.isEmpty());
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("1commercial@freese-home.de", list.get(0).getMail());
            Assert.assertEquals("1gehaim", list.get(0).getPassword());
            Assert.assertEquals("2commercial@freese-home.de", list.get(1).getMail());
            Assert.assertEquals("2gehaim", list.get(1).getPassword());
        }
    }
}
