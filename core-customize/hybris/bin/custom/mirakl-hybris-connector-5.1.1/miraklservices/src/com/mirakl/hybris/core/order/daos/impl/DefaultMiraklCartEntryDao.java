package com.mirakl.hybris.core.order.daos.impl;

import de.hybris.platform.core.model.order.CartEntryModel;

public class DefaultMiraklCartEntryDao extends DefaultMiraklAbstractOrderEntryDao<CartEntryModel> {

  public DefaultMiraklCartEntryDao() {
    super(CartEntryModel._TYPECODE);
  }

}
