package com.mirakl.hybris.core.catalog.strategies;

import com.mirakl.hybris.core.catalog.events.ExportableAttributeEvent;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface WriteValueListStrategy {

  /**
   * Writes the value list in the value lists file
   *
   * @param event The event fired when an exportable attribute is found
   */
  void handleEvent(ExportableAttributeEvent event);

}
