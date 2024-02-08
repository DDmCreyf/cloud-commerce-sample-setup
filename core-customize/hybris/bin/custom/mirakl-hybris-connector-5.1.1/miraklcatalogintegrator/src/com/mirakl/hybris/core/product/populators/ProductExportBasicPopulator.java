package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.DESCRIPTION_MAXLENGTH_CONFIG_KEY;
import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.PRODUCT_DESCRIPTION;
import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.PRODUCT_SKU;
import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.PRODUCT_TITLE;
import static org.apache.commons.lang3.StringUtils.left;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ProductExportBasicPopulator extends AbstractProductExportWithFallbackPopulator {

  protected ConfigurationService configurationService;

  @Override
  protected void populateAttributesIfNotPresent(ProductModel source, Map<String, String> target) throws ConversionException {
    if (target.get(PRODUCT_SKU.getCode()) == null) {
      target.put(PRODUCT_SKU.getCode(), source.getCode());
    }
    if (target.get(PRODUCT_DESCRIPTION.getCode()) == null) {
      target.put(PRODUCT_DESCRIPTION.getCode(),
          left(source.getDescription(), configurationService.getConfiguration().getInt(DESCRIPTION_MAXLENGTH_CONFIG_KEY)));
    }
    if (target.get(PRODUCT_TITLE.getCode()) == null) {
      target.put(PRODUCT_TITLE.getCode(), source.getName());
    }
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
