package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.MEDIA_URL_SECURE;
import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.MEDIA_URL;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.product.strategies.ProductPrimaryImageSelectionStrategy;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ProductExportMediaUrlPopulator extends AbstractProductExportWithFallbackPopulator {

  protected ProductPrimaryImageSelectionStrategy primaryImageSelectionStrategy;
  protected ConfigurationService configurationService;

  @Override
  protected void populateAttributesIfNotPresent(ProductModel source, Map<String, String> target) throws ConversionException {
    MediaModel picture = primaryImageSelectionStrategy.getPrimaryProductImage(source);
    if (picture != null && target.get(MEDIA_URL.getCode()) == null) {
      target.put(MEDIA_URL.getCode(), siteBaseUrlResolutionService.getMediaUrlForSite(baseSiteService.getCurrentBaseSite(),
          isSecureMediaUrl(), picture.getURL()));
    }
  }

  public boolean isSecureMediaUrl() {
    return configurationService.getConfiguration().getBoolean(MEDIA_URL_SECURE, true);
  }

  @Required
  public void setPrimaryImageSelectionStrategy(ProductPrimaryImageSelectionStrategy primaryImageSelectionStrategy) {
    this.primaryImageSelectionStrategy = primaryImageSelectionStrategy;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
