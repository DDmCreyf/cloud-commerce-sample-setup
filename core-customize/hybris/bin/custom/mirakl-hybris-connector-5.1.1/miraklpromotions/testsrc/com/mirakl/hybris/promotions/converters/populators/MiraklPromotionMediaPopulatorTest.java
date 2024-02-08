package com.mirakl.hybris.promotions.converters.populators;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.Collections;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.promotion.MiraklPromotionMedia;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotion;
import com.mirakl.hybris.core.media.services.MiraklMediaService;
import com.mirakl.hybris.core.util.strategies.LocaleMappingStrategy;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklPromotionMediaPopulatorTest {

  private static final String MEDIA_ID = "buy_one_get_one";
  private static final String MEDIA_1_URL = "https://media.url.mirakl.com/huge_media_id_1";
  private static final String MEDIA_2_URL = "https://media.url.mirakl.com/huge_media_id_2";
  private static final String MEDIA_3_URL = "https://media.url.mirakl.com/huge_media_id_3";
  private static final String HYBRIS_MEDIA_1_URL = "/stuff/media?context=huge_string_1";
  private static final String HYBRIS_MEDIA_2_URL = "/stuff/media?context=huge_string_2";
  private static final String HYBRIS_MEDIA_3_URL = "/stuff/media?context=huge_string_3";

  @InjectMocks
  private MiraklPromotionMediaPopulator testObj;
  @Mock
  private MiraklPromotion miraklPromotionDto;
  @Mock
  private MiraklPromotionMedia miraklPromotionMedia, miraklPromotionMedia2, miraklPromotionMedia3;
  @Mock
  private MiraklPromotionModel miraklPromotionModel;
  @Mock
  private LocaleMappingStrategy localeMappingStrategy;
  @Mock
  private MiraklMediaService miraklMediaService;
  @Mock
  private MediaModel mediaModel1, mediaModel2, mediaModel3;

  @Before
  public void setUp() throws Exception {
    when(miraklPromotionDto.getMedias()).thenReturn(Collections.singletonList(miraklPromotionMedia));
    when(miraklPromotionDto.getInternalId()).thenReturn(MEDIA_ID);
    when(miraklPromotionMedia.getLocale()).thenReturn(Locale.ENGLISH);
    when(miraklPromotionMedia.getUrl()).thenReturn(URI.create(MEDIA_1_URL));
    when(miraklPromotionMedia2.getLocale()).thenReturn(Locale.ENGLISH);
    when(miraklPromotionMedia2.getUrl()).thenReturn(URI.create(MEDIA_2_URL));
    when(miraklPromotionMedia3.getLocale()).thenReturn(Locale.ENGLISH);
    when(miraklPromotionMedia3.getUrl()).thenReturn(URI.create(MEDIA_3_URL));
    when(localeMappingStrategy.mapToHybrisLocale(Locale.ENGLISH)).thenReturn(Locale.ENGLISH);
    when(miraklMediaService.downloadMedia(anyString(), eq(MEDIA_1_URL))).thenReturn(mediaModel1);
    when(miraklMediaService.downloadMedia(anyString(), eq(MEDIA_2_URL))).thenReturn(mediaModel2);
    when(miraklMediaService.downloadMedia(anyString(), eq(MEDIA_3_URL))).thenReturn(mediaModel3);
    when(mediaModel1.getURL()).thenReturn(HYBRIS_MEDIA_1_URL);
    when(mediaModel2.getURL()).thenReturn(HYBRIS_MEDIA_2_URL);
    when(mediaModel3.getURL()).thenReturn(HYBRIS_MEDIA_3_URL);
  }

  @Test
  public void shouldPopulateMedia() throws Exception {
    testObj.populate(miraklPromotionDto, miraklPromotionModel);

    verify(miraklPromotionModel).setMediaUrl(HYBRIS_MEDIA_1_URL, Locale.ENGLISH);
  }

  @Test
  public void shouldPopulateMedias() throws Exception {
    when(miraklPromotionDto.getMedias()).thenReturn(asList(miraklPromotionMedia, miraklPromotionMedia2, miraklPromotionMedia3));

    testObj.populate(miraklPromotionDto, miraklPromotionModel);

    verify(miraklPromotionModel).setMediaUrl(HYBRIS_MEDIA_1_URL, Locale.ENGLISH);
    verify(miraklPromotionModel).setMediaUrl(HYBRIS_MEDIA_2_URL, Locale.ENGLISH);
    verify(miraklPromotionModel).setMediaUrl(HYBRIS_MEDIA_3_URL, Locale.ENGLISH);
  }

}
