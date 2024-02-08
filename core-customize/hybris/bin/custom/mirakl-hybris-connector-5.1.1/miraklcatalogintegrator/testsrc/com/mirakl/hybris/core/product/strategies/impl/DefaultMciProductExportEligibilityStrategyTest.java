package com.mirakl.hybris.core.product.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.product.daos.impl.DefaultMiraklProductDao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMciProductExportEligibilityStrategyTest {
  private static final Date LAST_EXPORT_DATE = new Date();

  @InjectMocks
  private DefaultMciProductExportEligibilityStrategy strategy;

  @Mock
  private ModelService modelService;
  @Mock
  private DefaultMiraklProductDao customProductDao;
  @Mock
  private CatalogVersionModel catalogVersion;
  @Mock
  private ProductModel product1, product2;

  @Test
  public void shouldReturnModifiedProducts() {
    when(customProductDao.findModifiedProductsWithNoVariantType(LAST_EXPORT_DATE, catalogVersion))
    .thenReturn(asList(product1, product2));

    Collection<ProductModel> modifiedProducts = strategy.getModifiedProductsEligibleForExport(catalogVersion, LAST_EXPORT_DATE);

    assertThat(modifiedProducts).containsOnly(product1, product2);
  }

  @Test
  public void shouldReturnAllProducts() {
    when(customProductDao.findModifiedProductsWithNoVariantType(null, catalogVersion)).thenReturn(asList(product1, product2));

    Collection<ProductModel> modifiedProducts = strategy.getAllProductsEligibleForExport(catalogVersion);

    assertThat(modifiedProducts).containsOnly(product1, product2);
  }
}
