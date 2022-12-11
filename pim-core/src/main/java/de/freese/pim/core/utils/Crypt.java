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

import de.freese.pim.core.PIMException;

/**
 * Klasse zum Ver- und Entschlüsseln.
 *
 * @author Thomas Freese
 */
public class Crypt
{
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    // /**
    // * 32bit entspricht AES256.
    // */
    // private static final int AES_KEY_SIZE = 32;

    private static final int BUFFER_SIZE = 4096;

    /**
     * 16bit<br>
     * AES Initialisierungsvektor, muss dem Empfänger bekannt sein !
     */
    private static final byte[] INIT_VECTOR =
            {
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
            };

    private static final Crypt INSTANCE = new Crypt(StandardCharsets.UTF_8);

    /**
     * @return {@link Crypt} mit UTF-8 {@link Charset}.
     */
    public static Crypt getUTF8Instance()
    {
        return INSTANCE;
    }

    private final Charset charset;

    /**
     * @param charset {@link Charset}; wird nur für die Strings benötigt, nicht für die Streams
     */
    public Crypt(final Charset charset)
    {
        super();

        this.charset = Objects.requireNonNull(charset, "charset required");
    }

    /**
     * @param input Verschlüsselter {@link InputStream}, dieser wird geschlossen.
     */
    public InputStream decrypt(final InputStream input)
    {
        Key key = getSecretKey();

        try
        {
            Path file = Files.createTempFile("pim", ".tmp");
            file.toFile().deleteOnExit();

            Cipher decodeCipher = Cipher.getInstance(AES_ALGORITHM);
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
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    public String decrypt(final String input)
    {
        if ((input == null) || input.isBlank())
        {
            return null;
        }

        Key key = getSecretKey();

        try
        {
            Cipher decodeCipher = Cipher.getInstance(AES_ALGORITHM);
            decodeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(INIT_VECTOR));

            // byte[] decrypted = decodeCipher.doFinal(Base64.decodeBase64(input));
            byte[] decrypted = decodeCipher.doFinal(Base64.getDecoder().decode(input));

            return new String(decrypted, getCharset());
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @param input Der {@link InputStream} wird geschlossen.
     */
    public InputStream encrypt(final InputStream input)
    {
        try
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
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    public String encrypt(final String input)
    {
        if ((input == null) || input.isBlank())
        {
            return null;
        }

        Key key = getSecretKey();

        try
        {
            Cipher encodeCipher = Cipher.getInstance(AES_ALGORITHM);
            encodeCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(INIT_VECTOR));

            byte[] encrypted = encodeCipher.doFinal(input.getBytes(getCharset()));

            // return Base64.encodeBase64String(encrypted);
            return new String(Base64.getEncoder().encode(encrypted), getCharset());
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    public Charset getCharset()
    {
        return this.charset;
    }

    public OutputStream getCipherOutputStream(final OutputStream output)
    {
        Key key = getSecretKey();

        try
        {
            Cipher encodeCipher = Cipher.getInstance(AES_ALGORITHM);
            encodeCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(INIT_VECTOR));

            return new CipherOutputStream(output, encodeCipher);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    private Key getSecretKey()
    {
        // byte[] key = new byte[AES_KEY_SIZE];
        // SecureRandom secureRandom = new SecureRandom();
        // secureRandom.nextBytes(key);
        //
        // return new SecretKeySpec(key, "AES");
        return new PimSecretKey();
    }
}
