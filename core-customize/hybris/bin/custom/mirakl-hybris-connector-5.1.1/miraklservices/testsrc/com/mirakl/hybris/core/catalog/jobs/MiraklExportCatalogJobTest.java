package com.mirakl.hybris.core.catalog.jobs;

import static com.mirakl.hybris.core.enums.MiraklExportType.ATTRIBUTE_EXPORT;
import static com.mirakl.hybris.core.enums.MiraklExportType.CATALOG_CATEGORY_EXPORT;
import static com.mirakl.hybris.core.enums.MiraklExportType.VALUE_LIST_EXPORT;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.beans.MiraklExportCatalogResultData;
import com.mirakl.hybris.core.catalog.services.MiraklCatalogService;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.strategies.PostProcessExportCatalogStrategy;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;
import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.model.MiraklExportCatalogCronJobModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklExportCatalogJobTest {

  private static final String ATTRIBUTE_IMPORT_JOB_ID = "20";
  private static final String CATALOG_CATEGORY_IMPORT_JOB_ID = "568";
  private static final String VALUE_LIST_IMPORT_JOB_ID = "852";

  @InjectMocks
  private MiraklExportCatalogJob job;

  @Mock
  private MiraklExportCatalogCronJobModel cronJob;
  @Mock
  private MiraklExportCatalogService exportCatalogService;
  @Mock
  private PostProcessExportCatalogStrategy postProcessExportCatalogStrategy;
  @Mock
  private MiraklCatalogService miraklCatalogService;
  @Mock
  private Converter<MiraklExportCatalogCronJobModel, MiraklExportCatalogConfig> exportConfigConverter;
  @Mock
  private Converter<Pair<MiraklExportCatalogConfig, ExportCatalogWriter>, MiraklExportCatalogContext> exportCatalogContextConverter;
  @Mock
  private MiraklExportCatalogConfig exportConfig;
  @Mock
  private MiraklExportCatalogResultData resultData;
  @Mock
  private ExportJobReportService exportJobReportService;
  @Mock
  private ExportCatalogWriter writer;
  @Mock
  private BeanFactory beanFactory;
  @Mock
  private MiraklExportCatalogContext context;

  @Before
  public void setUp() throws IOException {
    when(exportConfigConverter.convert(cronJob)).thenReturn(exportConfig);
    when(exportCatalogContextConverter.convert(any(Pair.class))).thenReturn(context);
    when(exportCatalogService.export(any(MiraklExportCatalogContext.class))).thenReturn(resultData);
    when(beanFactory.getBean(anyString(), Matchers.<Object>anyObject())).thenReturn(writer);
  }

  @Test
  public void shouldExportCatalog() throws IOException {
    PerformResult result = job.perform(cronJob);

    verify(exportCatalogService).export(context);
    verify(postProcessExportCatalogStrategy).postProcess(cronJob, context);
    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
  }

  @Test
  public void shouldCreateAllJobReports() {
    when(resultData.getMiraklValueListImportTracking()).thenReturn(VALUE_LIST_IMPORT_JOB_ID);
    when(resultData.getMiraklCatalogCategoryImportTracking()).thenReturn(CATALOG_CATEGORY_IMPORT_JOB_ID);
    when(resultData.getMiraklAttributeImportTracking()).thenReturn(ATTRIBUTE_IMPORT_JOB_ID);

    job.perform(cronJob);

    verify(exportJobReportService).createMiraklJobReport(VALUE_LIST_IMPORT_JOB_ID, VALUE_LIST_EXPORT);
    verify(exportJobReportService).createMiraklJobReport(CATALOG_CATEGORY_IMPORT_JOB_ID, CATALOG_CATEGORY_EXPORT);
    verify(exportJobReportService).createMiraklJobReport(ATTRIBUTE_IMPORT_JOB_ID, ATTRIBUTE_EXPORT);
    verify(exportJobReportService, times(3)).createMiraklJobReport(anyString(), any(MiraklExportType.class));
  }

  @Test
  public void shouldCreateOnlyOneJobReport() {
    when(resultData.getMiraklCatalogCategoryImportTracking()).thenReturn(CATALOG_CATEGORY_IMPORT_JOB_ID);

    job.perform(cronJob);

    verify(exportJobReportService).createMiraklJobReport(CATALOG_CATEGORY_IMPORT_JOB_ID, CATALOG_CATEGORY_EXPORT);
    verify(exportJobReportService).createMiraklJobReport(anyString(), any(MiraklExportType.class));
  }
}
