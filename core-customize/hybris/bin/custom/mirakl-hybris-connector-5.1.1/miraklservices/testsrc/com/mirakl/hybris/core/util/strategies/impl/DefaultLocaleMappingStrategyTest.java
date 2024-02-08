package com.mirakl.hybris.core.util.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.LOCALE_MAPPING_HYBRIS_TO_MIRAKL_PREFIX;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.LOCALE_MAPPING_MIRAKL_TO_HYBRIS_PREFIX;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

@RunWith(MockitoJUnitRunner.class)
public class DefaultLocaleMappingStrategyTest {
  private static final Locale LOCALE_ENGLISH = Locale.ENGLISH;
  private static final Locale LOCALE_US = Locale.US;
  private static final String US_LOCALE_ISOCODE = "en_US";
  private static final String ENGLISH_LOCALE_ISOCODE = "en";
  @InjectMocks
  private DefaultLocaleMappingStrategy localeMappingStrategy;
  @Mock
  private CommonI18NService commonI18NService;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private LanguageModel language;
  @Mock
  private Configuration configuration;

  @Before
  public void setUp() throws Exception {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(language.getIsocode()).thenReturn(ENGLISH_LOCALE_ISOCODE);
    when(commonI18NService.getLocaleForIsoCode(ENGLISH_LOCALE_ISOCODE)).thenReturn(LOCALE_ENGLISH);
    when(commonI18NService.getLocaleForIsoCode(US_LOCALE_ISOCODE)).thenReturn(LOCALE_US);
  }

  @Test
  public void shouldUseMappingToMiraklLocaleWhenAvailable() throws Exception {
    when(configuration.getString(format("%s.%s", LOCALE_MAPPING_HYBRIS_TO_MIRAKL_PREFIX, ENGLISH_LOCALE_ISOCODE),
        ENGLISH_LOCALE_ISOCODE)).thenReturn(US_LOCALE_ISOCODE);

    Locale locale = localeMappingStrategy.mapToMiraklLocale(language);

    assertThat(locale).isEqualTo(LOCALE_US);
  }

  @Test
  public void shouldUseDefaultLocaleWhenNoMappingToMiraklAvailable() throws Exception {
    when(configuration.getString(format("%s.%s", LOCALE_MAPPING_HYBRIS_TO_MIRAKL_PREFIX, ENGLISH_LOCALE_ISOCODE),
        ENGLISH_LOCALE_ISOCODE)).thenReturn(ENGLISH_LOCALE_ISOCODE);

    Locale locale = localeMappingStrategy.mapToMiraklLocale(language);

    assertThat(locale).isEqualTo(LOCALE_ENGLISH);
  }

  @Test
  public void shouldUseMappingToHybrisLocaleWhenAvailable() throws Exception {
    when(configuration.getString(format("%s.%s", LOCALE_MAPPING_MIRAKL_TO_HYBRIS_PREFIX, US_LOCALE_ISOCODE)))
        .thenReturn(ENGLISH_LOCALE_ISOCODE);

    Locale locale = localeMappingStrategy.mapToHybrisLocale(LOCALE_US);

    assertThat(locale).isEqualTo(LOCALE_ENGLISH);
  }

  @Test
  public void shouldUseDefaultLocaleWhenNoMappingToHybrisAvailable() throws Exception {
    when(configuration.getString(format("%s.%s", LOCALE_MAPPING_MIRAKL_TO_HYBRIS_PREFIX, US_LOCALE_ISOCODE))).thenReturn(EMPTY);

    Locale locale = localeMappingStrategy.mapToHybrisLocale(LOCALE_US);

    assertThat(locale).isEqualTo(LOCALE_US);
  }

}
