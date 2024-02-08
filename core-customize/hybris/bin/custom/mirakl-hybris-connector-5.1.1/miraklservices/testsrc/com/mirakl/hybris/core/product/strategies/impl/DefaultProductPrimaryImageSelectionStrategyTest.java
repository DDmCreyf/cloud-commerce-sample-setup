package com.mirakl.hybris.core.product.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductPrimaryImageSelectionStrategyTest {

  private static final String MEDIA_FORMAT_1_QUALIFIER = "1200W_1200H";
  private static final String MEDIA_FORMAT_2_QUALIFIER = "550W_550H";
  private static final String MEDIA_FORMAT_3_QUALIFIER = "96W_96H";

  @Mock
  private ProductModel product;
  @Mock
  private MediaContainerModel container1, container2, container3;
  @Mock
  private MediaModel media1Format1, media1Format2, media2Format1, media2Format2, media3Format1, media3Format2, media3Format3;
  @Mock
  private MediaFormatModel mediaFormat1, mediaFormat2, mediaFormat3;

  @InjectMocks
  @Spy
  private DefaultProductPrimaryImageSelectionStrategy testObj;

  @Before
  public void setUp() throws Exception {
    when(product.getGalleryImages()).thenReturn(asList(container1, container2, container3));
    when(container1.getMedias()).thenReturn(asList(media1Format1, media1Format2));
    when(media1Format1.getMediaFormat()).thenReturn(mediaFormat1);
    when(media1Format2.getMediaFormat()).thenReturn(mediaFormat2);
    when(mediaFormat1.getQualifier()).thenReturn(MEDIA_FORMAT_1_QUALIFIER);
    when(mediaFormat2.getQualifier()).thenReturn(MEDIA_FORMAT_2_QUALIFIER);
  }

  @Test
  public void getPrimaryProductImage() {
    testObj.setPreferredMediaFormatQualifiers(asList(MEDIA_FORMAT_2_QUALIFIER, //
        MEDIA_FORMAT_1_QUALIFIER, //
        MEDIA_FORMAT_3_QUALIFIER));
    MediaModel output = testObj.getPrimaryProductImage(product);

    assertThat(output).isEqualTo(media1Format2);
  }

}
