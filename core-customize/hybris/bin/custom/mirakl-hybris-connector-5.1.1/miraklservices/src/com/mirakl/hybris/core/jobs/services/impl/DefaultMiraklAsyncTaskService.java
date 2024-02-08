package com.mirakl.hybris.core.jobs.services.impl;

import static com.mirakl.hybris.core.enums.MiraklAsyncTaskStatus.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Sets;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskStatus;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskType;
import com.mirakl.hybris.core.jobs.dao.MiraklAsyncTaskDao;
import com.mirakl.hybris.core.jobs.services.MiraklAsyncTaskService;
import com.mirakl.hybris.core.model.MiraklAsyncTaskModel;

import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMiraklAsyncTaskService implements MiraklAsyncTaskService {

  protected static final Collection<MiraklAsyncTaskStatus> FINAL_STATUSES = Sets.newHashSet(DONE, ERROR);
  protected static final Collection<MiraklAsyncTaskStatus> RUNNING_STATUSES =
      CollectionUtils.subtract(Arrays.asList(MiraklAsyncTaskStatus.values()), FINAL_STATUSES);

  protected MiraklAsyncTaskDao miraklAsyncTaskDao;
  protected ModelService modelService;

  @Override
  public MiraklAsyncTaskModel saveNewTask(String trackingId, MiraklAsyncTaskType type, Date requestDate) {
    MiraklAsyncTaskModel miraklAsyncTask = modelService.create(MiraklAsyncTaskModel.class);
    miraklAsyncTask.setStatus(PENDING);
    miraklAsyncTask.setTrackingId(trackingId);
    miraklAsyncTask.setType(type);
    miraklAsyncTask.setRequestDate(requestDate);
    modelService.save(miraklAsyncTask);
    return miraklAsyncTask;
  }

  @Override
  public MiraklAsyncTaskModel updateTaskStatus(MiraklAsyncTaskModel task, MiraklAsyncTaskStatus status) {
    task.setStatus(status);
    modelService.save(task);
    return task;
  }

  @Override
  public boolean hasRunningTasks(MiraklAsyncTaskType type) {
    return miraklAsyncTaskDao.findOneWithTypeAndStatuses(type, RUNNING_STATUSES) != null;
  }

  @Override
  public List<MiraklAsyncTaskModel> findTasksToProcess(MiraklAsyncTaskType type) {
    return miraklAsyncTaskDao.findPendingTasksByRequestDate(type);
  }

  @Override
  public void delete(MiraklAsyncTaskModel task) {
    modelService.remove(task);
  }

  @Required
  public void setMiraklAsyncTaskDao(MiraklAsyncTaskDao miraklAsyncTaskDao) {
    this.miraklAsyncTaskDao = miraklAsyncTaskDao;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }
}
