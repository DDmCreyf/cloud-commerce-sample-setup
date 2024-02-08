package com.mirakl.hybris.b2bcore.order.strategies;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com
 * All Rights Reserved. Tous droits réservés.
 */
public interface B2BUnitBillingAddressStrategy {

    /**
     * Returns a billing address from a B2B unit
     *
     * @param unit The B2B unit to get the Billing Address
     * @return The AdressModel if found
     * @throws IllegalStateException if the strategy is unable to find the address
     */
    AddressModel getBillingAddressForUnit(B2BUnitModel unit);
}
