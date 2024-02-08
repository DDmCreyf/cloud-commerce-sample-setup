package com.mirakl.hybris.channels.search.facades.solrfacetsearch.populators.impl;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.DocumentData;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.SOLR_MIRAKL_CHANNEL_PARAMETER;
import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.SOLR_PROPERTY_TO_REPLACE_PARAMETER;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklChannelDocumentSearchResultValuePopulatorTest {

  private static final String CHANNEL1_CODE = "channel1-code";
  private static final String CHANNEL2_CODE = "channel2-code";
  private static final String PRICE_VALUE_PROPERTY = "priceValue";
  private static final String PRICE_VALUE_CHANNEL1_PROPERTY = "priceValue-channel1";
  private static final String PRICE_VALUE_CHANNEL2_PROPERTY = "priceValue-channel2";
  private static final String UNKOWN_PROPERTY = "unkown-property";
  private static final double PRICE_VALUE = 200d;
  private static final double PRICE_VALUE_CHANNEL1 = 150d;
  private static final double PRICE_VALUE_CHANNEL2 = 170d;

  @InjectMocks
  private DefaultMiraklChannelDocumentSearchResultValuePopulator populator;

  @Mock
  private MiraklChannelService miraklChannelService;
  @Mock
  private MiraklChannelModel channel1, channel2;
  @Mock
  private SearchQuery searchQuery;
  @Mock
  private IndexedType indexedType;
  @Mock
  private IndexedProperty priceValueIndexedProperty, priceValueChannel1IndexedProperty, priceValueChannel2IndexedProperty;
  private DocumentData<SearchQuery, Document> source;
  private SearchResultValueData target;
  private Map<String, Object> values;
  private Map<String, IndexedProperty> indexedProperties;

  @Before
  public void setUp() throws Exception {
    source = new DocumentData<>();
    source.setSearchQuery(searchQuery);
    target = new SearchResultValueData();
    values = new HashMap<>();
    values.put(PRICE_VALUE_PROPERTY, PRICE_VALUE);
    values.put(PRICE_VALUE_CHANNEL1_PROPERTY, PRICE_VALUE_CHANNEL1);
    values.put(PRICE_VALUE_CHANNEL2_PROPERTY, PRICE_VALUE_CHANNEL2);
    target.setValues(values);
    indexedProperties = new HashMap<>();
    indexedProperties.put(PRICE_VALUE_PROPERTY, priceValueIndexedProperty);
    indexedProperties.put(PRICE_VALUE_CHANNEL1_PROPERTY, priceValueChannel1IndexedProperty);
    indexedProperties.put(PRICE_VALUE_CHANNEL2_PROPERTY, priceValueChannel2IndexedProperty);

    when(searchQuery.getIndexedType()).thenReturn(indexedType);
    when(indexedType.getIndexedProperties()).thenReturn(indexedProperties);
    when(priceValueIndexedProperty.getValueProviderParameters()).thenReturn(null);
    when(priceValueChannel1IndexedProperty.getValueProviderParameters())
        .thenReturn(getValueProviderParameters(CHANNEL1_CODE, PRICE_VALUE_PROPERTY));
    when(priceValueChannel2IndexedProperty.getValueProviderParameters())
        .thenReturn(getValueProviderParameters(CHANNEL2_CODE, PRICE_VALUE_PROPERTY));
    when(channel1.getCode()).thenReturn(CHANNEL1_CODE);
    when(channel2.getCode()).thenReturn(CHANNEL2_CODE);
  }

  protected Map<String, String> getValueProviderParameters(String channelCode, String propertyToReplace) {
    Map<String, String> valueProviderParameters = new HashMap<>();
    valueProviderParameters.put(SOLR_MIRAKL_CHANNEL_PARAMETER, channelCode);
    valueProviderParameters.put(SOLR_PROPERTY_TO_REPLACE_PARAMETER, propertyToReplace);
    return valueProviderParameters;
  }

  @Test
  public void shouldOverrideProperty() throws Exception {
    when(miraklChannelService.getCurrentMiraklChannel()).thenReturn(channel1);

    populator.populate(source, target);

    assertThat(target.getValues().get(PRICE_VALUE_PROPERTY)).isEqualTo(PRICE_VALUE_CHANNEL1);
    assertThat(target.getValues().get(PRICE_VALUE_CHANNEL1_PROPERTY)).isNull();
    assertThat(target.getValues().get(PRICE_VALUE_CHANNEL2_PROPERTY)).isNull();
  }

  @Test
  public void shouldDoNothingWhenNoActiveChannel() throws Exception {
    when(miraklChannelService.getCurrentMiraklChannel()).thenReturn(null);

    populator.populate(source, target);

    assertThat(target.getValues().get(PRICE_VALUE_PROPERTY)).isEqualTo(PRICE_VALUE);
    assertThat(target.getValues().get(PRICE_VALUE_CHANNEL1_PROPERTY)).isNull();
    assertThat(target.getValues().get(PRICE_VALUE_CHANNEL2_PROPERTY)).isNull();
  }

  @Test
  public void shouldDoNothingWhenMissingReplacementProperty() throws Exception {
    when(miraklChannelService.getCurrentMiraklChannel()).thenReturn(channel1);
    when(priceValueChannel1IndexedProperty.getValueProviderParameters())
        .thenReturn(getValueProviderParameters(CHANNEL1_CODE, null));

    populator.populate(source, target);

    assertThat(target.getValues().get(PRICE_VALUE_PROPERTY)).isEqualTo(PRICE_VALUE);
    assertThat(target.getValues().get(PRICE_VALUE_CHANNEL1_PROPERTY)).isEqualTo(PRICE_VALUE_CHANNEL1);
    assertThat(target.getValues().get(PRICE_VALUE_CHANNEL2_PROPERTY)).isNull();
  }

  @Test
  public void shouldDoNothingWhenUnknownReplacementProperty() throws Exception {
    when(miraklChannelService.getCurrentMiraklChannel()).thenReturn(channel2);
    when(priceValueChannel2IndexedProperty.getValueProviderParameters())
        .thenReturn(getValueProviderParameters(CHANNEL2_CODE, UNKOWN_PROPERTY));

    populator.populate(source, target);

    assertThat(target.getValues().get(PRICE_VALUE_PROPERTY)).isEqualTo(PRICE_VALUE);
    assertThat(target.getValues().get(PRICE_VALUE_CHANNEL1_PROPERTY)).isNull();
    assertThat(target.getValues().get(PRICE_VALUE_CHANNEL2_PROPERTY)).isNull();
  }

}
