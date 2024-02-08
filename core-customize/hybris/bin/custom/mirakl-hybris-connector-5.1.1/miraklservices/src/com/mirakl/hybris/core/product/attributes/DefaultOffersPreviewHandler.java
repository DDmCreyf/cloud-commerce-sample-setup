package com.mirakl.hybris.core.product.attributes;

import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableSet;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class DefaultOffersPreviewHandler extends AbstractDynamicAttributeHandler<Set<OfferModel>, ProductModel> {

  private OfferService offerService;

  @Override
  public Set<OfferModel> get(ProductModel productModel) {
    return ImmutableSet.copyOf(offerService.getOffersForProductCode(productModel.getCode()));
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

}
