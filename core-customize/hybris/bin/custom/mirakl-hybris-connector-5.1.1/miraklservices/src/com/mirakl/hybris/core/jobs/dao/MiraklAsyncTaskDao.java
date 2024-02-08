package com.mirakl.hybris.core.jobs.dao;

import java.util.Collection;
import java.util.List;

import com.mirakl.hybris.core.enums.MiraklAsyncTaskStatus;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskType;
import com.mirakl.hybris.core.model.MiraklAsyncTaskModel;

import de.hybris.platform.servicelayer.internal.dao.GenericDao;

public interface MiraklAsyncTaskDao extends GenericDao<MiraklAsyncTaskModel> {

  MiraklAsyncTaskModel findOneWithTypeAndStatuses(MiraklAsyncTaskType type, Collection<MiraklAsyncTaskStatus> statuses);

  List<MiraklAsyncTaskModel> findPendingTasksByRequestDate(MiraklAsyncTaskType type);

}
