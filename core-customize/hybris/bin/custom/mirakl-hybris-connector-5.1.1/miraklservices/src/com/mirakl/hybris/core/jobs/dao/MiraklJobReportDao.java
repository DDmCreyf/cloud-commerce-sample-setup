package com.mirakl.hybris.core.jobs.dao;

import java.util.List;

import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public interface MiraklJobReportDao {

  List<MiraklJobReportModel> findPendingJobReportsForType(MiraklExportType exportType);
}
