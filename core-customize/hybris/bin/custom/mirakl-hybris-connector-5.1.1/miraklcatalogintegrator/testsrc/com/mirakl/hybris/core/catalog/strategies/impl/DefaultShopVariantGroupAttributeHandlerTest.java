package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopVariantGroupModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultShopVariantGroupAttributeHandlerTest extends AbstractCoreAttributeHandlerTest<MiraklCoreAttributeModel> {

  private static final String VARIANT_GROUP_CODE = "VG5";

  @InjectMocks
  @Spy
  private DefaultShopVariantGroupAttributeHandler testObj;

  @Captor
  private ArgumentCaptor<Collection<ShopVariantGroupModel>> shopVariantGroupsCaptor;

  @Mock
  private ProductModel productResolvedByShopVariantGroup, incorrectProduct;
  @Mock
  private VariantProductModel variantProduct;
  @Mock
  private ShopModel currentShop, otherShop;
  @Mock
  private ShopVariantGroupModel newProductVariantGroup, existingShopVariantGroup, otherShopVariantGroup;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(data.getShop()).thenReturn(currentShop);
    when(data.getProductToUpdate()).thenReturn(variantProduct);
    when(attributeValue.getValue()).thenReturn(VARIANT_GROUP_CODE);
    when(data.getProductResolvedByVariantGroup()).thenReturn(productResolvedByShopVariantGroup);
    when(data.getRootBaseProductToUpdate()).thenReturn(productResolvedByShopVariantGroup);
    when(productResolvedByShopVariantGroup.getShopVariantGroups())
            .thenReturn(asList(existingShopVariantGroup, otherShopVariantGroup));
    when(existingShopVariantGroup.getShop()).thenReturn(currentShop);
    when(otherShopVariantGroup.getShop()).thenReturn(otherShop);
    when(modelService.create(ShopVariantGroupModel.class)).thenReturn(newProductVariantGroup);
  }

  @Test
  public void newShopVariantGroupForProductCreation() throws Exception {
    when(productResolvedByShopVariantGroup.getShopVariantGroups()).thenReturn(singletonList(otherShopVariantGroup));

    testObj.setValue(attributeValue, data, importContext);

    verifyShopVariantGroupUpdate(newProductVariantGroup);
    verify(productResolvedByShopVariantGroup).setShopVariantGroups(shopVariantGroupsCaptor.capture());
    assertThat(shopVariantGroupsCaptor.getValue()).contains(newProductVariantGroup);
    verify(testObj).markItemsToSave(data, newProductVariantGroup, productResolvedByShopVariantGroup);
  }

  @Test
  public void ExistingShopVariantGroupForProductUpdate() throws Exception {
    testObj.setValue(attributeValue, data, importContext);

    verifyShopVariantGroupUpdate(existingShopVariantGroup);
    verify(testObj).markItemsToSave(data, existingShopVariantGroup, productResolvedByShopVariantGroup);
  }

  @Test(expected = ProductImportException.class)
  public void ensureShopVariantGroupConsistency() throws Exception {
    when(data.getRootBaseProductToUpdate()).thenReturn(incorrectProduct);

    testObj.setValue(attributeValue, data, importContext);
  }

  @Test(expected = ProductImportException.class)
  public void shouldThrowExceptionWhenVariantGroupSetOnNonVariantProduct() throws Exception {
    when(data.getProductToUpdate()).thenReturn(mock(ProductModel.class));

    testObj.setValue(attributeValue, data, importContext);
  }

  private void verifyShopVariantGroupUpdate(ShopVariantGroupModel shopVariantGroup) {
    verify(shopVariantGroup).setCode(VARIANT_GROUP_CODE);
    verify(shopVariantGroup).setProduct(productResolvedByShopVariantGroup);
    verify(shopVariantGroup).setShop(currentShop);
  }

}
