package com.mirakl.hybris.core.product.daos.impl;

import javax.annotation.Resource;

import org.junit.Before;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.servicelayer.ServicelayerTest;

public class DefaultMcmProductDaoIntegrationTest extends ServicelayerTest {

  private static final String PRODUCT_CATALOG = "productCatalog";
  private static final String STAGED_VERSION = "Staged";

  @Resource
  private DefaultMcmProductDao miraklProductDao;
  @Resource
  private CatalogVersionService catalogVersionService;

  @Before
  public void setUp() throws Exception {
    importCsv("/miraklservices/test/testProducts.impex", "utf-8");
  }

}
