package com.mirakl.hybris.core.product.daos.impl;


import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.mirakl.hybris.core.enums.ProductOrigin;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class DefaultMiraklProductDaoIntegrationTest extends ServicelayerTest {

  private static final String PRODUCT_CATALOG = "productCatalog";
  private static final String STAGED_VERSION = "Staged";

  @Resource
  private DefaultMiraklProductDao miraklProductDao;
  @Resource
  private CatalogVersionService catalogVersionService;

  @Before
  public void setUp() throws Exception {
    importCsv("/miraklservices/test/testProducts.impex", "utf-8");
  }

  @Test
  public void shouldFindAllProductsWithNoVariantType() {
    List<ProductModel> products = miraklProductDao.findModifiedProductsWithNoVariantType(null,
        catalogVersionService.getCatalogVersion(PRODUCT_CATALOG, STAGED_VERSION));

    ImmutableList<String> productCodes = FluentIterable.from(products).transform(toProductCode()).toList();
    assertThat(productCodes).containsOnly("product1", "product3");
  }

  @Test
  public void shouldFindProductsWithNoVariantTypeForOrigins() {
    List<ProductModel> products = miraklProductDao.findModifiedProductsWithNoVariantType(null,
        catalogVersionService.getCatalogVersion(PRODUCT_CATALOG, STAGED_VERSION), newHashSet(ProductOrigin.MARKETPLACE));

    ImmutableList<String> productCodes = FluentIterable.from(products).transform(toProductCode()).toList();
    assertThat(productCodes).containsOnly("product3");
  }

  @Test
  public void shouldFindProductsWithNoVariantTypeForApprovalStatuses() {
    List<ProductModel> products = miraklProductDao.findModifiedProductsWithNoVariantType(null,
        catalogVersionService.getCatalogVersion(PRODUCT_CATALOG, STAGED_VERSION), null, newHashSet(ArticleApprovalStatus.CHECK));

    ImmutableList<String> productCodes = FluentIterable.from(products).transform(toProductCode()).toList();
    assertThat(productCodes).containsOnly("product1");
  }

  @Test
  public void shouldFindOperatorProductsWithNoVariantType() {
    List<ProductModel> products = miraklProductDao.findModifiedProductsWithNoVariantType(null,
        catalogVersionService.getCatalogVersion(PRODUCT_CATALOG, STAGED_VERSION), newHashSet(ProductOrigin.OPERATOR));

    ImmutableList<String> productCodes = FluentIterable.from(products).transform(toProductCode()).toList();
    assertThat(productCodes).containsOnly("product1");
  }

  @Test
  public void shouldFindMarketplaceProductsWithNoVariantType() {
    List<ProductModel> products = miraklProductDao.findModifiedProductsWithNoVariantType(null,
        catalogVersionService.getCatalogVersion(PRODUCT_CATALOG, STAGED_VERSION), newHashSet(ProductOrigin.MARKETPLACE));

    ImmutableList<String> productCodes = FluentIterable.from(products).transform(toProductCode()).toList();
    assertThat(productCodes).containsOnly("product3");
  }

  protected Function<ProductModel, String> toProductCode() {
    return new Function<ProductModel, String>() {

      @Override
      public String apply(ProductModel product) {
        return product.getCode();
      }
    };
  }
}
