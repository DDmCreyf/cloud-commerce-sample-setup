package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.enums.MiraklAttributeRole.SHOP_SKU_ATTRIBUTE;

import com.mirakl.hybris.beans.MiraklRawProductData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

public class DefaultMciMiraklRawProductPopulator extends AbstractMiraklRawProductPopulator {

  @Override
  protected void populateRawProduct(MiraklRawProductData source, MiraklRawProductModel target) {
    ProductImportFileContextData context = source.getContext();
    target.setShopId(context.getShopId());
    target.setSku(source.getValues().get(getShopSkuAttributeCode(context)));
  }

  protected String getShopSkuAttributeCode(ProductImportFileContextData context) {
    return context.getGlobalContext().getCoreAttributePerRole().get(SHOP_SKU_ATTRIBUTE);
  }

}
