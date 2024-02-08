package com.mirakl.hybris.core.product.populators;

import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DefaultMcmSpecialAttributesPopulator implements Populator<MiraklRawProductModel, ProductModel> {

  @Override
  public void populate(MiraklRawProductModel rawProduct, ProductModel product) throws ConversionException {
    product.setMiraklProductId(rawProduct.getMiraklProductId());
    product.setChecksum(rawProduct.getChecksum());
  }

}
