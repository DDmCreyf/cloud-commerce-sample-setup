package com.mirakl.hybris.core.product.services;

import java.util.Collection;
import java.util.Date;

import com.mirakl.hybris.core.model.OfferModel;

public interface OfferImportService {

  /**
   * Imports all offers from Mirakl
   *
   * @param missingOffersDeletionDate Offers missing in Mirakl and modified before this date will be deleted
   * @param includeInactiveOffers To import all the offers, including the inactive ones
   * @return A collection of imported offers
   *
   */
  Collection<OfferModel> importAllOffers(Date missingOffersDeletionDate, boolean includeInactiveOffers);

  /**
   * Imports offers modified after a last import date. If the last import date is null, then all offers will be imported.
   * 
   * @param lastImportDate The last import date
   * @return A collection of imported offers
   *
   */
  Collection<OfferModel> importOffersUpdatedSince(Date lastImportDate);
}
