package com.mirakl.hybris.core.product.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.product.strategies.MciProductExportEligibilityStrategy;
import com.mirakl.hybris.core.product.strategies.MciProductExportStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMciProductExportServiceTest {

  private static final String EXPORT_FILENAME = "export-filename";
  private static final Date MODIFIED_AFTER = new Date();

  @InjectMocks
  private DefaultMciProductExportService exportService;

  @Mock
  private MciProductExportStrategy mciProductExportStrategy;
  @Mock
  private MciProductExportEligibilityStrategy eligibilityStrategy;
  @Mock
  private CategoryModel pcmCategory, brandCategory;
  @Mock
  private BaseSiteModel baseSite;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private ProductModel product1, product2, product3;
  private List<ProductModel> products;

  @Before
  public void setUp() {
    products = Arrays.asList(product1, product2, product3);
    when(pcmCategory.getCatalogVersion()).thenReturn(catalogVersion);
  }

  @Test
  public void shouldExportMciAllProducts() throws Exception {
    when(eligibilityStrategy.getAllProductsEligibleForExport(catalogVersion)).thenReturn(products);
    when(mciProductExportStrategy.exportProducts(products, pcmCategory, brandCategory, baseSite, EXPORT_FILENAME))
        .thenReturn(products.size());

    int exportedProductsCount = exportService.exportAllProducts(pcmCategory, brandCategory, baseSite, EXPORT_FILENAME);

    verify(mciProductExportStrategy).exportProducts(products, pcmCategory, brandCategory, baseSite, EXPORT_FILENAME);
    assertThat(exportedProductsCount).isEqualTo(products.size());
  }

  @Test
  public void shouldExportMciModifiedProducts() throws Exception {
    when(eligibilityStrategy.getModifiedProductsEligibleForExport(catalogVersion, MODIFIED_AFTER)).thenReturn(products);
    when(mciProductExportStrategy.exportProducts(products, pcmCategory, brandCategory, baseSite, EXPORT_FILENAME))
        .thenReturn(products.size());

    int exportedProductsCount =
        exportService.exportModifiedProducts(pcmCategory, brandCategory, baseSite, MODIFIED_AFTER, EXPORT_FILENAME);

    verify(mciProductExportStrategy).exportProducts(products, pcmCategory, brandCategory, baseSite, EXPORT_FILENAME);
    assertThat(exportedProductsCount).isEqualTo(products.size());
  }
}
