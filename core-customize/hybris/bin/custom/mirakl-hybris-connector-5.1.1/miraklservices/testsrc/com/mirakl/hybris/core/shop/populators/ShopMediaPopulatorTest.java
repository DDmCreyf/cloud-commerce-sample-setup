package com.mirakl.hybris.core.shop.populators;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.shop.MiraklMediaInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.hybris.core.media.services.MiraklMediaService;
import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ShopMediaPopulatorTest {

  private static final String SHOP_ID = "buy_one_get_one";
  private static final String BANNER_MEDIA_DOWNLOAD_URL = "https://media.url.mirakl.com/huge_media_id";
  private static final String LOGO_MEDIA_DOWNLOAD_URL = "https://media.url.mirakl.com/huge_media_id2";

  @InjectMocks
  private ShopMediaPopulator testObj;

  @Mock
  private MiraklShop miraklShop;
  @Mock
  private ShopModel shopModel;
  @Mock
  private MiraklMediaInformation mediaInformation;
  @Mock
  private MediaModel bannerMedia, logoMedia;
  @Mock
  private MiraklMediaService miraklMediaService;

  @Before
  public void setUp() throws Exception {
    when(miraklShop.getId()).thenReturn(SHOP_ID);
    when(miraklShop.getMediaInformation()).thenReturn(mediaInformation);
    when(mediaInformation.getBanner()).thenReturn(BANNER_MEDIA_DOWNLOAD_URL);
    when(mediaInformation.getLogo()).thenReturn(LOGO_MEDIA_DOWNLOAD_URL);
    when(miraklMediaService.downloadMedia(anyString(), eq(BANNER_MEDIA_DOWNLOAD_URL), eq(true))).thenReturn(bannerMedia);
    when(miraklMediaService.downloadMedia(anyString(), eq(LOGO_MEDIA_DOWNLOAD_URL), eq(true))).thenReturn(logoMedia);
  }

  @Test
  public void shouldPopulateBannerAndLogo() throws Exception {
    testObj.populate(miraklShop, shopModel);

    verify(shopModel).setBanner(bannerMedia);
    verify(shopModel).setLogo(logoMedia);
  }
}
