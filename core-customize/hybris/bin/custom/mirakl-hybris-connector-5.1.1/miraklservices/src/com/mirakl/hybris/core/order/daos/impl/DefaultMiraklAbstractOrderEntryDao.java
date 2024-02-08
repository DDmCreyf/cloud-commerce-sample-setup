package com.mirakl.hybris.core.order.daos.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.daos.MiraklAbstractOrderEntryDao;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

public class DefaultMiraklAbstractOrderEntryDao<T extends AbstractOrderEntryModel>
    extends DefaultGenericDao<AbstractOrderEntryModel> implements MiraklAbstractOrderEntryDao<T> {

  public DefaultMiraklAbstractOrderEntryDao(String typecode) {
    super(typecode);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T findEntryByOffer(AbstractOrderModel order, OfferModel offer) {
    checkArgument(order != null);
    checkArgument(offer != null);

    final Map<String, Object> params = new HashMap<>();
    params.put(AbstractOrderEntryModel.ORDER, order);
    params.put(AbstractOrderEntryModel.OFFERID, offer.getId());

    List<AbstractOrderEntryModel> orderEntries = find(params);
    if (CollectionUtils.isNotEmpty(orderEntries)) {
      if (orderEntries.size() > 1) {
        throw new IllegalStateException(
            format("Found more than one order entry for offer [%s] in order [%s]", offer.getId(), order.getCode()));
      }
      return (T) orderEntries.get(0);
    }
    return null;
  }


}
