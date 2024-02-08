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

import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class HasMiraklOfferValueProviderTest {

  private static final String HAS_MIRAKL_OFFER = "hasMiraklOffer";

  @InjectMocks
  private HasMiraklOfferValueProvider valueProvider;

  @Mock
  private IndexConfig indexConfig;
  @Mock
  private IndexedProperty indexedProperty;
  @Mock
  private ProductModel product;
  @Mock
  private FieldNameProvider fieldNameProvider;
  @Mock
  private OfferService offerService;

  @Before
  public void setUp() throws Exception {
    when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(Collections.singletonList(HAS_MIRAKL_OFFER));
  }

  @Test
  public void shouldReturnTrueIfTheirExistAtLeastOneOffer() throws FieldValueProviderException {
    when(offerService.hasOffers(product.getCode())).thenReturn(true);

    Collection<FieldValue> result = valueProvider.getFieldValues(indexConfig, indexedProperty, product);

    assertThat(result).hasSize(1);

    assertThat(result.iterator().next().getFieldName()).isEqualTo(HAS_MIRAKL_OFFER);
    assertThat(result.iterator().next().getValue()).isEqualTo(true);
  }

  @Test
  public void shouldReturnFalsefTheirExistAtLeastOneOffer() throws FieldValueProviderException {
    when(offerService.hasOffers(product.getCode())).thenReturn(false);

    Collection<FieldValue> result = valueProvider.getFieldValues(indexConfig, indexedProperty, product);

    assertThat(result).hasSize(1);

    assertThat(result.iterator().next().getFieldName()).isEqualTo(HAS_MIRAKL_OFFER);
    assertThat(result.iterator().next().getValue()).isEqualTo(false);
  }

  @Test
  public void shouldReturnNothingForBadClass() throws FieldValueProviderException {
    Collection<FieldValue> result = valueProvider.getFieldValues(indexConfig, indexedProperty, new Object());

    assertThat(result).hasSize(0);

  }
}
