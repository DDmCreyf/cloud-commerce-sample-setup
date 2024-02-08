package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
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
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;
import com.mirakl.hybris.core.enums.MiraklValueListExportHeader;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWriteValueListStrategyTest {

  @InjectMocks
  private DefaultWriteValueListStrategy testObj;

  @Mock
  private ExportableAttributeEvent event;
  @Mock
  private MiraklExportCatalogContext context;
  @Mock
  private MiraklExportCatalogConfig exportConfig;
  @Mock
  private ClassAttributeAssignmentModel classAttributeAssignment;
  @Mock
  private ClassificationAttributeValueModel attributeValue1, attributeValue2, attributeValue3;
  @Mock
  private DefaultValueListNamingStrategy namingStrategy;
  @Mock
  private ClassificationAttributeModel classificationAttribute;
  @Captor
  private ArgumentCaptor<Map<String, String>> lineArgumentCaptor;
  @Mock
  private ExportCatalogWriter writer;

  @Before
  public void setUp() {
    when(event.getContext()).thenReturn(context);
    when(context.getWriter()).thenReturn(writer);
    when(context.getExportConfig()).thenReturn(exportConfig);
    when(context.getExportedValueListCodes()).thenReturn(new HashSet<>());
    when(exportConfig.isExportValueLists()).thenReturn(true);
    when(event.getAttributeAssignment()).thenReturn(classAttributeAssignment);
    when(classAttributeAssignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.ENUM);
    when(classAttributeAssignment.getAttributeValues()).thenReturn(asList(attributeValue1, attributeValue2, attributeValue3));
  }

  @Test
  public void shouldExportValueList() throws IOException {
    List<Locale> additionalLocales = asList(Locale.ENGLISH, Locale.GERMAN);
    when(exportConfig.getAdditionalLocales()).thenReturn(additionalLocales);

    testObj.handleEvent(event);

    verify(writer, times(3)).writeAttributeValue(lineArgumentCaptor.capture());

    Map<String, String> line = lineArgumentCaptor.getValue();
    assertThat(line.containsKey(MiraklValueListExportHeader.LIST_CODE.getCode())).isTrue();
    assertThat(line.containsKey(MiraklValueListExportHeader.LIST_LABEL.getCode())).isTrue();
    assertThat(line.containsKey(MiraklValueListExportHeader.LIST_LABEL.getCode() + "[en]")).isTrue();
    assertThat(line.containsKey(MiraklValueListExportHeader.LIST_LABEL.getCode() + "[de]")).isTrue();
    assertThat(line.containsKey(MiraklValueListExportHeader.VALUE_CODE.getCode())).isTrue();
    assertThat(line.containsKey(MiraklValueListExportHeader.VALUE_LABEL.getCode())).isTrue();
    assertThat(line.containsKey(MiraklValueListExportHeader.VALUE_LABEL.getCode() + "[en]")).isTrue();
    assertThat(line.containsKey(MiraklValueListExportHeader.VALUE_LABEL.getCode() + "[de]")).isTrue();
  }

  @Test
  public void shouldNotExportValueList() throws IOException {
    when(exportConfig.isExportValueLists()).thenReturn(false);

    testObj.handleEvent(event);

    verifyZeroInteractions(writer);
  }

  @Test
  public void shouldNotExportValueListTwice() throws Exception {
    List<Locale> additionalLocales = asList(Locale.ENGLISH, Locale.GERMAN);
    when(exportConfig.getAdditionalLocales()).thenReturn(additionalLocales);

    testObj.handleEvent(event);
    testObj.handleEvent(event);

    verify(writer, times(3)).writeAttributeValue(anyMapOf(String.class, String.class));
  }

}
