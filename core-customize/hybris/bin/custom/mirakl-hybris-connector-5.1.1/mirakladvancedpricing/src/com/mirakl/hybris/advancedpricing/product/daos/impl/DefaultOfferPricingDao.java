package com.mirakl.hybris.advancedpricing.product.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mirakl.hybris.advancedpricing.product.daos.OfferPricingDao;
import com.mirakl.hybris.beans.OfferPricingCriteria;
import com.mirakl.hybris.core.model.OfferPricingModel;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultOfferPricingDao extends DefaultGenericDao<OfferPricingModel> implements OfferPricingDao {

  private static final String QUERY_DATE = "queryDate";

  // Queries
  protected final String FIND_BY_OFFER_ID_QUERY = "SELECT {" + OfferPricingModel.PK + "}" + //
      " FROM {" + OfferPricingModel._TYPECODE + "}" + //
      " WHERE {" + OfferPricingModel.OFFERID + "}=?" + OfferPricingModel.OFFERID;

  public DefaultOfferPricingDao() {
    super(OfferPricingModel._TYPECODE);
  }

  @Override
  public List<OfferPricingModel> findOfferPricingsByCriteria(OfferPricingCriteria offerPricingCriteria) {
    validateParameterNotNullStandardMessage("offerPricingCriteria", offerPricingCriteria);
    validateParameterNotNullStandardMessage("offerPricingCriteria.offerId", offerPricingCriteria.getOfferId());

    StringBuilder queryString =
        new StringBuilder("SELECT {").append(OfferPricingModel.PK).append("} FROM {").append(OfferPricingModel._TYPECODE)
            .append("} WHERE {").append(OfferPricingModel.OFFERID).append("}=?").append(OfferPricingModel.OFFERID);

    appendFilter(queryString, offerPricingCriteria.getChannelCode(), OfferPricingModel.CHANNELCODE);
    appendFilter(queryString, offerPricingCriteria.getCustomerOrganizationId(), OfferPricingModel.CUSTOMERORGANIZATIONID);
    appendFilter(queryString, offerPricingCriteria.getCustomerGroupId(), OfferPricingModel.CUSTOMERGROUPID);

    if (offerPricingCriteria.getDate() != null) {
      queryString.append(" AND ({").append(OfferPricingModel.STARTDATE).append("} IS NULL").append(" OR {")
          .append(OfferPricingModel.STARTDATE).append("} <= ?").append(QUERY_DATE).append(")");
      queryString.append(" AND ({").append(OfferPricingModel.ENDDATE).append("} IS NULL").append(" OR {")
          .append(OfferPricingModel.ENDDATE).append("} >= ?").append(QUERY_DATE).append(")");
    }

    Map<String, Object> params = new HashMap<>();
    params.put(OfferPricingModel.OFFERID, offerPricingCriteria.getOfferId());
    params.put(OfferPricingModel.CHANNELCODE, offerPricingCriteria.getChannelCode());
    params.put(OfferPricingModel.CUSTOMERGROUPID, offerPricingCriteria.getCustomerGroupId());
    params.put(OfferPricingModel.CUSTOMERORGANIZATIONID, offerPricingCriteria.getCustomerOrganizationId());
    params.put(QUERY_DATE, offerPricingCriteria.getDate());

    SearchResult<OfferPricingModel> searchResult =
        getFlexibleSearchService().search(new FlexibleSearchQuery(queryString.toString(), params));

    return searchResult.getResult();
  }

  @Override
  public List<OfferPricingModel> findByOfferId(String offerId) {
    validateParameterNotNullStandardMessage("offerId", offerId);

    Map<String, Object> params = new HashMap<>();
    params.put(OfferPricingModel.OFFERID, offerId);

    SearchResult<OfferPricingModel> searchResult =
        getFlexibleSearchService().search(new FlexibleSearchQuery(FIND_BY_OFFER_ID_QUERY, params));

    return searchResult.getResult();
  }

  protected void appendFilter(StringBuilder queryString, String fieldValue, String field) {
    if (isNotBlank(fieldValue)) {
      queryString.append(" AND ({").append(field).append("}=?").append(field).append(" OR {").append(field).append("} IS NULL)");
    } else {
      queryString.append(" AND {").append(field).append("} IS NULL");
    }
  }

}
