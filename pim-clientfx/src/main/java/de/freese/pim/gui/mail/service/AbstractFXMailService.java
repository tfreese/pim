// Created: 15.02.2017
package de.freese.pim.gui.mail.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.zip.GZIPInputStream;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.common.model.mail.JavaMailContent;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailFolder;

/**
 * Basisimplementierung eines JavaFX-MailService.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFXMailService implements FXMailService
{
    /**
    *
    */
    private Path basePath = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link AbstractFXMailService}
     */
    public AbstractFXMailService()
    {
        super();
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadContent(long, de.freese.pim.gui.mail.model.FXMail,
     *      java.util.function.BiConsumer)
     */
    @Override
    public MailContent loadContent(final long accountID, final FXMail mail, final BiConsumer<Long, Long> loadMonitor) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("load mail: msgnum={}; uid={}; size={}; subject={}", mail.getMsgNum(), mail.getUID(), mail.getSize(),
                    mail.getSubject());
        }

        Path folderPath = getBasePath().resolve(mail.getFolderFullName());
        // Path folderPath = mailAPI.getBasePath().resolve(mail.getFolderFullName().replaceAll("/", "__"));
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
                byte[] rawData = loadContent(accountID, mail.getFolderFullName(), mail.getUID(), mail.getSize(), loadMonitor);

                Files.copy(new ByteArrayInputStream(rawData), mailPath);
            }
            catch (Exception ex)
            {
                Files.deleteIfExists(mailPath);
                Files.deleteIfExists(mailPath.getParent());
                throw ex;
            }
        }

        // Lokal gespeicherte Mail laden.
        try (InputStream is = new GZIPInputStream(new BufferedInputStream(Files.newInputStream(mailPath))))
        {
            MimeMessage message = new MimeMessage(null, is);
            mailContent = new JavaMailContent(message);
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
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param accountID long
     * @param folderFullName String
     * @param mailUID long
     * @param size int
     * @param loadMonitor {@link BiConsumer}
     * @return {@link MailContent}
     * @throws Exception Falls was schief geht.
     */
    protected abstract byte[] loadContent(long accountID, String folderFullName, long mailUID, int size, BiConsumer<Long, Long> loadMonitor)
            throws Exception;
}
