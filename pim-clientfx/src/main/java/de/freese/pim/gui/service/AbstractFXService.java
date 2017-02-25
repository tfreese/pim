// Created: 16.02.2017
package de.freese.pim.gui.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Basis-Service für JavaFX.
 *
 * @author Thomas Freese
 */
public class AbstractFXService
{
    /**
    *
    */
    private ObjectMapper jsonMapper = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private AsyncTaskExecutor taskExecutor = null;

    /**
     *
     */
    private TaskScheduler taskScheduler = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractFXService}
     */
    public AbstractFXService()
    {
        super();
    }

    /**
     * @return {@link ObjectMapper}
     */
    protected ObjectMapper getJsonMapper()
    {
        return this.jsonMapper;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link AsyncTaskExecutor}
     */
    protected AsyncTaskExecutor getTaskExecutor()
    {
        return this.taskExecutor;
    }

    /**
     * @return {@link TaskScheduler}
     */
    protected TaskScheduler getTaskScheduler()
    {
        return this.taskScheduler;
    }

    /**
     * @param jsonMapper {@link ObjectMapper}
     */
    @Resource
    public void setJsonMapper(final ObjectMapper jsonMapper)
    {
        this.jsonMapper = jsonMapper;
    }

    /**
     * @param taskExecutor {@link AsyncTaskExecutor}
     */
    @Resource
    public void setTaskExecutor(final AsyncTaskExecutor taskExecutor)
    {
        this.taskExecutor = taskExecutor;
    }

    /**
     * @param taskScheduler {@link TaskScheduler}
     */
    @Resource
    public void setTaskScheduler(final TaskScheduler taskScheduler)
    {
        this.taskScheduler = taskScheduler;
    }

    /**
     * @param value String
     * @return String
     * @throws UnsupportedEncodingException Falls was schief geht.
     */
    protected String urlEncode(final String value) throws UnsupportedEncodingException
    {
        if (value == null)
        {
            return null;
        }

        String encoded = URLEncoder.encode(value.trim(), "UTF-8");

        return encoded;
    }
}
