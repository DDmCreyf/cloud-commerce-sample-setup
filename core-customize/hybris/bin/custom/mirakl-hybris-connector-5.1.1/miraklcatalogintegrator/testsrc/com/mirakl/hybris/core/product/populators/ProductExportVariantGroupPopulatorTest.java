package com.mirakl.hybris.core.product.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ProductExportVariantGroupPopulatorTest {

  private static final String PRODUCT_CODE = "product-code";
  private static final String VARIANT_PRODUCT_CODE = "variant-product-code";

  @InjectMocks
  private ProductExportVariantGroupPopulator populator;

  @Mock
  private ProductModel product;

  @Mock
  private VariantProductModel variantProduct;

  @Before
  public void setUp() throws Exception {
    when(product.getCode()).thenReturn(PRODUCT_CODE);
    when(variantProduct.getCode()).thenReturn(VARIANT_PRODUCT_CODE);
  }

  @Test
  public void shouldGroupProductsOnCode() {
    Map<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(product.getCode()).isEqualTo(PRODUCT_CODE);
  }

  @Test
  public void shouldGroupVariantsOnBaseProductCode() {
    Map<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(variantProduct.getCode()).isEqualTo(VARIANT_PRODUCT_CODE);

  }

}
