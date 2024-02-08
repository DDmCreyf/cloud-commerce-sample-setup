package com.mirakl.hybris.core.order.services;

import java.util.List;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeError;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Service operating on {@link AbstractOrderEntryModel} and modifying their shipping options, prices and quantities
 */
public interface ShippingOptionsService {

  /**
   * Sets Mirakl shipping options for the given {@link AbstractOrderModel}
   *
   * @param order {@link AbstractOrderModel}
   */
  void setShippingOptions(AbstractOrderModel order);

  /**
   * Removes all data related to shipping from the {@link AbstractOrderModel} (shippingFeesJSON, order line shipping methods and
   * costs)
   * 
   * @param order {@link AbstractOrderModel} to reset
   */
  void resetSavedShippingOptions(AbstractOrderModel order);

  /**
   * Updates selected shipping option of the entries for the given shop
   *
   * @param order {@link AbstractOrderModel}
   * @param shippingOptionCode new selected shipping option code
   * @param leadTimeToShip lead time to ship of the shop
   * @param shopId shop id
   */
  void setSelectedShippingOption(AbstractOrderModel order, String shippingOptionCode, Integer leadTimeToShip, String shopId);

  /**
   * Removes {@link AbstractOrderEntryModel} with offers with errors returned by Mirakl
   *
   * @param order {@link AbstractOrderModel} order to be searched through
   * @param shippingFeeErrors {@link MiraklOrderShippingFeeError} containing invalid offer ids
   */
  void removeOfferEntriesWithError(AbstractOrderModel order, List<MiraklOrderShippingFeeError> shippingFeeErrors);

  /**
   * Adjusts {@link AbstractOrderEntryModel} quantity to the quantity returned by Mirakl. If the entry contains quantity higher
   * than the Mirakl quantity, it will be reduced, If the reduced quantity is 0, the entry will be removed
   *
   * @param abstractOrderEntryModels {@link AbstractOrderEntryModel}
   * @param miraklOrderShippingFeeOffers {@link MiraklOrderShippingFeeOffer} with available quantity
   */
  void adjustOfferQuantities(List<AbstractOrderEntryModel> abstractOrderEntryModels,
      List<MiraklOrderShippingFeeOffer> miraklOrderShippingFeeOffers);
}
