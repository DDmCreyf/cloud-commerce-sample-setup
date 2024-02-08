package com.mirakl.hybris.core.product.jobs.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;
import com.mirakl.hybris.core.product.services.MciProductExportService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMciProductExportPerformJobStrategyTest {
  private static final String SYNCHRONIZATION_FILE_NAME = "Synchronization file name";

  @InjectMocks
  private DefaultMciProductExportPerformJobStrategy mciExportStrategy;

  @Mock
  private MciProductExportService productExportService;
  @Mock
  private ModelService modelService;
  @Mock
  private MiraklExportSellableProductsCronJobModel cronJob;
  @Mock
  private CategoryModel rootCategory;
  @Mock
  private CategoryModel rootBrandCategory;
  @Mock
  private BaseSiteModel baseSite;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private CatalogModel catalog;

  @Before
  public void setUp() {
    when(cronJob.getRootCategory()).thenReturn(rootCategory);
    when(cronJob.getRootBrandCategory()).thenReturn(rootBrandCategory);
    when(cronJob.getBaseSite()).thenReturn(baseSite);
    when(cronJob.getSynchronizationFileName()).thenReturn(SYNCHRONIZATION_FILE_NAME);
    when(rootCategory.getCatalogVersion()).thenReturn(catalogVersion);
    when(catalogVersion.getCatalog()).thenReturn(catalog);
    when(catalog.getMiraklCatalogSystem()).thenReturn(MiraklCatalogSystem.MCI);
  }

  @Test
  public void shouldExecuteFullExportSuccessfully() throws IOException {
    when(cronJob.isFullExport()).thenReturn(Boolean.TRUE);

    PerformResult result = mciExportStrategy.perform(cronJob);

    verify(modelService).save(cronJob);
    verify(productExportService).exportAllProducts(rootCategory, rootBrandCategory, baseSite, SYNCHRONIZATION_FILE_NAME);
    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
  }

  @Test
  public void shouldExecuteIncrementalExportSuccessfully() throws IOException {
    Date lastExportDate = new Date();
    when(cronJob.getLastExportDate()).thenReturn(lastExportDate);
    when(cronJob.isFullExport()).thenReturn(Boolean.FALSE);

    PerformResult result = mciExportStrategy.perform(cronJob);

    verify(modelService).save(cronJob);
    verify(productExportService).exportModifiedProducts(rootCategory, rootBrandCategory, baseSite, lastExportDate,
        SYNCHRONIZATION_FILE_NAME);
    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
  }

  @Test
  public void shouldExecuteFullExportWhenNoLastExportDate() throws IOException {
    when(cronJob.isFullExport()).thenReturn(Boolean.FALSE);
    when(cronJob.getLastExportDate()).thenReturn(null);

    PerformResult result = mciExportStrategy.perform(cronJob);

    verify(modelService).save(cronJob);
    verify(productExportService).exportAllProducts(rootCategory, rootBrandCategory, baseSite, SYNCHRONIZATION_FILE_NAME);
    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
  }

  @Test
  public void shouldEndWithErrorOnException() throws IOException {
    when(productExportService.exportAllProducts(rootCategory, rootBrandCategory, baseSite, SYNCHRONIZATION_FILE_NAME))
        .thenThrow(new MiraklApiException(new MiraklErrorResponseBean()));

    PerformResult result = mciExportStrategy.perform(cronJob);

    assertThat(result.getResult()).isEqualTo(CronJobResult.ERROR);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.ABORTED);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowIllegalStateExceptionWhenNoRootCategory() throws IOException {
    when(cronJob.getRootCategory()).thenReturn(null);

    mciExportStrategy.perform(cronJob);
  }

  @Test
  public void shouldExecuteExportSuccessfullyWhenNoMiraklCatalogSystemDefined() {
    when(catalog.getMiraklCatalogSystem()).thenReturn(null);

    PerformResult result = mciExportStrategy.perform(cronJob);

    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
  }
}