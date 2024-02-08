package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.MEDIA_URL_SECURE;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.strategies.ProductPrimaryImageSelectionStrategy;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.variants.model.VariantProductModel;

public class DefaultMcmGalleryImagesAttributeHandler extends AbstractMcmCoreAttributeHandler<MiraklCoreAttributeModel> {

  protected SiteBaseUrlResolutionService siteBaseUrlResolutionService;
  protected ConfigurationService configurationService;
  protected ProductPrimaryImageSelectionStrategy primaryImageSelectionStrategy;

  @Override
  public String getValue(ProductModel product, MiraklCoreAttributeModel coreAttribute,
      ProductDataSheetExportContextData context) {

    ProductModel currentProduct = product;
    MediaModel picture = getPrimaryProductImage(currentProduct);
    while (picture == null && currentProduct instanceof VariantProductModel) {
      currentProduct = ((VariantProductModel) currentProduct).getBaseProduct();
      picture = getPrimaryProductImage(currentProduct);
    }

    return picture != null
        ? siteBaseUrlResolutionService.getMediaUrlForSite(modelService.get(context.getBaseSite()), isSecureMediaUrl(),
            picture.getURL())
        : null;
  }

  protected MediaModel getPrimaryProductImage(ProductModel currentProduct) {
    return primaryImageSelectionStrategy.getPrimaryProductImage(currentProduct);
  }

  @Override
  public String getValue(ProductModel product, MiraklCoreAttributeModel coreAttribute, Locale locale,
      ProductDataSheetExportContextData context) {
    return getValue(product, coreAttribute, context);
  }

  public boolean isSecureMediaUrl() {
    return configurationService.getConfiguration().getBoolean(MEDIA_URL_SECURE, true);
  }

  @Required
  public void setSiteBaseUrlResolutionService(SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
    this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setPrimaryImageSelectionStrategy(ProductPrimaryImageSelectionStrategy primaryImageSelectionStrategy) {
    this.primaryImageSelectionStrategy = primaryImageSelectionStrategy;
  }
}
