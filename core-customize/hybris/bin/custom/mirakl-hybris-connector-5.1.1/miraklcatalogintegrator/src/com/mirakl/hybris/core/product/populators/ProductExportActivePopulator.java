package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.ACTIVE;
import static java.lang.Boolean.valueOf;

import java.util.Map;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ProductExportActivePopulator implements Populator<ProductModel, Map<String, String>> {

  @Override
  public void populate(ProductModel source, Map<String, String> target) throws ConversionException {
    Boolean active = valueOf(ArticleApprovalStatus.APPROVED.equals(source.getApprovalStatus()));
    target.put(ACTIVE.getCode(), active.toString());
  }

}
