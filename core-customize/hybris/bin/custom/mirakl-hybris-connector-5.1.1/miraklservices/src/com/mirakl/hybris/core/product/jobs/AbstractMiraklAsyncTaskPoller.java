package com.mirakl.hybris.core.product.jobs;

import static java.lang.Double.valueOf;
import static java.lang.String.format;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StopWatch;

import com.mirakl.hybris.core.enums.MiraklAsyncTaskStatus;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskType;
import com.mirakl.hybris.core.jobs.services.MiraklAsyncTaskService;
import com.mirakl.hybris.core.model.MiraklAsyncTaskModel;
import com.mirakl.hybris.core.model.MiraklAsyncTaskPollerModel;
import com.mirakl.hybris.core.model.MiraklAsyncTaskSchedulerModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public abstract class AbstractMiraklAsyncTaskPoller<T extends MiraklAsyncTaskPollerModel> extends AbstractJobPerformable<T> {

  // Messages
  protected static final String TASK_PROCESSING_ERROR = "Impossible to process async task [type=%s, trackingId=%s]";
  protected static final String TASK_PROCESSING_START = "Processing async task [type=%s, trackingId=%s]...";
  protected static final String TASK_STILL_PENDING = "Waiting for Mirakl [type=%s, trackingId=%s]";
  protected static final String TASK_PROCESSING_END = "Processed async task [type=%s, trackingId=%s, status=%s]";
  protected static final String TASK_CLEAN_UP = "Cleaned up task [type=%s, trackingId=%s, status=%s]";
  protected static final String TASK_PROCESSING_DURATION = "Processing for [type=%s, trackingId=%s, status=%s] took %f seconds.";

  // Logger
  protected final Logger LOG = Logger.getLogger(getClass());

  // Beans
  protected MiraklAsyncTaskService miraklAsyncTaskService;

  @Override
  public PerformResult perform(T cronJob) {
    try {
      return doPerform(cronJob);
    } catch (Exception e) {
      LOG.error("Impossible to process pending tasks", e);
      return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
    }
  }

  protected PerformResult doPerform(T cronJob) {
    MiraklAsyncTaskType type = getAsyncTaskType(cronJob);
    List<MiraklAsyncTaskModel> pendingTasks = miraklAsyncTaskService.findTasksToProcess(type);
    for (MiraklAsyncTaskModel task : pendingTasks) {
      try {
        handleTask(cronJob, task);
      } catch (Exception e) {
        LOG.error(format(TASK_PROCESSING_ERROR, task.getType(), task.getTrackingId()), e);
        setFinalStatus(cronJob, task, MiraklAsyncTaskStatus.ERROR);
      }
    }
    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  protected void handleTask(T cronJob, MiraklAsyncTaskModel task) throws Exception {
    MiraklAsyncTaskType taskType = task.getType();
    String taskTrackingId = task.getTrackingId();
    LOG.info(format(TASK_PROCESSING_START, taskType, taskTrackingId));
    final StopWatch taskProcessingWatch = new StopWatch();
    taskProcessingWatch.start();

    MiraklAsyncTaskStatus resultStatus = processTask(cronJob, task);
    switch (resultStatus) {
      case DONE:
        updateSchedulerDate(cronJob, task);
        setFinalStatus(cronJob, task, MiraklAsyncTaskStatus.DONE);
        break;
      case ERROR:
        setFinalStatus(cronJob, task, MiraklAsyncTaskStatus.ERROR);
        break;
      case PENDING:
      default:
        LOG.info(format(TASK_STILL_PENDING, taskType, taskTrackingId));
        break;
    }

    taskProcessingWatch.stop();
    LOG.info(format(TASK_PROCESSING_DURATION, taskType, taskTrackingId, resultStatus,
        valueOf(taskProcessingWatch.getTotalTimeSeconds())));
  }

  protected void updateSchedulerDate(T cronJob, MiraklAsyncTaskModel task) {
    MiraklAsyncTaskSchedulerModel schedulerInstance = cronJob.getSchedulerInstance();
    schedulerInstance.setLastImportTime(task.getRequestDate());
    modelService.save(schedulerInstance);
  }

  protected void setFinalStatus(T cronJob, MiraklAsyncTaskModel task, MiraklAsyncTaskStatus finalStatus) {
    if (cronJob.getCleanupMiraklAsyncTask()) {
      miraklAsyncTaskService.delete(task);
      LOG.info(format(TASK_CLEAN_UP, task.getType(), task.getTrackingId(), finalStatus));
    } else {
      miraklAsyncTaskService.updateTaskStatus(task, finalStatus);
      LOG.info(format(TASK_PROCESSING_END, task.getType(), task.getTrackingId(), finalStatus));
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
   * Check if the task is ready to be processed in Mirakl, and handles it if required.
   *
   * @param cronJob The current job model
   * @param task The task to handle
   */
  protected abstract MiraklAsyncTaskStatus processTask(T cronJob, MiraklAsyncTaskModel task) throws Exception;

  @Required
  public void setMiraklAsyncTaskService(MiraklAsyncTaskService miraklAsyncTaskService) {
    this.miraklAsyncTaskService = miraklAsyncTaskService;
  }

}
