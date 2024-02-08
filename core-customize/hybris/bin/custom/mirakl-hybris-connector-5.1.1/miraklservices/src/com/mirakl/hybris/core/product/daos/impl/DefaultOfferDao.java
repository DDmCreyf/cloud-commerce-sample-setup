package com.mirakl.hybris.core.product.daos.impl;

import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.daos.OfferDao;
import com.mirakl.hybris.core.util.QueryParam;
import com.mirakl.hybris.core.util.flexiblesearch.QueryDecorator;
import com.mirakl.hybris.core.util.flexiblesearch.impl.QueryBuilder;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.stream.Collectors;

import static com.mirakl.hybris.core.util.QueryParamsBuilder.queryParams;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.CollectionCondition.fieldIn;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Condition.condition;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Condition.fieldEquals;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Field.field;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Item.item;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.QueryBuilder.query;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

public class DefaultOfferDao extends DefaultGenericDao<OfferModel> implements OfferDao {

  private static final String PRODUCT_CODE_PARAMETER = "productCode";
  private static final String PRODUCT_CODES_PARAMETER = "productCodes";
  private static final String CURRENCY_PARAMETER = "currency";
  protected List<QueryDecorator> queryDecorators;

  public DefaultOfferDao() {
    super(OfferModel._TYPECODE);
  }

  @Override
  public OfferModel findOfferById(String offerId) {
    return findOfferById(offerId, false);
  }

  @Override
  public OfferModel findOfferById(String offerId, boolean ignoreQueryDecorators) {
    validateParameterNotNullStandardMessage("offerId", offerId);

    List<OfferModel> offers = findOffersForAttributeValues(queryParams()//
        .withAttributeEquals(OfferModel.ID, offerId)//
        .build(), ignoreQueryDecorators);

    return isEmpty(offers) ? null : offers.get(0);
  }

  @Override
  public List<OfferModel> findUndeletedOffersModifiedBeforeDate(Date modificationDate) {
    validateParameterNotNullStandardMessage("modificationDate", modificationDate);

    return findOffersForAttributeValues(queryParams() //
        .withAttributeMatchingCondition(ItemModel.MODIFIEDTIME, "<", modificationDate) //
        .withAttributeEquals(OfferModel.DELETED, false) //
        .build());
  }

  @Override
  public List<OfferModel> findOffersForProductCode(String productCode) {
    validateParameterNotNullStandardMessage(PRODUCT_CODE_PARAMETER, productCode);

    return findOffersForAttributeValues(queryParams() //
        .withAttributeEquals(OfferModel.PRODUCTCODE, productCode) //
        .build());
  }

  @Override
  public List<OfferModel> findOffersForProductCodeAndCurrency(String productCode, CurrencyModel offerCurrency) {
    validateParameterNotNullStandardMessage(PRODUCT_CODE_PARAMETER, productCode);
    validateParameterNotNullStandardMessage(CURRENCY_PARAMETER, offerCurrency);

    return findOffersForAttributeValues(queryParams() //
        .withAttributeEquals(OfferModel.PRODUCTCODE, productCode) //
        .withAttributeEquals(OfferModel.CURRENCY, offerCurrency) //
        .build());
  }

