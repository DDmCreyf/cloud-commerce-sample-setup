package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.WEBSITE_URL_SECURE;
import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.PRODUCT_URL;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.site.BaseSiteService;

public class ProductExportUrlPopulator implements Populator<ProductModel, Map<String, String>> {

  protected UrlResolver<ProductModel> productModelUrlResolver;
  protected SiteBaseUrlResolutionService siteBaseUrlResolutionService;
  protected BaseSiteService baseSiteService;
  protected ConfigurationService configurationService;

  @Override
  public void populate(ProductModel source, Map<String, String> target) throws ConversionException {
    target.put(PRODUCT_URL.getCode(), siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSiteService.getCurrentBaseSite(),
        isSecureUrl(), productModelUrlResolver.resolve(source)));
  }

  protected boolean isSecureUrl() {
    return configurationService.getConfiguration().getBoolean(WEBSITE_URL_SECURE, true);
  }

  @Required
  public void setProductModelUrlResolver(UrlResolver<ProductModel> productModelUrlResolver) {
    this.productModelUrlResolver = productModelUrlResolver;
  }

  @Required
  public void setBaseSiteService(BaseSiteService baseSiteService) {
    this.baseSiteService = baseSiteService;
  }

  @Required
  public void setSiteBaseUrlResolutionService(SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
    this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
