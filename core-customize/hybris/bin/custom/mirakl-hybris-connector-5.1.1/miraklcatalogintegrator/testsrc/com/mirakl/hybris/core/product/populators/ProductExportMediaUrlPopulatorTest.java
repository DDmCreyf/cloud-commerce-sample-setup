package com.mirakl.hybris.core.product.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import com.mirakl.hybris.core.product.strategies.ProductPrimaryImageSelectionStrategy;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.enums.MiraklProductExportHeader;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.variants.model.VariantProductModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ProductExportMediaUrlPopulatorTest {

  private static final boolean SECURE = true;
  private static final String RELATIVE_PICTURE_URL = "relative_pricture_url";
  private static final String ABSOLUTE_PICTURE_URL = "picture_url";
  private static final String RELATIVE_VARIANT_PICTURE_URL = "relative_variant_pricture_url";
  private static final String ABSOLUTE_VARIANT_PICTURE_URL = "variant_picture_url";

  @InjectMocks
  private ProductExportMediaUrlPopulator populator;

  @Mock
  private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
  @Mock
  private BaseSiteService baseSiteService;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Mock
  private ProductModel product;
  @Mock
  private VariantProductModel variantProduct;
  @Mock
  private MediaModel productPicture;
  @Mock
  private MediaModel variantProductPicture;
  @Mock
  private BaseSiteModel baseSite;
  @Mock
  private ProductPrimaryImageSelectionStrategy primaryImageSelectionStrategy;

  @Before
  public void setUp() throws Exception {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getBoolean(eq(MiraklservicesConstants.MEDIA_URL_SECURE), anyBoolean())).thenReturn(SECURE);

    when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
    when(variantProduct.getBaseProduct()).thenReturn(product);

    when(primaryImageSelectionStrategy.getPrimaryProductImage(product)).thenReturn(productPicture);
    when(productPicture.getURL()).thenReturn(RELATIVE_PICTURE_URL);
    when(siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, SECURE, RELATIVE_PICTURE_URL))
        .thenReturn(ABSOLUTE_PICTURE_URL);

    when(primaryImageSelectionStrategy.getPrimaryProductImage(variantProduct)).thenReturn(variantProductPicture);
    when(variantProductPicture.getURL()).thenReturn(RELATIVE_VARIANT_PICTURE_URL);
    when(siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, SECURE, RELATIVE_VARIANT_PICTURE_URL))
        .thenReturn(ABSOLUTE_VARIANT_PICTURE_URL);
  }

  @Test
  public void shouldExportMediaUrlForProductsWithNoVariants() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(result.get(MiraklProductExportHeader.MEDIA_URL.getCode())).isEqualTo(ABSOLUTE_PICTURE_URL);
  }

  @Test
  public void shouldNotFallbackToBaseProductIfMediaUrlIsPresent() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.MEDIA_URL.getCode())).isEqualTo(ABSOLUTE_VARIANT_PICTURE_URL);
  }

  @Test
  public void shouldFallbackToBaseProductIfMediaUrlIsNotPresent() {
    when(variantProductPicture.getURL()).thenReturn(null);

    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.MEDIA_URL.getCode())).isEqualTo(ABSOLUTE_PICTURE_URL);
  }

}
