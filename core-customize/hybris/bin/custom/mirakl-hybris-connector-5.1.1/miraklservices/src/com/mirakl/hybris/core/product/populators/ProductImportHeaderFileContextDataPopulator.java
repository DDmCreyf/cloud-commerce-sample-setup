package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.PRODUCTS_IMPORT_LOCALIZED_ATTRIBUTE_REGEX;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.HeaderInfoData;
import com.mirakl.hybris.beans.ProductImportFileContextData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ProductImportHeaderFileContextDataPopulator implements Populator<String[], ProductImportFileContextData> {

  protected ConfigurationService configurationService;

  @Override
  public void populate(String[] source, ProductImportFileContextData target) throws ConversionException {
    String localizedAttributeRegex = configurationService.getConfiguration().getString(PRODUCTS_IMPORT_LOCALIZED_ATTRIBUTE_REGEX);
    target.setHeaderInfos(getHeaderInfos(source, localizedAttributeRegex));
  }

  protected Map<String, HeaderInfoData> getHeaderInfos(String[] source, String localizedAttributeRegex) {
    Map<String, HeaderInfoData> headerInfos = new LinkedHashMap<>();
    Pattern pattern = Pattern.compile(localizedAttributeRegex);

    for (String header : source) {
      if (isEmpty(header)) {
        continue;
      }
      HeaderInfoData headerInfo = new HeaderInfoData();
      Matcher matcher = pattern.matcher(header);
      if (matcher.find()) {
        headerInfo.setAttribute(matcher.group(1));
        headerInfo.setLocale(new Locale(matcher.group(2)));
      } else {
        headerInfo.setAttribute(header);
      }
      headerInfos.put(header, headerInfo);
    }

    if (headerInfos.isEmpty()) {
      throw new IllegalStateException("Impossible to find headers from the CSV file. It may be corrupted");
    }

    return headerInfos;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
