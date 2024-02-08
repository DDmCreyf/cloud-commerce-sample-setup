package com.mirakl.hybris.channels.channel.strategies.impl;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.channel.strategies.MiraklChannelResolvingStrategy;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.MIRAKL_CHANNELS_PROPERTY_PREFIX;
import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.MIRAKL_CHANNELS_PROPERTY_SEPARATOR;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DefaultMiraklChannelResolvingStrategy implements MiraklChannelResolvingStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultMiraklChannelResolvingStrategy.class);

  protected BaseSiteService baseSiteService;
  protected UserService userService;
  protected MiraklChannelService miraklChannelService;
  protected ConfigurationService configurationService;

  @Override
  public MiraklChannelModel resolveCurrentChannel() {
    if (!miraklChannelService.isMiraklChannelsEnabled()) {
      return null;
    }
    BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
    if (currentBaseSite == null) {
      return null;
    }

    MiraklChannelModel userPriceGroupChannel = getUserPriceGroupMiraklChannel(currentBaseSite);
    if (userPriceGroupChannel != null) {
      return userPriceGroupChannel;
    }

    return getDefaultMiraklChannel(currentBaseSite);
  }

  protected MiraklChannelModel getUserPriceGroupMiraklChannel(BaseSiteModel currentBaseSite) {
    UserModel currentUser = userService.getCurrentUser();
    if (currentUser != null) {
      String miraklChannelCode = getMiraklChannelCode(currentBaseSite, currentUser);
      if (isNotBlank(miraklChannelCode)) {
        return loadMiraklChannel(miraklChannelCode);
      }
    }
    return null;
  }

  protected MiraklChannelModel getDefaultMiraklChannel(BaseSiteModel currentBaseSite) {
    String miraklChannelCode = getMiraklChannelCode(currentBaseSite);
    if (isNotBlank(miraklChannelCode)) {
      return loadMiraklChannel(miraklChannelCode);
    }
    return null;
  }

  protected String getMiraklChannelCode(BaseSiteModel currentBaseSite) {
    return getMiraklChannelCode(currentBaseSite, null);
  }

  protected String getMiraklChannelCode(BaseSiteModel currentBaseSite, UserModel currentUser) {
    StringBuilder miraklChannelPropertyKey = new StringBuilder(MIRAKL_CHANNELS_PROPERTY_PREFIX).append(currentBaseSite.getUid());
    if (currentUser != null && currentUser.getEurope1PriceFactory_UPG() != null) {
      miraklChannelPropertyKey.append(MIRAKL_CHANNELS_PROPERTY_SEPARATOR)
          .append(currentUser.getEurope1PriceFactory_UPG().getCode());
    }
    return configurationService.getConfiguration().getString(miraklChannelPropertyKey.toString());
  }

  protected MiraklChannelModel loadMiraklChannel(String miraklChannelId) {
    MiraklChannelModel miraklChannel = miraklChannelService.getMiraklChannelForCode(miraklChannelId);
    if (miraklChannel == null) {
      LOG.warn(format("Resolved current Mirakl channel to [%s] but was unable to find it in the system.", miraklChannelId));
      return null;
    }
    return miraklChannel;
  }

  @Required
  public void setBaseSiteService(BaseSiteService baseSiteService) {
    this.baseSiteService = baseSiteService;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }
}
