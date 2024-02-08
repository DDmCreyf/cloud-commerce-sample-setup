package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.google.common.collect.Sets.newHashSet;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.BOOLEAN_TYPE_PARAMETERS;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandler;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandlerResolver;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;
import com.mirakl.hybris.core.enums.MiraklAttributeExportHeader;
import com.mirakl.hybris.core.enums.MiraklAttributeRequirementLevel;
import com.mirakl.hybris.core.enums.MiraklAttributeType;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultExportCoreAttributesStrategyTest {

  private static final String LOCALIZED_ATTRIBUTE_CODE = "LocalizedAttribute";
  private static final String LOCALIZED_ATTRIBUTE_PATTERN = "%s [%s]";

  @InjectMocks
  private DefaultExportCoreAttributesStrategy strategy;

  @Mock
  private MiraklExportCatalogContext context;
  @Mock
  private MiraklExportCatalogConfig exportCatalogConfig;
  @Mock
  private ExportCatalogWriter writer;
  @Mock
  private MiraklCoreAttributeModel coreAttribute1, coreAttribute2;
  @Mock
  private CoreAttributeHandlerResolver coreAttributeHandlerResolver;
  @Mock
  private CoreAttributeHandler<MiraklCoreAttributeModel> listCoreAttributeHandler;
  @Captor
  private ArgumentCaptor<Map<String, String>> lineArgumentCaptor;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Mock
  private MiraklExportCatalogService exportCatalogService;
  @Mock
  private MiraklCatalogSystem miraklCatalogSystem;

  private MiraklAttributeRequirementLevel requirementLevel;

  @Before
  public void setUp() {
    requirementLevel = MiraklAttributeRequirementLevel.REQUIRED;

    when(context.getWriter()).thenReturn(writer);
    when(context.getExportConfig()).thenReturn(exportCatalogConfig);
    when(exportCatalogConfig.isExportAttributes()).thenReturn(true);
    when(exportCatalogConfig.isExportValueLists()).thenReturn(true);
    when(exportCatalogConfig.getCoreAttributes()).thenReturn(newHashSet(coreAttribute1, coreAttribute2));
    when(coreAttribute1.getType()).thenReturn(MiraklAttributeType.LIST);
    when(coreAttribute2.getType()).thenReturn(MiraklAttributeType.TEXT);
    when(coreAttribute1.getRequirementLevel()).thenReturn(requirementLevel);
    when(coreAttribute2.getRequirementLevel()).thenReturn(requirementLevel);
    when(exportCatalogService.formatAttributeExportName(LOCALIZED_ATTRIBUTE_CODE, Locale.ENGLISH))
        .thenReturn(LOCALIZED_ATTRIBUTE_CODE + " [en]");
    when(exportCatalogService.formatAttributeExportName(LOCALIZED_ATTRIBUTE_CODE, Locale.FRENCH))
        .thenReturn(LOCALIZED_ATTRIBUTE_CODE + " [fr]");
  }

  @Test
  public void shouldExportCoreAttributes() throws IOException {
    List<Locale> additionalLocales = asList(Locale.ENGLISH, Locale.GERMAN);
    when(exportCatalogConfig.getAdditionalLocales()).thenReturn(additionalLocales);
    when(coreAttributeHandlerResolver.determineHandler(coreAttribute1, context)).thenReturn(listCoreAttributeHandler);

    strategy.exportCoreAttributes(context);

    verify(writer, times(exportCatalogConfig.getCoreAttributes().size())).writeAttribute(lineArgumentCaptor.capture());
    Map<String, String> line = lineArgumentCaptor.getValue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.CODE.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.DESCRIPTION.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.DESCRIPTION.getCode() + "[en]")).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.DESCRIPTION.getCode() + "[de]")).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.DESCRIPTION.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.LABEL.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.TYPE.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.VARIANT.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.TYPE.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.TYPE_PARAMETERS.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.TYPE_PARAMETER.getCode())).isFalse();
    assertThat(line.containsKey(MiraklAttributeExportHeader.REQUIREMENT_LEVEL.getCode())).isTrue();
  }

  @Test
  public void shouldTransformBooleansIntoList() throws IOException {
    List<Locale> additionalLocales = asList(Locale.ENGLISH, Locale.GERMAN);
    when(exportCatalogConfig.getAdditionalLocales()).thenReturn(additionalLocales);
    when(coreAttribute1.getType()).thenReturn(MiraklAttributeType.BOOLEAN);
    when(exportCatalogConfig.getCoreAttributes()).thenReturn(Collections.singleton(coreAttribute1));

    strategy.exportCoreAttributes(context);

    verify(writer).writeAttribute(lineArgumentCaptor.capture());
    Map<String, String> line = lineArgumentCaptor.getValue();
    assertThat(line.get(MiraklAttributeExportHeader.TYPE.getCode())).isEqualTo(MiraklAttributeType.LIST.getCode());
    assertThat(line.get(MiraklAttributeExportHeader.TYPE_PARAMETERS.getCode())).isEqualTo(BOOLEAN_TYPE_PARAMETERS);
  }

  @Test
  public void shouldNotExportAttributesWhenNotNecessary() throws Exception {
    when(coreAttributeHandlerResolver.determineHandler(coreAttribute1, context)).thenReturn(listCoreAttributeHandler);
    when(exportCatalogConfig.isExportAttributes()).thenReturn(false);

    strategy.exportCoreAttributes(context);

    verify(writer, never()).writeAttribute(anyMapOf(String.class, String.class));
  }

  @Test
  public void shouldNotExportValuesWhenNotNecessary() throws Exception {
    when(exportCatalogConfig.isExportValueLists()).thenReturn(false);

    strategy.exportCoreAttributes(context);

    verify(writer, never()).writeAttributeValue(anyMapOf(String.class, String.class));
  }

  @Test
  public void shouldExportLocalizedCoreAttributes() throws Exception {
    List<String> coreAttributeCodes = new ArrayList<>();
    List<Locale> translatableLocales = asList(Locale.ENGLISH, Locale.FRENCH);
    when(exportCatalogConfig.getTranslatableLocales()).thenReturn(translatableLocales);
    when(coreAttributeHandlerResolver.determineHandler(coreAttribute1, context)).thenReturn(listCoreAttributeHandler);
    when(coreAttribute2.isLocalized()).thenReturn(true);
    when(coreAttribute2.getCode()).thenReturn(LOCALIZED_ATTRIBUTE_CODE);

    strategy.exportCoreAttributes(context);

    verify(writer, times(3)).writeAttribute(lineArgumentCaptor.capture());
    for (Map<String, String> line : lineArgumentCaptor.getAllValues()) {
      coreAttributeCodes.add(line.get(MiraklAttributeExportHeader.CODE.getCode()));
    }
    for (Locale translatableLocale : translatableLocales) {
      assertThat(coreAttributeCodes)
          .contains(String.format(LOCALIZED_ATTRIBUTE_PATTERN, LOCALIZED_ATTRIBUTE_CODE, translatableLocale.getLanguage()));
    }
  }


}
