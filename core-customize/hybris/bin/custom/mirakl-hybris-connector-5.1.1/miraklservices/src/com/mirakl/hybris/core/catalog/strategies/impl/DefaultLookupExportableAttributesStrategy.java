package com.mirakl.hybris.core.catalog.strategies.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.events.ExportableAttributeEvent;
import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;
import com.mirakl.hybris.core.catalog.strategies.LookupExportableAttributesStrategy;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.event.EventService;

public class DefaultLookupExportableAttributesStrategy implements LookupExportableAttributesStrategy {

  protected EventService eventService;

  @Override
  public void handleEvent(ExportableCategoryEvent event) {
    lookupExportableAttributes(event.getCategory(), event);
  }

  protected void lookupExportableAttributes(CategoryModel currentCategory, ExportableCategoryEvent event) {

    for (CategoryModel superCategory : currentCategory.getSupercategories()) {
      if (superCategory instanceof ClassificationClassModel) {
        ClassificationClassModel currentClassificationClass = (ClassificationClassModel) superCategory;
        if (event.getContext().getVisitedClassIds().add(currentClassificationClass.getCode())) {
          publishExportableAttributeEvents(currentClassificationClass, event);
          lookupExportableAttributes(currentClassificationClass, event);
        }
      }
    }
  }

  protected void publishExportableAttributeEvents(ClassificationClassModel currentClassificationClass,
      ExportableCategoryEvent event) {
    List<ClassAttributeAssignmentModel> attributeAssignments =
        currentClassificationClass.getDeclaredClassificationAttributeAssignments();
    for (ClassAttributeAssignmentModel attributeAssignment : attributeAssignments) {
      eventService.publishEvent(new ExportableAttributeEvent(attributeAssignment, event.getCategory(), event.getContext()));
    }
  }

  @Required
  public void setEventService(EventService eventService) {
    this.eventService = eventService;
  }
}
