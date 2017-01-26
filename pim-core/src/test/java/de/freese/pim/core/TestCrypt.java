/**
 *
 */
package de.freese.pim.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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

        // try (InputStream isEncrypted = crypt.encrypt(IOUtils.toInputStream(clearText, StandardCharsets.UTF_8));
        // InputStream decryptedStream = crypt.decrypt(isEncrypted))
        // {
        // String decrypted = IOUtils.toString(decryptedStream, StandardCharsets.UTF_8);
        // Assert.assertEquals(clearText, decrypted);
        // }

        try (InputStream isClear = new ByteArrayInputStream(clearText.getBytes(StandardCharsets.UTF_8));
             InputStream isEncrypted = crypt.encrypt(isClear); // Verschlüsseln
             InputStream decryptedStream = crypt.decrypt(isEncrypted); // Entschlüsseln
             BufferedReader buffer = new BufferedReader(new InputStreamReader(decryptedStream, StandardCharsets.UTF_8)))
        {
            String decrypted = buffer.lines().collect(Collectors.joining("\n"));
            Assert.assertEquals(clearText, decrypted);
        }

        // InputStream isClear = new ReaderInputStream(new StringReader(clearText);
        // Reader isr = new InputStreamReader(decryptedStream, StandardCharsets.UTF_8);
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
