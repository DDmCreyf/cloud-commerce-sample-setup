package com.mirakl.hybris.core.jobs.dao.impl;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.Collection;
import java.util.List;

import com.mirakl.hybris.core.enums.MiraklAsyncTaskStatus;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskType;
import com.mirakl.hybris.core.jobs.dao.MiraklAsyncTaskDao;
import com.mirakl.hybris.core.model.MiraklAsyncTaskModel;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultMiraklAsyncTaskDao extends DefaultGenericDao<MiraklAsyncTaskModel> implements MiraklAsyncTaskDao {

  protected static final String FIND_BY_TYPE_AND_STATUS_QUERY =
      "SELECT {mat:" + MiraklAsyncTaskModel.PK + "} FROM {" + MiraklAsyncTaskModel._TYPECODE + " AS mat} " //
          + "WHERE {mat:" + MiraklAsyncTaskModel.STATUS + "} IN (?" + MiraklAsyncTaskModel.STATUS + ") " //
          + "AND {mat:" + MiraklAsyncTaskModel.TYPE + "} = ?" + MiraklAsyncTaskModel.TYPE;

  protected static final String FIND_PENDING_BY_TYPE_QUERY =
      "SELECT {mat:" + MiraklAsyncTaskModel.PK + "} FROM {" + MiraklAsyncTaskModel._TYPECODE + " AS mat} " //
          + "WHERE {mat:" + MiraklAsyncTaskModel.STATUS + "} = ?" + MiraklAsyncTaskModel.STATUS + " " //
          + "AND {mat:" + MiraklAsyncTaskModel.TYPE + "} = ?" + MiraklAsyncTaskModel.TYPE + " " //
          + "ORDER BY {mat:" + MiraklAsyncTaskModel.REQUESTDATE + "} ASC";

  public DefaultMiraklAsyncTaskDao() {
    super(MiraklAsyncTaskModel._TYPECODE);
  }

  @Override
  public MiraklAsyncTaskModel findOneWithTypeAndStatuses(MiraklAsyncTaskType type, Collection<MiraklAsyncTaskStatus> statuses) {
    FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_BY_TYPE_AND_STATUS_QUERY);
    query.addQueryParameter(MiraklAsyncTaskModel.TYPE, type);
    query.addQueryParameter(MiraklAsyncTaskModel.STATUS, statuses);
    query.setCount(1);
    SearchResult<MiraklAsyncTaskModel> search = getFlexibleSearchService().search(query);
    List<MiraklAsyncTaskModel> result = search.getResult();
    return isNotEmpty(result) ? result.get(0) : null;
  }

  @Override
  public List<MiraklAsyncTaskModel> findPendingTasksByRequestDate(MiraklAsyncTaskType type) {
    FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_PENDING_BY_TYPE_QUERY);
    query.addQueryParameter(MiraklAsyncTaskModel.TYPE, type);
    query.addQueryParameter(MiraklAsyncTaskModel.STATUS, MiraklAsyncTaskStatus.PENDING);
    SearchResult<MiraklAsyncTaskModel> search = getFlexibleSearchService().search(query);
    return search.getResult();
  }

}
