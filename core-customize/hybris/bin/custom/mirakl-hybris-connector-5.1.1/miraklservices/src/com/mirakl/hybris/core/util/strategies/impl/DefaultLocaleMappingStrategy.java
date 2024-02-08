package com.mirakl.hybris.core.util.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.LOCALE_MAPPING_HYBRIS_TO_MIRAKL_PREFIX;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.LOCALE_MAPPING_MIRAKL_TO_HYBRIS_PREFIX;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.util.strategies.LocaleMappingStrategy;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class DefaultLocaleMappingStrategy implements LocaleMappingStrategy {

  protected ConfigurationService configurationService;
  protected CommonI18NService commonI18NService;

  @Override
  public Locale mapToMiraklLocale(LanguageModel language) {
    return mapToMiraklLocale(language.getIsocode());
  }

  @Override
  public Locale mapToMiraklLocale(String localeIsoCode) {
    validateParameterNotNull(localeIsoCode, "localeIsoCode must be provided");
    String mappedLocaleCode = configurationService.getConfiguration()
        .getString(format("%s.%s", LOCALE_MAPPING_HYBRIS_TO_MIRAKL_PREFIX, localeIsoCode.toLowerCase()), localeIsoCode);

    return commonI18NService.getLocaleForIsoCode(mappedLocaleCode);
  }

  @Override
  public Locale mapToHybrisLocale(Locale locale) {
    validateParameterNotNull(locale, "locale must be provided");
    String mappedLocaleCode = configurationService.getConfiguration()
        .getString(format("%s.%s", LOCALE_MAPPING_MIRAKL_TO_HYBRIS_PREFIX, locale.toString()));

    return isNotBlank(mappedLocaleCode) ? commonI18NService.getLocaleForIsoCode(mappedLocaleCode) : locale;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setCommonI18NService(CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }

}
