package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.enums.MiraklExportType.VALUE_LIST_EXPORT;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
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
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.front.domain.value.list.MiraklValueListImportResult;
import com.mirakl.client.mci.front.request.value.list.MiraklValueListImportStatusRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.dao.MiraklJobReportDao;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultValueListExportReportStrategyTest {

  private static final String JOB_ID_1 = "job-id-1";
  private static final String JOB_ID_2 = "job-id-2";

  @InjectMocks
  private DefaultValueListExportReportStrategy exportReportStrategy;

  @Mock
  private MiraklCatalogIntegrationFrontApi mciApi;
  @Mock
  private Populator<MiraklValueListImportResult, MiraklJobReportModel> reportPopulator;
  @Mock
  private MiraklValueListImportResult exportResult;
  @Mock
  private MiraklJobReportDao miraklJobReportDao;
  @Mock
  private MiraklJobReportModel pendingReport1, pendingReport2;
  @Mock
  private Map<MiraklProcessTrackingStatus, MiraklExportStatus> exportStatuses;
  @Mock
  private MiraklValueListImportResult valueListImportResult;
  @Mock
  private File errorReportFile;
  @Mock
  private ModelService modelService;
  @Mock
  private MiraklValueListImportResult exportResult1, exportResult2;
  @Mock
  private MiraklValueListImportResult attributesImportResult;
  @Captor
  private ArgumentCaptor<List<MiraklJobReportModel>> savedExportReportsCaptor;

  @Before
  public void setUp() {
    when(pendingReport1.getJobId()).thenReturn(JOB_ID_1);
    when(pendingReport2.getJobId()).thenReturn(JOB_ID_2);
    when(mciApi.getValueListImportResult(new MiraklValueListImportStatusRequest((JOB_ID_1)))).thenReturn(exportResult1);
    when(mciApi.getValueListImportResult(new MiraklValueListImportStatusRequest((JOB_ID_2)))).thenReturn(exportResult2);
    exportStatuses = new HashMap<>();
    exportStatuses.put(MiraklProcessTrackingStatus.COMPLETE, MiraklExportStatus.COMPLETE);
    exportStatuses.put(MiraklProcessTrackingStatus.QUEUED, MiraklExportStatus.PENDING);
    exportReportStrategy.setExportStatuses(exportStatuses);
  }

  @Test
  public void shouldUpdateCompletedExports() {
    when(miraklJobReportDao.findPendingJobReportsForType(VALUE_LIST_EXPORT))
        .thenReturn(asList(pendingReport1, pendingReport2));
    when(exportResult1.getImportStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);
    when(exportResult2.getImportStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);

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
    when(miraklJobReportDao.findPendingJobReportsForType(VALUE_LIST_EXPORT))
        .thenReturn(asList(pendingReport1, pendingReport2));
    when(exportResult1.getImportStatus()).thenReturn(MiraklProcessTrackingStatus.QUEUED);
    when(exportResult2.getImportStatus()).thenReturn(MiraklProcessTrackingStatus.QUEUED);

    boolean allReportsProcessedSuccessfully = exportReportStrategy.updatePendingExports();

    assertThat(allReportsProcessedSuccessfully).isTrue();
    verifyZeroInteractions(modelService);
  }

  @Test
  public void shouldHandleMiraklExceptions() {
    when(miraklJobReportDao.findPendingJobReportsForType(VALUE_LIST_EXPORT))
        .thenReturn(asList(pendingReport1, pendingReport2));
    when(mciApi.getValueListImportResult(new MiraklValueListImportStatusRequest(JOB_ID_1)))
        .thenThrow(new MiraklApiException(new MiraklErrorResponseBean()));
    when(exportResult2.getImportStatus()).thenReturn(MiraklProcessTrackingStatus.COMPLETE);

    boolean allReportsProcessedSuccessfully = exportReportStrategy.updatePendingExports();

    assertThat(allReportsProcessedSuccessfully).isFalse();
    verify(modelService).saveAll(savedExportReportsCaptor.capture());
    List<MiraklJobReportModel> savedExportReports = savedExportReportsCaptor.getValue();
    assertThat(savedExportReports).containsOnly(pendingReport2);
  }

}
