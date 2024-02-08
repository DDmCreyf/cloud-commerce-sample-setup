package com.mirakl.hybris.channels.search.facades.solrfacetsearch.provider.aspects;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.SOLR_MIRAKL_CHANNEL_PARAMETER;
import static java.lang.String.format;
import static org.apache.commons.collections.MapUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DefaultFieldValueProviderAspect {

  private static final Logger LOG = Logger.getLogger(DefaultFieldValueProviderAspect.class);

  protected MiraklChannelService miraklChannelService;

  public Object aroundGetFieldValues(ProceedingJoinPoint pjp, IndexConfig indexConfig, IndexedProperty indexedProperty,
      Object object) throws Throwable {
    MiraklChannelModel startingChannel = miraklChannelService.getCurrentMiraklChannel();
    boolean isMiraklChannelsEnabled = miraklChannelService.isMiraklChannelsEnabled();
    boolean channelHasChanged = false;

    try {
      if (isMiraklChannelsEnabled) {
        channelHasChanged = handleCurrentChannel(indexedProperty, pjp);
      }

      return pjp.proceed(new Object[] {indexConfig, indexedProperty, object});

    } finally {
      if (isMiraklChannelsEnabled && channelHasChanged) {
        miraklChannelService.setCurrentMiraklChannel(startingChannel);
      }
    }
  }

  protected boolean handleCurrentChannel(IndexedProperty indexedProperty, ProceedingJoinPoint pjp) {
    Map<String, String> valueProviderParameters = indexedProperty.getValueProviderParameters();
    if (isNotEmpty(valueProviderParameters)) {
      String channelCode = valueProviderParameters.get(SOLR_MIRAKL_CHANNEL_PARAMETER);
      if (isNotBlank(channelCode)) {
        return changeCurrentMiraklChannel(channelCode, pjp);
      }
    }
    return false;
  }

  protected boolean changeCurrentMiraklChannel(String channelCode, ProceedingJoinPoint pjp) {
    MiraklChannelModel channelToSet = miraklChannelService.getMiraklChannelForCode(channelCode);
    if (channelToSet == null) {
      LOG.warn(format("Unable to resolve channel [%s] for value provider [%s]", channelCode, pjp.getTarget()));
      return false;
    }
    miraklChannelService.setCurrentMiraklChannel(channelToSet);
    return true;
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

}
