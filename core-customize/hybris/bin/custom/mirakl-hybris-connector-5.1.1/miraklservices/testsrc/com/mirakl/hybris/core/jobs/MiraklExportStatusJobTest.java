package com.mirakl.hybris.core.jobs;

import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.model.MiraklExportStatusCronJobModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklExportStatusJobTest {

  @InjectMocks
  private MiraklExportStatusJob testObj = new MiraklExportStatusJob();

  @Mock
  private ExportJobReportService exportJobReportServiceMock;

  @Mock
  private MiraklExportStatusCronJobModel miraklExportStatusCronJob;

  @Test
  public void performsMiraklExportStatusUpdate() {
    when(exportJobReportServiceMock.updatePendingExportReports()).thenReturn(true);

    PerformResult result = testObj.perform(miraklExportStatusCronJob);

    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);

    verify(exportJobReportServiceMock).updatePendingExportReports();
  }

  @Test
  public void exportStatusUpdateEndsWithErrorIFNotAllPendingExportsWereProcessedSuccessfully() {
    when(exportJobReportServiceMock.updatePendingExportReports()).thenReturn(false);

    PerformResult result = testObj.perform(miraklExportStatusCronJob);

    assertThat(result.getResult()).isEqualTo(CronJobResult.ERROR);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
  }
}
