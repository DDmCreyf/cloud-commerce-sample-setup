package com.mirakl.hybris.channels.search.facades.solrfacetsearch.populators.impl;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.SOLR_MIRAKL_CHANNEL_PARAMETER;
import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.SOLR_PROPERTY_TO_REPLACE_PARAMETER;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.MapUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class MiraklSearchResponseFacetChannelFilterPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
    implements
    Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult>, FacetSearchPageData<SolrSearchQueryData, ITEM>> {

  protected MiraklChannelService miraklChannelService;

  @Override
  public void populate(
      SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
      FacetSearchPageData<SolrSearchQueryData, ITEM> target) {

    List<FacetData<SolrSearchQueryData>> facets = target.getFacets();
    if (isEmpty(facets)) {
      return;
    }

    MiraklChannelModel currentMiraklChannel = miraklChannelService.getCurrentMiraklChannel();
    IndexedType indexedType = source.getRequest().getSearchQuery().getIndexedType();
    List<FacetData<SolrSearchQueryData>> filteredFacets = new ArrayList<>(facets);

    for (FacetData<SolrSearchQueryData> facet : facets) {
      IndexedProperty indexedProperty = indexedType.getIndexedProperties().get(facet.getCode());
      Map<String, String> valueProviderParameters = indexedProperty.getValueProviderParameters();
      if (isEmpty(valueProviderParameters)) {
        continue;
      }

      String channelCode = valueProviderParameters.get(SOLR_MIRAKL_CHANNEL_PARAMETER);
      String facetNameToReplace = valueProviderParameters.get(SOLR_PROPERTY_TO_REPLACE_PARAMETER);
      if (isBlank(facetNameToReplace) || isBlank(channelCode)) {
        continue;
      }

      if (currentMiraklChannel != null && currentMiraklChannel.getCode().equals(channelCode)) {
        replaceFacet(facetNameToReplace, facet, filteredFacets);
      } else if (filteredFacets.contains(facet)) {
        filteredFacets.remove(facet);
      }
    }
    target.setFacets(filteredFacets);
  }

  protected void replaceFacet(String facetNameToReplace, FacetData<SolrSearchQueryData> replacingFacet,
      List<FacetData<SolrSearchQueryData>> filteredFacets) {
    FacetData<SolrSearchQueryData> facetToRemove = getFacetForCode(filteredFacets, facetNameToReplace);
    if (isNotBlank(facetNameToReplace) && facetToRemove != null) {
      replacingFacet.setName(facetToRemove.getName());
      filteredFacets.remove(facetToRemove);
    }
  }

  protected FacetData<SolrSearchQueryData> getFacetForCode(List<FacetData<SolrSearchQueryData>> facets, String code) {
    for (FacetData<SolrSearchQueryData> facet : facets) {
      if (facet.getCode().equals(code)) {
        return facet;
      }
    }
    return null;
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

}
