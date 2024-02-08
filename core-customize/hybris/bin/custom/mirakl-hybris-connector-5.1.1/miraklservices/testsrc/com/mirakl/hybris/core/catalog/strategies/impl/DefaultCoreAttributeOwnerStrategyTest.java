package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCoreAttributeOwnerStrategyTest {

  private static final String ATTRIBUTE_CODE = "code";
  private static final String ROOT_PRODUCT_TYPE_CODE = "rootProductType";
  private static final String INVALID_COMPOSED_TYPE_CODE = "invalidComposedType";

  @InjectMocks
  private DefaultCoreAttributeOwnerStrategy testObj;

  @Mock
  private TypeService typeService;
  @Mock
  private MiraklCoreAttributeModel attribute;
  @Mock
  private ProductImportData data;
  @Mock
  private ProductImportFileContextData context;
  @Mock
  private VariantLv1Model productToUpdateLv1;
  @Mock
  private VariantLv2Model productToUpdateLv2;
  @Mock
  private ProductModel rootProductToUpdate;
  @Mock
  private ComposedTypeModel rootProductComposedType, variantLv1ComposedType, variantLv2ComposedType, invalidComposedType;

  @Before
  public void setUp() throws Exception {
    when(attribute.getComposedTypeOwners()).thenReturn(singletonList(variantLv1ComposedType));
    when(data.getProductToUpdate()).thenReturn(productToUpdateLv2);
    when(productToUpdateLv2.getBaseProduct()).thenReturn(productToUpdateLv1);
    when(productToUpdateLv1.getBaseProduct()).thenReturn(rootProductToUpdate);
    when(typeService.getComposedTypeForClass(rootProductToUpdate.getClass())).thenReturn(rootProductComposedType);
    when(typeService.getComposedTypeForClass(productToUpdateLv1.getClass())).thenReturn(variantLv1ComposedType);
    when(typeService.getComposedTypeForClass(productToUpdateLv2.getClass())).thenReturn(variantLv2ComposedType);
    when(attribute.getCode()).thenReturn(ATTRIBUTE_CODE);
    when(rootProductComposedType.getCode()).thenReturn(ROOT_PRODUCT_TYPE_CODE);
    when(invalidComposedType.getCode()).thenReturn(INVALID_COMPOSED_TYPE_CODE);
    when(data.getRootBaseProductToUpdate()).thenReturn(rootProductToUpdate);
  }

  @Test
  public void composedTypeOwnersHaveHighestPriority() throws Exception {
    ProductModel output = testObj.determineOwner(attribute, data, context);

    assertThat(output).isEqualTo(productToUpdateLv1);
  }

  @Test
  public void variantsHaveMidPriority() throws Exception {
    when(attribute.getComposedTypeOwners()).thenReturn(Collections.<ComposedTypeModel>emptyList());
    when(attribute.isVariant()).thenReturn(true);

    ProductModel output = testObj.determineOwner(attribute, data, context);

    assertThat(output).isEqualTo(productToUpdateLv2);
  }

  @Test
  public void uniqueIdentifiersHaveMidPriority() throws Exception {
    when(attribute.getComposedTypeOwners()).thenReturn(Collections.<ComposedTypeModel>emptyList());
    when(attribute.isUniqueIdentifier()).thenReturn(true);

    ProductModel output = testObj.determineOwner(attribute, data, context);

    assertThat(output).isEqualTo(productToUpdateLv2);
  }

  @Test
  public void defaultOwnerProduct() throws Exception {
    when(attribute.getComposedTypeOwners()).thenReturn(Collections.<ComposedTypeModel>emptyList());

    ProductModel output = testObj.determineOwner(attribute, data, context);

    assertThat(output).isEqualTo(rootProductToUpdate);
  }

  @Test
  public void shouldIgnoreComposedTypeOwnerWhenIncorrect() throws Exception {
    when(attribute.getComposedTypeOwners()).thenReturn(singletonList(invalidComposedType));

    ProductModel output = testObj.determineOwner(attribute, data, context);

    assertThat(output).isEqualTo(rootProductToUpdate);
  }

  public class VariantLv1Model extends VariantProductModel {
  }

  public class VariantLv2Model extends VariantLv1Model {
  }

}
