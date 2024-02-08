package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.strategies.ProductPrimaryImageSelectionStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMcmGalleryImagesAttributeHandlerTest {

  private static final String FULL_MEDIA_URL = "full-media-url";
  private static final String IMAGE_URL = "image-url";
  private static final PK BASE_SITE_PK = PK.fromLong(2L);
  private static final boolean IS_SECURE_MEDIA_URL = true;

  @Mock
  private ModelService modelService;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Mock
  private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
  @Mock
  private ProductDataSheetExportContextData exportContext;
  @Mock
  private MiraklCoreAttributeModel coreAttribute;
  @Mock
  private MediaModel media;
  @Mock
  private BaseSiteModel baseSite;
  @Mock
  private VariantProductModel variantProduct;
  @Mock
  private ProductPrimaryImageSelectionStrategy primaryImageSelectionStrategy;

  private ProductModel product;

  @Before
  public void setUp() throws Exception {
    product = new ProductModel();
    when(exportContext.getBaseSite()).thenReturn(BASE_SITE_PK);
    when(modelService.get(BASE_SITE_PK)).thenReturn(baseSite);
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getBoolean(eq(MiraklservicesConstants.MEDIA_URL_SECURE), anyBoolean())).thenReturn(IS_SECURE_MEDIA_URL);
  }

  @InjectMocks
  private DefaultMcmGalleryImagesAttributeHandler handler;

  @Test
  public void shouldGetMediaUrlForProduct() throws Exception {
    when(primaryImageSelectionStrategy.getPrimaryProductImage(product)).thenReturn(media);
    when(media.getURL()).thenReturn(IMAGE_URL);
    when(siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, IS_SECURE_MEDIA_URL, IMAGE_URL)).thenReturn(FULL_MEDIA_URL);

    String mediaUrl = handler.getValue(product, coreAttribute, exportContext);

    assertThat(mediaUrl).isEqualTo(FULL_MEDIA_URL);
  }

  @Test
  public void shouldGetMediaUrlForVariantProduct() throws Exception {
    when(primaryImageSelectionStrategy.getPrimaryProductImage(product)).thenReturn(media);
    when(variantProduct.getBaseProduct()).thenReturn(product);
    when(media.getURL()).thenReturn(IMAGE_URL);
    when(siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, IS_SECURE_MEDIA_URL, IMAGE_URL)).thenReturn(FULL_MEDIA_URL);

    String mediaUrl = handler.getValue(variantProduct, coreAttribute, exportContext);

    assertThat(mediaUrl).isEqualTo(FULL_MEDIA_URL);
  }

  @Test
  public void shouldReturnNullForNoPicture() throws Exception {
    when(primaryImageSelectionStrategy.getPrimaryProductImage(product)).thenReturn(null);

    String mediaUrl = handler.getValue(product, coreAttribute, exportContext);

    assertThat(mediaUrl).isEqualTo(null);
  }

}
