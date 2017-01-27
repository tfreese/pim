// Created: 26.01.2017
package de.freese.pim.core.mail.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

/**
 * Container für den Inhalt einer Mail.
 *
 * @author Thomas Freese
 */
public class MailContent
{
    /**
     *
     */
    private final DataSource dataSource;

    /**
     *
     */
    private String encoding = null;

    /**
     *
     */
    private URL url = null;

    /**
     * Erzeugt eine neue Instanz von {@link MailContent}
     *
     * @param dataSource {@link DataSource}
     */
    public MailContent(final DataSource dataSource)
    {
        super();

        Objects.requireNonNull(dataSource, "dataSource required");

        this.dataSource = dataSource;

        try
        {
            try (InputStreamReader isr = new InputStreamReader(getInputStream()))
            {
                setEncoding(isr.getEncoding());
            }
        }
        catch (IOException ioex)
        {
            throw new RuntimeException(ioex);
        }
    }

    /**
     * Erzeugt eine neue Instanz von {@link MailContent}
     *
     * @param path {@link Path}
     */
    public MailContent(final Path path)
    {
        this(new FileDataSource(path.toFile()));

        try
        {
            setUrl(path.toUri().toURL());
        }
        catch (MalformedURLException ioex)
        {
            throw new RuntimeException(ioex);
        }
    }

    /**
     * Liefert den Text der Mail.
     *
     * @return String
     * @throws RuntimeException Falls was schief geht.
     */
    public String getContent()
    {
        // StandardCharsets.UTF_8
        String content = null;

        try
        {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(getInputStream(), Charset.forName(getEncoding()))))
            {
                content = buffer.lines().collect(Collectors.joining("\n"));
            }
        }
        catch (IOException ioex)
        {
            throw new RuntimeException(ioex);
        }

        return content;
    }

    /**
     * "text/html", "text/plain"
     *
     * @return String
     */
    public String getContentType()
    {
        String contentType = this.dataSource.getContentType();

        // contentType = contentType.replaceAll(System.getProperty("line.separator"), "");
        contentType = contentType.replaceAll("\\r\\n|\\r|\\n", "");

        return contentType;
    }

    /**
     * Liefert das Encoding oder null
     *
     * @return String
     */
    public String getEncoding()
    {
        return this.encoding;
    }

    /**
     * Liefert den {@link InputStream}.
     *
     * @return {@link InputStream}
     * @throws IOException Falls was schief geht.
     */
    public InputStream getInputStream() throws IOException
    {
        return this.dataSource.getInputStream();
    }

    /**
     * Liefert den Namen der {@link DataSource}.
     *
     * @return String
     */
    public String getName()
    {
        return this.dataSource.getName();
    }

    /**
     * Liefert die {@link URL} zum Content.
     *
     * @return {@link URL}
     */
    public URL getUrl()
    {
        return this.url;
    }

    /**
     * text/html
     *
     * @return boolean
     */
    public boolean isHTML()
    {
        return getContentType().startsWith("text/html");
    }

    /**
     * text/plain
     *
     * @return boolean
     */
    public boolean isText()
    {
        return getContentType().startsWith("text/plain");
    }

    /**
     * Setzt das Encoding.
     *
     * @param encoding String
     */
    public void setEncoding(final String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Setzt die {@link URL} zum Content.
     *
     * @param url {@link URL}
     */
    public void setUrl(final URL url)
    {
        this.url = url;
    }
}
