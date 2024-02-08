package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.COLLECTION_ITEM_SEPARATOR;
import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.KEY_VALUE_SEPARATOR;
import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.MiraklProductExportHeader;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ProductExportReferencesPopulatorTest {

  private static final String EAN_PREFIX = "EAN";
  private static final String EAN_VALUE = "1234567890";
  private static final String MANUFACTURERAID_PREFIX = "MANUFACTURERAID";
  private static final String MANUFACTURER_AID_VALUE = "ManufacturerAID-1";

  @InjectMocks
  private ProductExportReferencesPopulator populator;

  @Mock
  private ProductModel product;

  @Mock
  private ModelService modelService;


  @Before
  public void setUp() throws Exception {
    when(modelService.getAttributeValue(product, ProductModel.EAN)).thenReturn(EAN_VALUE);
    when(modelService.getAttributeValue(product, ProductModel.MANUFACTURERAID)).thenReturn(MANUFACTURER_AID_VALUE);
    HashMap<String, String> referneceAttributesConfig = new HashMap<>();
    referneceAttributesConfig.put(ProductModel.EAN, EAN_PREFIX);
    referneceAttributesConfig.put(ProductModel.MANUFACTURERAID, MANUFACTURERAID_PREFIX);
    populator.setReferenceAttributesConfig(referneceAttributesConfig);
  }

  @Test
  public void shouldPopulateSingleReference() {
    populator.setReferenceAttributesConfig(Collections.singletonMap(ProductModel.EAN, EAN_PREFIX));

    HashMap<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(result.get(MiraklProductExportHeader.PRODUCT_REFERENCES.getCode()))
    .isEqualTo(EAN_PREFIX + KEY_VALUE_SEPARATOR + EAN_VALUE);
  }

  @Test
  public void shouldPopulateMultipleReferences() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(product, result);

    String productReferences = result.get(MiraklProductExportHeader.PRODUCT_REFERENCES.getCode());
    assertThat(productReferences).isNotEmpty();
    assertThat(productReferences, anyOf(//
        equalTo(EAN_PREFIX + KEY_VALUE_SEPARATOR + EAN_VALUE + COLLECTION_ITEM_SEPARATOR + MANUFACTURERAID_PREFIX
            + KEY_VALUE_SEPARATOR + MANUFACTURER_AID_VALUE), //
        equalTo(MANUFACTURERAID_PREFIX + KEY_VALUE_SEPARATOR + MANUFACTURER_AID_VALUE + COLLECTION_ITEM_SEPARATOR + EAN_PREFIX
            + KEY_VALUE_SEPARATOR + EAN_VALUE)));
  }

  @Test
  public void shouldNotFallbackToBaseProductOnNoReferences() {
    VariantProductModel variantProduct = mock(VariantProductModel.class);

    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.PRODUCT_REFERENCES.getCode())).isEmpty();
  }

}
