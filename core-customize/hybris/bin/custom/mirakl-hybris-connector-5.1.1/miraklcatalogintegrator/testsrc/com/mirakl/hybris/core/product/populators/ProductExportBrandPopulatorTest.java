package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.ALL_BRANDS_CONTEXT_VARIABLE;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.MiraklProductExportHeader;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.variants.model.VariantProductModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ProductExportBrandPopulatorTest {
  private static final String BRAND_LABEL = "brand_name";

  @InjectMocks
  private ProductExportBrandPopulator populator;

  @Mock
  private SessionService sessionService;

  @Mock
  private ProductModel product;

  @Mock
  private VariantProductModel variantProduct;

  @Mock
  private CategoryModel brand;

  @Before
  public void setUp() throws Exception {
    when(variantProduct.getBaseProduct()).thenReturn(product);
    when(product.getSupercategories()).thenReturn(asList(brand, mock(CategoryModel.class), mock(CategoryModel.class)));
    when(brand.getName()).thenReturn(BRAND_LABEL);
    when(sessionService.getAttribute(ALL_BRANDS_CONTEXT_VARIABLE)).thenReturn(Arrays.asList(brand, mock(CategoryModel.class)));
  }

  @Test
  public void shouldExportBrandForProductsWithNoVariants() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(result.get(MiraklProductExportHeader.BRAND.getCode())).isEqualTo(BRAND_LABEL);
  }

  @Test
  public void shouldNotFallbackToBaseProductIfBrandIsPresent() {
    when(variantProduct.getSupercategories()).thenReturn(asList(mock(CategoryModel.class), mock(CategoryModel.class)));

    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.BRAND.getCode())).isEqualTo(BRAND_LABEL);
  }

  @Test
  public void shouldFallbackToBaseProductIfBrandIsNotPresent() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.BRAND.getCode())).isEqualTo(BRAND_LABEL);

  }
}
