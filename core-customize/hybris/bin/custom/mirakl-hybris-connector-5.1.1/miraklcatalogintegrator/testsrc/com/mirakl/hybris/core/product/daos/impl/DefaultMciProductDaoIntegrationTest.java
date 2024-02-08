package com.mirakl.hybris.core.product.daos.impl;

import static org.fest.assertions.Assertions.assertThat;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.mirakl.hybris.core.shop.services.ShopService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class DefaultMciProductDaoIntegrationTest extends ServicelayerTest {

  private static final String PRODUCT_CATALOG = "productCatalog";
  private static final String STAGED_VERSION = "Staged";
  private static final String ONLINE_VERSION = "Online";

  @Resource
  private DefaultMciProductDao mciProductDao;
  @Resource
  private CatalogVersionService catalogVersionService;
  @Resource
  private ShopService shopService;

  @Before
  public void setUp() throws Exception {
    importCsv("/miraklcatalogintegrator/test/testProducts.impex", "utf-8");
  }

  @Test
  public void shouldFindProductForShopVariantGroupCodeInCatalog() {
    ProductModel product = mciProductDao.findProductForShopVariantGroupCode(shopService.getShopForId("shop1"), "vg1",
        catalogVersionService.getCatalogVersion(PRODUCT_CATALOG, STAGED_VERSION));

    assertThat(product).isNotNull();
    assertThat(product.getCode()).isEqualTo("product1");
  }

  @Test
  public void shouldFindProductForShopVariantGroupCodeForDifferentShop() {
    ProductModel product = mciProductDao.findProductForShopVariantGroupCode(shopService.getShopForId("shop2"), "vg1",
        catalogVersionService.getCatalogVersion(PRODUCT_CATALOG, STAGED_VERSION));

    assertThat(product).isNotNull();
    assertThat(product.getCode()).isEqualTo("product2");
  }

  @Test
  public void shouldFindProductForShopVariantGroupCodeForDifferentCatalog() {
    ProductModel product = mciProductDao.findProductForShopVariantGroupCode(shopService.getShopForId("shop1"), "vg1",
        catalogVersionService.getCatalogVersion(PRODUCT_CATALOG, ONLINE_VERSION));

    assertThat(product).isNotNull();
    assertThat(product.getCode()).isEqualTo("product2");
  }

}