  @Override
  public Map<String, List<OfferModel>> findOffersForProductCodesAndCurrency(List<String> productCodes, CurrencyModel offerCurrency) {
    validateParameterNotNullStandardMessage(PRODUCT_CODES_PARAMETER, productCodes);
    validateParameterNotNullStandardMessage(CURRENCY_PARAMETER, offerCurrency);

    QueryBuilder query = query(queryDecorators) //
        .select(field("o", OfferModel.PRODUCTCODE)) //
        .select(field("o", ItemModel.PK)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(fieldIn(field("o", OfferModel.PRODUCTCODE), productCodes))
        .and(fieldEquals(field("o", OfferModel.CURRENCY), offerCurrency));

    FlexibleSearchQuery flexibleSearch = query.build();
    flexibleSearch.setResultClassList(Arrays.asList(String.class, OfferModel.class));
    SearchResult<List<Object>> result = getFlexibleSearchService().search(flexibleSearch);

    // A map of all input product codes to empty lists, as a fallback
    Map<String, List<OfferModel>> fallbackOffersForAllProductCodes = productCodes.stream()
        .collect(Collectors.toMap(code -> code, code -> new ArrayList<>(), (existingList, newList) -> existingList, HashMap::new));

    Map<String, List<OfferModel>> offersByProductCode = result.getResult().stream().collect(
        Collectors.groupingBy(offersForProductCode -> (String) offersForProductCode.get(0), Collectors.mapping(offersForProductCode -> (OfferModel) offersForProductCode.get(1),
            Collectors.toList())));

    // Merge the actual results to add missing product codes with empty lists to the offersByProductCode
    fallbackOffersForAllProductCodes.forEach(offersByProductCode::putIfAbsent);

    return offersByProductCode;

  }

  @Override
  public List<Pair<OfferState, CurrencyModel>> findOfferStatesAndCurrencyForProductCode(String productCode) {
    validateParameterNotNullStandardMessage(PRODUCT_CODE_PARAMETER, productCode);

    QueryBuilder query = query(queryDecorators) //
        .select(field("o", OfferModel.STATE)) //
        .select(field("o", OfferModel.CURRENCY)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(fieldEquals(field("o", OfferModel.PRODUCTCODE), productCode)) //
        .groupBy(field("o", OfferModel.STATE)) //
        .groupBy(field("o", OfferModel.CURRENCY));

    FlexibleSearchQuery flexibleSearch = query.build();
    flexibleSearch.setResultClassList(asList(OfferState.class, CurrencyModel.class));
    SearchResult<List<Object>> result = getFlexibleSearchService().search(flexibleSearch);

    return result.getResult().stream().map(tuple -> Pair.of((OfferState) tuple.get(0), (CurrencyModel) tuple.get(1))).collect(toList());
  }

  @Override
  public int countOffersForProduct(String productCode) {
    validateParameterNotNullStandardMessage(PRODUCT_CODE_PARAMETER, productCode);

    return countOffersForAttributeValues(queryParams() //
        .withAttributeEquals(OfferModel.PRODUCTCODE, productCode) //
        .build());
  }

  @Override
  public int countOffersForProductAndCurrency(String productCode, CurrencyModel currency) {
    validateParameterNotNullStandardMessage(PRODUCT_CODE_PARAMETER, productCode);
    validateParameterNotNullStandardMessage(CURRENCY_PARAMETER, currency);

    return countOffersForAttributeValues(queryParams() //
        .withAttributeEquals(OfferModel.PRODUCTCODE, productCode) //
        .withAttributeEquals(OfferModel.CURRENCY, currency) //
        .build());
  }

  @Override
  public List<String> findOfferIdsForProductCode(final String productCode) {
    validateParameterNotNullStandardMessage(PRODUCT_CODE_PARAMETER, productCode);
    QueryBuilder query = query(queryDecorators) //
        .select(field("o", OfferModel.ID)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(fieldEquals(field("o", OfferModel.PRODUCTCODE), productCode));
    FlexibleSearchQuery flexibleSearch = query.build();
    flexibleSearch.setResultClassList(Collections.singletonList(String.class));
    SearchResult<String> queryResult = getFlexibleSearchService().search(flexibleSearch);
    return queryResult.getResult();
  }

  @Override
  public Map<String, Integer> countOffersForProductsAndCurrency(final List<String> productCodes, CurrencyModel currency) {
    validateParameterNotNullStandardMessage(PRODUCT_CODES_PARAMETER, productCodes);
    validateParameterNotNullStandardMessage(CURRENCY_PARAMETER, currency);

    QueryBuilder query = query(queryDecorators) //
        .select(field("o", OfferModel.PRODUCTCODE)) //
        .selectCount(field("o", ItemModel.PK)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(fieldIn(field("o", OfferModel.PRODUCTCODE), productCodes)).and(fieldEquals(field("o", OfferModel.CURRENCY), currency))
        .groupBy(field("o", OfferModel.PRODUCTCODE)); //

    FlexibleSearchQuery flexibleSearch = query.build();
    flexibleSearch.setResultClassList(Arrays.asList(String.class, Integer.class));
    SearchResult<List<Object>> result = getFlexibleSearchService().search(flexibleSearch);

    return result.getResult().stream().collect(toMap(countByProduct -> (String) countByProduct.get(0), countByProduct -> (Integer) countByProduct.get(1)));
  }

  protected int countOffersForAttributeValues(List<QueryParam> queryParams) {
    return countOffersForAttributeValues(queryParams, false);
  }

  protected int countOffersForAttributeValues(List<QueryParam> queryParams, boolean ignoreDecorators) {
    FlexibleSearchQuery flexibleSearchQuery = getFlexibleSearchQuery(queryParams, true, ignoreDecorators);
    flexibleSearchQuery.setResultClassList(singletonList(Integer.class));
    SearchResult<Integer> result = getFlexibleSearchService().search(flexibleSearchQuery);
    return result.getResult().get(0);
  }

  protected List<OfferModel> findOffersForAttributeValues(List<QueryParam> queryParams) {
    return findOffersForAttributeValues(queryParams, false);
  }

  protected List<OfferModel> findOffersForAttributeValues(List<QueryParam> queryParams, boolean ignoreDecorators) {
    FlexibleSearchQuery build = getFlexibleSearchQuery(queryParams, false, ignoreDecorators);
    SearchResult<OfferModel> result = getFlexibleSearchService().search(build);
    return result.getResult();
  }

  protected FlexibleSearchQuery getFlexibleSearchQuery(List<QueryParam> queryParams, boolean count) {
    return getFlexibleSearchQuery(queryParams, count, false);
  }

  protected FlexibleSearchQuery getFlexibleSearchQuery(List<QueryParam> queryParams, boolean count, boolean ignoreDecorators) {
    QueryBuilder query = (ignoreDecorators ? query() : query(queryDecorators)) //
        .select(field("o", ItemModel.PK), count) //
        .from(item(OfferModel._TYPECODE, "o"));
    for (QueryParam queryParam : queryParams) {
      query.and(condition(field("o", queryParam.getAttribute()), queryParam.getOperator(), queryParam.getValue()));
    }

    return query.build();
  }

  @Required
  public void setQueryDecorators(List<QueryDecorator> queryDecorators) {
    this.queryDecorators = queryDecorators;
  }

}
