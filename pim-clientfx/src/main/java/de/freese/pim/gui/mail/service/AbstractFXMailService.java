// Created: 15.02.2017
package de.freese.pim.gui.mail.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import de.freese.pim.common.model.mail.DefaultMailContent;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.common.utils.io.IOMonitor;
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
     * @see de.freese.pim.gui.mail.service.FXMailService#loadMailContent(long, de.freese.pim.gui.mail.model.FXMail, de.freese.pim.common.utils.io.IOMonitor)
     */
    @Override
    public MailContent loadMailContent(final long accountID, final FXMail mail, final IOMonitor monitor) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("load mail: msgnum={}; uid={}; size={}; subject={}", mail.getMsgNum(), mail.getUID(), mail.getSize(), mail.getSubject());
        }

        Path folderPath = getBasePath().resolve(Long.toString(accountID)).resolve(mail.getFolderFullName());
        Path mailPath = folderPath.resolve(Long.toString(mail.getUID())).resolve(mail.getUID() + ".json.zip");

        MailContent mailContent = null;

        if (!Files.exists(mailPath))
        {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("download mail: msgnum={}; uid={}", mail.getMsgNum(), mail.getUID());
            }

            // Mail-Download.
            Files.createDirectories(mailPath.getParent());

            try
            {
                mailContent = loadMailContent(accountID, mail.getFolderFullName(), mail.getUID(), monitor);

                try (OutputStream os = Files.newOutputStream(mailPath);
                     OutputStream bos = new BufferedOutputStream(os);
                     OutputStream gos = new GZIPOutputStream(bos))
                {
                    getJsonMapper().writeValue(gos, mailContent);
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
            try (InputStream is = Files.newInputStream(mailPath);
                 InputStream bis = new BufferedInputStream(is);
                 InputStream gis = new GZIPInputStream(bis))
            {
                mailContent = getJsonMapper().readValue(gis, DefaultMailContent.class);
            }
        }

        return mailContent;
    }

    /**
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param accountID long
     * @param folderFullName String
     * @param mailUID long
     * @param monitor {@link IOMonitor}, optional
     * @return {@link MailContent}
     * @throws Exception Falls was schief geht.
     */
    protected abstract MailContent loadMailContent(final long accountID, final String folderFullName, final long mailUID, IOMonitor monitor) throws Exception;

    /**
     * Pfad zum lokalen Speicherort.
     *
     * @param basePath {@link Path}
     */
    public void setBasePath(final Path basePath)
    {
        this.basePath = basePath;
    }
}
