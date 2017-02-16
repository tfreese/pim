// Created: 15.02.2017
package de.freese.pim.gui.mail.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.zip.GZIPInputStream;

import javax.mail.internet.MimeMessage;

import de.freese.pim.common.model.mail.DefaultMailContent;
import de.freese.pim.common.model.mail.JavaMailContent;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailFolder;
import de.freese.pim.gui.service.AbstractFXService;

/**
 * Basisimplementierung eines JavaFX-MailService.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFXMailService extends AbstractFXService implements FXMailService
{
    /**
    *
    */
    private Path basePath = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractFXMailService}
     */
    public AbstractFXMailService()
    {
        super();
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadMailContent(long, de.freese.pim.gui.mail.model.FXMail,
     *      java.util.function.BiConsumer)
     */
    @Override
    public MailContent loadMailContent(final long accountID, final FXMail mail, final BiConsumer<Long, Long> loadMonitor) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("load mail: msgnum={}; uid={}; size={}; subject={}", mail.getMsgNum(), mail.getUID(), mail.getSize(),
                    mail.getSubject());
        }

        Path folderPath = getBasePath().resolve(Long.toString(accountID)).resolve(mail.getFolderFullName());
        Path mailPath = folderPath.resolve(Long.toString(mail.getUID())).resolve(mail.getUID() + ".eml");

        if (!Files.exists(mailPath))
        {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("download mail: msgnum={}; uid={}", mail.getMsgNum(), mail.getUID());
            }

            // Mail download.
            Files.createDirectories(mailPath.getParent());

            try
            {
                byte[] rawData = loadMailContent(accountID, mail.getFolderFullName(), mail.getUID(), loadMonitor, mail.getSize());

                Files.copy(new ByteArrayInputStream(rawData), mailPath, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (Exception ex)
            {
                Files.deleteIfExists(mailPath);
                Files.deleteIfExists(mailPath.getParent());
                throw ex;
            }
        }

        MailContent mailContent = null;

        // Lokal gespeicherte Mail laden.
        try (InputStream is = new GZIPInputStream(new BufferedInputStream(Files.newInputStream(mailPath))))
        {
            MimeMessage message = new MimeMessage(null, is);
            mailContent = new JavaMailContent(message);
        }

        return mailContent;
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadMailContent2(long, de.freese.pim.gui.mail.model.FXMail,
     *      java.util.function.BiConsumer)
     */
    @Override
    public MailContent loadMailContent2(final long accountID, final FXMail mail, final BiConsumer<Long, Long> loadMonitor) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("load mail: msgnum={}; uid={}; size={}; subject={}", mail.getMsgNum(), mail.getUID(), mail.getSize(),
                    mail.getSubject());
        }

        Path folderPath = getBasePath().resolve(Long.toString(accountID)).resolve(mail.getFolderFullName());
        Path mailPath = folderPath.resolve(Long.toString(mail.getUID())).resolve(mail.getUID() + ".eml");

        MailContent mailContent = null;

        if (!Files.exists(mailPath))
        {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("download mail: msgnum={}; uid={}", mail.getMsgNum(), mail.getUID());
            }

            // Mail download.
            Files.createDirectories(mailPath.getParent());

            try
            {
                mailContent = loadMailContent2(accountID, mail.getFolderFullName(), mail.getUID(), loadMonitor, mail.getSize());

                try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(mailPath)))
                {
                    getJsonMapper().writeValue(os, mailContent);
                }
            }
            catch (Exception ex)
            {
                Files.deleteIfExists(mailPath);
                Files.deleteIfExists(mailPath.getParent());
                throw ex;
            }
        }
        else
        {
            // Lokal gespeicherte Mail laden.
            try (InputStream is = new BufferedInputStream(Files.newInputStream(mailPath)))
            {
                mailContent = getJsonMapper().readValue(is, DefaultMailContent.class);
            }
        }

        return mailContent;
    }

    /**
     * Pfad zum lokalen Speicherort.
     *
     * @param basePath {@link Path}
     */
    public void setBasePath(final Path basePath)
    {
        this.basePath = basePath;
    }

    /**
     * Folder-Hierarchie aufbauen basierend auf Namen.
     *
     * @param mailFolders {@link List}
     */
    protected void buildHierarchie(final List<FXMailFolder> mailFolders)
    {
        // Hierarchie aufbauen basierend auf Namen.
        for (FXMailFolder folder : mailFolders)
        {
            // @formatter:off
            Optional<FXMailFolder> parent = mailFolders.stream()
                    .filter(mf -> !Objects.equals(mf, folder))
                    .filter(mf -> folder.getFullName().startsWith(mf.getFullName()))
                    .findFirst();
            // @formatter:on

            parent.ifPresent(p -> p.addChild(folder));
        }
    }

    /**
     * Pfad zum lokalen Speicherort.
     *
     * @return {@link Path}
     */
    protected Path getBasePath()
    {
        return this.basePath;
    }

    /**
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param accountID long
     * @param folderFullName String
     * @param mailUID long
     * @param loadMonitor {@link BiConsumer}, optional
     * @param size int, optional - wird nur für loadMonitor benötigt
     * @return byte[]
     * @throws Exception Falls was schief geht.
     */
    protected abstract byte[] loadMailContent(final long accountID, final String folderFullName, final long mailUID,
            BiConsumer<Long, Long> loadMonitor, int size) throws Exception;

    /**
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param accountID long
     * @param folderFullName String
     * @param mailUID long
     * @param loadMonitor {@link BiConsumer}, optional
     * @param size int, optional - wird nur für loadMonitor benötigt
     * @return {@link MailContent}
     * @throws Exception Falls was schief geht.
     */
    protected abstract MailContent loadMailContent2(final long accountID, final String folderFullName, final long mailUID,
            BiConsumer<Long, Long> loadMonitor, int size) throws Exception;
}
