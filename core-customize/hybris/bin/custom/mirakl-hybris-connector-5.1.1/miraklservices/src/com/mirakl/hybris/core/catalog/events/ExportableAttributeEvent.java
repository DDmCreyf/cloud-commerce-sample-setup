package com.mirakl.hybris.core.catalog.events;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class ExportableAttributeEvent extends AbstractEvent {
  private static final long serialVersionUID = 7330944423774391832L;

  protected ClassAttributeAssignmentModel attributeAssignment;
  protected CategoryModel currentCategory;
  protected MiraklExportCatalogContext context;

  public ExportableAttributeEvent(ClassAttributeAssignmentModel attributeAssignment, CategoryModel currentCategory,
      MiraklExportCatalogContext context) {
    this.attributeAssignment = attributeAssignment;
    this.currentCategory = currentCategory;
    this.context = context;
  }

  public ClassAttributeAssignmentModel getAttributeAssignment() {
    return attributeAssignment;
  }

  public MiraklExportCatalogContext getContext() {
    return context;
  }

  public CategoryModel getCurrentCategory() {
    return currentCategory;
  }
}
