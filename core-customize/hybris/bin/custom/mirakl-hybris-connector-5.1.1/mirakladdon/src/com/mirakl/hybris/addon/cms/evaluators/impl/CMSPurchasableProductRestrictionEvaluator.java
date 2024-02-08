package com.mirakl.hybris.addon.cms.evaluators.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.addon.model.restrictions.CMSPurchasableProductRestrictionModel;
import com.mirakl.hybris.core.product.services.MiraklProductService;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class CMSPurchasableProductRestrictionEvaluator implements CMSRestrictionEvaluator<CMSPurchasableProductRestrictionModel> {

  protected OfferService offerService;
  protected MiraklProductService miraklProductService;
  protected CommonI18NService commonI18NService;

  @Override
  public boolean evaluate(CMSPurchasableProductRestrictionModel restriction, RestrictionData restrictionData) {
    ProductModel product = restrictionData.getProduct();

    return miraklProductService.isSellableByOperator(product) || hasNoOffers(product);
  }

  protected boolean hasNoOffers(ProductModel product) {
    return !offerService.hasOffersWithCurrency(product.getCode(), commonI18NService.getCurrentCurrency());
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setMiraklProductService(MiraklProductService miraklProductService) {
    this.miraklProductService = miraklProductService;
  }

  @Required
  public void setCommonI18NService(CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }
}
