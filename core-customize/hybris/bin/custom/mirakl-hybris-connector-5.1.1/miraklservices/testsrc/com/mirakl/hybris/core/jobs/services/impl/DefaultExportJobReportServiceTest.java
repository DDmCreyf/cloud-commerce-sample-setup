package com.mirakl.hybris.core.jobs.services.impl;

import static com.mirakl.hybris.core.enums.MiraklExportType.ATTRIBUTE_EXPORT;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.jobs.factories.impl.MiraklJobReportFactory;
import com.mirakl.hybris.core.jobs.strategies.ExportReportStrategy;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultExportJobReportServiceTest {

  private static final String JOB_ID = "jobId";

  @InjectMocks
  private DefaultExportJobReportService exportJobReportService = new DefaultExportJobReportService();

  @Mock
  private ModelService modelService;
  @Mock
  private ExportReportStrategy categoryExportReportStrategy, productExportReportStrategy;
  @Mock
  private MiraklJobReportFactory miraklJobReportFactory;
  @Mock
  private MiraklJobReportModel miraklJobReport;

  @Before
  public void setUp() {
    when(miraklJobReportFactory.createMiraklJobReport(any(MiraklExportType.class))).thenReturn(miraklJobReport);
    when(categoryExportReportStrategy.updatePendingExports()).thenReturn(true);
    when(productExportReportStrategy.updatePendingExports()).thenReturn(true);

    exportJobReportService.setExportTypeStrategies(ImmutableList.of(categoryExportReportStrategy, productExportReportStrategy));
  }

  @Test
  public void createsMiraklJobReport() {
    MiraklJobReportModel result = exportJobReportService.createMiraklJobReport(JOB_ID, ATTRIBUTE_EXPORT);

    assertThat(result).isEqualTo(miraklJobReport);

    verify(miraklJobReport).setJobId(JOB_ID);
    verify(miraklJobReport).setReportType(ATTRIBUTE_EXPORT);
    verify(modelService).save(miraklJobReport);
  }

  @Test
  public void updatesPendingExportReports() {
    boolean result = exportJobReportService.updatePendingExportReports();

    assertThat(result).isTrue();

    verify(categoryExportReportStrategy).updatePendingExports();
    verify(productExportReportStrategy).updatePendingExports();
  }

  @Test
  public void updatePendingExportReportsReturnsFalseIfOneOfTheStrategiesDidNotEndSuccessfully() {
    when(productExportReportStrategy.updatePendingExports()).thenReturn(false);

    boolean result = exportJobReportService.updatePendingExportReports();

    assertThat(result).isFalse();

    verify(categoryExportReportStrategy).updatePendingExports();
    verify(productExportReportStrategy).updatePendingExports();
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfJobIdIsNull() {
    exportJobReportService.createMiraklJobReport(null, ATTRIBUTE_EXPORT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfJobIdIsEmpty() {
    exportJobReportService.createMiraklJobReport(EMPTY, ATTRIBUTE_EXPORT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfMiraklExportTypeIsNull() {
    exportJobReportService.createMiraklJobReport(JOB_ID, null);
  }
}
