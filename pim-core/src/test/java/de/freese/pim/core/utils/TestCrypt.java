package de.freese.pim.core.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Testklasse für {@link Crypt}
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestCrypt {
    @Test
    void testCryptStream() throws Exception {
        final String clearText = "testABC123,.öäü#+";

        final Crypt crypt = Crypt.getUTF8Instance();

        // try (InputStream isEncrypted = crypt.encrypt(IOUtils.toInputStream(clearText, StandardCharsets.UTF_8));
        // InputStream decryptedStream = crypt.decrypt(isEncrypted))
        // {
        // String decrypted = IOUtils.toString(decryptedStream, StandardCharsets.UTF_8);
        // Assert.assertEquals(clearText, decrypted);
        // }

        try (InputStream isClear = new ByteArrayInputStream(clearText.getBytes(StandardCharsets.UTF_8));
             InputStream isEncrypted = crypt.encrypt(isClear);
             // Verschlüsseln
             InputStream decryptedStream = crypt.decrypt(isEncrypted);
             // Entschlüsseln
             BufferedReader buffer = new BufferedReader(new InputStreamReader(decryptedStream, StandardCharsets.UTF_8))) {
            final String decrypted = buffer.lines().collect(Collectors.joining("\n"));
            Assertions.assertEquals(clearText, decrypted);
        }

        // InputStream isClear = new ReaderInputStream(new StringReader(clearText);
        // Reader isr = new InputStreamReader(decryptedStream, StandardCharsets.UTF_8);
    }

    @Test
    void testCryptString() {
        final String clearText = "testABC123,.öäü#+";

        final Crypt crypt = Crypt.getUTF8Instance();

        final String encrypted = crypt.encrypt(clearText);
        final String decrypted = crypt.decrypt(encrypted);

        Assertions.assertEquals(clearText, decrypted);
    }
}
