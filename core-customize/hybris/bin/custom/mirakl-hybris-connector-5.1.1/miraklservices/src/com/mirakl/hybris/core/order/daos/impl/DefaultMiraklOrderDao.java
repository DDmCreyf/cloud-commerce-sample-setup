package com.mirakl.hybris.core.order.daos.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.daos.impl.DefaultOrderDao;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultMiraklOrderDao extends DefaultOrderDao {

  protected static final String ORDER_ENTRIES_BY_PRODUCT_WITH_NO_OFFERS = //
      "SELECT {oe:" + AbstractOrderEntryModel.PK + "} FROM " + "{" + AbstractOrderEntryModel._TYPECODE + " AS oe" //
          + "} WHERE {oe:" + AbstractOrderEntryModel.ORDER + "}=?" + AbstractOrderEntryModel.ORDER//
          + " AND {oe:" + AbstractOrderEntryModel.PRODUCT + "}=?" + AbstractOrderEntryModel.PRODUCT //
          + " AND {oe:" + AbstractOrderEntryModel.OFFERID + "} is null";

  @Override
  public List<AbstractOrderEntryModel> findEntriesByProduct(AbstractOrderModel order, ProductModel product) {
    checkArgument(order != null);
    checkArgument(product != null);

    final Map<String, Object> params = new HashMap<>();
    params.put(AbstractOrderEntryModel.ORDER, order);
    params.put(AbstractOrderEntryModel.PRODUCT, product);

    SearchResult<AbstractOrderEntryModel> result =
        getFlexibleSearchService().search(ORDER_ENTRIES_BY_PRODUCT_WITH_NO_OFFERS, params);

    return result.getResult();
  }
}
