// Created: 20.01.2017
package de.freese.pim.server.mail;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import jakarta.annotation.Resource;

import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;
import de.freese.pim.core.service.AbstractRemoteService;
import de.freese.pim.core.service.MailService;
import de.freese.pim.core.utils.io.IOMonitor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * Service für die Mail-API.
 *
 * @author Thomas Freese
 */
@RestController()
@RequestMapping(path = "/mail", produces = MediaType.APPLICATION_JSON_VALUE)
public class MailRestController extends AbstractRemoteService implements MailService
{
    private MailService mailService;

    /**
     * @see de.freese.pim.core.service.MailService#connectAccount(de.freese.pim.core.model.mail.MailAccount)
     */
    @Override
    @PostMapping("/connect")
    public void connectAccount(@RequestBody final MailAccount account)
    {
        getMailService().connectAccount(account);
    }

    /**
     * @see de.freese.pim.core.service.MailService#deleteAccount(long)
     */
    @Override
    @PostMapping("/account/delete/{id}")
    public int deleteAccount(@PathVariable("id") final long accountID)
    {
        return getMailService().deleteAccount(accountID);
    }

    /**
     * @see de.freese.pim.core.service.MailService#disconnectAccounts(long[])
     */
    @Override
    @PostMapping("/account/disconnect")
    public void disconnectAccounts(@RequestParam("accountIDs") final long... accountIDs)
    {
        getMailService().disconnectAccounts(accountIDs);
    }

    /**
     * @see de.freese.pim.core.service.MailService#getMailAccounts()
     */
    @Override
    @GetMapping("/accounts")
    public List<MailAccount> getMailAccounts()
    {
        return getMailService().getMailAccounts();
    }

    /**
     * @see de.freese.pim.core.service.MailService#insertAccount(de.freese.pim.core.model.mail.MailAccount)
     */
    @Override
    @PostMapping("/account/insert")
    public long insertAccount(@RequestBody final MailAccount account)
    {
        return getMailService().insertAccount(account);
    }

    /**
     * @see de.freese.pim.core.service.MailService#insertFolder(long, java.util.List)
     */
    @Override
    @PostMapping("/folder/insert/{accountID}")
    public long[] insertFolder(@PathVariable("accountID") final long accountID, @RequestBody final List<MailFolder> folders)
    {
        return getMailService().insertFolder(accountID, folders);
    }

    /**
     * @see de.freese.pim.core.service.MailService#loadFolder(long)
     */
    @Override
    @GetMapping("/folder/{accountID}")
    public List<MailFolder> loadFolder(@PathVariable("accountID") final long accountID)
    {
        return getMailService().loadFolder(accountID);
    }

    /**
     * @see de.freese.pim.core.service.MailService#loadMailContent(long, java.lang.String, long, de.freese.pim.core.utils.io.IOMonitor)
     */
    @Override
    @GetMapping("/content/{accountID}/{folderFullName}/{mailUID}")
    public MailContent loadMailContent(@PathVariable("accountID") final long accountID, @PathVariable("folderFullName") final String folderFullName,
                                       @PathVariable("mailUID") final long mailUID, final @RequestBody(required = false) IOMonitor monitor)
    {
        String folderName = urlDecode(urlDecode(folderFullName));

        return getMailService().loadMailContent(accountID, folderName, mailUID, monitor);
    }

    /**
     * @see de.freese.pim.core.service.MailService#loadMails(long, long, java.lang.String)
     */
    @Override
    @GetMapping("/mails/{accountID}/{folderID}/{folderFullName}")
    public List<Mail> loadMails(@PathVariable("accountID") final long accountID, @PathVariable("folderID") final long folderID,
                                @PathVariable("folderFullName") final String folderFullName)
    {
        String folderName = urlDecode(urlDecode(folderFullName));

        return getMailService().loadMails(accountID, folderID, folderName);

        // https://dzone.com/articles/exception-handling-spring-rest
        // http://stackoverflow.com/questions/28902374/spring-boot-rest-service-exception-handling
        // https://blog.jayway.com/2014/10/19/spring-boot-error-responses/
        // http://www.ekiras.com/2016/02/how-to-do-exception-handling-in-springboot-rest-application.html
        // http://ahmadtechblog.blogspot.de/2015/10/spring-boot-handling-exceptions-in-rest.html
        // return new ResponseEntity<List<Mail>>(HttpStatus.NOT_FOUND);
    }

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.<br>
     * Das {@link Callable} entkoppelt den Server Thread von der Ausführung und verlagert<br>
     * ihn den ThreadPool des {@link RequestMappingHandlerAdapter}, siehe {@link WebMvcConfigurationSupport#configureAsyncSupport}.
     */
    @GetMapping("/mailsAsyncCallable/{accountID}/{folderID}/{folderFullName}")
    public Callable<List<Mail>> loadMailsAsyncCallable(@PathVariable("accountID") final long accountID, @PathVariable("folderID") final long folderID,
                                                       @PathVariable("folderFullName") final String folderFullName)
    {
        return () -> getMailService().loadMails(accountID, folderID, folderFullName);
    }

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.<br>
     * Das {@link DeferredResult} entkoppelt den Server Thread von der Ausführung.
     */
    @GetMapping("/mailsAsyncDeferredResult/{accountID}/{folderID}/{folderFullName}")
    public DeferredResult<List<Mail>> loadMailsAsyncDeferredResult(@PathVariable("accountID") final long accountID,
                                                                   @PathVariable("folderID") final long folderID,
                                                                   @PathVariable("folderFullName") final String folderFullName)
    {
        DeferredResult<List<Mail>> deferredResult = new DeferredResult<>();

        // @formatter:off
        CompletableFuture
            .supplyAsync(() -> getMailService().loadMails(accountID, folderID, folderFullName), getTaskExecutor())
            .whenCompleteAsync((result, throwable) -> {
                if (throwable != null)
                {
                    deferredResult.setErrorResult(throwable);
                }
                else
                {
                    deferredResult.setResult(result);
                }
            }, getTaskExecutor());
        // @formatter:on

        return deferredResult;
    }

    @Resource
    public void setMailService(final MailService mailService)
    {
        this.mailService = mailService;
    }

    /**
     * @see de.freese.pim.core.service.MailService#test(de.freese.pim.core.model.mail.MailAccount)
     */
    @Override
    @PostMapping("/test")
    public List<MailFolder> test(@RequestBody final MailAccount account)
    {
        return getMailService().test(account);
    }

    /**
     * @see de.freese.pim.core.service.MailService#updateAccount(de.freese.pim.core.model.mail.MailAccount)
     */
    @Override
    @PostMapping("/account/update")
    public int updateAccount(@RequestBody final MailAccount account)
    {
        return getMailService().updateAccount(account);
    }

    /**
     * @see de.freese.pim.core.service.MailService#updateFolder(long, java.util.List)
     */
    @Override
    @PostMapping("/folder/update/{accountID}")
    public int[] updateFolder(@PathVariable("accountID") final long accountID, @RequestBody final List<MailFolder> folders)
    {
        return getMailService().updateFolder(accountID, folders);
    }

    protected MailService getMailService()
    {
        return this.mailService;
    }
}
