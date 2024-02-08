package com.mirakl.hybris.core.shop.daos.impl;

import static com.mirakl.hybris.core.util.QueryParamsBuilder.queryParams;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Condition.condition;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Condition.fieldEquals;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Field.field;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Item.item;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Join.entity;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.QueryBuilder.query;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.daos.ShopDao;
import com.mirakl.hybris.core.util.QueryParam;
import com.mirakl.hybris.core.util.flexiblesearch.QueryDecorator;
import com.mirakl.hybris.core.util.flexiblesearch.impl.QueryBuilder;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultShopDao extends DefaultGenericDao<ShopModel> implements ShopDao {

  protected List<QueryDecorator> queryDecorators;

  public DefaultShopDao() {
    super(ShopModel._TYPECODE);
  }

  @Override
  public ShopModel findShopById(String shopId) {
    validateParameterNotNullStandardMessage("shopId", shopId);

    List<ShopModel> shops = findShopsForAttributeValues(queryParams() //
        .withAttributeEquals(ShopModel.ID, shopId) //
        .build());

    if (isNotEmpty(shops) && shops.size() > 1) {
      throw new AmbiguousIdentifierException(format("Multiple shops found for id [%s]", shopId));
    }
    return isEmpty(shops) ? null : shops.get(0);
  }

  @Override
  public Collection<ShopModel> findShopsForProductCode(String productCode) {
    validateParameterNotNull("productCode", productCode);

    QueryBuilder query = query(queryDecorators)//
        .select(field("s", ShopModel.PK)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .join(entity(item(ShopModel._TYPECODE, "s")).on(field("s", ShopModel.PK), field("o", OfferModel.SHOP))) //
        .where(fieldEquals(field("o", OfferModel.PRODUCTCODE), productCode)).groupBy(field("s", ShopModel.PK));

    return getFlexibleSearchService().<ShopModel>search(query.build()).getResult();
  }

  protected List<ShopModel> findShopsForAttributeValues(List<QueryParam> queryParams) {
    FlexibleSearchQuery build = getFlexibleSearchQuery(queryParams, false);
    SearchResult<ShopModel> result = getFlexibleSearchService().search(build);
    return result.getResult();
  }

  protected FlexibleSearchQuery getFlexibleSearchQuery(List<QueryParam> queryParams, boolean count) {
    QueryBuilder query = query(queryDecorators) //
        .select(field("s", ShopModel.PK), count) //
        .from(item(ShopModel._TYPECODE, "s"));
    for (QueryParam queryParam : queryParams) {
      query.and(condition(field("s", queryParam.getAttribute()), queryParam.getOperator(), queryParam.getValue()));
    }

    return query.build();
  }


  @Required
  public void setQueryDecorators(List<QueryDecorator> queryDecorators) {
    this.queryDecorators = queryDecorators;
  }

}
