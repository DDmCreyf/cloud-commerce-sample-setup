package com.mirakl.hybris.core.category.strategies.impl;

import static com.mirakl.hybris.core.enums.MiraklExportType.COMMISSION_CATEGORY_EXPORT;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mmp.domain.category.synchro.MiraklCategorySynchroResult;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.catalog.category.MiraklCategorySynchroStatusRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.dao.MiraklJobReportDao;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCategoryExportReportStrategyTest {

  private static final String JOB_ID_1 = "job-id-1";
  private static final String JOB_ID_2 = "job-id-2";

  @InjectMocks
  private DefaultCategoryExportReportStrategy exportReportStrategy;

  @Mock
  private MiraklMarketplacePlatformFrontApi mmpApi;
  @Mock
  private ModelService modelService;
  @Mock
  private Populator<MiraklCategorySynchroResult, MiraklJobReportModel> reportPopulator;
  @Mock
  private MiraklJobReportDao miraklJobReportDao;
  @Mock
  private MiraklJobReportModel pendingReport1, pendingReport2;
  @Mock
  private MiraklCategorySynchroResult exportResult1, exportResult2;
  @Captor
  private ArgumentCaptor<List<MiraklJobReportModel>> savedExportReportsCaptor;
  private Map<MiraklProcessTrackingStatus, MiraklExportStatus> exportStatuses;

  @Before
  public void setUp() {
    when(pendingReport1.getJobId()).thenReturn(JOB_ID_1);
    when(pendingReport2.getJobId()).thenReturn(JOB_ID_2);
    when(mmpApi.getCategorySynchroResult(new MiraklCategorySynchroStatusRequest(JOB_ID_1))).thenReturn(exportResult1);
    when(mmpApi.getCategorySynchroResult(new MiraklCategorySynchroStatusRequest(JOB_ID_2))).thenReturn(exportResult2);
    exportStatuses = new HashMap<>();
    exportStatuses.put(MiraklProcessTrackingStatus.COMPLETE, MiraklExportStatus.COMPLETE);
    exportStatuses.put(MiraklProcessTrackingStatus.QUEUED, MiraklExportStatus.PENDING);
    exportReportStrategy.setExportStatuses(exportStatuses);
  }

  @Test
  public void shouldUpdateCompletedExports() {
    when(miraklJobReportDao.findPendingJobReportsForType(COMMISSION_CATEGORY_EXPORT))
        .thenReturn(asList(pendingReport1, pendingReport2));
    when(exportResult1.getStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);
    when(exportResult2.getStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);

    boolean allReportsProcessedSuccessfully = exportReportStrategy.updatePendingExports();

    assertThat(allReportsProcessedSuccessfully).isTrue();
    verify(reportPopulator).populate(exportResult1, pendingReport1);
    verify(reportPopulator).populate(exportResult2, pendingReport2);
    verify(modelService).saveAll(savedExportReportsCaptor.capture());
    List<MiraklJobReportModel> savedExportReports = savedExportReportsCaptor.getValue();
    assertThat(savedExportReports).containsOnly(pendingReport1, pendingReport2);
  }

  @Test
  public void shouldDoNothingForPendingExports() {
    when(miraklJobReportDao.findPendingJobReportsForType(COMMISSION_CATEGORY_EXPORT))
        .thenReturn(asList(pendingReport1, pendingReport2));
    when(exportResult1.getStatus()).thenReturn(MiraklProcessTrackingStatus.QUEUED);
    when(exportResult2.getStatus()).thenReturn(MiraklProcessTrackingStatus.QUEUED);

    boolean allReportsProcessedSuccessfully = exportReportStrategy.updatePendingExports();

    assertThat(allReportsProcessedSuccessfully).isTrue();
    verifyZeroInteractions(modelService);
  }

  @Test
  public void shouldHandleMiraklExceptions() {
    when(miraklJobReportDao.findPendingJobReportsForType(COMMISSION_CATEGORY_EXPORT))
        .thenReturn(asList(pendingReport1, pendingReport2));
    when(mmpApi.getCategorySynchroResult(new MiraklCategorySynchroStatusRequest(JOB_ID_1)))
        .thenThrow(new MiraklApiException(new MiraklErrorResponseBean()));
    when(exportResult2.getStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);

    boolean allReportsProcessedSuccessfully = exportReportStrategy.updatePendingExports();

    assertThat(allReportsProcessedSuccessfully).isFalse();
    verify(modelService).saveAll(savedExportReportsCaptor.capture());
    List<MiraklJobReportModel> savedExportReports = savedExportReportsCaptor.getValue();
    assertThat(savedExportReports).containsOnly(pendingReport2);
  }

}
