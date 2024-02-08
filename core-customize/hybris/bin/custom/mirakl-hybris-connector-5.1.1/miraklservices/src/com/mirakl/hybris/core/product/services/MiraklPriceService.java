package com.mirakl.hybris.core.product.services;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.model.OfferModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.List;

public interface MiraklPriceService extends PriceService {

  /**
   * Transforms all Mirakl volume prices into {@link PriceInformation} for the given {@link OfferModel} and the current session
   * user.
   * 
   * @param offer
   * @return an empty list if no volume price exists.
   */
  List<PriceInformation> getPriceInformationsForOffer(OfferModel offer);

  /**
   * Returns the offer total price. It is the sum of the base price and the minimum shipping cost
   *
   * @param offer the offer considered for the min shipping cost
   * @param offerPricing the applicable offer pricing
   * @return the current offer total price
   */
  BigDecimal getOfferTotalPrice(OfferModel offer, OfferPricingData offerPricing);

  /**
   * Returns the effective offer total price. It is the sum of the base price and the minimum shipping cost
   *
   * @param offer
   * @return the current offer total price
   */
  BigDecimal getOfferTotalPrice(OfferOverviewData offer, OfferPricingData offerPricing);

  /**
   * Returns the volume price corresponding to a given quantity
   * 
   * @param offer
   * @param quantity
   * @return the volume price corresponding to the quantity or null if not found
   */
  OfferVolumePricingData getVolumePriceForQuantity(OfferModel offer, long quantity);

  /**
   * Returns the volume price corresponding to a given quantity
   * 
   * @param offerPricing
   * @param quantity
   * @return the volume price corresponding to the quantity or null if not found
   */
  OfferVolumePricingData getVolumePriceForQuantity(OfferPricingData offerPricing, long quantity);

  /**
   * Returns the offer unit price for a given quantity, taking into account the channels and volume prices
   * 
   * @param quantity
   * @param offer
   * @return a unitary price for an offer
   */
  BigDecimal getOfferUnitPriceForQuantity(OfferModel offer, long quantity);

  /**
   * Selects the applicable offer pricing given the current context (connected user, current channel, etc..)
   * 
   * @param offer
   * @return the offer pricing
   */
  OfferPricingData getOfferPricing(OfferModel offer);

  /**
   * Selects the applicable offer pricing given the current context (connected user, current channel, etc..)
   * 
   * @param offer
   * @return the offer pricing
   */
  OfferPricingData getOfferPricing(OfferOverviewData offer);

  /**
   * Returns the current price (takes into account if the price is in a discount period)
   * 
   * @param offerPricing the offer pricing to be considered
   * @return the current price
   */
  BigDecimal getPrice(OfferPricingData offerPricing);

  /**
   * Checks if the pricing is discounted and is in a valid discount period
   *
   * @param offerPricing the pricing to check
   * @return true if discounted
   */
  boolean isDiscountedPrice(OfferPricingData offerPricing);

  /**
   * Calculates the applicable prices and updates the {@link OfferPricingData}
   * 
   * @param offerPricing the pricing to update
   */
  void computeAndPopulateApplicablePrice(OfferPricingData offerPricing);

  /**
   * Creates the price for the Offer referenced by the id and for the giving quantity
   * @param offerId if of the offer whose price we want to retrieve the price
   * @param quantity quantity, in order to take volume prices into account
   * @param net is the price net ?
   * @return the PriceValue for the given quantity of this Offer items
   */
  PriceValue createPriceValue(String offerId, long quantity, boolean net);
}
