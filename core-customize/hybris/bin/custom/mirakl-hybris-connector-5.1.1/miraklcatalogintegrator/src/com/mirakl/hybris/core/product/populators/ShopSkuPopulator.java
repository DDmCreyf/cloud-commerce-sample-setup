package com.mirakl.hybris.core.product.populators;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.core.model.ShopSkuModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ShopSkuPopulator implements Populator<ProductImportData, ShopSkuModel> {

  @Override
  public void populate(ProductImportData source, ShopSkuModel target) throws ConversionException {
    target.setShop(source.getShop());
    target.setProduct(source.getIdentifiedProduct());
    target.setSku(source.getShopSku());
    target.setChecksum(source.getRawProduct().getChecksum());
  }

}
