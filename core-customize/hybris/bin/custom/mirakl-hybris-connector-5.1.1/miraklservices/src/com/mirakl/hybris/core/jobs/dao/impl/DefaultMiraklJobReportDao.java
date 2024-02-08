package com.mirakl.hybris.core.jobs.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.jobs.dao.MiraklJobReportDao;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

public class DefaultMiraklJobReportDao extends DefaultGenericDao<MiraklJobReportModel> implements MiraklJobReportDao {

  public DefaultMiraklJobReportDao() {
    super(MiraklJobReportModel._TYPECODE);
  }

  @Override
  public List<MiraklJobReportModel> findPendingJobReportsForType(MiraklExportType exportType) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(MiraklJobReportModel.REPORTTYPE, exportType);
    parameters.put(MiraklJobReportModel.STATUS, MiraklExportStatus.PENDING);

    return find(parameters);
  }
}
