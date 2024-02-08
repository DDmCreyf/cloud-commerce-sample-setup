package com.mirakl.hybris.core.catalog.events.listeners;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.events.ExportableAttributeEvent;
import com.mirakl.hybris.core.catalog.strategies.WriteAttributeStrategy;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class WriteAttributeEventListener extends AbstractEventListener<ExportableAttributeEvent> {

  protected WriteAttributeStrategy writeAttributeStrategy;

  @Override
  protected void onEvent(ExportableAttributeEvent event) {
    writeAttributeStrategy.handleEvent(event);
    }

  @Required
  public void setWriteAttributeStrategy(WriteAttributeStrategy writeAttributeStrategy) {
    this.writeAttributeStrategy = writeAttributeStrategy;
  }
}
