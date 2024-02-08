package com.mirakl.hybris.channels.shop.populators;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ShopChannelsPopulatorTest {
  private static final String EXISTING_CHANNEL_CODE = "existing-channel-code";
  private static final String NEW_CHANNEL_CODE = "new-channel-code";

  @InjectMocks
  private ShopChannelsPopulator populator;

  @Mock
  private MiraklShop miraklShop;
  @Mock
  private MiraklChannelService miraklChannelService;
  @Mock
  private MiraklChannelModel createdMiraklChannel, resolvedMiraklChannel;


  @Before
  public void setUp() {
    when(miraklChannelService.createMiraklChannelSilently(NEW_CHANNEL_CODE, NEW_CHANNEL_CODE)).thenReturn(createdMiraklChannel);
    when(miraklChannelService.getMiraklChannelForCode(EXISTING_CHANNEL_CODE)).thenReturn(resolvedMiraklChannel);
    when(createdMiraklChannel.getCode()).thenReturn(NEW_CHANNEL_CODE);
    when(resolvedMiraklChannel.getCode()).thenReturn(EXISTING_CHANNEL_CODE);
  }

  @Test
  public void populateChannelsWhenNew() {
    when(miraklShop.getChannels()).thenReturn(asList(NEW_CHANNEL_CODE));

    ShopModel shopModel = new ShopModel();
    populator.populate(miraklShop, shopModel);

    verify(miraklChannelService).createMiraklChannelSilently(NEW_CHANNEL_CODE, NEW_CHANNEL_CODE);
    assertThat(shopModel.getChannels()).hasSize(1);
    assertThat(shopModel.getChannels().iterator().next().getCode()).isEqualTo(NEW_CHANNEL_CODE);
  }

  @Test
  public void populateChannelsWhenExisting() {
    when(miraklShop.getChannels()).thenReturn(asList(EXISTING_CHANNEL_CODE));

    ShopModel shopModel = new ShopModel();
    populator.populate(miraklShop, shopModel);

    verify(miraklChannelService, never()).createMiraklChannelSilently(EXISTING_CHANNEL_CODE, EXISTING_CHANNEL_CODE);
    assertThat(shopModel.getChannels()).hasSize(1);
    assertThat(shopModel.getChannels().iterator().next().getCode()).isEqualTo(EXISTING_CHANNEL_CODE);
  }

}
