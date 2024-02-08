package com.mirakl.hybris.advancedpricing.product.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.advancedpricing.product.daos.OfferPricingDao;
import com.mirakl.hybris.advancedpricing.product.services.OfferPricingService;
import com.mirakl.hybris.beans.OfferPricingCriteria;
import com.mirakl.hybris.core.model.OfferPricingModel;

import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultOfferPricingService implements OfferPricingService {

  protected OfferPricingDao offerPricingDao;
  protected ModelService modelService;

  @Override
  public List<OfferPricingModel> getOfferPricingsForCriteria(OfferPricingCriteria offerPricingCriteria) {
    return offerPricingDao.findOfferPricingsByCriteria(offerPricingCriteria);
  }

  @Override
  public List<OfferPricingModel> findByOfferId(String offerId) {
    return offerPricingDao.findByOfferId(offerId);
  }

  @Override
  public void deleteByOfferId(String offerId) {
    modelService.removeAll(offerPricingDao.findByOfferId(offerId));
  }

  @Required
  public void setOfferPricingDao(OfferPricingDao offerPricingDao) {
    this.offerPricingDao = offerPricingDao;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }
}
