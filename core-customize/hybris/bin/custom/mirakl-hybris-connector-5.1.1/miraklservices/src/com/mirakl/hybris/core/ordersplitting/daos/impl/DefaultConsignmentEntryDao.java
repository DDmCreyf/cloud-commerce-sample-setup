package com.mirakl.hybris.core.ordersplitting.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import com.mirakl.hybris.core.ordersplitting.daos.ConsignmentEntryDao;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

public class DefaultConsignmentEntryDao extends DefaultGenericDao<ConsignmentEntryModel> implements ConsignmentEntryDao {

  public DefaultConsignmentEntryDao() {
    super(ConsignmentEntryModel._TYPECODE);
  }

  @Override
  public ConsignmentEntryModel findConsignmentEntryByMiraklLineId(String miraklOrderLineId) {
    validateParameterNotNullStandardMessage("miraklOrderLineId", miraklOrderLineId);
    List<ConsignmentEntryModel> consignmentEntries =
        find(singletonMap(ConsignmentEntryModel.MIRAKLORDERLINEID, miraklOrderLineId));
    if (isEmpty(consignmentEntries)) {
      return null;
    }
    if (consignmentEntries.size() > 1) {
      throw new AmbiguousIdentifierException(format("Multiple consignment entries found with code [%s]", miraklOrderLineId));
    }
    return consignmentEntries.get(0);
  }

}
