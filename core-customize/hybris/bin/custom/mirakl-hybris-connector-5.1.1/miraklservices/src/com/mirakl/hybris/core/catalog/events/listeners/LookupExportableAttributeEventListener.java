package com.mirakl.hybris.core.catalog.events.listeners;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;
import com.mirakl.hybris.core.catalog.strategies.LookupExportableAttributesStrategy;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

public class LookupExportableAttributeEventListener extends AbstractEventListener<ExportableCategoryEvent> {

  protected LookupExportableAttributesStrategy lookupExportableAttributesStrategy;

  @Override
  protected void onEvent(ExportableCategoryEvent event) {
    lookupExportableAttributesStrategy.handleEvent(event);
  }

  @Required
  public void setLookupExportableAttributesStrategy(LookupExportableAttributesStrategy lookupExportableAttributesStrategy) {
    this.lookupExportableAttributesStrategy = lookupExportableAttributesStrategy;
  }
}
