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

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

import de.freese.pim.core.PIMException;
import de.freese.pim.core.mail.DefaultMailContent;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.core.utils.io.MonitorInputStream;
import de.freese.pim.gui.mail.model.FxMail;
import de.freese.pim.gui.mail.model.FxMailAccount;
import de.freese.pim.gui.mail.model.FxMailFolder;
import de.freese.pim.gui.service.AbstractFxService;

/**
 * Basisimplementierung eines JavaFX-MailService.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFxMailService extends AbstractFxService implements FxMailService {
    private Path basePath;

    @PreDestroy
    public void disconnectAccounts() throws Exception {
        disconnectAccounts(new long[0]);
    }

    @Override
    public MailContent loadMailContent(final FxMailAccount account, final FxMail mail, final IOMonitor monitor) {
        try {
            getLogger().debug("load mail: msgnum={}; uid={}; size={}; subject={}", mail.getMsgNum(), mail.getUID(), mail.getSize(), mail.getSubject());

            final Path folderPath = getBasePath().resolve(account.getMail()).resolve(mail.getFolderFullName());
            final Path mailPath = folderPath.resolve(Long.toString(mail.getUID())).resolve(mail.getUID() + ".json.zip");

            MailContent mailContent = null;

            if (!Files.exists(mailPath)) {
                getLogger().debug("download mail: msgnum={}; uid={}", mail.getMsgNum(), mail.getUID());

                // Mail-Download.
                Files.createDirectories(mailPath.getParent());

                try {
                    mailContent = loadMailContent(mailPath, account, mail, monitor);
                }
                catch (Exception ex) {
                    Files.deleteIfExists(mailPath);
                    Files.deleteIfExists(mailPath.getParent());
                    throw ex;
                }
            }
            else {
                // Lokal gespeicherte Mail laden.
                try (InputStream is = Files.newInputStream(mailPath);
                     InputStream bis = new BufferedInputStream(is);
                     InputStream gis = new GZIPInputStream(bis);
                     InputStream mos = new MonitorInputStream(gis, monitor, mail.getSize())) {
                    mailContent = getJsonMapper().readValue(mos, DefaultMailContent.class);
                }
            }

            return mailContent;
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Resource(name = "pimHomePath")
    public void setBasePath(final Path basePath) {
        this.basePath = basePath;
    }

    protected void buildHierarchie(final List<FxMailFolder> mailFolders) {
        // Hierarchie aufbauen basierend auf Namen.
        for (FxMailFolder folder : mailFolders) {
            // @formatter:off
            final Optional<FxMailFolder> parent = mailFolders.stream()
                .filter(mf -> !Objects.equals(mf, folder))
                .filter(mf -> folder.getFullName().startsWith(mf.getFullName()))
                .findFirst();
            // @formatter:on

            parent.ifPresent(p -> p.addChild(folder));
        }
    }

    protected Path getBasePath() {
        return this.basePath;
    }

    protected abstract MailContent loadMailContent(Path mailPath, FxMailAccount account, FxMail mail, IOMonitor monitor) throws Exception;

    protected void saveMailContent(final Path mailPath, final MailContent mailContent) throws Exception {
        final Callable<Void> task = () -> {
            getLogger().info("Save Mail: {}", mailPath);

            try (OutputStream os = Files.newOutputStream(mailPath);
                 OutputStream bos = new BufferedOutputStream(os);
                 OutputStream gos = new GZIPOutputStream(bos)) {
                getJsonMapper().writeValue(gos, mailContent);

                gos.flush();
            }
            catch (Exception ex) {
                Files.deleteIfExists(mailPath);
                Files.deleteIfExists(mailPath.getParent());
                throw ex;
            }

            return null;
        };

        getTaskExecutor().submit(task);
    }

    protected void saveMailContent(final Path mailPath, final String jsonContent) throws Exception {
        final Callable<Void> task = () -> {
            getLogger().info("Save Mail: {}", mailPath);

            try (OutputStream os = Files.newOutputStream(mailPath);
                 OutputStream bos = new BufferedOutputStream(os);
                 OutputStream gos = new GZIPOutputStream(bos);
                 PrintWriter pw = new PrintWriter(gos, true, StandardCharsets.UTF_8)) {
                pw.write(jsonContent);

                pw.flush();
            }
            catch (Exception ex) {
                Files.deleteIfExists(mailPath);
                Files.deleteIfExists(mailPath.getParent());
                throw ex;
            }

            return null;
        };

        getTaskExecutor().submit(task);
    }
}
