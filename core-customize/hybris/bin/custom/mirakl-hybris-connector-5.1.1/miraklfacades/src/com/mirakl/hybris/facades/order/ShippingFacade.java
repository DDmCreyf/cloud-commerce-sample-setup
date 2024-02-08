package com.mirakl.hybris.facades.order;

import java.util.List;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.facades.shipping.data.ShippingOfferDiscrepancyData;

import de.hybris.platform.core.model.order.CartModel;

/**
 * Facade for setting shipping options on marketplace order entries
 */
public interface ShippingFacade {

  /**
   * Sets available shipping options on the session cart
   */
  void setAvailableShippingOptions();

  /**
   * Sets available shipping options on the session cart and return true if the cart price value was updated
   */
  boolean updateAvailableShippingOptions();

  /**
   * Updates selected shipping option of a marketplace order
   * 
   * @param selectedShippingOptionCode new selected shipping option code
   * @param leadTimeToShip lead time to ship
   * @param shopId shop id of the marketplace order
   */
  void updateShippingOptions(String selectedShippingOptionCode, Integer leadTimeToShip, String shopId);

  /**
   * Returns offer discrepancies found for the session cart entries
   * 
   * @return list of {@link ShippingOfferDiscrepancyData}
   */
  List<ShippingOfferDiscrepancyData> getOfferDiscrepancies();

  /**
   * Removes entries from the session cart with invalid offers (insufficient stock, offer not available, incorrect selected
   * shipping option)
   */
  void removeInvalidOffers();

  /**
   * Update offers price synchronously using data from SH02
   *
   * @return true if any price was updated
   */
  boolean updateOffersPrice();
}
