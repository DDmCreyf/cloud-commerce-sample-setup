package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.product.services.MiraklProductService;
import com.mirakl.hybris.core.shop.daos.ShopDao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
@RunWith(value = MockitoJUnitRunner.class)
public class ShopIdValueProviderTest {

  private static final String PRODUCT_CODE = "product-code";
  private static final String FIELD_NAME = "test_field_name";
  private static final String CURRENCY_ISO_CODE = "test_iso_code";
  private static final String SHOP_ID = "test_shop_id";
  private static final String OPERATOR_CODE = "operator-code";

  @InjectMocks
  ShopIdValueProvider shopIdValueProvider;

  @Mock
  private MiraklProductService miraklProductService;
  @Mock
  private ShopDao shopDao;
  @Mock
  private FieldNameProvider fieldNameProvider;
  @Mock
  private ShopModel shop;
  @Mock
  private CurrencyModel currency;
  @Mock
  private ProductModel product;
  @Mock
  private IndexedProperty indexedProperty;
  @Mock
  private IndexConfig indexConfig;
  @Mock
  private BaseSiteModel baseSite;
  @Mock
  private BaseStoreModel baseStore;

  @Before
  public void setUp() throws Exception {
    when(fieldNameProvider.getFieldNames(indexedProperty, CURRENCY_ISO_CODE)).thenReturn(singletonList(FIELD_NAME));
    when(product.getCode()).thenReturn(PRODUCT_CODE);
    when(shop.getCurrency()).thenReturn(currency);
    when(shop.getId()).thenReturn(SHOP_ID);
    when(currency.getIsocode()).thenReturn(CURRENCY_ISO_CODE);
    when(indexConfig.getBaseSite()).thenReturn(baseSite);
    when(baseSite.getStores()).thenReturn(singletonList(baseStore));
    when(baseStore.getCurrencies()).thenReturn(singleton(currency));
    when(baseSite.getOperatorCode()).thenReturn(OPERATOR_CODE);
  }

  @Test
  public void getsFieldValuesWhenShopsAndNotSellableByOperator() throws Exception {
    when(shopDao.findShopsForProductCode(anyString())).thenReturn(singletonList(shop));
    when(miraklProductService.isSellableByOperator(product)).thenReturn(false);

    Collection<FieldValue> result = shopIdValueProvider.getFieldValues(indexConfig, indexedProperty, product);
    FieldValue resultFieldValue = result.iterator().next();

    assertThat(result).hasSize(1);
    assertThat(resultFieldValue.getValue()).isEqualTo(SHOP_ID);
    assertThat(resultFieldValue.getFieldName()).isEqualTo(FIELD_NAME);
  }

  @Test
  public void getsFieldValuesIsEmptyWhenNoShopsAndNotSellableByOperator() throws Exception {
    when(shopDao.findShopsForProductCode(anyString())).thenReturn(Collections.<ShopModel>emptyList());
    when(miraklProductService.isSellableByOperator(product)).thenReturn(false);

    Collection<FieldValue> result = shopIdValueProvider.getFieldValues(indexConfig, indexedProperty, product);

    assertThat(result).isEmpty();
  }

  @Test
  public void getsFieldValuesWhenNoShopsAndSellableByOperator() throws Exception {
    when(shopDao.findShopsForProductCode(anyString())).thenReturn(Collections.<ShopModel>emptyList());
    when(miraklProductService.isSellableByOperator(product)).thenReturn(true);

    Collection<FieldValue> result = shopIdValueProvider.getFieldValues(indexConfig, indexedProperty, product);
    FieldValue resultFieldValue = result.iterator().next();

    assertThat(result).hasSize(1);
    assertThat(resultFieldValue.getValue()).isEqualTo(OPERATOR_CODE);
    assertThat(resultFieldValue.getFieldName()).isEqualTo(FIELD_NAME);
  }

  @Test
  public void getsFieldValuesWhenShopsAndSellableByOperator() throws Exception {
    when(shopDao.findShopsForProductCode(anyString())).thenReturn(singletonList(shop));
    when(miraklProductService.isSellableByOperator(product)).thenReturn(true);

    Collection<FieldValue> result = shopIdValueProvider.getFieldValues(indexConfig, indexedProperty, product);

    assertThat(result).hasSize(2);
    for (FieldValue fieldValue : result) {
      assertThat(fieldValue.getFieldName()).isEqualTo(FIELD_NAME);
      assertThat(fieldValue.getValue()).isIn(OPERATOR_CODE, SHOP_ID);
    }
  }
}
