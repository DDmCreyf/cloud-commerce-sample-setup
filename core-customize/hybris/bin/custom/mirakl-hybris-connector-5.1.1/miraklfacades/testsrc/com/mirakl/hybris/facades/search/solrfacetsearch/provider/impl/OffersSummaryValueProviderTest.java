package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OffersSummaryValueProviderTest {

  @InjectMocks
  private OffersSummaryValueProvider valueProvider;

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

  private List<FieldValue> fieldValues;

  @Before
  public void setUp() throws Exception {
    fieldValues = singletonList(mock(FieldValue.class));
    when(sessionService.executeInLocalView(any(SessionExecutionBody.class))).thenReturn(fieldValues);
  }

  @Test
  public void shouldGetValuesUsingIndexCurrencies() throws FieldValueProviderException {
    when(indexConfig.getCurrencies()).thenReturn(singleton(currency));

    Collection<FieldValue> result = valueProvider.getFieldValues(indexConfig, indexedProperty, product);

    assertThat(result).isEqualTo(fieldValues);
  }

  @Test
  public void shouldGetValuesUsingSessionCurrencyWhenNoIndexCurrencies() throws FieldValueProviderException {
    when(indexConfig.getCurrencies()).thenReturn(Collections.<CurrencyModel>emptyList());
    when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

    Collection<FieldValue> result = valueProvider.getFieldValues(indexConfig, indexedProperty, product);

    assertThat(result).isEqualTo(fieldValues);
  }

}
