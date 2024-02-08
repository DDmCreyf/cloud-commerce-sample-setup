package com.mirakl.hybris.core.order;

import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;

public interface MiraklCommerceCartModificationStatus extends CommerceCartModificationStatus {

  /**
   * Indicates a failure to add the requested number of items to cart due to min order quantity unreached.
   */
  String MIN_ORDER_QUANTITY_UNREACHED = "minOrderQuantityUnreached";

  /**
   * Indicates a failure to add the requested number of items to cart due to not respecting the package quantity constraint.
   */
  String PACKAGE_QUANTITY_CONSTRAINT = "packageQuantityConstraint";
}
