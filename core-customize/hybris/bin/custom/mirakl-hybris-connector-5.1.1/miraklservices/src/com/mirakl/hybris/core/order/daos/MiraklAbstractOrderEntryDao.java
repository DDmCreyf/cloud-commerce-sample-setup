package com.mirakl.hybris.core.order.daos;

import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface MiraklAbstractOrderEntryDao<T extends AbstractOrderEntryModel> {

  /**
   * Returns order entries containing a given offer
   *
   * @param order
   * @param offer
   * @return order entries with the given offerId
   */
  T findEntryByOffer(AbstractOrderModel order, OfferModel offer);


}
