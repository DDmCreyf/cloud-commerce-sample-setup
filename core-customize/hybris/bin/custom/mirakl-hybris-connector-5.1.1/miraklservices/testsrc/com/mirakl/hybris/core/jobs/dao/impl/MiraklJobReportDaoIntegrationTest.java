package com.mirakl.hybris.core.jobs.dao.impl;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.jobs.dao.MiraklJobReportDao;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class MiraklJobReportDaoIntegrationTest extends ServicelayerTest {

  private static final String SYNC_JOB_ID_1 = "syncJobId1";

  @Resource
  private MiraklJobReportDao miraklJobReportDao;

  @Before
  public void setUp() throws ImpExException {
    importCsv("/miraklservices/test/testJobReports.impex", "utf-8");
  }

  @Test
  public void findsPendingCategoryExportReports() {
    List<MiraklJobReportModel> result = miraklJobReportDao.findPendingJobReportsForType(MiraklExportType.ATTRIBUTE_EXPORT);

    assertThat(result).hasSize(1);
    assertThat(result).onProperty(MiraklJobReportModel.JOBID).containsOnly(SYNC_JOB_ID_1);
  }
}
