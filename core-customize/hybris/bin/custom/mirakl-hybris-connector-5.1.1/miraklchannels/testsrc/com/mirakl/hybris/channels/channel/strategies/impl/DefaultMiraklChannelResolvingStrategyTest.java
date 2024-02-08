package com.mirakl.hybris.channels.channel.strategies.impl;

import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.MIRAKL_CHANNELS_PROPERTY_PREFIX;
import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.MIRAKL_CHANNELS_PROPERTY_SEPARATOR;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklChannelResolvingStrategyTest {
  private static final String USER_PRICE_GROUP_CHANNEL_CODE = "user-price-group-channel-code";
  private static final String DEFAULT_CHANNEL_CODE = "default-channel-code";
  private static final String BASE_SITE_UID = "base-site-uid";
  private static final String USER_PRICE_GROUP_CODE = "user-price-group-code";

  @InjectMocks
  private DefaultMiraklChannelResolvingStrategy channelResolvingStrategy;

  @Mock
  private BaseSiteService baseSiteService;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private MiraklChannelService miraklChannelService;
  @Mock
  private UserService userService;
  @Mock
  private Configuration configuration;
  @Mock
  private UserModel currentUser;
  @Mock
  private BaseSiteModel baseSite;
  @Mock
  private UserPriceGroup userPriceGroup;
  @Mock
  private MiraklChannelModel userPriceGroupMiraklChannel, defaultMiraklChannel;

  @Before
  public void setUp() throws Exception {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration
        .getString(MIRAKL_CHANNELS_PROPERTY_PREFIX + BASE_SITE_UID + MIRAKL_CHANNELS_PROPERTY_SEPARATOR + USER_PRICE_GROUP_CODE))
            .thenReturn(USER_PRICE_GROUP_CHANNEL_CODE);
    when(configuration.getString(MIRAKL_CHANNELS_PROPERTY_PREFIX + BASE_SITE_UID)).thenReturn(DEFAULT_CHANNEL_CODE);
    when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
    when(baseSite.getUid()).thenReturn(BASE_SITE_UID);
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(userPriceGroup.getCode()).thenReturn(USER_PRICE_GROUP_CODE);
    when(miraklChannelService.getMiraklChannelForCode(USER_PRICE_GROUP_CHANNEL_CODE)).thenReturn(userPriceGroupMiraklChannel);
    when(miraklChannelService.getMiraklChannelForCode(DEFAULT_CHANNEL_CODE)).thenReturn(defaultMiraklChannel);
    when(miraklChannelService.isMiraklChannelsEnabled()).thenReturn(true);
  }

  @Test
  public void shouldResolveUserGroupChannel() throws Exception {
    when(currentUser.getEurope1PriceFactory_UPG()).thenReturn(userPriceGroup);

    MiraklChannelModel currentChannel = channelResolvingStrategy.resolveCurrentChannel();

    assertThat(currentChannel).isEqualTo(userPriceGroupMiraklChannel);
  }

  @Test
  public void shouldResolveDefaultChannel() throws Exception {
    when(currentUser.getEurope1PriceFactory_UPG()).thenReturn(null);

    MiraklChannelModel currentChannel = channelResolvingStrategy.resolveCurrentChannel();

    assertThat(currentChannel).isEqualTo(defaultMiraklChannel);
  }

  @Test
  public void shouldHandleAbsentChannelMapping() throws Exception {
    when(currentUser.getEurope1PriceFactory_UPG()).thenReturn(null);
    when(configuration.getString(MIRAKL_CHANNELS_PROPERTY_PREFIX + BASE_SITE_UID)).thenReturn(null);

    MiraklChannelModel currentChannel = channelResolvingStrategy.resolveCurrentChannel();

    assertThat(currentChannel).isNull();
  }

  @Test
  public void shouldHandleAbsentCurrentUser() throws Exception {
    when(userService.getCurrentUser()).thenReturn(null);

    MiraklChannelModel currentChannel = channelResolvingStrategy.resolveCurrentChannel();

    assertThat(currentChannel).isEqualTo(defaultMiraklChannel);
  }

  @Test
  public void shouldHandleAbsentCurrentSite() throws Exception {
    when(baseSiteService.getCurrentBaseSite()).thenReturn(null);

    MiraklChannelModel currentChannel = channelResolvingStrategy.resolveCurrentChannel();

    assertThat(currentChannel).isNull();
  }

  @Test
  public void shouldHandleAbsentCurrentSiteAndUser() throws Exception {
    when(baseSiteService.getCurrentBaseSite()).thenReturn(null);

    MiraklChannelModel currentChannel = channelResolvingStrategy.resolveCurrentChannel();

    assertThat(currentChannel).isNull();
  }

  @Test
  public void shouldNotResolveChannelIfFeatureDisabled() throws Exception {
    when(miraklChannelService.isMiraklChannelsEnabled()).thenReturn(false);

    MiraklChannelModel currentChannel = channelResolvingStrategy.resolveCurrentChannel();

    assertThat(currentChannel).isNull();
  }

}
