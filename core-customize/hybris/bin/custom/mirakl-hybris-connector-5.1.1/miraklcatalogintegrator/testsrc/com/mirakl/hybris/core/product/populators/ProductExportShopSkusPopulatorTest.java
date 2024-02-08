package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.COLLECTION_ITEM_SEPARATOR;
import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.KEY_VALUE_SEPARATOR;
import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.PRODUCT_SKU;
import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.SHOP_SKUS;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopSkuModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductExportShopSkusPopulatorTest {

  private static final String SHOP_1_ID = "shop-1-id";
  private static final String SHOP_2_ID = "shop-2-id";
  private static final String SKU1 = "sku1";
  private static final String SKU2 = "sku2";
  private static final String SKU3 = "sku3";

  @InjectMocks
  private ProductExportShopSkusPopulator populator;

  @Mock
  private ProductModel product;
  @Mock
  private ShopSkuModel shopSku1, shopSku2, shopSku3;
  @Mock
  private ShopModel shop1, shop2;
  private List<ShopSkuModel> shopSkus;


  @Before
  public void setUp() throws Exception {
    when(shopSku1.getSku()).thenReturn(SKU1);
    when(shopSku2.getSku()).thenReturn(SKU2);
    when(shopSku3.getSku()).thenReturn(SKU3);
    when(shopSku1.getShop()).thenReturn(shop1);
    when(shopSku2.getShop()).thenReturn(shop2);
    when(shopSku3.getShop()).thenReturn(shop1);
    when(shop1.getId()).thenReturn(SHOP_1_ID);
    when(shop2.getId()).thenReturn(SHOP_2_ID);
    shopSkus = asList(shopSku1, shopSku2, shopSku3);
  }

  @Test
  public void shouldPopulateShopSkus() throws Exception {
    when(product.getShopSkus()).thenReturn(shopSkus);

    HashMap<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(result.get(SHOP_SKUS.getCode())).isEqualTo(//
        SHOP_1_ID + KEY_VALUE_SEPARATOR + SKU1 //
            + COLLECTION_ITEM_SEPARATOR + SHOP_2_ID + KEY_VALUE_SEPARATOR + SKU2 //
            + COLLECTION_ITEM_SEPARATOR + SHOP_1_ID + KEY_VALUE_SEPARATOR + SKU3);
  }

  @Test
  public void shouldIgnoreEmptyShopSkus() throws Exception {
    when(product.getShopSkus()).thenReturn(null);

    HashMap<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(result.get(PRODUCT_SKU.getCode())).isNull();
  }

}
