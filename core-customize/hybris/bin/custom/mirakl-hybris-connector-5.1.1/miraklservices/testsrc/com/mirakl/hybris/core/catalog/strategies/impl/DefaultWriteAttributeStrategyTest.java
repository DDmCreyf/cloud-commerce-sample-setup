package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.events.ExportableAttributeEvent;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.strategies.AttributeDefaultValueStrategy;
import com.mirakl.hybris.core.catalog.strategies.AttributeVarianceStrategy;
import com.mirakl.hybris.core.catalog.strategies.ClassificationAttributeExportEligibilityStrategy;
import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;
import com.mirakl.hybris.core.enums.MiraklAttributeExportHeader;
import com.mirakl.hybris.core.enums.MiraklAttributeRequirementLevel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.category.model.CategoryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWriteAttributeStrategyTest {

  @InjectMocks
  private DefaultWriteAttributeStrategy testObj;

  @Mock
  private ValueListNamingStrategy valueListNamingStrategy;
  @Mock
  private AttributeVarianceStrategy attributeVarianceStrategy;
  @Mock
  private AttributeDefaultValueStrategy attributeDefaultValueStrategy;
  @Mock
  private ExportableAttributeEvent event;
  @Mock
  private MiraklExportCatalogContext context;
  @Mock
  private MiraklExportCatalogConfig exportConfig;
  @Mock
  private ClassAttributeAssignmentModel classAttributeAssignment;
  @Mock
  private ClassificationAttributeModel classificationAttribute;
  @Mock
  private ClassificationAttributeValueModel attrValue1, attrValue2;
  @Mock
  private CategoryModel parentCategory, currentCategory;
  @Captor
  private ArgumentCaptor<Map<String, String>> lineArgumentCaptor;
  @Mock
  private ExportCatalogWriter writer;
  @Mock
  private MiraklExportCatalogService exportCatalogService;
  @Mock
  private ClassificationAttributeExportEligibilityStrategy attributeExportEligibilityStrategy;

  private MiraklAttributeRequirementLevel requirementLevel;

  @Before
  public void setUp() {
    requirementLevel = MiraklAttributeRequirementLevel.REQUIRED;

    when(event.getContext()).thenReturn(context);
    when(event.getCurrentCategory()).thenReturn(currentCategory);
    when(context.getWriter()).thenReturn(writer);
    when(context.getExportConfig()).thenReturn(exportConfig);

    when(exportConfig.isExportAttributes()).thenReturn(true);
    when(event.getAttributeAssignment()).thenReturn(classAttributeAssignment);
    when(classAttributeAssignment.getClassificationAttribute()).thenReturn(classificationAttribute);
    when(classAttributeAssignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.ENUM);
    when(classAttributeAssignment.getMarketplaceRequirementLevel()).thenReturn(requirementLevel);
    when(attributeExportEligibilityStrategy.isExportableAttribute(classAttributeAssignment)).thenReturn(true);
  }

  @Test
  public void shouldWriteAttribute() throws IOException {
    List<Locale> additionalLocales = asList(Locale.ENGLISH, Locale.GERMAN);
    when(exportConfig.getAdditionalLocales()).thenReturn(additionalLocales);

    testObj.handleEvent(event);

    verify(writer).writeAttribute(lineArgumentCaptor.capture());

    Map<String, String> line = lineArgumentCaptor.getValue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.CODE.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.DESCRIPTION.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.DESCRIPTION.getCode() + "[en]")).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.DESCRIPTION.getCode() + "[de]")).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.DESCRIPTION.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.HIERARCHY_CODE.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.LABEL.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.TYPE.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.TYPE_PARAMETERS.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.VARIANT.getCode())).isTrue();
    assertThat(line.containsKey(MiraklAttributeExportHeader.REQUIREMENT_LEVEL.getCode())).isTrue();
  }

  @Test
  public void shouldNotExportAttributes() {
    when(exportConfig.isExportAttributes()).thenReturn(false);

    testObj.handleEvent(event);

    verifyZeroInteractions(writer);
  }

}
