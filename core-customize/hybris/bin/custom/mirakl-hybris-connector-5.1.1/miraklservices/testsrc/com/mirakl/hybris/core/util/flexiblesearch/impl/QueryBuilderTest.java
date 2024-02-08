package com.mirakl.hybris.core.util.flexiblesearch.impl;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.util.flexiblesearch.QueryDecorator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.mirakl.hybris.core.util.flexiblesearch.impl.Condition.condition;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Condition.fieldEquals;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Field.field;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Item.item;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.Join.entity;
import static com.mirakl.hybris.core.util.flexiblesearch.impl.QueryBuilder.query;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;

@UnitTest
public class QueryBuilderTest {

  private static final String PRODUCT_CODE_1 = "PRODUCT_CODE_1";
  private static final String PRODUCT_CODE_2 = "PRODUCT_CODE_2";

  @Before
  public void setUp() throws Exception {}

  @Test
  public void buildSimpleQuery() throws Exception {
    String expectedQueryString =
        "SELECT {o:pk} FROM {Offer AS o} WHERE {o:modifiedtime} < ?o_modifiedtime AND {o:deleted} = ?o_deleted";
    Date date = new Date();

    FlexibleSearchQuery flexibleSearch = query() //
        .select(field("o", OfferModel.PK)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(condition(field("o", OfferModel.MODIFIEDTIME), "<", date))//
        .and(condition(field("o", OfferModel.DELETED), "=", false))//
        .build();

    assertThat(flexibleSearch.getQuery()).isEqualTo(expectedQueryString);
    assertThat(flexibleSearch.getQueryParameters()).hasSize(2);
    assertThat((boolean) flexibleSearch.getQueryParameters().get("o_" + OfferModel.DELETED)).isFalse();
    assertThat(flexibleSearch.getQueryParameters().get("o_" + OfferModel.MODIFIEDTIME)).isEqualTo(date);
  }


  @Test
  public void buildQueryWithOrCondition() throws Exception {
    String expectedQueryString = "SELECT {o:pk} FROM {Offer AS o} WHERE {o:modifiedtime} < ?o_modifiedtime OR {o:deleted} = ?o_deleted";
    Date date = new Date();

    FlexibleSearchQuery flexibleSearch = query() //
        .select(field("o", OfferModel.PK)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(condition(field("o", OfferModel.MODIFIEDTIME), "<", date))//
        .or(condition(field("o", OfferModel.DELETED), "=", false))//
        .build();

    assertThat(flexibleSearch.getQuery()).isEqualTo(expectedQueryString);
    assertThat(flexibleSearch.getQueryParameters()).hasSize(2);
    assertThat((boolean) flexibleSearch.getQueryParameters().get("o_" + OfferModel.DELETED)).isFalse();
    assertThat(flexibleSearch.getQueryParameters().get("o_" + OfferModel.MODIFIEDTIME)).isEqualTo(date);
  }

  @Test
  public void buildQueryWithMultipleSelectsAndGroupByClause() throws Exception {
    String expectedQueryString =
        "SELECT {o:state}, {o:currency} FROM {Offer AS o} WHERE {o:productCode} = ?o_productCode GROUP BY {o:state},{o:currency}";
    String productCode = "12345678";

    FlexibleSearchQuery flexibleSearch = query()//
        .select(field("o", OfferModel.STATE)) //
        .select(field("o", OfferModel.CURRENCY)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(condition(field("o", OfferModel.PRODUCTCODE), "=", productCode)) //
        .groupBy(field("o", OfferModel.STATE))//
        .groupBy(field("o", OfferModel.CURRENCY))//
        .build();

    assertThat(flexibleSearch.getQuery()).isEqualTo(expectedQueryString);
    assertThat(flexibleSearch.getQueryParameters()).hasSize(1);
    assertThat(flexibleSearch.getQueryParameters().get("o_" + OfferModel.PRODUCTCODE)).isEqualTo(productCode);
  }

  @Test
  public void buildQueryWithJoinClauses() throws Exception {
    String expectedQueryString =
        "SELECT {o:pk} FROM {Offer AS o JOIN Shop2OffersRel AS rel ON {rel:target}={o:pk} JOIN Shop AS s ON {rel:source}={s:pk}} WHERE {s:id} = ?s_id";
    String shopId = "1234";

    FlexibleSearchQuery flexibleSearch = query()//
        .select(field("o", OfferModel.PK)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .join(entity(item(OfferModel._SHOP2OFFERSREL, "rel")).on(field("rel", "target"), field("o", OfferModel.PK))) //
        .join(entity(item(ShopModel._TYPECODE, "s")).on(field("rel", "source"), field("s", ShopModel.PK))) //
        .where(condition(field("s", ShopModel.ID), "=", shopId)) //
        .build();

    assertThat(flexibleSearch.getQuery()).isEqualTo(expectedQueryString);
    assertThat(flexibleSearch.getQueryParameters()).hasSize(1);
    assertThat(flexibleSearch.getQueryParameters().get("s_" + ShopModel.ID)).isEqualTo(shopId);
  }

  @Test
  public void buildQueryWithCount() throws Exception {
    String expectedQueryString = "SELECT COUNT({o:pk}) FROM {Offer AS o} WHERE {o:active} = ?o_active";

    FlexibleSearchQuery flexibleSearch = query()//
        .selectCount(field("o", OfferModel.PK)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(condition(field("o", OfferModel.ACTIVE), "=", true)) //
        .build();

    assertThat(flexibleSearch.getQuery()).isEqualTo(expectedQueryString);
    assertThat(flexibleSearch.getQueryParameters()).hasSize(1);
    assertThat((boolean) flexibleSearch.getQueryParameters().get("o_" + OfferModel.ACTIVE)).isTrue();
  }


  @Test
  public void buildQueryWithOneCountedSelectAndAnotherSelect() throws Exception {
    String expectedQueryString = "SELECT {o:pk}, COUNT({o:state}) FROM {Offer AS o} WHERE {o:active} = ?o_active";

    FlexibleSearchQuery flexibleSearch = query()//
        .select(field("o", OfferModel.PK)) //
        .selectCount(field("o", OfferModel.STATE)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(condition(field("o", OfferModel.ACTIVE), "=", true)) //
        .build();

    assertThat(flexibleSearch.getQuery()).isEqualTo(expectedQueryString);
    assertThat(flexibleSearch.getQueryParameters()).hasSize(1);
    assertThat((boolean) flexibleSearch.getQueryParameters().get("o_" + OfferModel.ACTIVE)).isTrue();
  }

  @Test
  public void buildQueryWithInCondition() throws Exception {
    String expectedQueryString = "SELECT {o:state} FROM {Offer AS o} WHERE {o:productCode} IN (?o_productCode)";
    List<String> prooductCodesValue = Arrays.asList(PRODUCT_CODE_1, PRODUCT_CODE_2);

    FlexibleSearchQuery flexibleSearch = query()//
        .select(field("o", OfferModel.STATE)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(CollectionCondition.fieldIn(field("o", OfferModel.PRODUCTCODE), prooductCodesValue)) //
        .build();

    assertThat(flexibleSearch.getQuery()).isEqualTo(expectedQueryString);
    assertThat(flexibleSearch.getQueryParameters()).hasSize(1);
    assertThat((List<String>)flexibleSearch.getQueryParameters().get("o_" + OfferModel.PRODUCTCODE)).containsOnly(PRODUCT_CODE_1, PRODUCT_CODE_2);
  }
  @Test
  public void buildQueryWithInConditionWithEmptyValues() throws Exception {
    String expectedQueryString = "SELECT {o:state} FROM {Offer AS o} WHERE {o:productCode} IN (?o_productCode)";
    List<String> prooductCodesValue = Collections.emptyList();
    FlexibleSearchQuery flexibleSearch = query()//
        .select(field("o", OfferModel.STATE)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(CollectionCondition.fieldIn(field("o", OfferModel.PRODUCTCODE), prooductCodesValue)) //
        .build();

    assertThat(flexibleSearch.getQuery()).isEqualTo(expectedQueryString);
    assertThat(flexibleSearch.getQueryParameters()).hasSize(1);
    assertThat((List<String>)flexibleSearch.getQueryParameters().get("o_" + OfferModel.PRODUCTCODE)).isEmpty();
  }


  @Test
  public void buildQueryWithOneCountedSelectAndAnotherSelectAndInCondition() throws Exception {
    String expectedQueryString = "SELECT {o:pk}, COUNT({o:state}) FROM {Offer AS o} WHERE {o:productCode} IN (?o_productCode)";
    List<String> prooductCodesValue = Arrays.asList(PRODUCT_CODE_1, PRODUCT_CODE_2);

    FlexibleSearchQuery flexibleSearch = query()//
        .select(field("o", OfferModel.PK)) //
        .selectCount(field("o", OfferModel.STATE)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(CollectionCondition.fieldIn(field("o", OfferModel.PRODUCTCODE), prooductCodesValue)) //
        .build();

    assertThat(flexibleSearch.getQuery()).isEqualTo(expectedQueryString);
    assertThat(flexibleSearch.getQueryParameters()).hasSize(1);
    assertThat((List<String>)flexibleSearch.getQueryParameters().get("o_" + OfferModel.PRODUCTCODE)).containsOnly(PRODUCT_CODE_1, PRODUCT_CODE_2);
  }

  @Test
  public void idempotentToString() {
    QueryBuilder query = query(singletonList(new QueryDecoratorExample()))//
        .selectCount(field("o", OfferModel.PK)) //
        .from(item(OfferModel._TYPECODE, "o")) //
        .where(condition(field("o", OfferModel.ACTIVE), "=", true));

    query.build();

    String firstCall = query.toString();
    String secondCall = query.toString();

    assertThat(firstCall).isEqualTo(secondCall);
    assertThat(query.whereClause.getConditions()).hasSize(2);
    assertThat(query.fromClause.getJoinedEntities()).hasSize(2);
  }

  private static class QueryDecoratorExample implements QueryDecorator {

    private static final String SHOP_ID = "9999";

    @Override
    public void decorate(QueryBuilder queryBuilder) {
      queryBuilder //
          .join(entity(item(OfferModel._SHOP2OFFERSREL, "rel")).on(field("rel", "target"), field("o", OfferModel.PK))) //
          .join(entity(item(ShopModel._TYPECODE, "s")).on(field("rel", "source"), field("s", ShopModel.PK))) //
          .and(fieldEquals(field("s", ShopModel.ID), SHOP_ID));
    }

  }

}
