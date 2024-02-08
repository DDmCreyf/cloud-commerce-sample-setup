package com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl;

import static com.mirakl.hybris.facades.constants.MiraklfacadesConstants.OFFERS_SUMMARY_SOLR_PROPERTY_NAME;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OffersSummaryData;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklSearchResultProductPopulatorTest {

  private static final String JSON_CONTENT = "json-string";

  @InjectMocks
  private MiraklSearchResultProductPopulator populator;

  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private Converter<List<OfferOverviewData>, OffersSummaryData> offersSummaryDataFromOverviewConverter;
  @Mock
  private SearchResultValueData searchResultValueData;
  @Mock
  private OffersSummaryData offersSummaryData;

  @Before
  public void setUp() throws Exception {
    when(jsonMarshallingService.fromJson(JSON_CONTENT, OffersSummaryData.class)).thenReturn(offersSummaryData);
    when(searchResultValueData.getValues())
        .thenReturn(Collections.<String, Object>singletonMap(OFFERS_SUMMARY_SOLR_PROPERTY_NAME, JSON_CONTENT));
  }

  @Test
  public void shouldPopulateOffersSummary() {
    ProductData result = new ProductData();
    populator.populate(searchResultValueData, result);

    assertThat(result.getOffersSummary()).isEqualTo(offersSummaryData);
  }

  @Test
  public void shouldDoNothingWhenNoSummary() {
    when(searchResultValueData.getValues()).thenReturn(Collections.<String, Object>emptyMap());

    ProductData result = new ProductData();
    populator.populate(searchResultValueData, result);

    assertThat(result.getOffersSummary()).isNull();
  }


}
