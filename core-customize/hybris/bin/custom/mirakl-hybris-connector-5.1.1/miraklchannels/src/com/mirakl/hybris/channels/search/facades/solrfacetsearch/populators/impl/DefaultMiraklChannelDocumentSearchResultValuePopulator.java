package com.mirakl.hybris.channels.search.facades.solrfacetsearch.populators.impl;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.DocumentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import org.springframework.beans.factory.annotation.Required;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.SOLR_MIRAKL_CHANNEL_PARAMETER;
import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.SOLR_PROPERTY_TO_REPLACE_PARAMETER;
import static org.apache.commons.collections.MapUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

public class DefaultMiraklChannelDocumentSearchResultValuePopulator
    implements Populator<DocumentData<SearchQuery, Document>, SearchResultValueData> {

  protected MiraklChannelService miraklChannelService;

  @Override
  public void populate(DocumentData<SearchQuery, Document> source, SearchResultValueData target) throws ConversionException {
    Map<String, Object> values = target.getValues();
    if (isEmpty(values)) {
      return;
    }

    MiraklChannelModel currentMiraklChannel = miraklChannelService.getCurrentMiraklChannel();
    Map<String, IndexedProperty> indexedProperties = source.getSearchQuery().getIndexedType().getIndexedProperties();

    for (Iterator<Entry<String, Object>> iterator = values.entrySet().iterator(); iterator.hasNext();) {
      Entry<String, Object> entry = iterator.next();
      IndexedProperty indexedProperty = indexedProperties.get(entry.getKey());
      if (indexedProperty == null || isEmpty(indexedProperty.getValueProviderParameters())) {
        continue;
      }

      Map<String, String> valueProviderParameters = indexedProperty.getValueProviderParameters();
      String channelCode = valueProviderParameters.get(SOLR_MIRAKL_CHANNEL_PARAMETER);
      String propertyNameToReplace = valueProviderParameters.get(SOLR_PROPERTY_TO_REPLACE_PARAMETER);
      if (isBlank(propertyNameToReplace) || isBlank(channelCode)) {
        continue;
      }

      if (currentMiraklChannel != null && currentMiraklChannel.getCode().equals(channelCode)
          && values.containsKey(propertyNameToReplace)) {
        values.put(propertyNameToReplace, entry.getValue());
      }
      iterator.remove();
    }
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

}
