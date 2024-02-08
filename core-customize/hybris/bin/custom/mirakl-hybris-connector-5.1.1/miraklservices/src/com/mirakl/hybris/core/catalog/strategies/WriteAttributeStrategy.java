package com.mirakl.hybris.core.catalog.strategies;

import com.mirakl.hybris.core.catalog.events.ExportableAttributeEvent;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface WriteAttributeStrategy {

  /**
   * Writes the attribute in the attributes file
   *
   * @param event The event fired when an exportable attribute is found
   */
  void handleEvent(ExportableAttributeEvent event);

}
