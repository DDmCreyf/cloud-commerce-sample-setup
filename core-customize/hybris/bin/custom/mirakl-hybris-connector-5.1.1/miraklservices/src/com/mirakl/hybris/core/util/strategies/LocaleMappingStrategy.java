package com.mirakl.hybris.core.util.strategies;

import java.util.Locale;

import de.hybris.platform.core.model.c2l.LanguageModel;

public interface LocaleMappingStrategy {

  /**
   * Maps a Hybris Language to a Mirakl Locale.
   * 
   * @param language the language to be used
   * @return the corresponding Locale in Mirakl
   */
  Locale mapToMiraklLocale(LanguageModel language);

  /**
   * Maps a Locale Isocode used in Hybris into its corresponding Locale in Mirakl
   * 
   * @param localeIsoCode the locale isocode
   * @return the corresponding Locale in Mirakl
   */
  Locale mapToMiraklLocale(String localeIsoCode);

  /**
   * Maps a Locale used in Mirakl to its corresponding Locale in Hybris.
   * 
   * @param locale the locale used in Mirakl
   * @return the corresponding Locale in Hybris
   */
  Locale mapToHybrisLocale(Locale locale);


}
