package com.mirakl.hybris.channels.search.facades.solrfacetsearch.populators.impl;

import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.SOLR_MIRAKL_CHANNEL_PARAMETER;
import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.SOLR_PROPERTY_TO_REPLACE_PARAMETER;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklSearchResponseFacetChannelFilterPopulatorTest<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM> {

  private static final String OTHER_CHANNEL_CODE = "other";
  private static final String PLATINUM_CHANNEL_CODE = "platinum";
  private static final String GOLD_CHANNEL_CODE = "gold";
  private static final String CONDITION_FACET_CODE = "condition";
  private static final String SELLER_FACET_CODE = "seller";
  private static final String PRICE_PLATINUM_FACET_CODE = "price-platinum";
  private static final String PRICE_GOLD_FACET_CODE = "price-gold";
  private static final String PRICE_FACET_CODE = "price";

  @InjectMocks
  private MiraklSearchResponseFacetChannelFilterPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM> populator;

  @Mock
  private SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> source;
  @Mock
  private SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE> request;
  @Mock
  private SearchQuery searchQuery;
  @Mock
  private IndexedType indexedType;
  @Mock
  private IndexedProperty priceIndexedProperty, priceGoldIndexedProperty, pricePlatinumIndexedProperty, sellerIndexedProperty,
      conditionIndexedProperty;
  @Mock
  private MiraklChannelService miraklChannelService;
  @Mock
  private MiraklChannelModel goldChannel, platinumChannel, otherChannel;

  private Map<String, IndexedProperty> indexedProperties;
  private FacetSearchPageData<SolrSearchQueryData, ITEM> target;

  @Before
  public void setUp() throws Exception {
    when(source.getRequest()).thenReturn(request);
    when(request.getSearchQuery()).thenReturn(searchQuery);
    when(searchQuery.getIndexedType()).thenReturn(indexedType);
    indexedProperties = new HashMap<>();
    indexedProperties.put(PRICE_FACET_CODE, priceIndexedProperty);
    indexedProperties.put(PRICE_GOLD_FACET_CODE, priceGoldIndexedProperty);
    indexedProperties.put(PRICE_PLATINUM_FACET_CODE, pricePlatinumIndexedProperty);
    indexedProperties.put(SELLER_FACET_CODE, sellerIndexedProperty);
    indexedProperties.put(CONDITION_FACET_CODE, conditionIndexedProperty);
    when(indexedType.getIndexedProperties()).thenReturn(indexedProperties);
    when(priceGoldIndexedProperty.getValueProviderParameters())
        .thenReturn(getValueProvideParameters(GOLD_CHANNEL_CODE, PRICE_FACET_CODE));
    when(pricePlatinumIndexedProperty.getValueProviderParameters())
        .thenReturn(getValueProvideParameters(PLATINUM_CHANNEL_CODE, PRICE_FACET_CODE));
    when(goldChannel.getCode()).thenReturn(GOLD_CHANNEL_CODE);
    when(otherChannel.getCode()).thenReturn(OTHER_CHANNEL_CODE);

    target = new FacetSearchPageData<>();
    List<FacetData<SolrSearchQueryData>> facets = new ArrayList<>();
    facets.add(createFacetData(PRICE_FACET_CODE));
    facets.add(createFacetData(PRICE_GOLD_FACET_CODE));
    facets.add(createFacetData(PRICE_PLATINUM_FACET_CODE));
    facets.add(createFacetData(SELLER_FACET_CODE));
    facets.add(createFacetData(CONDITION_FACET_CODE));
    target.setFacets(facets);
  }

  @Test
  public void shouldUseDefaultWhenNoUserPriceChannel() throws Exception {
    populator.populate(source, target);

    assertThat(target.getFacets()).hasSize(3);
    assertThat(getFacetForCode(PRICE_FACET_CODE, target.getFacets())).isNotNull();
    assertThat(getFacetForCode(SELLER_FACET_CODE, target.getFacets())).isNotNull();
    assertThat(getFacetForCode(CONDITION_FACET_CODE, target.getFacets())).isNotNull();
    assertThat(getFacetForCode(PRICE_GOLD_FACET_CODE, target.getFacets())).isNull();
    assertThat(getFacetForCode(PRICE_PLATINUM_FACET_CODE, target.getFacets())).isNull();
  }

  @Test
  public void shouldFilterByCurrentChannelWhenUserPriceChannel() throws Exception {
    when(miraklChannelService.getCurrentMiraklChannel()).thenReturn(goldChannel);

    populator.populate(source, target);

    assertThat(target.getFacets()).hasSize(3);
    assertThat(getFacetForCode(PRICE_GOLD_FACET_CODE, target.getFacets())).isNotNull();
    assertThat(getFacetForCode(SELLER_FACET_CODE, target.getFacets())).isNotNull();
    assertThat(getFacetForCode(CONDITION_FACET_CODE, target.getFacets())).isNotNull();
    assertThat(getFacetForCode(PRICE_FACET_CODE, target.getFacets())).isNull();
    assertThat(getFacetForCode(PRICE_PLATINUM_FACET_CODE, target.getFacets())).isNull();
  }

  @Test
  public void shouldUseDefaultWhenNoMatchingPriceChannel() throws Exception {
    when(miraklChannelService.getCurrentMiraklChannel()).thenReturn(otherChannel);

    populator.populate(source, target);

    assertThat(target.getFacets()).hasSize(3);
    assertThat(getFacetForCode(PRICE_FACET_CODE, target.getFacets())).isNotNull();
    assertThat(getFacetForCode(SELLER_FACET_CODE, target.getFacets())).isNotNull();
    assertThat(getFacetForCode(CONDITION_FACET_CODE, target.getFacets())).isNotNull();
    assertThat(getFacetForCode(PRICE_GOLD_FACET_CODE, target.getFacets())).isNull();
    assertThat(getFacetForCode(PRICE_PLATINUM_FACET_CODE, target.getFacets())).isNull();
  }

  protected Map<String, String> getValueProvideParameters(String priceChannelCode, String propertyToReplace) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put(SOLR_MIRAKL_CHANNEL_PARAMETER, priceChannelCode);
    parameters.put(SOLR_PROPERTY_TO_REPLACE_PARAMETER, propertyToReplace);
    return parameters;
  }

  protected FacetData<SolrSearchQueryData> createFacetData(String facetCode) {
    FacetData<SolrSearchQueryData> facetData = new FacetData<SolrSearchQueryData>();
    facetData.setCode(facetCode);
    return facetData;
  }

  protected FacetData<SolrSearchQueryData> getFacetForCode(String name, List<FacetData<SolrSearchQueryData>> facets) {
    for (FacetData<SolrSearchQueryData> facet : facets) {
      if (name.equals(facet.getCode())) {
        return facet;
      }
    }
    return null;
  }

}
