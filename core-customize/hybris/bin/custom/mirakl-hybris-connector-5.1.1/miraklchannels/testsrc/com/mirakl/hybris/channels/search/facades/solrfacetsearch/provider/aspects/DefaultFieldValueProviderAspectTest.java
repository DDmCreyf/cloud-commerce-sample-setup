package com.mirakl.hybris.channels.search.facades.solrfacetsearch.provider.aspects;

import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.SOLR_MIRAKL_CHANNEL_PARAMETER;
import static java.util.Collections.singletonMap;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFieldValueProviderAspectTest {

  private static final String INDEXED_PROPERTY_CHANNEL_CODE = "indexed-property-channel-code";

  @InjectMocks
  private DefaultFieldValueProviderAspect aspect;
  @Mock
  private MiraklChannelService miraklChannelService;
  @Mock
  private MiraklChannelModel currentMiraklChannel, indexedPropertyMiraklChannel;
  @Mock
  private ProceedingJoinPoint pjp;
  @Mock
  private IndexConfig indexConfig;
  @Mock
  private IndexedProperty indexedProperty;
  @Mock
  private Object object;


  @Before
  public void setUp() throws Exception {
    when(miraklChannelService.isMiraklChannelsEnabled()).thenReturn(true);
  }

  @Test
  public void shouldChangeCurrentMiraklChannelIfProvided() throws Throwable {
    when(miraklChannelService.getCurrentMiraklChannel()) //
        .thenReturn(currentMiraklChannel) //
        .thenReturn(indexedPropertyMiraklChannel);
    when(indexedProperty.getValueProviderParameters())
        .thenReturn(singletonMap(SOLR_MIRAKL_CHANNEL_PARAMETER, INDEXED_PROPERTY_CHANNEL_CODE));
    when(miraklChannelService.getMiraklChannelForCode(INDEXED_PROPERTY_CHANNEL_CODE)).thenReturn(indexedPropertyMiraklChannel);

    aspect.aroundGetFieldValues(pjp, indexConfig, indexedProperty, object);

    verify(miraklChannelService).setCurrentMiraklChannel(indexedPropertyMiraklChannel);
    verify(miraklChannelService).setCurrentMiraklChannel(currentMiraklChannel);
  }


  @Test
  public void shouldDoNothingIfNoActiveChannel() throws Throwable {
    when(miraklChannelService.getCurrentMiraklChannel()) //
        .thenReturn(currentMiraklChannel);
    when(indexedProperty.getValueProviderParameters()).thenReturn(null);

    aspect.aroundGetFieldValues(pjp, indexConfig, indexedProperty, object);

    verify(miraklChannelService, never()).setCurrentMiraklChannel(any(MiraklChannelModel.class));
  }

  @Test
  public void shouldDoNothingIfChannelsAreNotEnabled() throws Throwable {
    when(miraklChannelService.isMiraklChannelsEnabled()).thenReturn(false);

    aspect.aroundGetFieldValues(pjp, indexConfig, indexedProperty, object);

    verify(miraklChannelService, never()).setCurrentMiraklChannel(any(MiraklChannelModel.class));
  }

  @Test
  public void shouldDoNothingIfUnkownChannel() throws Throwable {
    when(miraklChannelService.getCurrentMiraklChannel()).thenReturn(currentMiraklChannel);
    when(indexedProperty.getValueProviderParameters())
        .thenReturn(singletonMap(SOLR_MIRAKL_CHANNEL_PARAMETER, INDEXED_PROPERTY_CHANNEL_CODE));
    when(miraklChannelService.getMiraklChannelForCode(INDEXED_PROPERTY_CHANNEL_CODE)).thenReturn(null);

    aspect.aroundGetFieldValues(pjp, indexConfig, indexedProperty, object);

    verify(miraklChannelService, never()).setCurrentMiraklChannel(any(MiraklChannelModel.class));
  }
}
