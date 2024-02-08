package com.mirakl.hybris.core.media.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklMediaServiceTest {

  private static final String MEDIA_ID = "mp-promo-PERCENTAGE_OFF_2018-02-22-10-48";
  private static final String MEDIA_DOWNLOAD_URL =
      "https://yourmiraklenv.mirakl.com/mmp/media/promotion-media/2b2d551c-cc28-4de8-a008-ad52184b35aa";

  @Spy
  @InjectMocks
  private DefaultMiraklMediaService testObj;

  @Mock
  private ModelService modelService;
  @Mock
  private MediaService mediaService;
  @Mock
  private CatalogUnawareMediaModel existingMedia, newMedia;

  @Before
  public void setUp() throws Exception {
    doNothing().when(testObj).downloadMediaFromURL(any(MediaModel.class), any(URL.class));
    when(modelService.create(CatalogUnawareMediaModel.class)).thenReturn(newMedia);
  }

  @Test
  public void shouldNotDownloadExistingMedia() throws Exception {
    when(mediaService.getMedia(MEDIA_ID)).thenReturn(existingMedia);

    MediaModel output = testObj.downloadMedia(MEDIA_ID, MEDIA_DOWNLOAD_URL);

    verify(testObj, never()).downloadMediaFromURL(any(CatalogUnawareMediaModel.class), any(URL.class));
    assertThat(output).isEqualTo(existingMedia);
  }

  @Test
  public void shouldDownloadExistingMedia() throws Exception {
    when(mediaService.getMedia(MEDIA_ID)).thenReturn(existingMedia);

    MediaModel output = testObj.downloadMedia(MEDIA_ID, MEDIA_DOWNLOAD_URL, true);

    verify(testObj).downloadMediaFromURL(eq(existingMedia), eq(new URL(MEDIA_DOWNLOAD_URL)));
    assertThat(output).isEqualTo(existingMedia);
  }

  @Test
  public void shouldDownloadNewMedia() throws Exception {
    when(mediaService.getMedia(MEDIA_ID)).thenThrow(new UnknownIdentifierException(""));

    MediaModel output = testObj.downloadMedia(MEDIA_ID, MEDIA_DOWNLOAD_URL);

    verify(testObj).downloadMediaFromURL(eq(newMedia), eq(new URL(MEDIA_DOWNLOAD_URL)));
    assertThat(output).isEqualTo(newMedia);
  }

}
