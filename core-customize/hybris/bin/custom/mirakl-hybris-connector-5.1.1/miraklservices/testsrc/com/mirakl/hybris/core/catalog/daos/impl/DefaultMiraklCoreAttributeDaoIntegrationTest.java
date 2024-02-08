package com.mirakl.hybris.core.catalog.daos.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import java.util.List;

import javax.annotation.Resource;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

@IntegrationTest
public class DefaultMiraklCoreAttributeDaoIntegrationTest extends ServicelayerTest {

  private static final String UNIQUE_ATTRIBUTE_CODE = "ean";
  private static final String VARIANT_ATTRIBUTE_CODE_1 = "variant_attribute_1";
  private static final String VARIANT_ATTRIBUTE_CODE_2 = "variant_attribute_2";
  private static final String SHOP_SKU_ATTRIBUTE_CODE = "shop_sku_attribute";

  @Resource
  private DefaultMiraklCoreAttributeDao miraklCoreAttributeDao;
  @Resource
  private ModelService modelService;
  @Resource
  private FlexibleSearchService flexibleSearchService;

  @Before
  public void setUp() throws ImpExException {
    importCsv("/miraklservices/test/testMiraklCoreAttributes.impex", "utf-8");
  }

  @Test
  public void findUniqueIdentifierCoreAttributes() throws Exception {
    List<MiraklCoreAttributeModel> output = miraklCoreAttributeDao.findUniqueIdentifierCoreAttributes();

    assertThat(output, hasSize(1));
    assertThat(output.get(0).getCode(), equalTo(UNIQUE_ATTRIBUTE_CODE));
  }

  @Test
  public void findVariantCoreAttributes() throws Exception {
    List<MiraklCoreAttributeModel> output = miraklCoreAttributeDao.findVariantCoreAttributes();

    assertThat(output, hasSize(2));
    assertThat(output, Matchers.<MiraklCoreAttributeModel>hasItem(hasProperty("code", equalTo(VARIANT_ATTRIBUTE_CODE_1))));
    assertThat(output, Matchers.<MiraklCoreAttributeModel>hasItem(hasProperty("code", equalTo(VARIANT_ATTRIBUTE_CODE_2))));
  }

  @Test
  public void findCoreAttributeByRole() throws Exception {
    List<MiraklCoreAttributeModel> output =
        miraklCoreAttributeDao.findCoreAttributeByRole(MiraklAttributeRole.SHOP_SKU_ATTRIBUTE);

    assertThat(output, hasSize(1));
    assertThat(output.get(0).getCode(), equalTo(SHOP_SKU_ATTRIBUTE_CODE));
  }

}
