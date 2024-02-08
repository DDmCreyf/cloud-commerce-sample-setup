package com.mirakl.hybris.core.product.daos.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import java.util.List;

import javax.annotation.Resource;

import org.fest.assertions.Assertions;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.mirakl.hybris.beans.ShopVariantGroupCode;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.daos.MiraklRawProductDao;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class DefaultMiraklRawProductDaoIntegrationTest extends ServicelayerTest {

  private static final String SHOP_ID = "shopId";
  private static final String VARIANT_GROUP_CODE = "variantGroupCode";

  @Resource
  private MiraklRawProductDao miraklRawProductDao;

  @Before
  public void setUp() throws ImpExException {
    importCsv("/miraklservices/test/testMiraklRawProducts.impex", "utf-8");
  }

  @Test
  public void shouldFindRawProductsByImportId() {
    List<ShopVariantGroupCode> shopVariantGroupCodes = miraklRawProductDao.findShopVariantGroupCodesByImportId("import-1");

    assertThat(shopVariantGroupCodes, hasSize(2));
    assertThat(shopVariantGroupCodes, Matchers.<ShopVariantGroupCode>hasItem(
        allOf(hasProperty(SHOP_ID, equalTo("shop-1")), hasProperty(VARIANT_GROUP_CODE, equalTo("vgc-1")))));
    assertThat(shopVariantGroupCodes, Matchers.<ShopVariantGroupCode>hasItem(
        allOf(hasProperty(SHOP_ID, equalTo("shop-1")), hasProperty(VARIANT_GROUP_CODE, equalTo("vgc-2")))));
  }

  @Test
  public void shouldFindRawProductsByImportIdAndShopVariantGroupCode() {
    List<MiraklRawProductModel> rawProducts =
        miraklRawProductDao.findRawProductsByImportIdAndVariantGroupCode("import-1", "vgc-1");

    assertThat(rawProducts, hasSize(3));
    Assertions.assertThat(rawProducts).onProperty(MiraklRawProductModel.SKU).containsExactly("1", "2", "6");
  }

}
