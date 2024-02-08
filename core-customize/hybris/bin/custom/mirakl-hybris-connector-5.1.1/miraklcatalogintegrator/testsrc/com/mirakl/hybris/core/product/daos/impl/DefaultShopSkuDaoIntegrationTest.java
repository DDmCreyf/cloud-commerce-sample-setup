package com.mirakl.hybris.core.product.daos.impl;

import static org.fest.assertions.Assertions.assertThat;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopSkuModel;
import com.mirakl.hybris.core.shop.services.ShopService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class DefaultShopSkuDaoIntegrationTest extends ServicelayerTest {

  private static final String PRODUCT_CATALOG = "productCatalog";
  private static final String STAGED_VERSION = "Staged";
  private static final String ONLINE_VERSION = "Online";

  @Resource
  private DefaultShopSkuDao shopSkuDao;
  @Resource
  private ShopService shopService;
  private ShopModel shop1, shop2;
  private CatalogVersionModel stagedCV, onlineCV;

  @Resource
  private CatalogVersionService catalogVersionService;

  @Before
  public void setUp() throws ImpExException {
    importCsv("/miraklservices/test/testShopSkus.impex", "utf-8");
    shop1 = shopService.getShopForId("shop1");
    shop2 = shopService.getShopForId("shop2");
    stagedCV = catalogVersionService.getCatalogVersion(PRODUCT_CATALOG, STAGED_VERSION);
    onlineCV = catalogVersionService.getCatalogVersion(PRODUCT_CATALOG, ONLINE_VERSION);
  }

  @Test
  public void shouldFindShopByChecksum() {
    ShopSkuModel shopSku = shopSkuDao.findShopSkuByChecksum("checksum2", shop2, stagedCV);

    assertThat(shopSku.getProduct().getCode()).isEqualTo("product1");
  }

  @Test
  public void shouldFindShopByChecksumForDifferentShop() {
    ShopSkuModel shopSku = shopSkuDao.findShopSkuByChecksum("checksum2", shop1, stagedCV);

    assertThat(shopSku).isNull();
  }

  @Test
  public void shouldNotFindShopByChecksumForDifferentCatalogVersion() {
    ShopSkuModel shopSku = shopSkuDao.findShopSkuByChecksum("checksum2", shop2, onlineCV);

    assertThat(shopSku).isNull();

    shopSku = shopSkuDao.findShopSkuByChecksum("checksum3", shop2, stagedCV);

    assertThat(shopSku).isNull();
  }

  @Test
  public void shouldFindShopSkuBySku() {
    ShopSkuModel shopSku = shopSkuDao.findShopSkuBySku("sku1", shop1, stagedCV);

    assertThat(shopSku.getProduct().getCode()).isEqualTo("product1");

    shopSku = shopSkuDao.findShopSkuBySku("sku1", shop2, onlineCV);

    assertThat(shopSku.getProduct().getCode()).isEqualTo("product3");
  }

  @Test
  public void shouldNotFindShopBySkuForDifferentCatalogVersion() {
    ShopSkuModel shopSku = shopSkuDao.findShopSkuByChecksum("sku2", shop2, onlineCV);

    assertThat(shopSku).isNull();
  }

  @Test
  public void shouldNotFindShopBySkuForDifferentShop() {
    ShopSkuModel shopSku = shopSkuDao.findShopSkuByChecksum("sku1", shop2, stagedCV);

    assertThat(shopSku).isNull();
  }

}
