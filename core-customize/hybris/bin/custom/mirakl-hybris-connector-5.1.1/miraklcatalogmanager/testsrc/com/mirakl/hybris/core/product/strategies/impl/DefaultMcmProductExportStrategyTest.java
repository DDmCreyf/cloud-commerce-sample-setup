package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklcatalogmanagerConstants.PRODUCTS_DATASHEETS_EXPORT_MAX_PRODUCTS_PER_FILE;
import static java.lang.Math.ceil;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncItem;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncTracking;
import com.mirakl.client.mci.domain.product.MiraklSynchronizedProductDataSheetAcceptanceStatus;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncRequest;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.model.MiraklExportProductDataSheetJobReportModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMcmProductExportStrategyTest {
  private static final int MAX_PRODUCTS_PER_FILE = 2;

  @InjectMocks
  private DefaultMcmProductExportStrategy mcmExportStrategy;

  @Mock
  private ConfigurationService configurationService;
  @Mock
  private ExportJobReportService exportJobReportService;
  @Mock
  private ModelService modelService;
  @Mock
  private MiraklCatalogIntegrationFrontApi mciFrontApi;
  @Mock
  private Converter<Pair<ProductModel, ProductDataSheetExportContextData>, MiraklProductDataSheetSyncItem> productDataSheetSyncItemConverter;
  @Mock
  private ProductDataSheetExportContextData productExportContext;
  @Mock
  private Configuration configuration;
  @Mock
  private ProductModel product1, product2, product3, product4, product5;
  @Mock
  private Map<ArticleApprovalStatus, MiraklSynchronizedProductDataSheetAcceptanceStatus> productApprovalStatusMapping;

  @Before
  public void setUp() throws Exception {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getInt(PRODUCTS_DATASHEETS_EXPORT_MAX_PRODUCTS_PER_FILE)).thenReturn(MAX_PRODUCTS_PER_FILE);
    when(mciFrontApi.synchronizeProductDataSheets(any(MiraklProductDataSheetSyncRequest.class)))
        .thenReturn(mock(MiraklProductDataSheetSyncTracking.class));
    when(exportJobReportService.createMiraklJobReport(any(), eq(MiraklExportType.PRODUCT_DATASHEET_EXPORT)))
        .thenReturn(mock(MiraklExportProductDataSheetJobReportModel.class));
  }

  @Test
  public void shouldExportProductsWhenNumberOfProductsIsMultipleOfPageSize() throws Exception {
    shouldExportProducts(asList(product1, product2, product3, product4));
  }

  @Test
  public void shouldExportProductsWhenNumberOfProductsIsNotMultipleOfPageSize() throws Exception {
    shouldExportProducts(asList(product1, product2, product3, product4, product5));
  }

  @Test
  public void shouldDoNothingWhenNoProducts() throws Exception {
    int exportedProductsCount = mcmExportStrategy.exportProductDataSheets(new ArrayList<>(), productExportContext);

    verifyZeroInteractions(mciFrontApi);
    assertThat(exportedProductsCount).isEqualTo(0);
  }

  protected void shouldExportProducts(List<ProductModel> products) throws Exception {
    int exportedProductsCount = mcmExportStrategy.exportProductDataSheets(products, productExportContext);

    for (ProductModel product : products) {
      verify(productDataSheetSyncItemConverter).convert(Pair.of(product, productExportContext));
    }
    double productsPerPage = (double) products.size() / MAX_PRODUCTS_PER_FILE;
    verify(mciFrontApi, times((int) ceil(productsPerPage)))
        .synchronizeProductDataSheets(any(MiraklProductDataSheetSyncRequest.class));
    assertThat(exportedProductsCount).isEqualTo(products.size());
  }

}
