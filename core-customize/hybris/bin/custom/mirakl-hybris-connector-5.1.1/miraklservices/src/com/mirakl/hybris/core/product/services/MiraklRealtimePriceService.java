package com.mirakl.hybris.core.product.services;

import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
/**
 * Provides methods for fetching and computing real-time pricing information related to offers and product entries.
 */
public interface MiraklRealtimePriceService {

  /**
   * Computes the total price for a given offer.
   *
   * @param offerOnProduct the offer object obtained from the real-time call.
   * @return a BigDecimal representing the total computed price of the offer.
   */
  BigDecimal getOfferTotalPrice(MiraklOfferOnProduct offerOnProduct);

  /**
   * Retrieves the real-time price value of a specified cart or order entry that contains a certain offer.
   *
   * @param entry the cart/order entry whose price is to be determined.
   * @return a PriceValue object representing the real-time price of the entry associated with the referenced offer.
   */
  PriceValue getPriceValue(AbstractOrderEntryModel entry);

  /**
   * Fetches the real-time offer pricing for a specified entry using the Mirakl API endpoint OF22.
   *
   * @param entry the cart/order entry for which pricing information is to be fetched.
   * @return a MiraklOfferPricing object containing the offer price data fetched from the Mirakl API endpoint OF22.
   */
  MiraklOfferPricing getRealtimeOfferPricing(AbstractOrderEntryModel entry);

}
