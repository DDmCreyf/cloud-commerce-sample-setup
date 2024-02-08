package com.mirakl.hybris.core.order.strategies.hooks;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.model.order.CartEntryModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface MiraklCartValidationHook {

  /**
   * Performs additional validations on a cart entry before the default ones
   *
   * @param cartEntry The cart entry
   * @return the performed modification if any, null otherwise
   */
  CommerceCartModification beforeValidateCartEntry(CartEntryModel cartEntry);
}
