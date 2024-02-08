package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.catalog.events.ExportableAttributeEvent;
import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.event.EventService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultLookupExportableAttributesStrategyTest {

  @InjectMocks
  private DefaultLookupExportableAttributesStrategy testObj;

  @Mock
  private EventService eventService;
  @Mock
  private ExportableCategoryEvent event;
  @Mock
  private CategoryModel category, superCategory, brandCategory;
  @Mock
  private ClassificationClassModel classificationClass1, classificationClass2;
  @Mock
  private ClassAttributeAssignmentModel classAttributeAssignment1, classAttributeAssignment2, classAttributeAssignment3;
  @Mock
  private MiraklExportCatalogContext context;
  @Mock
  private Set<String> visitedClassId;

  @Before
  public void setUp() {
    when(event.getCategory()).thenReturn(category);
    when(event.getContext()).thenReturn(context);
    when(context.getVisitedClassIds()).thenReturn(visitedClassId);
    when(category.getSupercategories())
        .thenReturn(asList(superCategory, brandCategory, classificationClass1, classificationClass2));
    when(classificationClass1.getDeclaredClassificationAttributeAssignments())
        .thenReturn(asList(classAttributeAssignment1, classAttributeAssignment2));
    when(classificationClass2.getDeclaredClassificationAttributeAssignments()).thenReturn(asList(classAttributeAssignment3));
    when(classificationClass1.getCode()).thenReturn("class-1");
    when(classificationClass2.getCode()).thenReturn("class-2");
    when(visitedClassId.add(anyString())).thenReturn(true);
  }

  @Test
  public void shouldHandleEvent() {
    testObj.handleEvent(event);

    verify(eventService, times(3)).publishEvent(any(ExportableAttributeEvent.class));
  }

}
