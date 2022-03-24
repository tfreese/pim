// Created: 15.02.2017
package de.freese.pim.gui.mail.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import de.freese.pim.core.PIMException;
import de.freese.pim.core.mail.DefaultMailContent;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.core.utils.io.MonitorInputStream;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailAccount;
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
    private Path basePath;

    /**
     * Schliessen der MailAPI-Verbindung aller MailAccounts.
     *
     * @throws Exception Falls was schief geht.
     */
    @PreDestroy
    public void disconnectAccounts() throws Exception
    {
        disconnectAccounts(new long[0]);
    }

    /**
     * @see de.freese.pim.gui.mail.service.FXMailService#loadMailContent(de.freese.pim.gui.mail.model.FXMailAccount, de.freese.pim.gui.mail.model.FXMail,
     * de.freese.pim.core.utils.io.IOMonitor)
     */
    @Override
    public MailContent loadMailContent(final FXMailAccount account, final FXMail mail, final IOMonitor monitor)
    {
        try
        {
            getLogger().debug("load mail: msgnum={}; uid={}; size={}; subject={}", mail.getMsgNum(), mail.getUID(), mail.getSize(), mail.getSubject());

            Path folderPath = getBasePath().resolve(account.getMail()).resolve(mail.getFolderFullName());
            Path mailPath = folderPath.resolve(Long.toString(mail.getUID())).resolve(mail.getUID() + ".json.zip");

            MailContent mailContent = null;

            if (!Files.exists(mailPath))
            {
                getLogger().debug("download mail: msgnum={}; uid={}", mail.getMsgNum(), mail.getUID());

                // Mail-Download.
                Files.createDirectories(mailPath.getParent());

                try
                {
                    mailContent = loadMailContent(mailPath, account, mail, monitor);
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
                     InputStream gis = new GZIPInputStream(bis);
                     InputStream mos = new MonitorInputStream(gis, monitor, mail.getSize()))
                {
                    mailContent = getJsonMapper().readValue(mos, DefaultMailContent.class);
                }
            }

            return mailContent;
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * Pfad zum lokalen Speicherort.
     *
     * @param basePath {@link Path}
     */
    @Resource(name = "pimHomePath")
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
     * Liefert den Inhalt der Mail im JSON-Format.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param mailPath {@link Path}
     * @param account {@link FXMailAccount}
     * @param mail {@link FXMail}
     * @param monitor {@link IOMonitor}, optional
     *
     * @return {@link MailContent}
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract MailContent loadMailContent(Path mailPath, final FXMailAccount account, final FXMail mail, IOMonitor monitor) throws Exception;

    /**
     * Speichert in einem separaten Thread den Mail-Inhalt im lokalen Cache.
     *
     * @param mailPath {@link Path}
     * @param mailContent {@link MailContent}
     *
     * @throws Exception Falls was schief geht.
     */
    protected void saveMailContent(final Path mailPath, final MailContent mailContent) throws Exception
    {
        Callable<Void> task = () ->
        {
            getLogger().info("Save Mail: {}", mailPath);

            try (OutputStream os = Files.newOutputStream(mailPath);
                 OutputStream bos = new BufferedOutputStream(os);
                 OutputStream gos = new GZIPOutputStream(bos))
            {
                getJsonMapper().writeValue(gos, mailContent);

                gos.flush();
            }
            catch (Exception ex)
            {
                Files.deleteIfExists(mailPath);
                Files.deleteIfExists(mailPath.getParent());
                throw ex;
            }

            return null;
        };

        getTaskExecutor().submit(task);
    }

    /**
     * Speichert in einem separaten Thread den Mail-Inhalt im lokalen Cache.
     *
     * @param mailPath {@link Path}
     * @param jsonContent String
     *
     * @throws Exception Falls was schief geht.
     */
    protected void saveMailContent(final Path mailPath, final String jsonContent) throws Exception
    {
        Callable<Void> task = () ->
        {
            getLogger().info("Save Mail: {}", mailPath);

            try (OutputStream os = Files.newOutputStream(mailPath);
                 OutputStream bos = new BufferedOutputStream(os);
                 OutputStream gos = new GZIPOutputStream(bos);
                 PrintWriter pw = new PrintWriter(gos, true, StandardCharsets.UTF_8))
            {
                pw.write(jsonContent);

                pw.flush();
            }
            catch (Exception ex)
            {
                Files.deleteIfExists(mailPath);
                Files.deleteIfExists(mailPath.getParent());
                throw ex;
            }

            return null;
        };

        getTaskExecutor().submit(task);
    }
}
