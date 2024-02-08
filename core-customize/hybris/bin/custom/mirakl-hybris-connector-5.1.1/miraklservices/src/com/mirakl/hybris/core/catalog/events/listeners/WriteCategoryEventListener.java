package com.mirakl.hybris.core.catalog.events.listeners;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;
import com.mirakl.hybris.core.catalog.strategies.WriteCategoryStrategy;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

public class WriteCategoryEventListener extends AbstractEventListener<ExportableCategoryEvent> {

  protected WriteCategoryStrategy writeCategoryStrategy;

  @Override
  protected void onEvent(ExportableCategoryEvent event) {
    writeCategoryStrategy.handleEvent(event);
  }

  @Required
  public void setWriteCategoryStrategy(WriteCategoryStrategy writeCategoryStrategy) {
    this.writeCategoryStrategy = writeCategoryStrategy;
  }
}
