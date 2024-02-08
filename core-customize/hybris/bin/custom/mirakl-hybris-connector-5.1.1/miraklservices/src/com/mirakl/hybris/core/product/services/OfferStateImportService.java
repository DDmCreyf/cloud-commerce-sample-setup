package com.mirakl.hybris.core.product.services;

import com.mirakl.hybris.core.enums.OfferState;

import java.util.Collection;

public interface OfferStateImportService {

  /**
   * Imports all offer states from Mirakl
   *
   * @return A collection of imported offer states
   *
   */
  Collection<OfferState> importAllOfferStates();

}
