package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopSkuModel;
import com.mirakl.hybris.core.product.services.ShopSkuService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultShopSkuAttributeHandlerTest extends AbstractCoreAttributeHandlerTest<MiraklCoreAttributeModel> {

  private static final String PRODUCT_SKU = "0897541321";

  @InjectMocks
  @Spy
  private DefaultShopSkuAttributeHandler testObj;

  @Mock
  private ShopSkuService shopSkuService;
  @Mock
  private Converter<ProductImportData, ShopSkuModel> shopSkuConverter;
  @Mock
  private ProductModel identifiedProduct, productToUpdate;
  @Mock
  private ShopSkuModel shopSku;
  @Mock
  private MiraklRawProductModel rawProduct;
  @Mock
  private ProductModel productResolvedByShopSku;
  @Mock
  private ShopModel shop;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(data.getIdentifiedProduct()).thenReturn(identifiedProduct);
    when(data.getProductToUpdate()).thenReturn(productToUpdate);
    when(data.getRawProduct()).thenReturn(rawProduct);
    when(rawProduct.getSku()).thenReturn(PRODUCT_SKU);
  }

  @Test
  public void shouldSaveShopSkuOnNewProduct() throws Exception {
    when(data.getIdentifiedProduct()).thenReturn(null);
    when(shopSkuConverter.convert(data)).thenReturn(shopSku);

    testObj.setValue(attributeValue, data, importContext);

    verify(shopSkuService).addShopSkuToProduct(shopSku, productToUpdate);
    verify(testObj).markItemsToSave(data, productToUpdate, shopSku);
    verify(shopSkuService, never()).removeShopSkuFromProduct(anyString(), any(ShopModel.class), any(ProductModel.class));
  }

  @Test
  public void shouldSaveShopSkuOnIdentifiedProduct() throws Exception {
    when(shopSkuConverter.convert(data)).thenReturn(shopSku);

    testObj.setValue(attributeValue, data, importContext);

    verify(shopSkuService).addShopSkuToProduct(shopSku, identifiedProduct);
    verify(testObj).markItemsToSave(data, identifiedProduct, shopSku);
  }

  @Test
  public void shouldRemoveOldShopSkuWhenDuplicated() throws Exception {
    when(data.getProductResolvedBySku()).thenReturn(productResolvedByShopSku);
    when(data.getShop()).thenReturn(shop);
    when(shopSkuConverter.convert(data, shopSku)).thenReturn(shopSku);
    when(shopSkuService.getShopSku(shop, identifiedProduct)).thenReturn(shopSku);

    testObj.setValue(attributeValue, data, importContext);

    verify(shopSkuService).removeShopSkuFromProduct(PRODUCT_SKU, shop, productResolvedByShopSku);
    verify(shopSkuService).addShopSkuToProduct(shopSku, identifiedProduct);
    verify(testObj).markItemsToSave(data, identifiedProduct, shopSku);
    verify(testObj).markItemsToSave(data, productResolvedByShopSku);
  }


}
