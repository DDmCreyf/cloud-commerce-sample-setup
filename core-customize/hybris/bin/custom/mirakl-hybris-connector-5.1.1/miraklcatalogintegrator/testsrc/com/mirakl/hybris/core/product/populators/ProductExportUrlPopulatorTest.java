package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.WEBSITE_URL_SECURE;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.MiraklProductExportHeader;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.variants.model.VariantProductModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ProductExportUrlPopulatorTest {

  private static final String RELATIVE_URL = "relative_url";
  private static final String ABSOLUTE_URL = "url";
  private static final String RELATIVE_VARIANT_URL = "relative_variant_url";
  private static final String ABSOLUTE_VARIANT_URL = "variant_url";
  private static final boolean IS_SECURE = true;

  @InjectMocks
  private ProductExportUrlPopulator populator;

  @Mock
  protected SiteBaseUrlResolutionService siteBaseUrlResolutionService;
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
  private UrlResolver<ProductModel> productModelUrlResolver;
  @Mock
  private BaseSiteModel baseSite;


  @Before
  public void setUp() throws Exception {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getBoolean(eq(WEBSITE_URL_SECURE), anyBoolean())).thenReturn(IS_SECURE);
    when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
    when(productModelUrlResolver.resolve(product)).thenReturn(RELATIVE_URL);
    when(siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, IS_SECURE, RELATIVE_URL)).thenReturn(ABSOLUTE_URL);
    when(productModelUrlResolver.resolve(variantProduct)).thenReturn(RELATIVE_VARIANT_URL);
    when(siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, IS_SECURE, RELATIVE_VARIANT_URL))
        .thenReturn(ABSOLUTE_VARIANT_URL);
  }

  @Test
  public void shouldExportUrlForProductsWithNoVariants() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(product, result);

    assertThat(result.get(MiraklProductExportHeader.PRODUCT_URL.getCode())).isEqualTo(ABSOLUTE_URL);
  }

  @Test
  public void shouldNotFallbackToBaseProduct() {
    HashMap<String, String> result = new HashMap<>();
    populator.populate(variantProduct, result);

    assertThat(result.get(MiraklProductExportHeader.PRODUCT_URL.getCode())).isEqualTo(ABSOLUTE_VARIANT_URL);
  }

}
