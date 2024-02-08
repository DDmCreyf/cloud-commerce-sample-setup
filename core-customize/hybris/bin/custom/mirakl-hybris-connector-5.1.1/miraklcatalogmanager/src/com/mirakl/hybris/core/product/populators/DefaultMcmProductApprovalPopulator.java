package com.mirakl.hybris.core.product.populators;

import static de.hybris.platform.catalog.enums.ArticleApprovalStatus.valueOf;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.strategies.McmProductAcceptanceStrategy;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DefaultMcmProductApprovalPopulator implements Populator<MiraklRawProductModel, ProductModel> {

  protected McmProductAcceptanceStrategy productAcceptanceStrategy;

  @Override
  public void populate(MiraklRawProductModel source, ProductModel target) throws ConversionException {
    target.setApprovalStatus(valueOf(productAcceptanceStrategy.getArticleApprovalStatusCode(source)));
  }

  @Required
  public void setProductAcceptanceStrategy(McmProductAcceptanceStrategy productAcceptanceStrategy) {
    this.productAcceptanceStrategy = productAcceptanceStrategy;
  }

}
