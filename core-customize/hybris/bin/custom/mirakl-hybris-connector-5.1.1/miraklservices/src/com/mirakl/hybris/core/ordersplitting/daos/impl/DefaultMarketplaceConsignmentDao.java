package com.mirakl.hybris.core.ordersplitting.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.daos.MarketplaceConsignmentDao;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultMarketplaceConsignmentDao extends DefaultGenericDao<MarketplaceConsignmentModel>
    implements MarketplaceConsignmentDao {
  private static final String MARKETPLACE_CONSIGNMENTS_BY_PAYMENT_STATUSES =
      "SELECT {mc." + MarketplaceConsignmentModel.PK + "}, {status.code} FROM {" + MarketplaceConsignmentModel._TYPECODE
          + " AS mc JOIN " + MarketplaceConsignmentPaymentStatus._TYPECODE //
          + " AS status ON {mc." + MarketplaceConsignmentModel.PAYMENTSTATUS + "} = {status.pk}" //
          + " AND {" + MarketplaceConsignmentModel.PAYMENTSTATUS + "} IN (?" + MarketplaceConsignmentModel.PAYMENTSTATUS + ")}";



  public DefaultMarketplaceConsignmentDao() {
    super(MarketplaceConsignmentModel._TYPECODE);
  }

  @Override
  public MarketplaceConsignmentModel findMarketplaceConsignmentByCode(String code) {
    validateParameterNotNullStandardMessage("code", code);
    List<MarketplaceConsignmentModel> consignments = find(singletonMap(MarketplaceConsignmentModel.CODE, code));
    if (isEmpty(consignments)) {
      return null;
    }
    if (consignments.size() > 1) {
      throw new AmbiguousIdentifierException(format("Multiple consignments found with code [%s]", code));
    }
    return consignments.get(0);
  }

  @Override
  public List<MarketplaceConsignmentModel> findMarketplaceConsignmentsByPaymentStatuses(
      Set<MarketplaceConsignmentPaymentStatus> paymentStatuses) {
    if (isEmpty(paymentStatuses)) {
      return Collections.emptyList();
    }
    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put(MarketplaceConsignmentModel.PAYMENTSTATUS, paymentStatuses);
    FlexibleSearchQuery query = new FlexibleSearchQuery(MARKETPLACE_CONSIGNMENTS_BY_PAYMENT_STATUSES, queryParams);
    SearchResult<MarketplaceConsignmentModel> searchResult = getFlexibleSearchService().search(query);

    List<MarketplaceConsignmentModel> consignmentsWherePaymentStatusIsInitial = searchResult.getResult();
    if (isEmpty(consignmentsWherePaymentStatusIsInitial)) {
      return Collections.emptyList();
    }
    return consignmentsWherePaymentStatusIsInitial;
  }

}
