package com.mirakl.hybris.core.product.populators;

import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants;
import com.mirakl.hybris.core.enums.MiraklProductExportHeader;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.variants.model.VariantProductModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ProductExportBasicPopulatorTest {

  private static final String PRODUCT_CODE = "Product code";
  private static final String PRODUCT_NAME = "Product name";
  private static final String PRODUCT_DESCRIPTION = "Product description";
  private static final String VARIANT_PRODUCT_CODE = "VariantProduct code";
  private static final String VARIANT_PRODUCT_NAME = "VariantProduct name";
  private static final String VARIANT_PRODUCT_DESCRIPTION = "VariantProduct description";
  private static final int DESCRIPTION_MAXIMUM_LENGTH = 2000;

  @InjectMocks
  private ProductExportBasicPopulator populator;

  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Mock
  private ProductModel product;
  @Mock
  private VariantProductModel variantProduct;

  @Before
  public void setUp() throws Exception {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getInt(MiraklcatalogintegratorConstants.DESCRIPTION_MAXLENGTH_CONFIG_KEY))
        .thenReturn(DESCRIPTION_MAXIMUM_LENGTH);
    when(variantProduct.getBaseProduct()).thenReturn(product);

    when(product.getCode()).thenReturn(PRODUCT_CODE);
    when(product.getName()).thenReturn(PRODUCT_NAME);
    when(product.getDescription()).thenReturn(PRODUCT_DESCRIPTION);

    when(variantProduct.getCode()).thenReturn(VARIANT_PRODUCT_CODE);
    when(variantProduct.getName()).thenReturn(VARIANT_PRODUCT_NAME);
    when(variantProduct.getDescription()).thenReturn(VARIANT_PRODUCT_DESCRIPTION);
  }

  @Test
  public void shouldExportBasicInformationForProductsWithNoVariants() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(result.get(MiraklProductExportHeader.PRODUCT_SKU.getCode())).isEqualTo(PRODUCT_CODE);
    assertThat(result.get(MiraklProductExportHeader.PRODUCT_TITLE.getCode())).isEqualTo(PRODUCT_NAME);
    assertThat(result.get(MiraklProductExportHeader.PRODUCT_DESCRIPTION.getCode())).isEqualTo(PRODUCT_DESCRIPTION);
  }

  @Test
  public void shouldNotFallbackToBaseProductIfDataIsPresent() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.PRODUCT_SKU.getCode())).isEqualTo(VARIANT_PRODUCT_CODE);
    assertThat(result.get(MiraklProductExportHeader.PRODUCT_TITLE.getCode())).isEqualTo(VARIANT_PRODUCT_NAME);
    assertThat(result.get(MiraklProductExportHeader.PRODUCT_DESCRIPTION.getCode())).isEqualTo(VARIANT_PRODUCT_DESCRIPTION);
  }

  @Test
  public void shouldFallbackToBaseProductIfDataIsNotPresent() {
    when(variantProduct.getName()).thenReturn(null);
    when(variantProduct.getDescription()).thenReturn(null);

    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.PRODUCT_TITLE.getCode())).isEqualTo(PRODUCT_NAME);
    assertThat(result.get(MiraklProductExportHeader.PRODUCT_DESCRIPTION.getCode())).isEqualTo(PRODUCT_DESCRIPTION);
  }

  @Test
  public void shouldCutDescriptionIfLongerThanMaximumAllowed() {
    when(product.getDescription()).thenReturn(randomAlphanumeric(DESCRIPTION_MAXIMUM_LENGTH * 3));

    HashMap<String, String> target = new HashMap<>();
    populator.populate(product, target);

    assertThat(target.get(MiraklProductExportHeader.PRODUCT_DESCRIPTION.getCode())).hasSize(DESCRIPTION_MAXIMUM_LENGTH);
  }

}
