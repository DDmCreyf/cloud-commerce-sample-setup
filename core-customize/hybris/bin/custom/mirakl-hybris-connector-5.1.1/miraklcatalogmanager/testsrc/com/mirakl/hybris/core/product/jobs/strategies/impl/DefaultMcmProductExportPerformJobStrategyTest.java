package com.mirakl.hybris.core.product.jobs.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.enums.ProductOrigin;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;
import com.mirakl.hybris.core.product.services.McmProductExportService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMcmProductExportPerformJobStrategyTest {
  @InjectMocks
  private DefaultMcmProductExportPerformJobStrategy mcmExportStrategy;
  @Mock
  private ModelService modelService;
  @Mock
  private Converter<MiraklExportSellableProductsCronJobModel, ProductDataSheetExportContextData> productExportContextDataConverter;
  @Mock
  private McmProductExportService productExportService;
  @Mock
  private MiraklExportSellableProductsCronJobModel cronJob;
  @Mock
  private ProductDataSheetExportContextData exportContext;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private CatalogModel catalog;

  @Before
  public void setUp() throws Exception {
    when(cronJob.getCatalogVersion()).thenReturn(catalogVersion);
    when(cronJob.getProductOrigins()).thenReturn(Sets.newHashSet(ProductOrigin.OPERATOR));
    when(catalogVersion.getCatalog()).thenReturn(catalog);
    when(catalog.getMiraklCatalogSystem()).thenReturn(MiraklCatalogSystem.MCM);
  }

  @Test
  public void shouldPerformExportSuccessfully() throws Exception {
    when(productExportContextDataConverter.convert(cronJob)).thenReturn(exportContext);

    PerformResult result = mcmExportStrategy.perform(cronJob);

    verify(productExportService).exportProductDataSheets(exportContext);
    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
  }

  @Test
  public void shouldEndWithErrorOnExceptionDuringExport() throws Exception {
    when(productExportContextDataConverter.convert(cronJob)).thenReturn(exportContext);
    when(productExportService.exportProductDataSheets(exportContext)).thenThrow(new IOException());

    PerformResult result = mcmExportStrategy.perform(cronJob);

    assertThat(result.getResult()).isEqualTo(CronJobResult.ERROR);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.ABORTED);
  }

  @Test
  public void shouldEndWithErrorOnExceptionDuringConversion() throws Exception {
    when(productExportContextDataConverter.convert(cronJob)).thenThrow(new RuntimeException());

    PerformResult result = mcmExportStrategy.perform(cronJob);

    assertThat(result.getResult()).isEqualTo(CronJobResult.ERROR);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.ABORTED);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldTrowIllegalStateExceptionWhenCatalogVersionProvided() {
    when(cronJob.getCatalogVersion()).thenReturn(null);

    mcmExportStrategy.perform(cronJob);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldTrowIllegalStateExceptionWhenMiraklCatalogSystemEmpty() {
    when(catalog.getMiraklCatalogSystem()).thenReturn(null);

    mcmExportStrategy.perform(cronJob);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldTrowIllegalStateExceptionWhenNoProductOrigin() {
    when(cronJob.getProductOrigins()).thenReturn(null);

    mcmExportStrategy.perform(cronJob);
  }

}
