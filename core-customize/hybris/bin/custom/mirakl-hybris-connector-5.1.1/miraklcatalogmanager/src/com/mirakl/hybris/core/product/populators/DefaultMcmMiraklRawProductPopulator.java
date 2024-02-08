package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogmanagerConstants.MCM_MIRAKL_ACCEPTANCE_STATUS_HEADER;
import static com.mirakl.hybris.core.constants.MiraklcatalogmanagerConstants.MCM_MIRAKL_PRODUCT_ID_HEADER;
import static com.mirakl.hybris.core.constants.MiraklcatalogmanagerConstants.MCM_MIRAKL_PRODUCT_SKU_HEADER;
import static com.mirakl.hybris.core.enums.MarketplaceProductAcceptanceStatus.valueOf;

import java.util.Map;

import com.mirakl.hybris.beans.MiraklRawProductData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

public class DefaultMcmMiraklRawProductPopulator extends AbstractMiraklRawProductPopulator {

  @Override
  protected void populateRawProduct(MiraklRawProductData source, MiraklRawProductModel target) {
    Map<String, String> values = source.getValues();
    target.setMiraklProductId(values.get(MCM_MIRAKL_PRODUCT_ID_HEADER));
    target.setSku(values.get(MCM_MIRAKL_PRODUCT_SKU_HEADER));
    target.setAcceptanceStatus(valueOf(values.get(MCM_MIRAKL_ACCEPTANCE_STATUS_HEADER)));

  }

}
