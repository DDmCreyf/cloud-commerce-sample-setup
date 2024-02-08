package com.mirakl.hybris.core.catalog.events;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class ExportableCategoryEvent extends AbstractEvent {

  private static final long serialVersionUID = 4912467439372996798L;

  protected CategoryModel category;

  protected MiraklExportCatalogContext context;

  public ExportableCategoryEvent(CategoryModel category, MiraklExportCatalogContext context) {
    super();
    this.category = category;
    this.context = context;
  }

  public CategoryModel getCategory() {
    return category;
  }

  public MiraklExportCatalogContext getContext() {
    return context;
  }
}
