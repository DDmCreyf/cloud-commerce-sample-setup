package com.mirakl.hybris.core.catalog.strategies;

import com.mirakl.hybris.core.catalog.events.ExportableAttributeEvent;
import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;

public interface LookupExportableAttributesStrategy {

  /**
   * Browses the classification category tree. Fires an {@link ExportableAttributeEvent} every time it encounters an exportable
   * attribute. It updates the export context to keep track of the already visited categories to avoid double exports.
   *
   * @param event The event fired when an exportable category is found
   */
  void handleEvent(ExportableCategoryEvent event);
}
