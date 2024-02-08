package com.mirakl.hybris.core.order.strategies;

import com.mirakl.hybris.beans.CartAdjustment;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

/**
 * A convenience class to share cart operations strategies (add to cart, update quantity,..)
 */
public interface CommonMiraklCartStrategy {

  /**
   * Calculates the allowed cart adjustment for an 'Add to Cart' request
   *
   * @param parameter cart parameter
   * @return a bean containing the allowed quantity to add and the 'Add to Cart' request status
   */
  CartAdjustment calculateCartAdjustment(CommerceCartParameter parameter);

}
