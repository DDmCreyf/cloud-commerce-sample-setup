package com.mirakl.hybris.core.jobs.strategies.impl;

import static java.lang.String.format;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.enums.MiraklAsyncTaskType;
import com.mirakl.hybris.core.jobs.services.MiraklAsyncTaskService;
import com.mirakl.hybris.core.model.MiraklAsyncTaskSchedulerModel;
import com.mirakl.hybris.core.product.strategies.PerformJobStrategy;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public abstract class AbstractAsyncTaskSchedulingStrategy<T extends MiraklAsyncTaskSchedulerModel>
    implements PerformJobStrategy<T> {

  // Messages
  protected static final String TASK_SCHEDULED_INFO = "Scheduled async task [type=%s]";
  protected static final String TASK_ALREADY_PENDING_INFO =
      "Skipping async task [type=%s] schedule: Another task is already pending";
  protected static final String UNEXPECTED_ERROR = "An unexpected error occurred while scheduling async task [type=%s]";

  // Logger
  protected final Logger LOG = Logger.getLogger(getClass());

  // Beans
  protected MiraklAsyncTaskService miraklAsyncTaskService;

  @Override
  public PerformResult perform(T cronJob) {
    try {
      MiraklAsyncTaskType type = getAsyncTaskType(cronJob);
      if (miraklAsyncTaskService.hasRunningTasks(type)) {
        LOG.info(format(TASK_ALREADY_PENDING_INFO, type));
      } else {
        Date requestDate = new Date();
        String trackingId = createAsyncTaskInMirakl(cronJob);
        miraklAsyncTaskService.saveNewTask(trackingId, type, requestDate);
        LOG.info(format(TASK_SCHEDULED_INFO, type));
      }
      return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    } catch (Exception e) {
      LOG.error(format(UNEXPECTED_ERROR, getAsyncTaskType(cronJob)), e);
      return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
    }
  }

  /**
   * Provides the asynchronous task type. This type is used to:
   * <ul>
   * <li>Determine the poller to use for this task</li>
   * <li>Avoid concurrency issues</li>
   * </ul>
   *
   * @param cronjob The cronjob model
   * @return The asynchronous task type
   */
  protected abstract MiraklAsyncTaskType getAsyncTaskType(T cronjob);

  /**
   * Creates the asynchronous task in Mirakl.
   *
   * @param cronjob The cronjob model
   * @return The Mirakl tracking ID of the created task
   */
  protected abstract String createAsyncTaskInMirakl(T cronjob);

  @Required
  public void setMiraklAsyncTaskService(MiraklAsyncTaskService miraklAsyncTaskService) {
    this.miraklAsyncTaskService = miraklAsyncTaskService;
  }
}
