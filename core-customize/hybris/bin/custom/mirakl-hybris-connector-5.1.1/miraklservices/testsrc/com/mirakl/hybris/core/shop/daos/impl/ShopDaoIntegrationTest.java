package com.mirakl.hybris.core.shop.daos.impl;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.daos.ShopDao;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

@IntegrationTest
public class ShopDaoIntegrationTest extends ServicelayerTest {

  private static final String SHOP_ID_1 = "shop1";
  private static final String SHOP_ID_2 = "shop2";
  private static final String SHOP_ID_3 = "shop3";
  public static final String PRODUCT_CODE_1 = "productCode1";

  @Resource
  private ShopDao shopDao;

  @Before
  public void setUp() throws ImpExException {
    importCsv("/miraklservices/test/testShops.impex", "utf-8");
  }

  @Test
  public void findsShopById() {
    ShopModel shop = shopDao.findShopById(SHOP_ID_1);

    assertThat(shop.getId()).isEqualTo(SHOP_ID_1);
  }

  @Test
  public void findShopsForProductCode() {
    Collection<ShopModel> shops = shopDao.findShopsForProductCode(PRODUCT_CODE_1);

    assertThat(shops).hasSize(3);
    assertThat(shops).onProperty(ShopModel.ID).containsOnly(SHOP_ID_1, SHOP_ID_2, SHOP_ID_3);
  }
}
