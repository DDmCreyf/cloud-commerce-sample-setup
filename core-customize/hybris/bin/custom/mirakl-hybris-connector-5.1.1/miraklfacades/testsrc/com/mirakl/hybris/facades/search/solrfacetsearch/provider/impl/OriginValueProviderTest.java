package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.ProductOrigin;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OriginValueProviderTest {

  private static final String ORIGIN = "origin";

  @InjectMocks
  private OriginValueProvider valueProvider;

  @Mock
  private SessionService sessionService;
  @Mock
  private CommonI18NService commonI18NService;
  @Mock
  private IndexConfig indexConfig;
  @Mock
  private IndexedProperty indexedProperty;
  @Mock
  private ProductModel product;
  @Mock
  private CurrencyModel currency;
  @Mock
  private FieldNameProvider fieldNameProvider;

  private ProductOrigin productOrigin = ProductOrigin.MARKETPLACE;

  @Before
  public void setUp() throws Exception {
    when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(Collections.singletonList(ORIGIN));
    when(product.getOrigin()).thenReturn(productOrigin);
  }

  @Test
  public void shouldGetValuesUsingIndexCurrencies() throws FieldValueProviderException {
    Collection<FieldValue> result = valueProvider.getFieldValues(indexConfig, indexedProperty, product);

    assertThat(result).hasSize(1);

    FieldValue fieldValue = result.iterator().next();
    assertThat(fieldValue.getFieldName()).isEqualTo(ORIGIN);
    assertThat(fieldValue.getValue()).isEqualTo(productOrigin.getCode());
  }

  @Test
  public void shouldGetValuesUsingSessionCurrencyWhenNoIndexCurrencies() throws FieldValueProviderException {
    Collection<FieldValue> result = valueProvider.getFieldValues(indexConfig, indexedProperty, product);

    assertThat(result).hasSize(1);

    FieldValue fieldValue = result.iterator().next();
    assertThat(fieldValue.getFieldName()).isEqualTo(ORIGIN);
    assertThat(fieldValue.getValue()).isEqualTo(productOrigin.getCode());
  }

}
