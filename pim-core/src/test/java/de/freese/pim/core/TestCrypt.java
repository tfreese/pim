/**
 *
 */
package de.freese.pim.core;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.freese.pim.core.utils.Crypt;

/**
 * Testklasse für {@link Crypt}
 *
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCrypt
{
    /**
     * Erzeugt eine neue Instanz von {@link TestCrypt}
     */
    public TestCrypt()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testCryptStream() throws Exception
    {
        String clearText = "testABC123,.öäü#+";

        Crypt crypt = Crypt.getUTF8Instance();

        try (InputStream isEncrypted = crypt.encrypt(IOUtils.toInputStream(clearText, StandardCharsets.UTF_8));
             InputStream decryptedStream = crypt.decrypt(isEncrypted))
        {
            String decrypted = IOUtils.toString(decryptedStream, StandardCharsets.UTF_8);
            Assert.assertEquals(clearText, decrypted);
        }

        // InputStream isClear = new ReaderInputStream(new StringReader(clearText);
        // InputStreamReader isr = new InputStreamReader(decryptedStream, StandardCharsets.UTF_8);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testCryptString() throws Exception
    {
        String clearText = "testABC123,.öäü#+";

        Crypt crypt = Crypt.getUTF8Instance();

        String encrypted = crypt.encrypt(clearText);
        String decrypted = crypt.decrypt(encrypted);

        Assert.assertEquals(clearText, decrypted);
    }
}
