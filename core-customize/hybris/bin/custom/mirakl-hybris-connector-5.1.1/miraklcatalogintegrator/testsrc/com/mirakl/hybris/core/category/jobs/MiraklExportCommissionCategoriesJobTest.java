package com.mirakl.hybris.core.category.jobs;

import static com.mirakl.hybris.core.enums.MiraklExportType.COMMISSION_CATEGORY_EXPORT;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.category.synchro.MiraklCategorySynchroTracking;
import com.mirakl.hybris.core.category.services.CategoryExportService;
import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.model.MiraklExportCommissionCategoriesCronJobModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklExportCommissionCategoriesJobTest {

  private static final String SYNC_JOB_ID = "syncJobId";
  private static final String SYNCHRONIZATION_FILE_NAME = "Synchronization file name";

  @InjectMocks
  private MiraklExportCommissionCategoriesJob testObj;

  @Mock
  private CategoryExportService categoryExportService;
  @Mock
  private CommonI18NService commonI18NService;
  @Mock
  private I18NService i18NService;
  @Mock
  private ExportJobReportService exportJobReportService;
  @Mock
  private MiraklExportCommissionCategoriesCronJobModel miraklExportCategoriesCronJob;
  @Mock
  private CategoryModel rootCategory;
  @Mock
  private LanguageModel language;
  @Mock
  private MiraklCategorySynchroTracking miraklCategorySynchroTracking;

  @Before
  public void setUp() throws IOException {
    when(miraklExportCategoriesCronJob.getRootCategory()).thenReturn(rootCategory);
    when(miraklExportCategoriesCronJob.getSessionLanguage()).thenReturn(language);
    when(miraklExportCategoriesCronJob.getSynchronizationFileName()).thenReturn(SYNCHRONIZATION_FILE_NAME);

    when(commonI18NService.getLocaleForLanguage(language)).thenReturn(Locale.ENGLISH);
    when(i18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
    when(categoryExportService.exportCommissionCategories(eq(rootCategory), any(), anyString(), anySetOf(Locale.class)))
        .thenReturn(miraklCategorySynchroTracking);
    when(miraklCategorySynchroTracking.getSynchroId()).thenReturn(SYNC_JOB_ID);
  }

  @Test
  public void exportsCategoriesForConfiguredCatalogVersionAndSessionLanguage() throws IOException {
    PerformResult result = testObj.perform(miraklExportCategoriesCronJob);
    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);


    verify(categoryExportService).exportCommissionCategories(eq(rootCategory), eq(Locale.ENGLISH), eq(SYNCHRONIZATION_FILE_NAME),
        anySetOf(Locale.class));
    verify(exportJobReportService).createMiraklJobReport(SYNC_JOB_ID, COMMISSION_CATEGORY_EXPORT);
  }

  @Test
  public void exportCategoriesEndsWithErrorIfMiraklApiExceptionOccurs() throws IOException {
    when(categoryExportService.exportCommissionCategories(eq(rootCategory), eq(Locale.ENGLISH), eq(SYNCHRONIZATION_FILE_NAME),
        anySetOf(Locale.class))).thenThrow(new MiraklApiException(new MiraklErrorResponseBean()));

    PerformResult result = testObj.perform(miraklExportCategoriesCronJob);

    assertThat(result.getResult()).isEqualTo(CronJobResult.ERROR);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.ABORTED);

    verify(exportJobReportService, never()).createMiraklJobReport(anyString(), any(MiraklExportType.class));
  }

  @Test
  public void exportCategoriesEndsWithErrorIfIOExceptionOccurs() throws IOException {
    when(categoryExportService.exportCommissionCategories(eq(rootCategory), any(), anyString(), anySetOf(Locale.class)))
        .thenThrow(new IOException());

    PerformResult result = testObj.perform(miraklExportCategoriesCronJob);

    assertThat(result.getResult()).isEqualTo(CronJobResult.ERROR);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.ABORTED);

    verify(exportJobReportService, never()).createMiraklJobReport(anyString(), any(MiraklExportType.class));
  }

  @Test
  public void exportsCategoriesForCurrentLanguageIfNoSessionLanguageFound() throws IOException {
    when(miraklExportCategoriesCronJob.getSessionLanguage()).thenReturn(null);
    when(i18NService.getCurrentLocale()).thenReturn(Locale.FRENCH);

    PerformResult result = testObj.perform(miraklExportCategoriesCronJob);

    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);

    verify(categoryExportService).exportCommissionCategories(eq(rootCategory), eq(Locale.FRENCH), eq(SYNCHRONIZATION_FILE_NAME),
        anySetOf(Locale.class));
    verify(exportJobReportService).createMiraklJobReport(SYNC_JOB_ID, COMMISSION_CATEGORY_EXPORT);
  }
}
