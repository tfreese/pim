package de.freese.pim.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.StringUtils;

/**
 * Klasse zum Ver- und Entschlüsseln.
 *
 * @author Thomas Freese
 */
public class Crypt
{
    /**
     *
     */
    private static final String AES_ALGORYTHM = "AES/CBC/PKCS5Padding";

    /**
     * 32bit entspricht AES256.
     */
    private static final int AES_KEY_SIZE = 32;

    /**
     *
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * 16bit<br>
     * AES Initialisierungsvektor, muss dem Empfänger bekannt sein !
     */
    private static final byte[] INIT_VECTOR = new byte[]
    {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    /**
     *
     */
    private static final Crypt INSTANCE = new Crypt(StandardCharsets.UTF_8);

    /**
     * @return {@link Crypt} mit UTF-8 {@link Charset}.
     */
    public static Crypt getUTF8Instance()
    {
        return INSTANCE;
    }

    /**
    *
    */
    private final Charset charset;

    /**
     * Erzeugt eine neue Instanz von {@link Crypt}
     *
     * @param charset {@link Charset}; wird nur für die Strings benötigt, nicht für die Streams
     */
    public Crypt(final Charset charset)
    {
        super();

        Objects.requireNonNull(charset, "charset required");

        this.charset = charset;
    }

    /**
     * @param input Verschlüsselter {@link InputStream}, dieser wird geschlossen.
     * @return Entschlüsselter {@link InputStream}
     * @throws Exception Falls was schief geht.
     */
    public InputStream decrypt(final InputStream input) throws Exception
    {
        Key key = getKey();

        Path file = Files.createTempFile("pim", ".tmp");
        file.toFile().deleteOnExit();

        Cipher decodeCipher = Cipher.getInstance(AES_ALGORYTHM);
        decodeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(INIT_VECTOR));

        try (OutputStream fileOS = new BufferedOutputStream(Files.newOutputStream(file));
             OutputStream cipherOS = new CipherOutputStream(fileOS, decodeCipher))
        {
            // IOUtils.copy(input, cipherOS);
            byte[] buffer = new byte[BUFFER_SIZE];
            int numRead = 0;

            while ((numRead = input.read(buffer)) >= 0)
            {
                cipherOS.write(buffer, 0, numRead);
                // byte[] output = decodeCipher.doFinal(buffer, 0, numRead);
                // os.write(output);
            }

            cipherOS.flush();
            fileOS.flush();
        }

        return new BufferedInputStream(Files.newInputStream(file));
    }

    /**
     * @param input Verschlüsselter String
     * @return Klartext
     * @throws Exception Falls was schief geht.
     */
    public String decrypt(final String input) throws Exception
    {
        if (StringUtils.isBlank(input))
        {
            return null;
        }

        Key key = getKey();

        Cipher decodeCipher = Cipher.getInstance(AES_ALGORYTHM);
        decodeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(INIT_VECTOR));

        // byte[] decrypted = decodeCipher.doFinal(Base64.decodeBase64(input));
        byte[] decrypted = decodeCipher.doFinal(Base64.getDecoder().decode(input));

        return new String(decrypted, getCharset());
    }

    /**
     * @param input Der {@link InputStream} wird geschlossen.
     * @return Entschlüsselter {@link InputStream}
     * @throws Exception Falls was schief geht.
     */
    public InputStream encrypt(final InputStream input) throws Exception
    {
        Path file = Files.createTempFile("pim", ".tmp");
        file.toFile().deleteOnExit();

        try (OutputStream fileOS = new BufferedOutputStream(Files.newOutputStream(file));
             OutputStream cipherOS = getCipherOutputStream(fileOS))
        {
            // IOUtils.copy(input, cipherOS);
            byte[] buffer = new byte[BUFFER_SIZE];
            int numRead = 0;

            while ((numRead = input.read(buffer)) >= 0)
            {
                cipherOS.write(buffer, 0, numRead);
                // byte[] output = encodeCipher.doFinal(buffer, 0, numRead);
                // cipherOS.write(output);
            }

            cipherOS.flush();
            fileOS.flush();
        }

        return new BufferedInputStream(Files.newInputStream(file));
    }

    /**
     * @param input Klartext
     * @return Verschlüsselter String
     * @throws Exception Falls was schief geht.
     */
    public String encrypt(final String input) throws Exception
    {
        if (StringUtils.isBlank(input))
        {
            return null;
        }

        Key key = getKey();

        Cipher encodeCipher = Cipher.getInstance(AES_ALGORYTHM);
        encodeCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(INIT_VECTOR));

        byte[] encrypted = encodeCipher.doFinal(input.getBytes(getCharset()));

        // return Base64.encodeBase64String(encrypted);
        return new String(Base64.getEncoder().encode(encrypted), getCharset());
    }

    /**
     * @return {@link Charset}
     */
    public Charset getCharset()
    {
        return this.charset;
    }

    /**
     * Liefert den {@link OutputStream} zum verschlüsseln.
     *
     * @param output {@link OutputStream}
     * @return {@link OutputStream}
     * @throws Exception Falls was schief geht.
     */
    public OutputStream getCipherOutputStream(final OutputStream output) throws Exception
    {
        Key key = getKey();

        Cipher encodeCipher = Cipher.getInstance(AES_ALGORYTHM);
        encodeCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(INIT_VECTOR));

        CipherOutputStream cipherOS = new CipherOutputStream(output, encodeCipher);

        return cipherOS;
    }

    /**
     * Liefert den {@link Key}.
     *
     * @return {@link Key}
     */
    private Key getKey()
    {
        // byte[] key = new byte[AES_KEY_SIZE];
        // SecureRandom secureRandom = new SecureRandom();
        // secureRandom.nextBytes(key);
        //
        // Key keySpec = new SecretKeySpec(key, "AES");
        Key keySpec = new PimSecretKey();

        return keySpec;
    }
}
