package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultValueListNamingStrategyTest {

  private static final String CLASSIFICATION_CLASS_LABEL = "classification-class-label";
  private static final String CLASSIFICATION_ATTRIBUTE_LABEL = "classification-attribute-label";
  private static final String CLASSIFICATION_CLASS_CODE = "classification-class-code";
  private static final String CLASSIFICATION_ATTRIBUTE_CODE = "classification-attribute-code";
  private static final String CORE_ATTRIBUTE_UID = "coreAttributeUID";
  private static final String LOCALIZED_CORE_ATTRIBUTE_LABEL = "description";

  @InjectMocks
  private DefaultValueListNamingStrategy strategy;

  private Locale locale = Locale.ENGLISH;

  @Mock
  private ClassAttributeAssignmentModel classAttributeAssignment;
  @Mock
  private ClassificationClassModel classificationClass;
  @Mock
  private ClassificationAttributeModel classificationAttribute;
  @Mock
  private MiraklCoreAttributeModel coreAttribute;

  @Before
  public void setUp() {
    when(classAttributeAssignment.getClassificationAttribute()).thenReturn(classificationAttribute);
    when(classAttributeAssignment.getClassificationClass()).thenReturn(classificationClass);
    when(classificationAttribute.getCode()).thenReturn(CLASSIFICATION_ATTRIBUTE_CODE);
    when(classificationAttribute.getName(locale)).thenReturn(CLASSIFICATION_ATTRIBUTE_LABEL);
    when(classificationClass.getCode()).thenReturn(CLASSIFICATION_CLASS_CODE);
    when(classificationClass.getName(locale)).thenReturn(CLASSIFICATION_CLASS_LABEL);
    when(coreAttribute.getUid()).thenReturn(CORE_ATTRIBUTE_UID);
    when(coreAttribute.getLabel(locale)).thenReturn(LOCALIZED_CORE_ATTRIBUTE_LABEL);
  }

  @Test
  public void shouldGenerateValueListName() {
    String code = strategy.getCode(classAttributeAssignment);

    assertThat(code).isEqualTo(CLASSIFICATION_ATTRIBUTE_CODE + "-" + CLASSIFICATION_CLASS_CODE);
  }

  @Test
  public void shouldGenerateValueListLabel() {
    String code = strategy.getLabel(classAttributeAssignment, locale);

    assertThat(code).isEqualTo(CLASSIFICATION_ATTRIBUTE_LABEL + "-" + CLASSIFICATION_CLASS_LABEL);
  }

  @Test
  public void shouldGenerateValueListNameForCoreAttribute() {
    String code = strategy.getCode(coreAttribute);

    assertThat(code).isEqualTo(CORE_ATTRIBUTE_UID + "-values");
  }

  @Test
  public void shouldGenerateValueListLabelForCoreAttribute() {
    String code = strategy.getLabel(coreAttribute, locale);

    assertThat(code).isEqualTo(LOCALIZED_CORE_ATTRIBUTE_LABEL);
  }

}
