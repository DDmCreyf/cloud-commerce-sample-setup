package com.mirakl.hybris.core.product.jobs;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.jobs.strategies.ExportProductsCatalogResolutionStrategy;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;
import com.mirakl.hybris.core.product.strategies.PerformJobStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.enumeration.EnumerationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklExportSellableProductsJobTest {

  @InjectMocks
  private MiraklExportSellableProductsJob miraklExportSellableProductsJob;

  @Mock
  private Map<MiraklCatalogSystem, PerformJobStrategy<MiraklExportSellableProductsCronJobModel>> performJobStrategies;
  @Mock
  private PerformJobStrategy<MiraklExportSellableProductsCronJobModel> performJobStrategy1, performJobStrategy2;
  @Mock
  private EnumerationService enumerationService;
  @Mock
  private MiraklExportSellableProductsCronJobModel cronJob;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private CatalogModel catalog;
  @Mock
  private MiraklCatalogSystem catalogSystem1, catalogSystem2;
  @Mock
  private ExportProductsCatalogResolutionStrategy catalogResolutionStrategy;

  protected List<ExportProductsCatalogResolutionStrategy> catalogResolutionStrategies;


  @Before
  public void setUp() {
    when(catalogResolutionStrategy.resolveCatalog(cronJob)).thenReturn(catalog);
    catalogResolutionStrategies = Lists.newArrayList(catalogResolutionStrategy);
    miraklExportSellableProductsJob.setCatalogResolutionStrategies(catalogResolutionStrategies);
  }

  @Test
  public void shouldUseExportStrategy() {
    when(performJobStrategies.get(catalogSystem2)).thenReturn(performJobStrategy2);
    when(catalog.getMiraklCatalogSystem()).thenReturn(catalogSystem2);
    when(enumerationService.getEnumerationValues(MiraklCatalogSystem.class)).thenReturn(asList(catalogSystem1, catalogSystem2));

    miraklExportSellableProductsJob.perform(cronJob);

    verify(performJobStrategy2).perform(cronJob);
  }

  @Test
  public void shouldUseDefaultCatalogSystem() {
    when(performJobStrategies.get(catalogSystem1)).thenReturn(performJobStrategy1);
    when(enumerationService.getEnumerationValues(MiraklCatalogSystem.class)).thenReturn(asList(catalogSystem1));

    miraklExportSellableProductsJob.perform(cronJob);

    verify(performJobStrategy1).perform(cronJob);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenNoCatalogVersionResolved() {
    miraklExportSellableProductsJob.perform(cronJob);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenNoCatalogSystemConfigured() {
    when(enumerationService.getEnumerationValues(MiraklCatalogSystem.class)).thenReturn(asList(catalogSystem1, catalogSystem2));
    when(catalog.getMiraklCatalogSystem()).thenReturn(null);

    miraklExportSellableProductsJob.perform(cronJob);
  }

}
