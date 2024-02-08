package com.mirakl.hybris.core.product.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.product.strategies.McmProductExportEligibilityStrategy;
import com.mirakl.hybris.core.product.strategies.McmProductExportStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMcmProductExportServiceTest {

  @InjectMocks
  private DefaultMcmProductExportService exportService;

  @Mock
  private McmProductExportStrategy mcmProductExportStrategy;
  @Mock
  private McmProductExportEligibilityStrategy eligibilityStrategy;
  @Mock
  private ProductDataSheetExportContextData exportContext;
  @Mock
  private ProductModel product1, product2, product3;
  private List<ProductModel> products;

  @Before
  public void setUp() {
    products = Arrays.asList(product1, product2, product3);
  }

  @Test
  public void shouldExportMcmProducts() throws Exception {
    when(eligibilityStrategy.getProductDataSheetsEligibleForExport(exportContext)).thenReturn(products);
    when(mcmProductExportStrategy.exportProductDataSheets(products, exportContext)).thenReturn(products.size());

    int exportedProductsCount = exportService.exportProductDataSheets(exportContext);

    verify(mcmProductExportStrategy).exportProductDataSheets(products, exportContext);
    assertThat(exportedProductsCount).isEqualTo(products.size());
  }

}
